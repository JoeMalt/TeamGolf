package experiments;// author: dsr31

import Catalano.Core.IntPoint;
import Catalano.Imaging.Corners.SusanCornersDetector;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.*;
import experiments.PDFs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CatalanoExperiments {


    public static void main(String[] args) throws IOException {


        String original = PDFs.TEST_1_TEXT_ORIGINAL;
        String modified = PDFs.TEST_1_TEXT_LIGHT_ANNOTATION;




        //BufferedImage originalBuffImage = ImageIO.read(new File(PDFs.TEST_1_TEXT_ORIGINAL));
        //BufferedImage modifiedBuffImage = ImageIO.read(new File(PDFs.TEST_1_TEXT_LIGHT_ANNOTATION));


        // threshold("scannedversion.png");

        cornerDetection(modified);
        cornerDetection(original);
        //edgeDetector(original);
        //edgeDetector(modified);
        // FFT(original);
        // imageSubtraction(threshold(new FastBitmap(original)), threshold(new FastBitmap(modified_worse)));
        // imageSubtraction(threshold(new FastBitmap(modified_worse)), threshold(new FastBitmap(original)));
    }



    public static void FFT(String original) {
        FastBitmap fb = new FastBitmap(original);
        fb.toGrayscale();
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Image", JOptionPane.PLAIN_MESSAGE);

        System.out.println("Applying FFT");
        FourierTransform ft = new FourierTransform(fb);
        ft.Forward();
        fb = ft.toFastBitmap();
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Fourier Transform", JOptionPane.PLAIN_MESSAGE);

        System.out.println("Applying frequency filter");
        FrequencyFilter ff = new FrequencyFilter(0, 60);
        ff.ApplyInPlace(ft);
        fb = ft.toFastBitmap();
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Frequency Filter", JOptionPane.PLAIN_MESSAGE);

        ft.Backward();
        fb = ft.toFastBitmap();
        JOptionPane.showMessageDialog(null, fb.toIcon(), "Result", JOptionPane.PLAIN_MESSAGE);
    }

    public static void imageXOR(FastBitmap original, FastBitmap overlay) {
        Xor xor = new Xor(overlay);
        xor.applyInPlace(original);
        showResult(original);
    }

    public static void imageSubtraction(FastBitmap original, FastBitmap overlay) {

        //Subtract the original with overlay and just see the differences.
        Subtract sub = new Subtract(overlay);
        sub.applyInPlace(original);

        // Show the results
        JOptionPane.showMessageDialog(null, original.toIcon());
    }

    public static void edgeDetector(String original) {
        //Load an image
        FastBitmap fb = new FastBitmap(original);


        //Convert to grayscale
        Grayscale g = new Grayscale();
        g.applyInPlace(fb);

        //Apple edge detector
        SobelEdgeDetector sobel = new SobelEdgeDetector ();
        sobel.applyInPlace(fb);

        // Show the result
        showResult(fb);

    }


    public static void cornerDetection(String original) {
        //Load an image
        FastBitmap fb = new FastBitmap(original);

        Grayscale g = new Grayscale(Grayscale.Algorithm.Average);
        g.applyInPlace(fb);

        SusanCornersDetector susan = new SusanCornersDetector();
        ArrayList<IntPoint> lst = susan.ProcessImage(fb);

        //If you want to detect using Harris
        //HarrisCornersDetector harris = new HarrisCornersDetector();
        //ArrayList<IntPoint> lst = harris .ProcessImage(fb);

        fb.toRGB();
        for (IntPoint p : lst) {
            fb.setRGB(p, 255, 0, 0);
        }

        //Show the result
        showResult(fb);
    }


    public static FastBitmap threshold(FastBitmap fb) {

        // Convert to grayscale
        Grayscale g = new Grayscale();
        g.applyInPlace(fb);

        // Apply Bradley local threshold
        BradleyLocalThreshold bradley = new BradleyLocalThreshold();
        bradley.applyInPlace(fb);

        //Show the result
        // showResult(fb);
        return fb;
    }

    public static void showResult(FastBitmap fb) {
        showResult(fb, "Result");
    }


        public static void showResult(FastBitmap fb, String title) {
        JOptionPane.showMessageDialog(null, fb.toIcon(), title, JOptionPane.PLAIN_MESSAGE);

    }

}
