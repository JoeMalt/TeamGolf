package MixedRealityPDF.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessorTest {


  public static void main(String[] args) throws IOException{
    
    String path = "../Data/New Data";
    String type = "png";
    String inName = "scan."  + type;
    String outName = "scanBW." + type;

    BufferedImage bi = ImageIO.read(new File(path + inName));
    BufferedImage out = ImageProcessor.computeBlackAndWhite(bi);

    File output = new File(path + outName);
    ImageIO.write(out, type, output);
  }

}
