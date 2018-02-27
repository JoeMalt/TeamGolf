package MixedRealityPDF.ImageProcessor.Alignment;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


import MixedRealityPDF.ImageProcessor.ColourRemoval.Stats;
import javafx.util.Pair;

public class ImageWrapper {


    // Testing -- TODO : delete
    public static void main(String[] args) throws IOException {
        String origFP = "Data/scans/new2a.png";
        String newFP = "Data/scans/new1a.png";

        BufferedImage origBI = ImageIO.read(new File(origFP));
        BufferedImage newBI = ImageIO.read(new File(newFP));

        ImageWrapper origPDFW = new ImageWrapper(origBI);
        ImageWrapper newPDFW = new ImageWrapper(newBI);

        String outPath1 = "Data/scans/new1a--new.png";
        String outPath2 = "Data/scans/new2a--new.png";

        save(newPDFW.getImage(false, true), outPath1);
        save(ImageWrapper.align(origBI, newBI), outPath1);
    }

    // return aligned scan without colour modifications
    public static BufferedImage align(BufferedImage original, BufferedImage modified) {

        ImageWrapper originalImageWrapper = new ImageWrapper(original);
        ImageWrapper modifiedImageWrapper = new ImageWrapper(modified);

        // Need to extract just the black component to find the bounding box of the black text
        BufferedImage blackComponentOfModified = modifiedImageWrapper.getImage(true, false);

        // Perform alignment based on the original and the black component of the modified image
        TextBoundingBox originalBB = originalImageWrapper.boundingBox();
        TextBoundingBox scanBB = (new ImageWrapper(blackComponentOfModified)).boundingBox();

        // Apply the transformation to the image with the colour and the black text.
        BufferedImage correctlyAlignedImage = correctAlignment(scanBB, originalBB, modifiedImageWrapper.bufferedImage);

        return correctlyAlignedImage;
    }

    public static BufferedImage getBlackComponent(BufferedImage image) {
        ImageWrapper imageWrapper = new ImageWrapper(image);
        return imageWrapper.getImage(true, false);
    }

    public static BufferedImage getColourComponent(BufferedImage image) {
        ImageWrapper imageWrapper = new ImageWrapper(image);
        return imageWrapper.getImage(false, true);
    }

    

    BufferedImage bufferedImage;
    int originalHeight, originalWidth;

    // x, y indexed
    boolean[][] blackPixelArray;
    boolean[][] colouredPixelArray;

    private ImageWrapper(BufferedImage b) {
        this.bufferedImage = b;

        originalHeight = bufferedImage.getHeight();
        originalWidth = bufferedImage.getWidth();

        blackPixelArray = new boolean[originalWidth][originalHeight];
        colouredPixelArray = new boolean[originalWidth][originalHeight];

        for (int currXOffset = 0; currXOffset  < originalWidth; currXOffset++) {
            for (int currYOffset = 0; currYOffset< originalHeight; currYOffset++) {
                blackPixelArray[currXOffset][currYOffset] = isABlackPixel(bufferedImage, currXOffset, currYOffset, 0.1);
                colouredPixelArray[currXOffset][currYOffset] = isAColouredPixel(bufferedImage, currXOffset, currYOffset, 0.1);
            }
        }
    }

    ImageWrapper(String path) throws IOException {

        bufferedImage = ImageIO.read(new File(path));

        originalHeight = bufferedImage.getHeight();
        originalWidth = bufferedImage.getWidth();

        blackPixelArray = new boolean[originalWidth][originalHeight];
        colouredPixelArray = new boolean[originalWidth][originalHeight];

        for (int currXOffset = 0; currXOffset  < originalWidth; currXOffset++) {
            for (int currYOffset = 0; currYOffset< originalHeight; currYOffset++) {
                blackPixelArray[currXOffset][currYOffset] = isABlackPixel(bufferedImage, currXOffset, currYOffset, 0.5) && !isAColouredPixel(bufferedImage, currXOffset, currYOffset, 0.5);
                colouredPixelArray[currXOffset][currYOffset] = isAColouredPixel(bufferedImage, currXOffset, currYOffset, 0.5);
            }
        }
    }

