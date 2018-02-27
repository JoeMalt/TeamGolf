package boundingtests;

import experiments.FastPDF;
import experiments.PDFs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlignmentTrials1 {
/*

    public static void main(String[] args) throws IOException {
        BufferedImage original = ImageIO.read(new File(PDFs.ORIGINAL_NEW));
        PDFWrapper originalPDFWrapeper = new PDFWrapper(original);

        BufferedImage scan = ImageIO.read(new File(PDFs.TRANSFORMED_NEW));
        FastPDF scanFPDF = new FastPDF(scan, 1);
        PDFWrapper scanPDFWrapper = new PDFWrapper(scan);

        double thetaRotation = scanFPDF.getAngle();

        BufferedImage rotationCorrectedScan = FastPDF.rotationCorrect(thetaRotation, scan);

        System.out.println("thetaRotation = " + thetaRotation);

        BufferedImage alignedScan = PDFWrapper.align(original, rotationCorrectedScan);

        PDFWrapper.save(alignedScan, "output_images_new/alignedscan"+ date());

    }
*/


    private static String date() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
