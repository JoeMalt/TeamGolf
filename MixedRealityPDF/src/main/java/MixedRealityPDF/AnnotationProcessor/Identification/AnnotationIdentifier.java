package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;
import MixedRealityPDF.Image.Handler;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Decides what sort of Annotation parts of document specified by bounding boxes are.
 * **/
public class AnnotationIdentifier {
    private Collection<AnnotationBoundingBox> points;
    private Collection<BufferedImage> annotationImages;

    public AnnotationIdentifier(Collection<AnnotationBoundingBox> points) {
        this.points = points;
        annotationImages = new ArrayList<>();
    }

    public Collection<Annotation> identifyAnnotations(Image image) {
        // TODO(aga): remove changing to grayscale as images will be black and white on input
        Handler handler = new Handler((BufferedImage) image);
        image = handler.getBlackAndWhite();

        cropAnnotations((BufferedImage) image);
        ArrayList<Annotation> identifiedAnnotations = new ArrayList<>();
        FeatureExtractor featureExtractor = new FeatureExtractor();
        int imageByteData [] = null;
        int i = 0;

        PythonInterpreter interpreter = new PythonInterpreter();
        PyObject trainTree = interpreter.get("train_tree");
        PyObject testTree = interpreter.get("predict");
        for(BufferedImage annotationImage : annotationImages){
            annotationImage = scaleAnnotation(annotationImage);
            saveAnnotationBox(annotationImage, "scale_test_" + i);
            imageByteData = imageToByteArray(annotationImage);
            trainTree.__call__();
            i++;
        }


        return identifiedAnnotations;
    }

    private BufferedImage scaleAnnotation(BufferedImage image){
        int scaledHeight = 100;
        int scaledWidth = 100;
        BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaled;
    }

    public int [] imageToByteArray(BufferedImage image){
        int [] imageByteData = new int[image.getWidth() * image.getWidth()];
        int index;
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                index = image.getWidth() * y + x;
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    imageByteData[index] = 0;
                }
                else if(image.getRGB(x, y) == Color.WHITE.getRGB()){
                    imageByteData[index] = 1;
                }
                else{
                    imageByteData[index] = -1;
                }
            }
        }
        return imageByteData;
    }

    private void cropAnnotations(BufferedImage fullImage){
        BufferedImage image;
        ClusteringPoint topLeft;
        int width;
        int height;
        for(AnnotationBoundingBox boundingBox : points) {
            topLeft = boundingBox.getTopLeft();
            width = boundingBox.getTopRight().getX() - topLeft.getX();
            height = boundingBox.getBottomLeft().getY() - topLeft.getY();
            BufferedImage subImage = fullImage.getSubimage(topLeft.getX(), topLeft.getY(), width, height);

            // get a copy of image because .getSubimage operates on the original
            image = new BufferedImage(subImage.getWidth(), subImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(subImage, 0, 0, null);

            annotationImages.add(image);
        }
    }

    /**
     * Debugging function saving annotation cutouts from the big image**/
    public void saveAnnotationBoxes(){
        Path currentRelativePath = Paths.get("");
        String RELATIVE_PATH = String.format("%s/Data/", currentRelativePath.toAbsolutePath().toString());
        int i = 0;
        for(BufferedImage image : annotationImages) {
            String filename = String.format("annotation_box%d.jpg", i);
            try {
                ImageIO.write(image, "png", new File(RELATIVE_PATH + filename));
            } catch (IOException e) {
                System.err.println("IOException when writing image: ");
                e.printStackTrace();
            }
            i++;
        }
    }

    public void saveAnnotationBox(BufferedImage image, String name){
        Path currentRelativePath = Paths.get("");
        String RELATIVE_PATH = String.format("%s/Data/", currentRelativePath.toAbsolutePath().toString());
        int i = 0;
        String filename = String.format("%s.jpg", name);
        try {
            ImageIO.write(image, "png", new File(RELATIVE_PATH + filename));
        } catch (IOException e) {
            System.err.println("IOException when writing image: ");
            e.printStackTrace();
        }
    }

    public Collection<AnnotationBoundingBox> getPoints() {
        return points;
    }

    public void setPoints(Collection<AnnotationBoundingBox> points) {
        this.points = points;
    }

    public Collection<BufferedImage> getAnnotationImages() {
        return annotationImages;
    }

    public void setAnnotationImages(Collection<BufferedImage> annotationImages) {
        this.annotationImages = annotationImages;
    }
}
