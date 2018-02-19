package MixedRealityPDF.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test {


  public static void main(String[] args) throws IOException{
    
    String path = "../Data/";
    String type = "png";
    String inName = "New Data/scan."  + type;
    String outName = "New Data/scanBW." + type;

    BufferedImage bi = ImageIO.read(new File(path + inName));
    BufferedImage out = ImageProcessor.computeBlackAndWhite(bi);

    File output = new File(path + outName);
    ImageIO.write(out, type, output);
  }

}
