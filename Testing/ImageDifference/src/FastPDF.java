
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.LinkedList;

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


        FastPDF input = new FastPDF(ImageIO.read(new File(PDFs.THRESHOLDED_SCAN)), 1);
        FastPDF original = new FastPDF(ImageIO.read(new File(PDFs.TEST_3_TEXT_ORIGINAL)), 1);
        int [] results_input = input.tryFixedThetaValue(0.0, 1);
        int [] original_input = original.tryFixedThetaValue(0.0, 1);
        System.out.println("results_input = " + results_input);
        System.out.println("original_input = " + original_input);



/*

        BufferedImage scaled = ImageIO.read(new File(PDFs.TEST_3_TEXT_HEAVY_ANNOTATION));


        FastPDF fpdf_scaled = new FastPDF(scaled, 3);

        fpdf_original.saveToOutputFile("output_images/original_fastpdf.png");
        fpdf_scaled.saveToOutputFile("output_images/tmp-2.png");

        int[] results_original = fpdf_original.tryFixedThetaValue(0.0, 1);
        int[] results_scanned = fpdf_scaled.tryFixedThetaValue(0.0, 1);



        double sum1 = 0;
        double sum2 = 0;
        for(int i = 0; i < results_original.length; i++) {
            sum1 += results_original[i];
        }
        for(int i = 0; i < results_scanned.length; i++) {
            sum2 += results_scanned[i];
        }

        // Divide out highest power of ten
        sum1 /= 10000;
        sum2 /= 10000;

        System.out.println("sum1 = " + sum1);
        System.out.println("sum2 = " + sum2);


        int[] results_original_rescaled = new int[results_original.length];
        int[] results_scanned_rescaled = new int[results_scanned.length];

        for (int i = 0; i < results_original.length; i++) {
            results_original_rescaled[i] = (int) (results_original[i]/sum1);
        }


        for (int i = 0; i < results_scanned.length; i++) {
            results_scanned_rescaled[i] = (int) (results_scanned[i]/sum2);

        }


        System.out.println("results_original AUC = " + Arrays.toString(results_original_rescaled));
        System.out.println("results_scanned AUC = " + Arrays.toString(results_scanned_rescaled));

*/         // int[] diffs1_original =


        /*

        String outputPath = "output_images/TMP.png";


        BufferedImage inputImage = ImageIO.read(new File(PDFs.TEST_3_TEXT_ORIGINAL));
        BufferedImage modifiedScanImage = ImageIO.read(new File(PDFs.TEST_3_TEXT_HEAVY_ANNOTATION));



        FastPDF pdf_original  = new FastPDF(inputImage, 3);
        FastPDF pdf_modified  = new FastPDF(modifiedScanImage, 3);


        pdf_original.saveToOutputFile(outputPath);
        int[] results = pdf_modified.performScan(0.0, 10);
        System.out.println(Arrays.toString(results));


        double minTheta = -0.1;
        double maxTheta = 0.1;
        int numSteps = 100;
        int verticalSpacing = 1;


        // ASSUMPTION HERE -- rotational difference, plus possible translation
        // double[] results_f_theta = pdf.tryThetaRange(-20, 20, 100, 1);
        // BufferedImage withScanLines = pdf.superimposeScanLinesOnCurrentDownsampledImage(minTheta, maxTheta, numSteps, verticalSpacing);
        // System.out.println("results_f_theta = " + Arrays.toString(results_f_theta));
        // HillClimbing.writeOutputImageToFile(withScanLines, "output_images/" + "TEST3_no_annotations"+"_rotated_downsampled_with_rotation_lines.png");



        // Assumption here -- no rotational difference, possible translation
        int[] results_original = pdf_original.tryFixedThetaValue(0.0, 1);
        int[] results_scanned = pdf_modified.tryFixedThetaValue(0.0, 1);


        System.out.println("results_original = " + Arrays.toString(results_original));
        System.out.println("results_scanned = " + Arrays.toString(results_scanned));



        // Purpose of taking the gradient -- cancel out the effect of images or other large blobs of stuff which makes the pixel count high so we can still distinguish the peaks.
        // see the example of test 3 heavy annotation around y = 300.
        int[] gradient_original = findStepsBetweenAdjacentElements(results_original);
        int[] gradient_of_annotated_scan = findStepsBetweenAdjacentElements(results_scanned);

        int[] rescaled_results_scanned = rescale(results_original, results_scanned, 200);

        // System.out.println("results_original = " + Arrays.toString(results_original));

        //System.out.println("rescaled_results_scanned = " + Arrays.toString(rescaled_results_scanned));


        // Now that the result has been rescaled, take the differences

        int[] gradient_of_rescaled_modified = findStepsBetweenAdjacentElements(rescaled_results_scanned);

        System.out.println("gradient_original = " + Arrays.toString(gradient_original));
        System.out.println("gradient_of_rescaled_modified = " + Arrays.toString(gradient_of_rescaled_modified));
        // System.out.println("differences_1 = " + Arrays.toString(gradient_original));

        // Want the windowsize of the covariance to be much less than that of the rescaling above...
        double[] covariance_of_rescaled_gradients = covarianceWithinWindow(gradient_original, gradient_of_rescaled_modified, 10);

        System.out.println("covariance_of_rescaled_gradients = " + Arrays.toString(covariance_of_rescaled_gradients));



        // double[] variance_of_covariance_of_rescaled_gradients = varianceWithinWindow(covariance_of_rescaled_gradients, 10);
        // System.out.println("variance_of_covariance_of_rescaled_gradients = " + Arrays.toString(variance_of_covariance_of_rescaled_gradients));

        // Actually don't care about the variance of the covariance. A point of good alignment should have very high positive covariance.


        // Use this as the central point of the scaling / covariance maximisation ...
        int pos_of_max_point = getPositionOfMaxPoint(covariance_of_rescaled_gradients);
        System.out.println("pos_of_max_point = " + pos_of_max_point);


        double minscaling = 0.0;
        double maxscaling = 3.5;
        int numberofscalingsteps = 1000;

        double[] covariance_hill_climbing = tryScalingRange(results_original, rescaled_results_scanned, minscaling, maxscaling, numberofscalingsteps, pos_of_max_point);
        System.out.println("covariance_hill_climbing = " + Arrays.toString(covariance_hill_climbing));

        double best_scaling = minscaling + ((maxscaling - minscaling) / numberofscalingsteps) * (getPositionOfMaxPoint(covariance_hill_climbing));
        System.out.println("best_scaling = " + best_scaling);


        Pair<Integer, Double> opt_pos_scaling = hillClimb(gradient_original, gradient_of_rescaled_modified, 0.0001, 1, pos_of_max_point, 1000);


        */
    }



    // Pair ::= < < xstart, xend >, height>

    // zero_gradient_threshold = the maximum dydx value which is considered as being zero
    // sd_negative_threshold = a positive number, such that d2ydx2 < - sd_negative_threshold for the second derivative to be considered negative in that region

    public static LinkedList<Pair<Integer, Integer>>  find_peaks(int[] signal, int zero_gradient_threshold, int sd_negative_threshold){








        // TODO: FINISH OFF THIS PEAK FINDING METHOD ............. OR ............... USE MATLAB







        // Compute dy/dx
        int[] dydx = diff1(signal);

        // Compute second derivative
        int[] d2ydx2 = diff2(signal);

        LinkedList<Pair<Pair<Integer, Integer>, Integer>> peaks = new LinkedList<>();


        boolean currently_in_a_peak = false;

        for (int i = 0; i < d2ydx2.length; i++) {
            // If we are currently not in a peak and we find the gradient to be sufficiently negative, then enter a peak and
            //

            if (!currently_in_a_peak && Math.abs(dydx[i]) < zero_gradient_threshold) {
                if (Math.abs(d2ydx2[i]) < - sd_negative_threshold) {
                    currently_in_a_peak = true;
                }
            }
        }

        return null;

    }




    // Better to do an exhaustive search rather than a hill climb ?
    public static int[] diff1(int[] data) {
        int[] differences1 = new int[data.length-1];
        for (int i = 1; i < data.length; i++){
            differences1[i] = data[i] - data[i-1];
        }
        return differences1;
    }

    public static int[] diff2(int[] data) {
        return diff1(diff1(data));
    }


    public static double parameterCost(double scaling, int centralpoint, int originalcentralpoint) {
        //return Math.pow(scaling - 1.0, 2) + Math.pow(centralpoint - originalcentralpoint, 2);
        return 0.0;
    }

    public static Pair<Integer, Double> hillClimb(int[] original_data, int[] data_to_scale,
                                                 double eps_scaling,
                                                 int eps_translation,
                                                 int initial_central_point,
                                                 int num_iterations) {
        double currScaling = 1.0;
        int currCentralPoint = initial_central_point;


        double curr_covariance = getCovariance(original_data, data_to_scale, currCentralPoint, currScaling) - parameterCost(currScaling, currCentralPoint, initial_central_point);;


        for (int i = 0; i < num_iterations; i++) {

            System.out.println("i = " + i);
            System.out.println("curr_covariance = " + curr_covariance);
            System.out.println("currCentralPoint = " + currCentralPoint);
            System.out.println("currScaling = " + currScaling);
            System.out.println();


            double cov1 = getCovariance(original_data, data_to_scale, currCentralPoint+eps_translation, currScaling+eps_scaling);
            double cov2 = getCovariance(original_data, data_to_scale, currCentralPoint+eps_translation, currScaling-eps_scaling);
            double cov3 = getCovariance(original_data, data_to_scale, currCentralPoint-eps_translation, currScaling+eps_scaling);
            double cov4 = getCovariance(original_data, data_to_scale, currCentralPoint-eps_translation, currScaling-eps_scaling);


            double pc1 = parameterCost(currScaling+eps_scaling, currCentralPoint+eps_translation, initial_central_point);
            double pc2 = parameterCost(currScaling-eps_scaling, currCentralPoint+eps_translation, initial_central_point);
            double pc3 = parameterCost(currScaling+eps_scaling, currCentralPoint-eps_translation, initial_central_point);
            double pc4 = parameterCost(currScaling-eps_scaling, currCentralPoint-eps_translation, initial_central_point);


            double max_covariance = Math.max(cov1 - pc1, Math.max(cov2-pc2, Math.max(cov3-pc3, cov4-pc4)));

            if (cov1-pc1 >= max_covariance) {

                curr_covariance = cov1-pc1;
                currScaling += eps_scaling;
                currCentralPoint += eps_translation;

            } else if (cov2-pc2 >=  max_covariance) {

                curr_covariance = cov2-pc2;
                currScaling -= eps_scaling;
                currCentralPoint += eps_translation;

            } else if (cov3-pc3 == max_covariance) {

                curr_covariance = cov3-pc3;
                currScaling += eps_scaling;
                currCentralPoint -= eps_translation;

            } else {



                curr_covariance = cov4-pc4;
                currScaling -= eps_scaling;
                currCentralPoint -= eps_translation;

            }

        }

        return new Pair<>(currCentralPoint, currScaling);

    }

    public static double getCovariance(int[] original_data, int[] data_to_scale,  int centre_point, double currScaling) {


        // Compute the covariance with the scaling applied
        double EX = 0, EY = 0;

        for(int index = 0; index < data_to_scale.length; index++) {

            int scaled_coordinate = scalingTransformation(index, centre_point, currScaling);
            if (scaled_coordinate < 0 || scaled_coordinate >=  data_to_scale.length) {
                continue;
            }
            EX+= data_to_scale[scaled_coordinate];
            EY+= original_data[index];
        }
        EX = EX/data_to_scale.length;
        EY = EY/data_to_scale.length;



        double nEXEXYEY = 0.0;
        for (int index = 0; index < data_to_scale.length; index++) {
            int scaled_coordinate = scalingTransformation(index, centre_point, currScaling);
            if (scaled_coordinate < 0 || scaled_coordinate >=  data_to_scale.length) {
                continue;
            }
            double currX = data_to_scale[scaled_coordinate];
            double currY = original_data[index];

            nEXEXYEY += (currX - EX) * (currY - EY);
        }

        double covariance = nEXEXYEY / data_to_scale.length;
        return covariance;
    }


    public static double[] tryScalingRange(int[] original_data, int[] data_to_scale, double minScalingFactor, double maxScalingFactor, int numSteps, int centre_point) {


        assert (data_to_scale.length == original_data.length);

        double currScaling;

        double[] covariance_of_scaling_factor = new double[numSteps];

        double delta_scaling = (maxScalingFactor - minScalingFactor)/numSteps;

        for (int i = 0; i < numSteps; i++) {

            System.out.println("i = " + i);

            currScaling = minScalingFactor + i * delta_scaling;

            // Compute the covariance with the scaling applied
            double EX = 0, EY = 0;

            for(int index = 0; index < data_to_scale.length; index++) {

                int scaled_coordinate = scalingTransformation(index, centre_point, currScaling);
                if (scaled_coordinate < 0 || scaled_coordinate >=  data_to_scale.length) {
                    continue;
                }
                EX+= data_to_scale[scaled_coordinate];
                EY+= original_data[index];
            }
            EX = EX/data_to_scale.length;
            EY = EY/data_to_scale.length;



            double nEXEXYEY = 0.0;
            for (int index = 0; index < data_to_scale.length; index++) {
                int scaled_coordinate = scalingTransformation(index, centre_point, currScaling);
                if (scaled_coordinate < 0 || scaled_coordinate >=  data_to_scale.length) {
                    continue;
                }
                double currX = data_to_scale[scaled_coordinate];
                double currY = original_data[index];

                nEXEXYEY += (currX - EX) * (currY - EY);
            }

            double covariance = nEXEXYEY / data_to_scale.length;
            covariance_of_scaling_factor[i] = covariance;
        }

        return covariance_of_scaling_factor;
    }

    // If we try to access position x, return the index that we'd have accessed
    // if the data had been `scaled' about the centre point by the stated scaling factor
    private static int scalingTransformation(int position, int centre_point, double scaling_factor) {
        int pos_prime = (int) Math.floor((position - centre_point) * scaling_factor + centre_point);
        return pos_prime;
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


    public static int getPositionOfMaxPoint(double[] data) {
        int pos_max = 0;
        double max = data[0];
        for (int i = 0; i < data.length; i++) {
            if (max < data[i]) {
                max = data[i];
                pos_max = i;
            }
        }
        return pos_max;
    }

    public static double computeOverallVariance(double[] data) {



        double EX = 0;
        for (int i = 0; i < data.length; i++) {
            EX += data[i];
        }
        EX = EX/data.length;


        double SSD = 0.0;
        for (int i = 0; i < data.length; i++) {
            SSD += Math.pow(data[i] - EX, 2);
        }

        double variance = SSD/data.length;
        return variance;
    }

    public static double[] varianceWithinWindow(double[] data,int windowsize) {

        double[] result = new double[data.length];

        for (int base = 0; base < data.length - windowsize; base++) {



            double EX = 0;
            for (int offset = 0; offset < windowsize; offset++) {
                EX += data[base+offset];
            }
            EX = EX/windowsize;


            double SSD = 0.0;
            for (int offset = 0; offset < windowsize; offset++) {
                SSD += Math.pow(data[base+offset] - EX, 2);
            }

            double variance = SSD/windowsize;

            for (int offset = 0; offset < windowsize; offset++) {
                result[base+offset] = variance;
            }
        }
        return result;


    }

    public static double[] covarianceWithinWindow(int[] original_input, int[] modified_input, int windowsize){

        assert(original_input.length == modified_input.length);


        double[] result = new double[original_input.length];

        for (int base = 0; base < original_input.length - windowsize; base++) {

            // Find the covariance of the original and modified vectors over the window starting from base


            // Find EX, EY
            double EX = 0, EY = 0;
            for (int offset = 0; offset < windowsize; offset++) {
                EX += original_input[base+offset];
                EY += modified_input[base+offset];
            }
            EX = EX/windowsize;
            EY = EY/windowsize;


            // The thing to divide by windowsize to get the covariance
            double nEXEXYEY = 0.0;

            for (int offset = 0; offset < windowsize; offset++) {

                int currX = original_input[base+offset];
                int currY = modified_input[base+offset];

                nEXEXYEY += (currX - EX) * (currY - EY);
            }

            nEXEXYEY = nEXEXYEY / windowsize;

            double covariance = nEXEXYEY;

            for (int offset = 0; offset < windowsize; offset++) {
                result[base+offset] = covariance;
            }
        }
        return result;


    }

    public static int[] rescale(int[] original_input, int[] modified_input, int windowsize) {


        assert(original_input.length == modified_input.length);

        int[] result = new int[original_input.length];

        for (int base = 0; base < original_input.length - windowsize; base+=windowsize) {

            int original_max = original_input[base];
            int modified_max = modified_input[base];

            for (int offset = 0; offset < windowsize; offset++) {
                original_max = Integer.max(original_max, original_input[base+offset]);
                modified_max = Integer.max(modified_max, modified_input[base+offset]);
            }

            double correction = (double)original_max/(double)modified_max;

            // TODO: need a more robust/sophisticated thing here to pick the magic numbers

            // Probably wrong (peak alignment)
            if (correction > 3) {
                correction = 1;
            }

            for (int offset = 0; offset < windowsize; offset++) {
                result[base+offset] = (int) (modified_input[base+offset] * correction);
            }
        }
        return result;
    }

    // Input: n element array A, output = n element array
    // Output[i] = A[i] - A[i-1] if i > 0
    //           = A[i] if i == 0
    public static int[] findStepsBetweenAdjacentElements(int[] input) {
        int[] result = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            if (i == 0) {
                result[i] = input[i];
            } else {
                result[i] = input[i] - input[i-1];
            }
        }
        return result;
    }

    public int[] tryFixedThetaValue(double theta, int verticalSpacing) {

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

        System.out.println("results = " + Arrays.toString(results));

        return results;
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


        // TODO:Remember to change YSTARTPOS to change the position of the lines!

        int YSTARTPOS = 500;
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
