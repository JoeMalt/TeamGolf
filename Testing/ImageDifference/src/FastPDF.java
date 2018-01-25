
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import javafx.util.Pair;

public class FastPDF {


    // Open up a PDF, find the height, width, downsample, store in a 2D array of a new size
    // Threshold the image, so store booleans rather than true integer values
    // true == black, white == false


    public static final double THRESHOLD = 0.5;
    int originalHeight, originalWidth;
    int height, width;
    int downsamplingFactor;

    boolean[][] pixelvalue;

    BufferedImage originalImage;


    public static void main(String[] args) throws IOException {

        String outputPath = "output_images/rotated_downsampled1.png";

        BufferedImage inputImage = ImageIO.read(new File(PDFs.ORIGINAL_ROTATED_10_DEGREES));
        FastPDF pdf = new FastPDF(inputImage, 3);
        pdf.saveToOutputFile(outputPath);
        int[] results = pdf.performScan(0.0, 10);
        System.out.println(Arrays.toString(results));

        double minTheta = -0.1;
        double maxTheta = 0.1;
        int numSteps = 1000;
        int verticalSpacing = 1;

        //double[] results_f_theta = pdf.tryThetaRange(-20, 20, 100, 1);
        BufferedImage withScanLines = pdf.superimposeScanLinesOnCurrentDownsampledImage(minTheta, maxTheta, numSteps, verticalSpacing);
        //System.out.println("results_f_theta = " + Arrays.toString(results_f_theta));

        HillClimbing.writeOutputImageToFile(withScanLines, "output_images/rotated_downsampled1_withscanlines.png");
    }

    // Downsample each dimension by a factor downsamplingFactor
    // so there will be approx dsF^2 pixels in the original for every pixel in the downsampled version

    FastPDF(BufferedImage bufferedImage, int downsamplingFactor) {


        originalHeight = bufferedImage.getHeight();
        originalWidth = bufferedImage.getWidth();
        originalImage = bufferedImage;
        this.downsamplingFactor = downsamplingFactor;

        height = 1+Math.floorDiv(originalHeight, downsamplingFactor);
        width = 1+Math.floorDiv(originalWidth, downsamplingFactor);

        pixelvalue = new boolean[width][height];

        for (int currXOffset = 0; currXOffset * downsamplingFactor < originalWidth; currXOffset++) {
            for (int currYOffset = 0; currYOffset * downsamplingFactor < originalHeight; currYOffset++) {
                pixelvalue[currXOffset][currYOffset] =
                        isABlackPixel(bufferedImage,
                                currXOffset*downsamplingFactor, currYOffset*downsamplingFactor,
                                THRESHOLD);
            }
        }
    }


    public double[] tryThetaRange(double minTheta, double maxTheta, int numSteps, int verticalSpacing) {


        double stepSize = (maxTheta - minTheta)/numSteps;
        double currentTheta = minTheta;
        double var_i;

        double[] results = new double[numSteps];


        for (int i = 0; i < numSteps; i++) {
            currentTheta = minTheta + stepSize * i;
            var_i = computeVariance(performScan(currentTheta, verticalSpacing));
            results[i] = var_i;
        }
        return results;
    }


    public double computeVariance(int[] readings) {
        double sum = 0.0;
        for (int i = 0; i < readings.length; i++) {
            sum += readings[i];
        }
        double mean = sum / readings.length;

        double sum_squared_errors = 0.0;
        for (int i = 0; i < readings.length; i++) {
            sum_squared_errors += Math.pow(mean - readings[i], 2);
        }
        double variance = sum_squared_errors/readings.length;
        return variance;
    }

    public int[] performScan(double theta, int verticalSpacing) {

        int[] results = new int[Math.floorDiv(height, verticalSpacing) + 1];

        int index = 0;

        for (int yStartPos = 0; yStartPos < height; yStartPos += verticalSpacing) {


            if (yStartPos >= height) {
                break;
            }


            int offsetAlongLine = 0;
            int score = 0;

            while(true) {

                Pair<Integer, Integer> pdfCoordinates = pdfCoordinatesGivenStartAndOffset(yStartPos, offsetAlongLine, theta);
                int pdf_x = pdfCoordinates.getKey();
                int pdf_y = pdfCoordinates.getValue();

                // If y < 0 or x > width, done for this scan line.
                if (pdf_x >= width || pdf_y < 0 || pdf_x < 0 || pdf_y >= height) {
                    break;
                }

                score += pixelvalue[pdf_x][pdf_y] ? 1 : 0;

                offsetAlongLine++;
            }
            results[index++] = score;
        }
        return results;
    }



