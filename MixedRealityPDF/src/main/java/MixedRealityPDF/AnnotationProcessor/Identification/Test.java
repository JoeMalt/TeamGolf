package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String [] args){
        BufferedImage fullImage = readFullImage("test1_heavy");
        AnnotationBoundingBox boxWithNoText = new AnnotationBoundingBox(
                new ClusteringPoint(0, 0), new ClusteringPoint(10, 0),
                new ClusteringPoint(0, 10), new ClusteringPoint(10, 10));

        AnnotationBoundingBox boxWithLittleText = new AnnotationBoundingBox(
                new ClusteringPoint(100, 100), new ClusteringPoint(200, 100),
                new ClusteringPoint(100, 200), new ClusteringPoint(200, 200));

        AnnotationBoundingBox boxWithManyText = new AnnotationBoundingBox(
                new ClusteringPoint(500, 500), new ClusteringPoint(800, 500),
                new ClusteringPoint(500, 800), new ClusteringPoint(800, 800));

        ArrayList<AnnotationBoundingBox> list = new ArrayList<>();
        list.add(boxWithNoText);
        list.add(boxWithLittleText);
        list.add(boxWithManyText);

        AnnotationIdentifier annotationIdentifier = new AnnotationIdentifier(list);
        annotationIdentifier.identifyAnnotations(fullImage);
        annotationIdentifier.saveAnnotationBoxes();
    }

    private static BufferedImage readFullImage(String filename){
        Path currentRelativePath = Paths.get("");
        String RELATIVE_PATH = String.format("%s/Data/", currentRelativePath.toAbsolutePath().toString());
        BufferedImage fullImage = null;
        try {
            fullImage = ImageIO.read(new File(String.format("%s%s", RELATIVE_PATH, filename));
        }catch(IOException ioe){
            System.err.println(String.format("IOException reading image %s", filename));
            ioe.printStackTrace();
        }
        return fullImage;
    }

    private static Map<String, ArrayList<BufferedImage>> readClassImageMap(){
        Path currentRelativePath = Paths.get("");
        String RELATIVE_PATH = String.format("%s/Data/", currentRelativePath.toAbsolutePath().toString());
        String [] dirNames = new String[]{"text", "underline", "highlight"};
        String filename = "annotation";
        Map<String, ArrayList<BufferedImage>> imagesInClasses = new HashMap<>();

        int numbOfFiles = 0;
        for(String dirName : dirNames) {
            try {
                numbOfFiles = (int) Files.list(Paths.get(String.format("%s%s", RELATIVE_PATH, dirName))).count();
            }catch (IOException ie){
                System.err.println("Error counting number of files.");
                ie.printStackTrace();
            }

            ArrayList<BufferedImage> imagesList = new ArrayList<>();
            BufferedImage image = null;
            for(int i = 0; i < numbOfFiles; i++){
                try {
                    image = ImageIO.read(new File(String.format("%s%s%d.png", RELATIVE_PATH, filename, i)));
                }catch(IOException ioe){
                    System.err.println(String.format("IOException reading image %d: ", i));
                    ioe.printStackTrace();
                }
                imagesList.add(image);
            }
            imagesInClasses.put(dirName, imagesList);
        }
    }
}
