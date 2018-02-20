package MixedRealityPDF.ImageProcessor;

import java.awt.image.BufferedImage;

public interface IAlignment {

    /**
     * Aligns an image to match another image works best with documents.
     * @param original
     * @param match
     * @return          @match aligned to match @original.
     */
    public ImageProcessor alignDocument(BufferedImage original, BufferedImage match);

}
