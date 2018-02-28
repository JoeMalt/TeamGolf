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
        AnnotationBoundingBox box;
        ArrayList<AnnotationBoundingBox> points = new ArrayList<>();
        for(int i = 0; i < 11; i++){
            box = new AnnotationBoundingBox(
                    new ClusteringPoint(0, 0), new ClusteringPoint(10, 0),
                    new ClusteringPoint(0, 10), new ClusteringPoint(10, 10));
            points.add(box);
        }

        AnnotationIdentifier identifier = new AnnotationIdentifier();
        identifier.testIdentifyAnnotations(points, 0);
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
