import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;



public class HillClimbing {
    
    /*
        Assume roughly same perspective, possible translation / rotation / scaling
     */

    public static void main(String[] args) throws IOException {

        String outputpath = "output_images/";

        // Diff map betweeen ORIGINAL and MODIFIED_ORIGINAL_CORRECT_PERSPECTIVE
        BufferedImage diff1 = generateDiffMap(getBufferedImageFromFile(PDFs.ORIGINAL), getBufferedImageFromFile(PDFs.MODIFIED_ORIGINAL_CORRECT_PERSPECTIVE));
        writeOutputImageToFile(diff1,outputpath + "diff_original_modified_correct_perspective.png");


        // Diff map between a blurred original and MODIFIED_ORIGINAL_CORRECT_PERSPECTIVE
        BufferedImage diff2 = generateDiffMap(getBufferedImageFromFile(PDFs.ORIGINAL_BLUR_SATURATED), getBufferedImageFromFile(PDFs.MODIFIED_ORIGINAL_CORRECT_PERSPECTIVE));
        writeOutputImageToFile(diff2,outputpath + "diff_blurred_original_modified_correct_perspective.png");

        // Diff map between a blurred original and scan thresholded + translated
        BufferedImage diff3 = generateDiffMap(getBufferedImageFromFile(PDFs.ORIGINAL_BLUR_SATURATED), getBufferedImageFromFile(PDFs.SCAN_THRESHOLD_TRANSLATED));
        writeOutputImageToFile(diff3,outputpath + "diff_blurred_original_scanned_thresholded_not_translated.png");

        // Try to align the ORIGINAL_PDF with the pure scan
        BufferedImage aligned = getAlignedScan(getBufferedImageFromFile(PDFs.ORIGINAL), getBufferedImageFromFile(PDFs.SCAN_PURE), 10);
        writeOutputImageToFile(aligned,outputpath + "aligned.png");

    }

    public static BufferedImage getBufferedImageFromFile(String path) throws IOException {
        return ImageIO.read(new File(path));
    }


