package MixedRealityPDF.AnnotationProcessor.Identification;


import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.Text;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.ImageProcessor.ImgHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

/**
 * Created by Diptarko Roy on 03/03/2018.
 */
public class BasicClassifier implements IAnnotationIdentifier{
    private static final String highlightDir = "Data/highlight";
    private static final String underlineDir = "Data/underline";
    private static final String textDir = "Data/text";


    private static Annotation identifySingleAnnotation(BufferedImage fullImage,
                                                      BufferedImage annotationSubImage,
                                                      AnnotationBoundingBox annotationBoundingBox,
                                                      int pageNumber,
                                                      boolean defaultToText,
                                                      double coverageThreshold,
                                                      double aspectRatioThreshold) {

        int toAdd_x = annotationBoundingBox.getTopLeft().getX();
        int toAdd_y = annotationBoundingBox.getTopLeft().getY();
        toAdd_y = Annotation.ImageYToPDFY(toAdd_y, fullImage.getHeight());
        toAdd_y -= annotationSubImage.getHeight();

        Annotation toAdd = null;

        if (getFractionOfNonTransparentPixels(annotationSubImage) > coverageThreshold) {
            if (aspectRatio(annotationSubImage, true) > aspectRatioThreshold) {
                // aspect ratio = width / height ...
                // aspect ratio is higher than the threshold in this case ...
                // choose [underline] -- or but just default to (text) here

                if (defaultToText) {
                    toAdd = new Text(toAdd_x, toAdd_y, annotationSubImage, pageNumber);
                } else {
                    toAdd = new UnderLine(toAdd_x, toAdd_y, annotationBoundingBox.getTopRight().getX()-annotationBoundingBox.getTopLeft().getX(), pageNumber);
                }
            } else {
                // high coverage but relatively small aspect ratio -- choose [highlight]

                float highlight_width = annotationBoundingBox.getTopRight().getX() - annotationBoundingBox.getTopLeft().getX();
                float highlight_height = annotationBoundingBox.getBottomLeft().getY() - annotationBoundingBox.getTopLeft().getY();

                toAdd = new Highlight(toAdd_x, toAdd_y, highlight_width, highlight_height, pageNumber);
            }
        } else {
            // text
            toAdd = new Text(toAdd_x, toAdd_y, annotationSubImage, pageNumber);
        }

        return toAdd;
    }

    @Override
    public Collection<Annotation> identifyAnnotations(BufferedImage fullImage, Collection<AnnotationBoundingBox> points, int pageNumber) {

        double coverageThreshold = 0.35;

        // 10  - works better in most cases.
        double aspectRatioThreshold = 10;

        List<Annotation> classifiedAnnotations = new LinkedList<>();

        for (AnnotationBoundingBox annotationBoundingBox : points) {

            int topLeftX = annotationBoundingBox.getTopLeft().getX();
            int topLeftY = annotationBoundingBox.getTopLeft().getY();
            int annotationWidth = annotationBoundingBox.getTopRight().getX()-annotationBoundingBox.getTopLeft().getX();
            int annotationHeight = annotationBoundingBox.getBottomLeft().getY() - annotationBoundingBox.getTopLeft().getY();

            BufferedImage annotationSubImage = fullImage.getSubimage(topLeftX, topLeftY, annotationWidth, annotationHeight);



            Annotation toAdd = BasicClassifier.identifySingleAnnotation(
                    fullImage,
                    annotationSubImage,
                    annotationBoundingBox,
                    pageNumber,
                    false,
                    coverageThreshold,
                    aspectRatioThreshold);

            classifiedAnnotations.add(toAdd);

        }
        return classifiedAnnotations;
    }
    private static List<File> getAllFileNamesInDirectory(String myDirectoryPath) {
        File directoryFile = new File(myDirectoryPath);
        File[] files = directoryFile.listFiles();
        List<File> filesToReturn = new LinkedList<>();
        for (int iFile = 0; iFile < files.length ; iFile++) {
            if (files[iFile].isFile()){ //this line weeds out other directories/folders
                filesToReturn.add(files[iFile]);
            }
        }
        return filesToReturn;
    }
    private  static List<Color> getColoredPixelList(BufferedImage subImage) {
        List<Color> colorsOfPixelsInInputImage = new LinkedList<>();
        for (int x = 0; x < subImage.getWidth(); x++) {
            for (int y = 0; y <  subImage.getHeight(); y++) {
                if (!pixelIsTransparent(subImage, x, y)) {
                    colorsOfPixelsInInputImage.add(new Color(subImage.getRGB(x, y)));
                }
            }
        }
        return colorsOfPixelsInInputImage;
    }
    private  static Double getFractionOfNonTransparentPixels(BufferedImage subImage) {


        int totalNumPixels = (subImage.getHeight()) * subImage.getWidth();
        int nonTransparentCount = 0;



        for (int x = 0; x < subImage.getWidth() ; x++) {
            for (int y = 0; y < subImage.getHeight(); y++) {
                if (!pixelIsTransparent(subImage, x, y)) {
                    nonTransparentCount++;
                }
            }
        }

        return nonTransparentCount/(double)totalNumPixels;
    }

    private static int getHeightOfActualAnnotation(BufferedImage subImage) {
        BufferedImage blackNWhite = ImgHelper.computeBlackAndWhite(subImage);
        ArrayList<String> vector = new ArrayList<>();
        FeatureExtractorV2 featureExtractorV2 = new FeatureExtractorV2();
        int height = featureExtractorV2.height(blackNWhite);
        return height;
    }

    private static double aspectRatio(BufferedImage subImage, boolean morePreciseHeight) {

        if (!morePreciseHeight) {
            return subImage.getWidth() / (double) subImage.getHeight();
        } else {
            return subImage.getWidth()/(double)getHeightOfActualAnnotation(subImage);
        }
    }

    private static boolean pixelIsTransparent(BufferedImage subImage, int x, int y) {
        return ((subImage.getRGB(x, y)>>24) == 0x00);
    }


}
