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
    public static void main(String [] args){
        Path currentRelativePath = Paths.get("");
        String RELATIVE_PATH = String.format("%s/Data/", currentRelativePath.toAbsolutePath().toString());
        System.out.println(currentRelativePath.toAbsolutePath().toString());
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
}
