package MixedRealityPDF.DocumentProcessor;

import java.awt.*;

// Difference map given 2 of the same doccument modified and unmodified extract
// the difference between the 2.
public interface IDifferenceMap {

    Image findDifference(Image original, Image modified);

}
