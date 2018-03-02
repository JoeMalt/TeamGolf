package MixedRealityPDF.ImageProcessor.ColourRemoval;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Differ extends DiffMap {

    public Differ(){}

    protected static BufferedImage diffPix(BufferedImage a, BufferedImage b, double threshold) {
        assert a.getWidth()==b.getWidth();
        assert a.getHeight()==b.getHeight();

        BufferedImage out = new BufferedImage(a.getWidth(), a.getHeight(), a.getType());
        for (int x=0; x<Math.min(a.getWidth(), b.getWidth()); x++) {
            for (int y=0; y<Math.min(a.getHeight(), b.getHeight()); y++) {
                Color aCol = new Color(a.getRGB(x,y) , true);
                Color bCol = new Color(b.getRGB(x,y) , true);

                Color outCol;
                if (d.apply(aCol, bCol)/d.max() > threshold) outCol=aCol;
                else outCol = new Color(255,255,255,0);
                out.setRGB( x, y, outCol.getRGB() );
            }
        }

        return out;
    }

    @Override
    public BufferedImage findDifference(BufferedImage original, BufferedImage modified) {
        return diffPix(modified, original, 0.3);
    }

}
