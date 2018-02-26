package bounding.DocumentProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorExtractor extends DiffMap {

    protected static BufferedImage extractCol(BufferedImage a, double threshold) {

        BufferedImage out = new BufferedImage(a.getWidth(), a.getHeight(), a.getType());
        for (int x=0; x<a.getWidth(); x++) {
            for (int y=0; y<a.getHeight(); y++) {
                Color aCol = new Color( a.getRGB(x,y) , true);
                Color outCol;
                if (
//                        ColorDistance(aCol, Color.WHITE)/WHITE_DIST > threshold
//                     && ColorDistance(aCol, Color.BLACK)/WHITE_DIST > threshold
                        Stats.getStdDev(new double[]{aCol.getRed(), aCol.getGreen(), aCol.getBlue()})/255 > threshold
                        ) outCol = aCol;
                else outCol = new Color(255, 255, 255, 0);
                out.setRGB( x, y, outCol.getRGB() );
            }
        }

        return out;
    }

    @Override
    public BufferedImage findDifference(BufferedImage original, BufferedImage modified) {
        return extractCol(modified, 0.1);
    }

}
