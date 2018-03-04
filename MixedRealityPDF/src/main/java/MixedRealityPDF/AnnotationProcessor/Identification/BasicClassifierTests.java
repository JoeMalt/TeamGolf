package MixedRealityPDF.AnnotationProcessor.Identification;


import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Diptarko Roy on 03/03/2018.
 */
public class BasicClassifierTests {


    public static void main(String[] args) throws IOException {
        //testGetAllFileNamesInDirectory();
        //testColorDetector();
        // testCoverageDetector();
        // testBasicAspectRatioDetector();
        // testSubimageAspectRatioDetector();
        // testClassifier();
    }
/*


    private static void testClassifier() throws IOException {
        BufferedImage highlightTest = ImageIO.read(new File("Data/highlight/0.png"));
        BufferedImage underlineTest = ImageIO.read(new File("Data/underline/1--annotation--3.png"));
        BufferedImage textTest = ImageIO.read(new File("Data/text/0.png"));

        BasicClassifier basicClassifier = new BasicClassifier();

        Annotation predictionHighlight = BasicClassifier.identifySingleAnnotation(highlightTest, getSimpleBoundingBoxForWholeImage(highlightTest), 1);
        Annotation predictionUnderline = BasicClassifier.identifySingleAnnotation(underlineTest, getSimpleBoundingBoxForWholeImage(underlineTest), 1);
        Annotation predictionText = BasicClassifier.identifySingleAnnotation(textTest, getSimpleBoundingBoxForWholeImage(textTest), 1);

        System.out.println("predictionHighlight = " + predictionHighlight);
        System.out.println("predictionUnderline = " + predictionUnderline);
        System.out.println("predictionText = " + predictionText);


    }

    private static AnnotationBoundingBox getSimpleBoundingBoxForWholeImage (BufferedImage annotationSubimage) {
        ClusteringPoint topLeft = new ClusteringPoint(0, 0);
        ClusteringPoint topRight = new ClusteringPoint(annotationSubimage.getWidth(), 0);
        ClusteringPoint bottomRight = new ClusteringPoint(annotationSubimage.getWidth(), annotationSubimage.getHeight());
        ClusteringPoint bottomLeft = new ClusteringPoint(0, annotationSubimage.getHeight());
        return new AnnotationBoundingBox(topLeft, topRight, bottomLeft, bottomRight);
    }

    private static void testBasicAspectRatioDetector() throws IOException {
        System.out.println("testBasicAspectRatioDetector running-------");
        double ar_highlight = BasicClassifier.aspectRatio(ImageIO.read(new File("Data/highlight/0.png")), false);
        double ar_underline = BasicClassifier.aspectRatio(ImageIO.read(new File("Data/underline/1--annotation--3.png")), false);
        double ar_text = BasicClassifier.aspectRatio(ImageIO.read(new File("Data/text/0.png")), false);
        System.out.println("ar_highlight = " + ar_highlight);
        System.out.println("ar_underline = " + ar_underline);
        System.out.println("ar_text = " + ar_text);
    }

    private static void testSubimageAspectRatioDetector() throws IOException {
        System.out.println("testSubimageAspectRatioDetector running-------");
        double ar_highlight = BasicClassifier.aspectRatio(ImageIO.read(new File("Data/highlight/0.png")), true);
        double ar_underline = BasicClassifier.aspectRatio(ImageIO.read(new File("Data/underline/1--annotation--3.png")), true);
        double ar_text = BasicClassifier.aspectRatio(ImageIO.read(new File("Data/text/0.png")), true);
        System.out.println("ar_text = " + ar_text);
        System.out.println("ar_underline = " + ar_underline);
        System.out.println("ar_highlight = " + ar_highlight);



    }



    private static void testCoverageDetector() throws IOException {
        Double allColorsHighlight = BasicClassifier.getFractionOfNonTransparentPixels(ImageIO.read(new File("Data/highlight/0.png")));
        Double allColorsUnderline = BasicClassifier.getFractionOfNonTransparentPixels(ImageIO.read(new File("Data/underline/1--annotation--3.png")));
        Double allColorsText = BasicClassifier.getFractionOfNonTransparentPixels(ImageIO.read(new File("Data/text/0.png")));
        System.out.println("allColorsText = " + allColorsText);
        System.out.println("allColorsUnderline = " + allColorsUnderline);
        System.out.println("allColorsHighlight = " + allColorsHighlight);
    }

    private static void testColorDetector() throws IOException {
        List<Color> allColorsHighlight = BasicClassifier.getColoredPixelList(ImageIO.read(new File("Data/highlight/0.png")));
        List<Color> allColorsUnderline = BasicClassifier.getColoredPixelList(ImageIO.read(new File("Data/underline/1--annotation--3.png")));
        List<Color> allColorsText = BasicClassifier.getColoredPixelList(ImageIO.read(new File("Data/text/0.png")));
        System.out.println("allColorsText = " + allColorsText);
        System.out.println("allColorsUnderline = " + allColorsUnderline);
        System.out.println("allColorsHighlight = " + allColorsHighlight);
    }


    private static void testGetAllFileNamesInDirectory() {
        String dirName = "Data/highlight";
        BasicClassifier.getAllFileNamesInDirectory(dirName);
    }

    */
}
