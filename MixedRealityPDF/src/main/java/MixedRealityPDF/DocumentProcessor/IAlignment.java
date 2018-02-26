package MixedRealityPDF.DocumentProcessor;

import java.awt.*;

public interface IAlignment {

    /**
     * Aligns as image of a page of the scanned document with an image of the same page from the original document
     * @param original
     * @param modified
     * @return          Image of the scanned document perfectly aligned with the original
     */
    public Image align(Image original, Image modified);
}
