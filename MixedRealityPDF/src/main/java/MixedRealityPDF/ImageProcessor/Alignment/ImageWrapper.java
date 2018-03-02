package MixedRealityPDF.ImageProcessor.Alignment;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.DBSCANClusterDetector;
import MixedRealityPDF.AnnotationProcessor.DBSCANClusterDetectorTest;
import MixedRealityPDF.ImageProcessor.ColourRemoval.ColorExtractor;
import MixedRealityPDF.ImageProcessor.Stats;
import javafx.util.Pair;

public class ImageWrapper {


  public static void main(String[] args) throws IOException {
    // testColourExtraction();
    // generateFinalReportTestData();
    generateNewTestData();
  }

  private static void testColourExtraction() throws IOException {
    String inputPath = "Data/FinalReportImages/modified.png";
    String outputPath = "Data/FinalReportImages/extracted-modified.png";
    BufferedImage colorExtracted = ColorExtractor.extractColorComponent(ImageIO.read(new File(inputPath)));
    save(colorExtracted, outputPath );

  }




  private static void generateFinalReportTestData() throws IOException {

    String outputDir = "Data/FinalReportImages/";

    BufferedImage originalRawInput = ImageIO.read(new File("Data/FinalReportImages/original.png"));
    BufferedImage modifiedRawInput = ImageIO.read(new File("Data/FinalReportImages/modified.png"));

    BufferedImage resizedModifiedRawInput = scaleToFirstArgument(originalRawInput, modifiedRawInput);

    BufferedImage blackComponent1 = getBlackComponent(modifiedRawInput);
    BufferedImage blackComponent2 = ColorExtractor.extractBlackComponent(modifiedRawInput);

    BufferedImage colourComponent1 = getColourComponent(modifiedRawInput);
    BufferedImage colourComponent2 = ColorExtractor.extractColorComponent(modifiedRawInput);

    ImageWrapper imageWrapperOriginal = new ImageWrapper(originalRawInput);
    ImageWrapper imageWrapperModified = new ImageWrapper(resizedModifiedRawInput);
    ImageWrapper imageWrapperModified2 = new ImageWrapper(resizedModifiedRawInput);

    TextBoundingBox tbbOriginal = imageWrapperOriginal.boundingBox();
    TextBoundingBox tbbModified = imageWrapperModified.boundingBox();

        /*
        BufferedImage bbOriginal = ImageWrapper.overlayBB(imageWrapperOriginal.bufferedImage, tbbOriginal);

        BufferedImage bbModified = ImageWrapper.overlayBB(imageWrapperModified.bufferedImage, tbbModified);


        save(bbOriginal, outputDir+"originalbb.png");
        save(bbModified, outputDir+"modifiedbb.png");
        */


    BufferedImage aligned = correctAlignment(tbbModified, tbbOriginal, imageWrapperModified2.bufferedImage);

    save(aligned, outputDir+"aligned.png");

    BufferedImage colourOfAligned = ColorExtractor.extractColorComponent(aligned);

    save(colourOfAligned, outputDir+"colour-of-aligned.png");

    DBSCANClusterDetectorTest.testClustering(outputDir+"colour-of-aligned.png", outputDir+"segments.png");

  }


  private static void generateNewTestData() throws IOException {
    String baseDirectory = "Data/new test data 1 Mar/";
    DBSCANClusterDetector dbscanClusterDetector = new DBSCANClusterDetector();
    for (int i = 1; i <= 10 ; i++) {
      String inputDirectory = baseDirectory+"scanned-"+String.format("%02d", i)+".png";

      System.out.println("inputDirectory = " + inputDirectory);

      BufferedImage originalBI = ImageIO.read(new File(inputDirectory));
      BufferedImage diffBI = ColorExtractor.extractColorComponent(originalBI);

      save(diffBI, baseDirectory + i + ".png");
      DBSCANClusterDetectorTest.testClustering(baseDirectory + i + ".png", baseDirectory + i + "--withclusters.png");


      int ctr = 0;

      for (AnnotationBoundingBox abb : dbscanClusterDetector.cluster(diffBI)) {

        int x = abb.getTopLeft().getX();
        int y = abb.getTopLeft().getY();
        int w = abb.getTopRight().getX() - abb.getTopLeft().getX();
        int h = abb.getBottomLeft().getY() - abb.getTopLeft().getY();

        BufferedImage annotationBI = ColorExtractor.extractColorComponent(diffBI.getSubimage(x, y, w, h)) ;

        save(annotationBI, baseDirectory + i + "--annotation--" + ctr + ".png");
        ctr++;

      }


    }
  }