    public BufferedImage superimposeScanLinesOnCurrentDownsampledImage(double minTheta, double maxTheta, int numSteps, int verticalSpacing) {



        double[] variance_info = tryThetaRange(minTheta, maxTheta, numSteps, verticalSpacing);

        assert (numSteps == variance_info.length);

        double maximum_variance = variance_info[0];

        int single_best_i = 0;
        double single_best_angle;

        for (int i = 0; i < variance_info.length; i++) {

            if (variance_info[i] > maximum_variance) {
                maximum_variance = variance_info[i];
                single_best_i = i;
            }
        }


        double delta_theta = (maxTheta - minTheta) / numSteps;

        single_best_angle = minTheta + delta_theta * single_best_i;


        int YSTARTPOS = 100;
        int offsetAlongLine;

        double curr_theta;

        for (int n = 0; n < numSteps; n++) {
            System.out.println("n = " + n);

            curr_theta = minTheta + n * delta_theta;

            double curr_score = 255 * (variance_info[n]/maximum_variance);
            System.out.println("curr_score = " + curr_score);

            offsetAlongLine = 0;


            for (int dy = 0; dy < 10; dy++) {

                while (true) {


                    Pair<Integer, Integer> pdfCoordinates = pdfCoordinatesGivenStartAndOffset(YSTARTPOS + dy, offsetAlongLine, curr_theta);
                    int pdf_x = pdfCoordinates.getKey();
                    int pdf_y = pdfCoordinates.getValue();

                    // If y < 0 or x > width, done for this scan line.
                    if (pdf_x * downsamplingFactor >= originalWidth || pdf_y < 0 || pdf_x < 0 || pdf_y * downsamplingFactor >= originalHeight) {
                        break;
                    }


                    // Determine a color intensity between [0, 255] depending on how high the variance for that angle was.
                    int intensity = (int) Math.max(0, Math.min(255, curr_score));


                    originalImage.setRGB(pdf_x * downsamplingFactor, pdf_y * downsamplingFactor, (new Color(0, intensity, 0)).getRGB());

                    offsetAlongLine++;
                }
            }
        }


        System.out.println("results_f_theta = " + Arrays.toString(variance_info));
        System.out.println("single_best_angle = " + single_best_angle);
        System.out.println("single_best_i = " + single_best_i);


        return originalImage;

    }


    public BufferedImage getDownsampledImage() {
        BufferedImage tmpBuffImage = (new BufferedImage(width, height, originalImage.getType()));
        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tmpBuffImage.setRGB(
                        x, y,
                        ((pixelvalue[x][y]) ? (new Color(0,0,0)) : (new Color(255, 255, 255))).getRGB());
            }
        }
        return tmpBuffImage;
    }


    public void saveToOutputFile(String path) throws IOException {
        BufferedImage tmpBuffImage = getDownsampledImage();
        // Write the output image to disk
        File outputfile = new File(path);
        ImageIO.write(tmpBuffImage, "png", outputfile);
    }

    static boolean isABlackPixel(BufferedImage bufferedImage, int x, int y, double threshold) {
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

    static Color thresholdColor(Color c, double threshold) {
        // Length of the vector we'd get if we added up the rgb components, component-wise.
        int sumlengthsquared = (int) (Math.pow(c.getRed(), 2) + Math.pow(c.getGreen(), 2) + Math.pow(c.getBlue(), 2));

        // (255 ^2) * 3  is the max value of sumlengthsquared (white + white)
        if (sumlengthsquared > threshold * Math.pow(255 , 2) * 3) {
            return new Color(255, 255, 255);
        } else {
            return new Color(0,0,0);
        }
    }

    private Pair<Integer, Integer> pdfCoordinatesGivenStartAndOffset(int yLineStart, int offsetAlongLine, double thetaRadians) {
        int x = getClampedXPixelCoordinate(offsetAlongLine * Math.cos(thetaRadians)); // x = offset cos (theta)
        int y = getClampedYPixelCoordinate(yLineStart - offsetAlongLine * Math.sin(thetaRadians));
        Pair<Integer, Integer> coordinate = new Pair<>(x, y);
        return coordinate;
    }

    private int getClampedXPixelCoordinate(double toClamp) {
        return clampForDownsampledPDFCoordinates(0, width, (int) Math.floor(toClamp));
    }
    private int getClampedYPixelCoordinate(double toClamp) {
        return clampForDownsampledPDFCoordinates(0, height, (int) Math.floor(toClamp));
    }
    private int clampForDownsampledPDFCoordinates(int min, int max, int toClamp) {
        return Math.min(Math.max(min, toClamp), max);
    }


}
