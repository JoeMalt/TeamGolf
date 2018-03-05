package MixedRealityPDF.ImageProcessor.ColourRemoval;

import MixedRealityPDF.ImageProcessor.IDifferenceMap;
import MixedRealityPDF.ImageProcessor.Stats;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorExtractor implements IDifferenceMap {




    @Override
    public BufferedImage findDifference(BufferedImage original, BufferedImage modified) {
        return extractColorComponent(modified);
    }

    private static BufferedImage extractCol(BufferedImage a, double threshold) {
        BufferedImage out = new BufferedImage(a.getWidth(), a.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int x=0; x<a.getWidth(); x++) {
            for (int y=0; y<a.getHeight(); y++) {
                Color aCol = new Color( a.getRGB(x,y) , true);
                Color outCol;
                if (Stats.getStdDev(new double[]{aCol.getRed(),
                        aCol.getGreen(), aCol.getBlue()})/255 > threshold) {
                    outCol = aCol;
                }
                else outCol = new Color(255, 255, 255, 0);
                out.setRGB( x, y, outCol.getRGB() );
            }
        }
        return out;
    }


    private static BufferedImage extractBlack(BufferedImage a, double threshold) {
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
