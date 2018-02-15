package MixedRealityPDF.DocumentProcessor;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Subtractor extends DiffMap {
    /**
     * Performs simple image subtraction, subtracting b from a
     * @param a
     * @param b
     * @return a-b
     */
    protected static BufferedImage subtract(BufferedImage a, BufferedImage b) {
        return subtract(a,b,0);
//        assert a.getWidth()==b.getWidth();
//        assert a.getHeight()==b.getHeight();
//
//        BufferedImage out = new BufferedImage(a.getWidth(), a.getHeight(), a.getType());
//        for (int x=0; x<a.getWidth(); x++) {
//            for (int y=0; y<a.getHeight(); y++) {
//                Color aCol = new Color( a.getRGB(x,y) , true);
//                Color bCol = new Color( b.getRGB(x,y) , true);
//
//                Color diff = new Color(
//                        255 - Math.abs( aCol.getRed() - bCol.getRed() ),
//                        255 - Math.abs( aCol.getBlue() - bCol.getBlue() ),
//                        255 - Math.abs( aCol.getGreen() - bCol.getGreen() ),
//                        255 - Math.abs( aCol.getAlpha() - bCol.getAlpha() )
//                );
////                if (!aCol.equals(Color.WHITE)) {
////                    System.out.println();
////                }
//                if (!aCol.equals(bCol)) diff = aCol;
//                else diff.equals(Color.TRANSLUCENT);
//                out.setRGB( x, y, diff.getRGB() );
//            }
//        }
//
//        return out;
    }

    protected static BufferedImage subtract(BufferedImage a, BufferedImage b, double threshold) {
        assert a.getWidth()==b.getWidth();
        assert a.getHeight()==b.getHeight();

        BufferedImage out = new BufferedImage(a.getWidth(), a.getHeight(), a.getType());
        for (int x=0; x<a.getWidth(); x++) {
            for (int y=0; y<a.getHeight(); y++) {
                Color aCol = new Color( a.getRGB(x,y) , true);
                Color bCol = new Color( b.getRGB(x,y) , true);

                Color diff = new Color(
//                        255 - Math.abs
                        Math.max(0,
                                ( aCol.getRed() - bCol.getRed() )
                                )
                        ,
//                        255 - Math.abs
                        Math.max(0,
                                ( aCol.getBlue() - bCol.getBlue() )
                                )
                        ,
//                        255 - Math.abs
                        Math.max(0,
                                ( aCol.getGreen() - bCol.getGreen() )
                                )
                        ,
                        Math.max(0,
                                255 - Math.abs
                                        ( aCol.getAlpha() - bCol.getAlpha() )
                        )
                );
                if (d.apply(aCol, bCol)/d.max() < threshold) diff = new Color(255, 255, 255, 0);
                out.setRGB( x, y, diff.getRGB() );
            }
        }

        return out;
    }


    @Override
    public BufferedImage findDifference(BufferedImage original, BufferedImage modified) {
        return subtract(modified, original, 0.1);
    }
}