  private static void generateTestData() throws IOException {

    String s4 = "Data/TestData -- DifferenceOutputForDecisionTrees/resized_AnnotatedScan4.png";
    String s6 = "Data/TestData -- DifferenceOutputForDecisionTrees/resized_AnnotatedScan6.png";
    String s9 ="Data/TestData -- DifferenceOutputForDecisionTrees/resized_AnnotatedScan9.png";
    String s10 = "Data/TestData -- DifferenceOutputForDecisionTrees/resized_AnnotatedScan10.png";

    String s11 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan1.png";
    String s12 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan2.png";
    String s13 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan3.png";
    String s14 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan4.png";
    String s15 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan5.png";
    String s16 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan6.png";
    String s17 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan7.png";
    String s18 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan10.png";
    String s19 = "Data/TestData -- DifferenceOutputForDecisionTrees/Test 1 heavy annotations.pdf.png";
    String s20 = "Data/TestData -- DifferenceOutputForDecisionTrees/Test 2 heavy annotations.pdf.png";
    String s21 = "Data/TestData -- DifferenceOutputForDecisionTrees/Test 3 heavy annotations.pdf.png";
    String s22 = "Data/TestData -- DifferenceOutputForDecisionTrees/Test 4 heavy annotations.pdf.png";
    String s23 = "Data/TestData -- DifferenceOutputForDecisionTrees/Test 4 light annotations.pdf.png";
    String s24 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan8.png";
    String s25 = "Data/TestData -- DifferenceOutputForDecisionTrees/AnnotatedScan9.png";
    String s26 = "Data/TestData -- DifferenceOutputForDecisionTrees/newdata1.png";
    String s27 = "Data/TestData -- DifferenceOutputForDecisionTrees/newdata2.png";

    String o4 ="Data/TestData -- DifferenceOutputForDecisionTrees/resized_TestDoc4.png";
    String o6 ="Data/TestData -- DifferenceOutputForDecisionTrees/resized_TestDoc6.png";
    String o9 ="Data/TestData -- DifferenceOutputForDecisionTrees/resized_TestDoc9.png";
    String o10 ="Data/TestData -- DifferenceOutputForDecisionTrees/resized_TestDoc10.png";

    String directoryPath = "Data/TestData -- DifferenceOutputForDecisionTrees/";

    String[] originalDocumentPaths = new String[]{o4, o6, o9, o10};
    String[] modifiedDocumentPaths = new String[]{s4, s6, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20, s21, s22, s23, s24, s25, s26, s27};

    DBSCANClusterDetector dbscanClusterDetector = new DBSCANClusterDetector();

    for (int i = 0; i < modifiedDocumentPaths.length ; i++) {

      System.out.println("i = " + i);


      if (i < originalDocumentPaths.length) {

        String originalDocPath = originalDocumentPaths[i];
        String modifiedDocPath = modifiedDocumentPaths[i];


        BufferedImage originalBI = ImageIO.read(new File(originalDocPath));
        BufferedImage modifiedBI = ImageIO.read(new File(modifiedDocPath));


        BufferedImage alignedDiffNoColourMod = align(originalBI, modifiedBI);
        BufferedImage alignedDiff = ColorExtractor.extractColorComponent(alignedDiffNoColourMod);


        save(alignedDiff, directoryPath + i + ".png");
        DBSCANClusterDetectorTest.testClustering(directoryPath + i + ".png", directoryPath + i + "--withclusters.png");

        int ctr = 0;

        for (AnnotationBoundingBox abb : dbscanClusterDetector.cluster(alignedDiff)) {


          int x = abb.getTopLeft().getX();
          int y = abb.getTopLeft().getY();
          int w = abb.getTopRight().getX() - abb.getTopLeft().getX();
          int h = abb.getBottomLeft().getY() - abb.getTopLeft().getY();

          BufferedImage annotationBI = alignedDiffNoColourMod.getSubimage(x, y, w, h);

          save(annotationBI, directoryPath + i + "--annotation--" + ctr + ".png");
          ctr++;

        }

      } else {

        String modifiedDocPath = modifiedDocumentPaths[i];

        BufferedImage originalBI = ImageIO.read(new File(modifiedDocPath));
        BufferedImage diffBI = ColorExtractor.extractColorComponent(originalBI);


        save(diffBI, directoryPath + i + ".png");
        DBSCANClusterDetectorTest.testClustering(directoryPath + i + ".png", directoryPath + i + "--withclusters.png");


        int ctr = 0;

        for (AnnotationBoundingBox abb : dbscanClusterDetector.cluster(diffBI)) {

          int x = abb.getTopLeft().getX();
          int y = abb.getTopLeft().getY();
          int w = abb.getTopRight().getX() - abb.getTopLeft().getX();
          int h = abb.getBottomLeft().getY() - abb.getTopLeft().getY();

          BufferedImage annotationBI = originalBI.getSubimage(x, y, w, h);

          save(annotationBI, directoryPath + i + "--annotation--" + ctr + ".png");
          ctr++;

        }

      }
    }
  }