    public static BufferedImage getAlignedScan(BufferedImage originalPDFBufferedImage, BufferedImage scannedVersionBufferedImage, int iterations) {

        double pixelsDeltaX = 0;
        double pixelsDeltaY = 0;

        // The actual scaling is kX^2, kY^2.
        double kX = 1;
        double kY = 1;

        // in radians
        double theta  = 0;
        double currentCost;

        double[] state_vector = {pixelsDeltaX, pixelsDeltaY, kX, kY, theta};

        int ITERATIONS = iterations;

        // Learning rates

        // 2, 0.001, 0.008 (around half a degree)
        double epsilon_for_translations = 10;
        double epsilon_for_scales = 0.01;
        double epsllon_for_theta = 0.01;

        double[] learning_rates = {epsilon_for_translations, epsilon_for_translations, epsilon_for_scales, epsilon_for_scales, epsllon_for_theta};

        // Find the initial cost (or just use + infinity)
        currentCost = totalCostOfState(originalPDFBufferedImage, scannedVersionBufferedImage, state_vector);

        double[] change_to_current_cost_by_modifying_parameter_positive = new double[5];
        double[] change_to_current_cost_by_modifying_parameter_negative  = new double[5];

        double startingCost = currentCost;

        // Random randomNumberGenerator = new Random();

        for(int timestep = 0; timestep < ITERATIONS; timestep++) {

            System.out.println("iteration = " +  timestep + ", current cost = " + currentCost);
            System.out.println("state_vector = " + Arrays.toString(state_vector));

            double new_cost;
            // 5 parameters in state vector
            for (int index = 0; index < 5; index++) {

                // Perform positive modification
                state_vector[index] += (+1) * learning_rates[index];

                // Compute new cost
                new_cost = totalCostOfState(originalPDFBufferedImage, scannedVersionBufferedImage, state_vector);
                change_to_current_cost_by_modifying_parameter_positive[index] = new_cost;

                // Rollback positive modification
                state_vector[index] -= learning_rates[index];

                // Perform negative modification
                state_vector[index] -= learning_rates[index];

                // Compute new cost
                new_cost = totalCostOfState(originalPDFBufferedImage, scannedVersionBufferedImage, state_vector);
                change_to_current_cost_by_modifying_parameter_negative[index] = new_cost;

                // Rollback negative modification
                state_vector[index] += learning_rates[index];

            }

            // Find the (parameter, sign) which produced the smallest cost
            // else return

            int parameter_index_to_modify = 0;
            boolean modify_positive = true;
            new_cost = currentCost;

            for (int i = 0; i < 5; i++) {
                if (change_to_current_cost_by_modifying_parameter_positive[i] < new_cost) {
                    parameter_index_to_modify = i;
                    new_cost = change_to_current_cost_by_modifying_parameter_positive[i];
                }
            }


            for (int i = 0; i < 5; i++) {
                if (change_to_current_cost_by_modifying_parameter_negative[i] < new_cost) {
                    parameter_index_to_modify = i;
                    new_cost = change_to_current_cost_by_modifying_parameter_negative[i];
                    modify_positive = false;
                }
            }

            // Perform the modification
            state_vector[parameter_index_to_modify] += (modify_positive ? +1 : -1) * learning_rates[parameter_index_to_modify];
            currentCost = new_cost;
        }

        System.out.println("reduction in cost = " + (currentCost - startingCost));
        System.out.println("final state = ");
        for (int x = 0; x < state_vector.length; x++) {
            System.out.println(state_vector[x]);
        }

        // Make an image of the same size as the input
        BufferedImage outputBufferedImage = new BufferedImage(originalPDFBufferedImage.getWidth(), originalPDFBufferedImage.getHeight(), originalPDFBufferedImage.getType());

        // Write out the translated file
        for (int x = 0; x < scannedVersionBufferedImage.getWidth(); x++) {
            for (int y = 0; y < scannedVersionBufferedImage.getHeight(); y++) {

                Pair<Integer, Integer> scan_coordinates = getScanCoordinatesGivenPDFCoordinates(state_vector[0], state_vector[1],state_vector[2], state_vector[3], state_vector[4], x, y);

                int scan_x = scan_coordinates.getKey();
                int scan_y = scan_coordinates.getValue();

                if (x==0 && y== 0) {
                    System.out.println("scan_x = " + scan_x);
                    System.out.println("scan_y = " + scan_y);
                }

                // Set the pdf coordinates in the output image (x, y) to the pixel value in the transformed scan coordinates
                if ((x < 0 || x >= outputBufferedImage.getWidth() || y < 0 || y >= outputBufferedImage.getHeight())) {
                    continue;
                } else {
                    createOutputImage(outputBufferedImage, x, y, getRGBWithBoundChecking(scannedVersionBufferedImage, scan_x, scan_y).getRGB());
                }
            }
        }

        return outputBufferedImage;
    }