    private BufferedImage getImage(boolean justBlack, boolean justColour) {
        BufferedImage outputBufferedImage  = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 1+bufferedImage.getType());
        for (int x = 0; x < originalWidth; x++) {
            for (int y = 0; y < originalHeight; y++) {
                if (justColour && !justBlack) {
                    if (colouredPixelArray[x][y]) {
                        outputBufferedImage.setRGB(x, y, (new Color(255, 0, 0)).getRGB());
                    } else {
                        outputBufferedImage.setRGB(x, y, (new Color(255, 255, 255)).getRGB());
                    }
                } else if (justBlack && !justColour) {
                    if (blackPixelArray[x][y]){
                        outputBufferedImage.setRGB(x, y, (new Color(0,0,0)).getRGB());
                    } else {
                        outputBufferedImage.setRGB(x, y, (new Color(255, 255, 255)).getRGB());
                    }
                } else {
                    if (colouredPixelArray[x][y]) {
                        outputBufferedImage.setRGB(x, y, (new Color(255, 0, 0)).getRGB());
                    }
                    else if (blackPixelArray[x][y]){
                        outputBufferedImage.setRGB(x, y, (new Color(0,0,0)).getRGB());
                    } else {
                        outputBufferedImage.setRGB(x, y, (new Color(255, 255, 255)).getRGB());
                    }
                }
            }
        }
        return outputBufferedImage;
    }

    private static void save(BufferedImage toSave, String outputpath) throws IOException {
        File outputFile = new File(outputpath);
        ImageIO.write(toSave, "png", outputFile);
    }

    private static boolean isABlackPixel(BufferedImage bufferedImage, int x, int y, double threshold) {

        Color c = new Color(bufferedImage.getRGB(x, y));

        int sumlengthsquared = (int) (Math.pow(c.getRed(), 2) + Math.pow(c.getGreen(), 2) + Math.pow(c.getBlue(), 2));

        // (255 ^2) * 3  is the max value of sumlengthsquared (white + white)
        if (sumlengthsquared > threshold * Math.pow(255 , 2) * 3) {

            // It was deemed to be a white pixel
            return false;
        } else if (sumlengthsquared < threshold * Math.pow(255, 2) * 3){
            // Deemed to be a black pixel according to the the threshold
            return true;
        } else {
            // Lies between the black and white threshold
            // TODO: (Reconsider?) Default to a white pixel (as the background is assumed white)
            return false;
        }
    }

    private static boolean isAColouredPixel(BufferedImage bufferedImage, int x, int y, double threshold) {
        Color c = new Color(bufferedImage.getRGB(x, y));
        double stdev = Stats.getStdDev(new double[]{c.getRed(), c.getGreen(), c.getBlue()});
        return (stdev > threshold);
    }

    List<LineSegment>  xScans() {

        List<LineSegment> segments = new LinkedList<>();

        for (int y = 0; y < originalHeight; y++) {

            boolean seenBlackPixel = false;

            Integer startx = null, starty = null, endx = null, endy = null;

            for (int x = 0; x < originalWidth; x++) {
                if (!seenBlackPixel && blackPixelArray[x][y]) {
                    seenBlackPixel = true;
                    startx = x;
                    starty = y;
                    endx = x;
                    endy = y;
                } else if (seenBlackPixel && blackPixelArray[x][y]) {
                    assert (y == endy);
                    if (x > endx) {
                        endx = x;
                    }
                }
            }
            if (seenBlackPixel) {
                segments.add(new LineSegment(new Coordinate(startx, starty), new Coordinate(endx, endy)));
            }
        }
        return segments;
    }

    private BufferedImage getWithBoundingBox() {

        BufferedImage underlyingImage = getImage(false, false);
        TextBoundingBox boundingBoxCoordinates = boundingBox();
        return overlayBB(underlyingImage, boundingBoxCoordinates);

    }

    private TextBoundingBox boundingBox() {




        Coordinate possA = null, possB = null, possC = null, possD = null;


        List<LineSegment> xScansList = xScans();


        for (LineSegment lineSeg: xScansList) {

            Coordinate startPoint = lineSeg.start;
            Coordinate finishPoint = lineSeg.finish;


            if (possA == null || startPoint.beatsA(possA)) {
                possA = startPoint.getCopy();
            }

            if (possB == null || finishPoint.beatsB(possB, bufferedImage)) {
                possB = finishPoint.getCopy();
            }

            if (possC == null || finishPoint.beatsC(possC, bufferedImage)) {
                possC = finishPoint.getCopy();
            }

            if (possD == null || startPoint.beatsD(possD, bufferedImage)) {
                possD = startPoint.getCopy();
            }
        }

        TextBoundingBox tbb = new TextBoundingBox(possA, possB, possC, possD);
        return tbb;



        /*

        // TODO: modify logic to incorporate minimum line segment length
        // So instead of firstSegment use first segment which has the minimum line length, similarly instead of lastSegment...

        int threshold = 300;

        List<LineSegment> xScansList = xScans();

        Coordinate possA = null, possB = null, possC = null, possD = null;


        LineSegment firstSegment = xScansList.get(0);

        int index = 0;
        while(firstSegment.xprojlength() < threshold && index < xScans().size()) {
            index++;
            firstSegment = xScansList.get(index);
        }

        LineSegment lastSegment = xScansList.get(xScansList.size() - 1);

        index = xScansList.size() - 1;
        while(lastSegment.xprojlength() < threshold && index >= 0) {
            index--;
            lastSegment = xScansList.get(index);
        }

        possA = firstSegment.start;
        possB = firstSegment.finish;
        possC = lastSegment.finish;
        possD = lastSegment.start;


        */
    }

    private static BufferedImage overlayBB(BufferedImage underlyingImage, TextBoundingBox boundingBoxCoordinates) {

        Graphics2D bufferedGraphics2D = underlyingImage.createGraphics();
        bufferedGraphics2D.setPaint(Color.GREEN);
        int[] xpoints = new int[4];
        int[] ypoints = new int[4];

        xpoints[0] = boundingBoxCoordinates.coordA.x;
        ypoints[0] = boundingBoxCoordinates.coordA.y;

        xpoints[1] = boundingBoxCoordinates.coordB.x;
        ypoints[1] = boundingBoxCoordinates.coordB.y;

        xpoints[2] = boundingBoxCoordinates.coordC.x;
        ypoints[2] = boundingBoxCoordinates.coordC.y;

        xpoints[3] = boundingBoxCoordinates.coordD.x;
        ypoints[3] = boundingBoxCoordinates.coordD.y;

        Polygon boundingBoxGraphicsPolygon = new Polygon(xpoints, ypoints, 4);

        bufferedGraphics2D.draw(boundingBoxGraphicsPolygon);
        bufferedGraphics2D.drawImage(underlyingImage, 0,0, null);
        bufferedGraphics2D.dispose();

        return underlyingImage;
    }


    /*
       Input
              bbscan -- boundingtests box of scan
              bborig -- boundingtests box of original
        Output
              coordinate (dx, dy) such that if (x, y) is a point in the scan then (x + dx, y + dy) is the corresponding position in the original (before scaling).
     */
    private static Coordinate findTranslation(TextBoundingBox bbscan, TextBoundingBox bborig) {
        int dx = (bborig.coordA.x - bbscan.coordA.x);
        int dy = (bborig.coordA.y - bbscan.coordA.y);
        return new Coordinate(dx, dy);
    }

    /*
        Input
              bbscan -- boundingtests box of scan
              bborig -- boundingtests box of original
        Output
              coordinate (kx, ky)
              where
                kx -- stretch factor to be applied to the scan ..
                ky -- stretch factor to be applied to the scan ..

                such that, assuming that the stretch is applied with a centre point which is chosen as a point of alignment between the scan and the original,
                then, the stretched scan will be aligned with the original.
     */
    private static Pair<Double, Double> findScaling(TextBoundingBox bbscan, TextBoundingBox bborig) {



        Coordinate Bprime = bbscan.coordB;
        Coordinate Aprime = bbscan.coordA;

        Coordinate B = bborig.coordB;
        Coordinate A = bborig.coordA;

        Coordinate Dprime = bbscan.coordD;
        Coordinate D = bborig.coordD;


        double kx = Math.abs((double) (B.x - A.x) / (Bprime.x - Aprime.x));
        double ky = Math.abs((double) (D.y - A.y) / (Dprime.y - Aprime.y));

        return new Pair<>(kx, ky);


    }

    private static BufferedImage applyTranslate_new(int dx, int dy, BufferedImage toTranslate) {

        if (true) {
            throw new IllegalArgumentException("Implement this method");
        }


        AffineTransform at = new AffineTransform();
        at.translate(dx, dy);


        BufferedImage whiteImage = new BufferedImage(toTranslate.getWidth(), toTranslate.getHeight(), toTranslate.getType());
        for (int x = 0; x < whiteImage.getWidth(); x++) {
            for (int y = 0; y < whiteImage.getHeight(); y++) {
                whiteImage.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        Graphics2D g2d = whiteImage.createGraphics();
        g2d.drawImage(toTranslate, at, null);

        return null;
    }

    /*
       this will output an image where the pixel
            toTranslate(x, y)
       is found at position
            (x + dx, y + dy)
     */
    private static BufferedImage applyTranslate(int dx, int dy, BufferedImage toTranslate) {


        BufferedImage outputImage = new BufferedImage(toTranslate.getWidth(), toTranslate.getHeight(), toTranslate.getType());
        for (int x = 0; x < toTranslate.getWidth(); x++) {
            for (int y = 0; y < toTranslate.getHeight(); y++) {
                outputImage.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        for (int x = 0; x < toTranslate.getWidth(); x++) {
            for (int y = 0; y < toTranslate.getHeight(); y++) {

                int rgbval;
                if (x+dx >= 0 && x+dx < toTranslate.getWidth() && y+dy >= 0 && y+dy < toTranslate.getHeight()) {
                    rgbval = toTranslate.getRGB(x, y);
                    outputImage.setRGB(x + dx, y + dy, rgbval);
                }
            }
        }
        return outputImage;
    }

    private static BufferedImage applyScalingAboutCentrePoint(int x0, int y0, double kx, double ky, BufferedImage toScale) {

        BufferedImage translatedImage = new BufferedImage(toScale.getWidth(), toScale.getHeight(), toScale.getType());

        for (int x = 0; x < toScale.getWidth(); x++) {
            for (int y = 0; y < toScale.getHeight(); y++) {
                translatedImage.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        int xprime = -1, yprime = -1;
        for (int y = 0; y < toScale.getHeight(); y++) {

            System.out.println("xprime = " + xprime);
            System.out.println("yprime = " + yprime);
            System.out.println();

            for (int x = 0; x < toScale.getWidth(); x++) {

                double xprime_d = x0 + (x - x0) * kx;
                double yprime_d = y0 + (y - y0) * ky;
                xprime = (int) Math.floor(xprime_d);
                yprime = (int) Math.floor(yprime_d);

                int rgbval;
                if(xprime >= 0 && xprime < toScale.getWidth() && yprime >= 0 && yprime < toScale.getHeight()) {
                    rgbval = toScale.getRGB(x, y);
                    translatedImage.setRGB(xprime, yprime, rgbval);
                }
            }
        }
        return translatedImage;

    }

    private static BufferedImage correctAlignment(TextBoundingBox bbscan, TextBoundingBox bborig, BufferedImage inputImage) {

        Coordinate translation = findTranslation(bbscan, bborig);

        Pair<Double, Double> scales = ImageWrapper.findScaling(bbscan, bborig);

        BufferedImage translatedImage = applyTranslate(translation.x, translation.y,inputImage);

        double kx = scales.getKey();
        double ky = scales.getValue();
        BufferedImage scaledTranslatedImage = applyScalingAboutCentrePoint(bborig.coordA.x, bborig.coordA.y, kx, ky, translatedImage);

        return scaledTranslatedImage;
    }





}