  private static void testAlignment() throws IOException {
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


  /**
   *
   * @param original
   * @param modified
   * @return A BufferedImage instance which is of the same dimensions as the modified image, with the image pixels
   * moved such that the bounding box of text (i.e. non-colour pixels) is aligned with that of the BufferedImage `original'.
   * No colour modifications are made to the aligned image.
   */
  public static BufferedImage align(BufferedImage original, BufferedImage modified) {

    ImageWrapper originalImageWrapper = new ImageWrapper(original);
    ImageWrapper modifiedImageWrapper = new ImageWrapper(modified);

    // Need to extract just the black component to find the bounding box of the black text
    BufferedImage blackComponentOfModified = modifiedImageWrapper.getImage(true, false);

    // Perform alignment based on the original and the black component of the modified image
    TextBoundingBox originalBB = originalImageWrapper.boundingBox();
    TextBoundingBox scanBB = (new ImageWrapper(blackComponentOfModified)).boundingBox();

    // Apply the transformation to the image without any colour modifications.
    BufferedImage correctlyAlignedImage = correctAlignment(scanBB, originalBB, modifiedImageWrapper.bufferedImage);

    return correctlyAlignedImage;
  }


  /**
   *
   * @param image
   * @return A BufferedImage instance of the same dimensions as `image', but discarding (i.e. setting to white)
   * any pixels that are not either white or black.
   */
  public static BufferedImage getBlackComponent(BufferedImage image) {
    return ColorExtractor.extractBlackComponent(image);
    // ImageWrapper imageWrapper = new ImageWrapper(image);
    // return imageWrapper.getImage(true, false);
  }

  /**
   *
   * @param image
   * @return A BufferedImage instance of the same dimensions as `image', but discarding (i.e. setting to white) any
   * pixels that are not considered `black'.
   */
  public static BufferedImage getColourComponent(BufferedImage image) {
    return ColorExtractor.extractColorComponent(image);
    // ImageWrapper imageWrapper = new ImageWrapper(image);
    // return imageWrapper.getImage(false, true);
  }

  BufferedImage bufferedImage;
  int originalHeight, originalWidth;

  // Array representations of the pixels in the input image with which an ImageWrapper
  // instance is constructed. An entry is `true' iff that pixel (x, y) is considered a `black' pixel or a `coloured' pixel,
  // respectively.
  boolean[][] blackPixelArray;
  boolean[][] colouredPixelArray;


  /**
   * @param bufferedImage
   * Take a BufferedImage instance and initialise the blackPixelArray and colouredPixelArray fields.
   */
  private ImageWrapper(BufferedImage bufferedImage) {

    this.bufferedImage = bufferedImage;

    originalHeight = this.bufferedImage.getHeight();
    originalWidth = this.bufferedImage.getWidth();

    blackPixelArray = new boolean[originalWidth][originalHeight];
    colouredPixelArray = new boolean[originalWidth][originalHeight];

    for (int currXOffset = 0; currXOffset  < originalWidth; currXOffset++) {
      for (int currYOffset = 0; currYOffset< originalHeight; currYOffset++) {
        blackPixelArray[currXOffset][currYOffset] = isABlackPixel(this.bufferedImage, currXOffset, currYOffset, 0.2);
        colouredPixelArray[currXOffset][currYOffset] = isAColouredPixel(this.bufferedImage, currXOffset, currYOffset, 0.1);
      }
    }
  }

  /**
   *
   * @param justBlack
   * @param justColour
   * @return If justBlack == false && justColour == false then return the original image.
   * If justBlack == true  && justColour == false then return the black component of the image.
   * If justBlack == false && justColour == true then return the coloured component of the image.
   * If justBlack == true && justColour == true then return the original image.
   */
  private BufferedImage getImage(boolean justBlack, boolean justColour) {
    BufferedImage outputBufferedImage  = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 1+bufferedImage.getType());
    for (int x = 0; x < originalWidth; x++) {
      for (int y = 0; y < originalHeight; y++) {
        if (justColour && !justBlack) {
          if (colouredPixelArray[x][y]) {
            outputBufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y));
          } else {
            outputBufferedImage.setRGB(x, y, Color.WHITE.getRGB());
          }
        } else if (justBlack && !justColour) {
          if (blackPixelArray[x][y]){
            outputBufferedImage.setRGB(x, y, Color.BLACK.getRGB());
          } else {
            outputBufferedImage.setRGB(x, y, Color.WHITE.getRGB());
          }
        } else {
          if (colouredPixelArray[x][y]) {
            outputBufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y));
          }
          else if (blackPixelArray[x][y]){
            outputBufferedImage.setRGB(x, y, Color.BLACK.getRGB());
          } else {
            outputBufferedImage.setRGB(x, y, Color.WHITE.getRGB());
          }
        }
      }
    }
    return outputBufferedImage;
  }


  /**
   *
   * @param bufferedImage
   * @param x
   * @param y
   * @param threshold
   * @return true iff the pixel at (x, y) is considered to be a black pixel. A higher threshold value makes the test
   * less strict -- so as threshold is made larger, more grey pixels get accepted as black.
   */
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
      // Default to a white pixel (as the background is assumed white)
      return false;
    }
  }

  /**
   *
   * @param bufferedImage
   * @param x
   * @param y
   * @param threshold
   * @return true iff the pixel at (x, y) in bufferedImage is considered to be a coloured pixel. A larger threshold makes the test
   * more strict -- a larger threshold requires the pixel to be more colourful (i.e. have a larger standard deviation among its
   * RGB components) to be considered as a coloured pixel.
   */
  private static boolean isAColouredPixel(BufferedImage bufferedImage, int x, int y, double threshold) {
    Color c = new Color(bufferedImage.getRGB(x, y));
    double stdev = Stats.getStdDev(new double[]{c.getRed(), c.getGreen(), c.getBlue()})/255;
    return (stdev > threshold);
  }


  /**
   * Perform horizontal scans across the image.
   * Keep track of the start and end positions at which each scan line intersects the text in the image (assumed black)
   * @return A list of Line Segments corresponding to where the horizontal scan lines intersected the text of the image.
   */
  private List<LineSegment>  xScans() {

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


  /**
   *
   * @return A BufferedImage instance with the bounding box of text overlaid as a rectangle.
   */
  private BufferedImage getWithBoundingBox() {

    BufferedImage underlyingImage = getImage(false, false);
    TextBoundingBox boundingBoxCoordinates = boundingBox();
    return overlayBB(underlyingImage, boundingBoxCoordinates);

  }


  /**
   *
   * @return A TextBoundingBox instance containing the parameters of the bounding box of the text in the image.
   */
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


  }


  /**
   *
   * @param underlyingImage
   * @param boundingBoxCoordinates
   * @return A BufferedImage instance with the provided bounding box drawn on the image.
   */
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

  /**
   *
   * @param bbscan
   * @param bborig
   * @return A coordinate (dx, dy) such that if (x, y) is a point in the scan then (x + dx, y + dy) is the corresponding position in the original (before scaling).
   */
  private static Coordinate findTranslation(TextBoundingBox bbscan, TextBoundingBox bborig) {
    int dx = (bborig.coordA.x - bbscan.coordA.x);
    int dy = (bborig.coordA.y - bbscan.coordA.y);
    return new Coordinate(dx, dy);
  }



  /**
   *
   * @param bbscan
   * @param bborig
   * @return A Pair of Double values (kx, ky) where kx and ky are the stretch factors to be applied to the image of the modified document,
   * taking the centre point to be the top left corner of the text bounding box of the original image, to bring the bounding boxes into alignment.
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


  /**
   *
   * @param dx
   * @param dy
   * @param input
   * @return Shift every pixel in `input' from (x, y) to (x + dx, y + dy).
   */
  private static BufferedImage translate(int dx, int dy, BufferedImage input) {
    AffineTransform at = new AffineTransform();
    at.translate(dx, dy);

    BufferedImage outputImage = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
    Graphics2D graphics2D = outputImage.createGraphics();
    graphics2D.setTransform(at);

    graphics2D.drawImage(input, 0, 0, null);
    graphics2D.dispose();
    return outputImage;
  }


  /**
   *
   * @param x0
   * @param y0
   * @param kx
   * @param ky
   * @param inputImage
   * @return Scale the `inputImage' by kx, ky in the x- and y- directions respectively, taking (x0, y0) as the centre point.
   */
  private static BufferedImage scaleAboutCentre(int x0, int y0, double kx, double ky, BufferedImage inputImage) {
    AffineTransform at = new AffineTransform();
    at.translate(-x0, -y0);
    at.scale(kx, ky);
    at.translate(x0, y0);

    BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), inputImage.getType());
    Graphics2D graphics2D = outputImage.createGraphics();
    graphics2D.setTransform(at);

    graphics2D.drawImage(inputImage, 0, 0, null);
    graphics2D.dispose();

    return outputImage;
  }


  /**
   *
   * @param bbscan
   * @param bborig
   * @param inputImage
   * @return Return an image of the same dimensions as inputImage but with the pixels moved around such that
   * the bounding box of the text now has coordinates given by `bborig'.
   */
  private static BufferedImage correctAlignment(TextBoundingBox bbscan, TextBoundingBox bborig, BufferedImage inputImage) {
    Coordinate translation = findTranslation(bbscan, bborig);
    Pair<Double, Double> scales = ImageWrapper.findScaling(bbscan, bborig);
    BufferedImage translatedImage = translate(translation.x, translation.y, inputImage);

    double kx = scales.getKey();
    double ky = scales.getValue();

    BufferedImage scaledTranslatedImage = scaleAboutCentre(bborig.coordA.x, bborig.coordA.y, kx, ky, translatedImage);

    return scaledTranslatedImage;

  }

  private static void save(BufferedImage toSave, String outputpath) throws IOException {
    File outputFile = new File(outputpath);
    ImageIO.write(toSave, "png", outputFile);
  }


  /**
   *
   * @param original
   * @param modified
   * @return A BufferedImage instance with the same content as `modified' but scaled such that the dimensions of the image
   * are the same as that of `original'.
   */
  private static BufferedImage scaleToFirstArgument(BufferedImage original, BufferedImage modified) {

    double kx = original.getWidth()/modified.getWidth();
    double ky = original.getHeight()/modified.getHeight();



    AffineTransform at = new AffineTransform();
    at.scale(kx, ky);
    BufferedImage scaledModifiedImage = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

    for (int x = 0; x < scaledModifiedImage.getWidth(); x++) {
      for (int y = 0; y < scaledModifiedImage.getHeight(); y++) {
        scaledModifiedImage.setRGB(x, y, Color.WHITE.getRGB());
      }
    }



    Graphics2D graphics2D = scaledModifiedImage.createGraphics();
    graphics2D.setTransform(at);
    graphics2D.drawImage(modified, 0, 0, null);
    graphics2D.dispose();

    return scaledModifiedImage;
  }


}