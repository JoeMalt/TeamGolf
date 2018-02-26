package bounding.integration;


import experiments.PDFs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Diptarko Roy on 26/02/2018.
 */
public class AlignmentControllerTests {


    public static void main(String[] args) throws IOException {
        String pdf_original_path = PDFs.TEST_1_TEXT_ORIGINAL;
        String pdf_new_path = PDFs.TEST_1_TEXT_HEAVY_ANNOTATION;


        BufferedImage extractedDifference = AlignmentController.getDifferenceMap(pdf_original_path, pdf_new_path);



        ImageIO.write(extractedDifference,
                "png",
                new File("output_images_new/OUT-"+Alignment.date()));

    }


}
