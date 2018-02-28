package MixedRealityPDF.ImageProcessor;

import java.awt.image.BufferedImage;

public interface IAlignment {

    /**
     * Aligns as image of a page of the scanned document with an image of the same page from the original document
     * @param original
     * @param modified
     * @return          Image of the scanned document aligned with the original
     */
    public BufferedImage align(BufferedImage original, BufferedImage modified);
}
