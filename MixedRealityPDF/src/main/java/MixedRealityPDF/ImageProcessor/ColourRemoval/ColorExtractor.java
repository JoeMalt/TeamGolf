package MixedRealityPDF.ImageProcessor.ColourRemoval;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorExtractor  {

    public static void main(String[] args) throws IOException {

        String originalPath = "Data/scans/new1a.png";
        String modifiedPath = "Data/scans/new2a.png";

        String output1 = "Data/scans/out1.png";
        String output2 = "Data/scans/out2.png";

        BufferedImage originalBuffImage = ImageIO.read(new File(originalPath));
        BufferedImage modifiedBuffImage = ImageIO.read(new File(modifiedPath));

        ImageIO.write(ColorExtractor.extractColorComponent(originalBuffImage),"png", new File(output1));
        ImageIO.write(ColorExtractor.extractBlackComponent(modifiedBuffImage),"png", new File(output2));

    }


    protected static BufferedImage extractCol(BufferedImage a, double threshold) {
        BufferedImage out = new BufferedImage(a.getWidth(), a.getHeight(), a.getType());
        for (int x=0; x<a.getWidth(); x++) {
            for (int y=0; y<a.getHeight(); y++) {
                Color aCol = new Color( a.getRGB(x,y) , true);
                Color outCol;
                if (
//                        ColorDistance(aCol, Color.WHITE)/WHITE_DIST > threshold
//                     && ColorDistance(aCol, Color.BLACK)/WHITE_DIST > threshold
                        Stats.getStdDev(new double[]{aCol.getRed(), aCol.getGreen(), aCol.getBlue()})/255 > threshold) {
                    outCol = aCol;
                }
                else outCol = new Color(255, 255, 255, 0);
                out.setRGB( x, y, outCol.getRGB() );
            }
        }
        return out;
    }


    protected static BufferedImage extractBlack(BufferedImage a, double threshold) {
        BufferedImage out = new BufferedImage(a.getWidth(), a.getHeight(), a.getType());
        for (int x=0; x<a.getWidth(); x++) {
            for (int y=0; y<a.getHeight(); y++) {
                Color aCol = new Color( a.getRGB(x,y) , true);
                Color outCol;
                if (
//                        ColorDistance(aCol, Color.WHITE)/WHITE_DIST > threshold
//                     && ColorDistance(aCol, Color.BLACK)/WHITE_DIST > threshold
                        Stats.getStdDev(new double[]{aCol.getRed(), aCol.getGreen(), aCol.getBlue()})/255 < threshold) {
                    outCol = aCol;
                }
                else outCol = Color.WHITE;
                out.setRGB( x, y, outCol.getRGB() );
            }
        }
        return out;
    }


    public static BufferedImage extractBlackComponent(BufferedImage original) {
        return extractBlack(original, 0.1);
    }


    public static BufferedImage extractColorComponent(BufferedImage original) {
        return extractCol(original, 0.1);
    }

}
