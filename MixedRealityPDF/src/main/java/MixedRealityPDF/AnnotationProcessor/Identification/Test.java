package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Test {
    static String RELATIVE_PATH;
    public static void main(String [] args){
        Path currentRelativePath = Paths.get("");
        RELATIVE_PATH = currentRelativePath.toAbsolutePath().toString();
        BufferedImage fullImage = readFullImage("test1_heavy.jpg");
        AnnotationBoundingBox boxWithNoText = new AnnotationBoundingBox(
                new ClusteringPoint(0, 0), new ClusteringPoint(10, 0),
                new ClusteringPoint(0, 10), new ClusteringPoint(10, 10));

        AnnotationBoundingBox boxWithLittleText = new AnnotationBoundingBox(
                new ClusteringPoint(100, 100), new ClusteringPoint(200, 100),
                new ClusteringPoint(100, 200), new ClusteringPoint(200, 200));

        AnnotationBoundingBox boxWithManyText = new AnnotationBoundingBox(
                new ClusteringPoint(500, 500), new ClusteringPoint(800, 500),
                new ClusteringPoint(500, 800), new ClusteringPoint(800, 800));

        AnnotationBoundingBox box4 = new AnnotationBoundingBox(
                new ClusteringPoint(700, 700), new ClusteringPoint(940, 700),
                new ClusteringPoint(700, 900), new ClusteringPoint(940, 900));

        ArrayList<AnnotationBoundingBox> list = new ArrayList<>();
        list.add(boxWithNoText);
        list.add(boxWithLittleText);
        list.add(boxWithManyText);
        list.add(box4);

        AnnotationIdentifier identifier = new AnnotationIdentifier();
        identifier.identifyAnnotations(fullImage, list, 0);
    }

    private static BufferedImage readFullImage(String filename){
        BufferedImage fullImage = null;
        try {
            String filepath = Paths.get(RELATIVE_PATH, "Data", filename).toString();
            fullImage = ImageIO.read(new File(filepath));
        }catch(IOException ioe){
            System.err.println(String.format("IOException reading image %s", filename));
            ioe.printStackTrace();
        }
        return fullImage;
    }

}
