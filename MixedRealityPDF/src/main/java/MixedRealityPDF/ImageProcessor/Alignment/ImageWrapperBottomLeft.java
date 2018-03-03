package MixedRealityPDF.ImageProcessor.Alignment;

import MixedRealityPDF.ImageProcessor.ColourRemoval.ColorExtractor;
import MixedRealityPDF.ImageProcessor.IAlignment;
import MixedRealityPDF.ImageProcessor.Stats;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ImageWrapperBottomLeft implements IAlignment {







  public ImageWrapperBottomLeft(){}

  /**
   *
   * @param original
   * @param modified
   * @return A BufferedImage instance which is of the same dimensions as the modified image, with the image pixels
   * moved such that the bounding box of text (i.e. non-colour pixels) is aligned with that of the BufferedImage `original'.
   * No colour modifications are made to the aligned image.
   */
  @Override
  public BufferedImage align(BufferedImage original, BufferedImage modified) {

    ImageWrapperBottomLeft originalImageWrapper = new ImageWrapperBottomLeft(original);
    ImageWrapperBottomLeft modifiedImageWrapper = new ImageWrapperBottomLeft(modified);
    // Need to extract just the black component to find the bounding box of the black text
    BufferedImage blackComponentOfModified = modifiedImageWrapper.getImage(true, false);

    // Perform alignment based on the original and the black component of the modified image
    TextBoundingBox originalBB = originalImageWrapper.boundingBox();
    TextBoundingBox scanBB = (new ImageWrapperBottomLeft(blackComponentOfModified)).boundingBox();

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
  private ImageWrapperBottomLeft(BufferedImage bufferedImage) {

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
    int dx = (bborig.coordD.x - bbscan.coordD.x);
    int dy = (bborig.coordD.y - bbscan.coordD.y);
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
    Pair<Double, Double> scales = ImageWrapperBottomLeft.findScaling(bbscan, bborig);
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