package bounding;

import experiments.FastPDF;

import java.awt.image.BufferedImage;

public class Alignment {

    public static void angle(BufferedImage originalBufferedImage, BufferedImage scanBufferedImage) {

        FastPDF fpdfOriginal = new FastPDF(originalBufferedImage, 1);
        FastPDF fpdfScan = new FastPDF(scanBufferedImage, 1);

        Double rotationAngle = fpdfScan.getAngle();

    }

}
