package main.java.MixedRealityPDF.AnnotationProcessor.Identification;

import main.java.MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import main.java.MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Test {
    public static void main(String [] args){
        Path currentRelativePath = Paths.get("");
        String RELATIVE_PATH = String.format("%s/Data/", currentRelativePath.toAbsolutePath().toString());
        String filename = "test1_heavy.jpg";
        BufferedImage fullImage = null;
        try {
            fullImage = ImageIO.read(new File(RELATIVE_PATH + filename));
        }catch(IOException ioe){
            System.err.println("IOException: ");
            ioe.printStackTrace();
        }

        AnnotationBoundingBox boxWithNoText = new AnnotationBoundingBox(
                new ClusteringPoint(0, 0), new ClusteringPoint(10, 0),
                new ClusteringPoint(0, 10), new ClusteringPoint(10, 10));

        AnnotationBoundingBox boxWithLittleText = new AnnotationBoundingBox(
                new ClusteringPoint(100, 100), new ClusteringPoint(200, 100),
                new ClusteringPoint(100, 200), new ClusteringPoint(200, 200));

        ArrayList<AnnotationBoundingBox> list = new ArrayList<>();
        list.add(boxWithNoText);
        list.add(boxWithLittleText);
        AnnotationIdentifier annotationIdentifier = new AnnotationIdentifier(list);
        annotationIdentifier.identifyAnnotations(fullImage);
        annotationIdentifier.saveAnnotationBoxes();
    }
}
