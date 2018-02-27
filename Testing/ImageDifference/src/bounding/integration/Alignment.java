package bounding.integration;

import boundingtests.PDFWrapper;
import experiments.FastPDF;
import experiments.PDFs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;




public class Alignment {

    public static BufferedImage getAlignedScan(BufferedImage original, BufferedImage scan) {
        PDFWrapper originalPDFWrapeper = new PDFWrapper(original);
        PDFWrapper scanPDFWrapper = new PDFWrapper(scan);
        BufferedImage alignedScan = PDFWrapper.align(original, scan);
        return alignedScan;
    }

    public static String date() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
