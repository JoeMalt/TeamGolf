package main.java.MixedRealityPDF.AnnotationProcessor.Identification;

import main.java.MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import main.java.MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import main.java.MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

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
        cropAnnotations((BufferedImage) image);
        ArrayList<Annotation> identifiedAnnotations = new ArrayList<>();
        FeatureExtractor featureExtractor = new FeatureExtractor();
        for(BufferedImage annotationImage : annotationImages){
            double coverage = featureExtractor.getCoverage(annotationImage);
        }
        return identifiedAnnotations;
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
