package MixedRealityPDF.ImageProcessor.ColourRemoval;

import java.awt.image.BufferedImage;

// Difference map given 2 of the same doccument modified and unmodified extract
// the difference between the 2.
public interface IDifferenceMap {

    BufferedImage findDifference(BufferedImage original, BufferedImage modified);

}