    public static BufferedImage generateDiffMap(BufferedImage original, BufferedImage originalPlusOverlay) {
        // Assumes alignment

        BufferedImage outputBufferedImage  = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());;
        for (int x = 0; x < outputBufferedImage.getWidth(); x++) {
            for (int y = 0; y < outputBufferedImage.getHeight(); y++) {


                Color c_original = getRGBWithBoundChecking(original, x, y);
                Color c_scanned = getRGBWithBoundChecking(originalPlusOverlay, x, y);


                // To output: Original - Scanned
                // Color difference = subtract(c_original, c_scanned);

                // To output: Scanned - Original
                // Color difference = subtract(c_scanned, c_original);

                // To output: operation1(scanned, original) (see note below)
                /*
                        original    scan    diff map
                        W           W       W               (alignment)
                        W           B       B               (EITHER a mark in the original that isn't properly aligned OR a new marking that has been added in to the paper copy)
                        B           W       don't care (choose either W or B depending upon the input passed to operation one)
                                            (this represents lines that were present in the original but not present in the scan at this position -- this must be an alignment error?)
                                                    op1 -- does the dot product for everything other than black+black = white, so chooses B
                        B           B       W               (dark spots are aligned - this may not be the correct alignment though!)



                 */

                // Color difference = operation1(c_scanned, c_original, 0.02);

                // operation2(Scanned, original)
                    /*
                                                    [original][scanned]
                        Idea: blur original, choose black + white = white

                        pdf scan    diff
                        w   w       w
                        w   b       b
                        b   w       W
                        b   b       w

                        ... implemented by op2

                       BUT assume original pdf has been blurred ...

                     */
                Color difference = operation2(c_original, c_scanned, 0.1);
                // color_elementwise_multiply(scanned, original)
                // Color difference = color_elementwise_multiply(c_scanned, c_original);

                // Max?
                //Color difference  = maxColors(c_original, c_scanned);

                // Min?
                // Color difference = minColors(c_original, c_scanned);

                outputBufferedImage.setRGB(x, y, thresholdRGB(difference.getRGB(), 0.5));
            }
        }
        return outputBufferedImage;
    }



    public static void writeOutputImageToFile(BufferedImage outputBufferedImage, String path) throws IOException {
        // Write the output image to disk
        File outputfile = new File(path);
        ImageIO.write(outputBufferedImage, "png", outputfile);
    }




    private static void createOutputImage(BufferedImage outputBufferedImage, int x, int y, int rgb) {
        outputBufferedImage.setRGB(x, y, rgb);
    }




    static Color getRGBWithBoundChecking(BufferedImage image, int x, int y) {
        if (isOutOfBounds(image, x, y)) {
            return new Color(255, 255, 255);
        } else {
            int rgb_original = image.getRGB(x, y);
            return new Color(rgb_original);
        }
    }



    static boolean isOutOfBounds(BufferedImage image, int x, int y) {
        return (x < 0
            || x >= image.getWidth()
            || y < 0
            || y >= image.getHeight());
    }




    static Color subtract(Color c1, Color c2) {
        return new Color(Math.max(c1.getRed() - c2.getRed(), 0),
                Math.max(c1.getGreen() - c2.getGreen(), 0),
                Math.max(c1.getBlue() - c2.getBlue(), 0));
    }




    static Color maxColors(Color c1, Color c2) {
        return new Color(Math.max(c1.getRed(), c2.getRed()), Math.max(c1.getGreen(), c2.getGreen()), Math.max(c1.getBlue(), c2.getBlue()));
    }



    static Color minColors(Color c1, Color c2) {
        return new Color(Math.min(c1.getRed(), c2.getRed()), Math.min(c1.getGreen(), c2.getGreen()), Math.min(c1.getBlue(), c2.getBlue()));
    }



    static Color color_elementwise_multiply(Color c1, Color c2) {
        // normalised to the range (0, 255)
        return new Color(c1.getRed()* c2.getRed() /(255), c1.getGreen() * c2.getGreen() / (255), c1.getBlue() * c2.getBlue() / (255 ));
    }



    static double color_dotproduct(Color c1, Color c2) {
        return (c1.getRed()* c2.getRed()+ c1.getGreen() * c2.getGreen() + c1.getBlue() * c2.getBlue())/255.0;
    }





    /*
    (XOR)
            pdf         scan        diff
            W           W           W
            W           B           B
            B           W           B           (*)
            B           B           W

     */

    static Color operation1(Color c1, Color c2, double threshold) {
        // Length of the vector we'd get if we added up the rgb components, component-wise.
        int sumlengthsquared = (int) (Math.pow(c1.getRed() + c2.getRed(), 2) + Math.pow(c1.getGreen() + c2.getGreen(), 2) + Math.pow(c1.getBlue() + c2.getBlue(), 2));

        // (255 ^2) * 3  is the max value of sumlengthsquared (white + white)
        if (sumlengthsquared <= threshold * Math.pow(255 , 2) * 3) {
            // Then we have the black + black case, need to output white
            return new Color(255,255,255);
        } else {
            return color_elementwise_multiply(c1, c2);
        }
    }

    /*
    (allow blurring in pdf to `clean up' the remaining mis-alignment
            pdf         scan        diff
            W           W           W
            W           B           B
            B           W           W           (*)
            B           B           W

     */

    static Color operation2(Color c1, Color c2, double threshold) {

        int r1 = c1.getRed();
        int g1 = c1.getGreen();
        int b1 = c1.getBlue();

        int r2 = c2.getRed();
        int g2 = c2.getGreen();
        int b2 = c2.getBlue();

        int sum_red = r1+r2;
        int sum_blue = g1+g2;
        int sum_green = b1+b2;


        // from pdf
        int len_1 = r1*r1+g1*g1+b1*b1;

        // from scan
        int len_2 = r2*r2+g2*g2+b2*b2;


        // pdf pixel is white, scan pixel is black
        if(len_1 > (1.0-threshold)*255*255 && len_2 < threshold*255*255 ) {
            return new Color(0,0,0);
        } else {
            return new Color(255, 255, 255);
        }

    }

    static Color vectorColorSubtract(Color s, Color t) {
        return new Color(s.getRed()-t.getRed(),s.getGreen()-t.getGreen(),s.getBlue()-t.getBlue());
    }

    static double pixelCost(BufferedImage pdf, BufferedImage scan, int x, int y) {

        Color rgb_pdf = getRGBWithBoundChecking(pdf, x, y);
        Color rgb_scan = getRGBWithBoundChecking(scan, x, y);

        int d_red  = rgb_pdf.getRed() - rgb_scan.getRed();
        int d_green  = rgb_pdf.getGreen() - rgb_scan.getGreen();
        int d_blue  = rgb_pdf.getBlue() - rgb_scan.getBlue();

        // Scale into (0,1)
        double pixel_cost = (Math.pow(d_red, 2)+Math.pow(d_green, 2)+Math.pow(d_blue, 2))/(255*255);
        return pixel_cost;
    }



    //      COST METRICS

    static double cost_of_theta(double theta) {
        return Math.pow(Math.tan(theta/2), 2);
    }

    // Transformations
    static Pair<Integer, Integer> getScanCoordinatesGivenPDFCoordinates(double deltaX, double deltaY, double sqrtscaleX, double sqrtscaleY, double theta, int pdf_x, int pdf_y) {
        int scan_x_before_rotation = (int)Math.floor(((double)pdf_x-deltaX)/(sqrtscaleX*sqrtscaleX));
        int scan_y_before_rotation = (int)Math.floor(((double)pdf_y-deltaY)/(sqrtscaleY*sqrtscaleY));
        int scan_y = (int)Math.floor(scan_x_before_rotation * Math.sin(theta) + scan_y_before_rotation * Math.cos(theta));
        int scan_x = (int)Math.floor(scan_x_before_rotation * Math.cos(theta) - scan_y_before_rotation * Math.sin(theta));
        return new Pair<>(scan_x, scan_y);
    }

    // Compute the total cost that we want to minimise
    static double totalCostOfState(BufferedImage pdf, BufferedImage scan, double[] statevector) {
        return totalCost(pdf, scan, statevector[0], statevector[1], statevector[2], statevector[3], statevector[4]);
    }

    static double totalCost(BufferedImage pdf, BufferedImage scan, double deltaX, double deltaY, double sqrtscaleX, double sqrtscaleY, double theta) {

        double total_cost = 0.0;

        for(int x = 0; x < Math.max(pdf.getWidth(), scan.getWidth()); x++) {
            for (int y = 0; y < Math.max(pdf.getHeight(), scan.getHeight()); y++) {
                Pair<Integer, Integer> scan_coordinates = getScanCoordinatesGivenPDFCoordinates(deltaX, deltaY, sqrtscaleX, sqrtscaleY, theta, x, y);

                int scan_x = scan_coordinates.getKey();
                int scan_y = scan_coordinates.getValue();

                double alignment_cost_for_this_pixel = pixelCost(pdf, scan, scan_x, scan_y);
                double parameter_cost_for_scale_and_translation = 0.01 * Math.pow(deltaX, 2) + 0.01 * Math.pow(deltaY, 2) + Math.pow(sqrtscaleX -1, 2) + Math.pow(sqrtscaleY-1, 2) + cost_of_theta(theta);

                // Add on the cost for this pixel
                total_cost += alignment_cost_for_this_pixel + parameter_cost_for_scale_and_translation;
            }
        }

        return total_cost;
    }

    static int thresholdRGB(int rgb, double threshold) {
        return rgb;
        //return thresholdColor(new Color(rgb), 0.5).getRGB();
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
}
