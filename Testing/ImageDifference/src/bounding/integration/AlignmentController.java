package bounding.integration;



import bounding.Alignment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Diptarko Roy on 26/02/2018.
 */
public class AlignmentController {

    public static BufferedImage getDifferenceMap(String originalFilePath, String scanFilePath) throws IOException {

        BufferedImage originalBufferedImage = ImageIO.read(new File(originalFilePath));
        BufferedImage scanBufferedImage = ImageIO.read(new File(scanFilePath));
        BufferedImage alignedScan = Alignment.getAlignedScan(originalBufferedImage, scanBufferedImage);



    }

}
