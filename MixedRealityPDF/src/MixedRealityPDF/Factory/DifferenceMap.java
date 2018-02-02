package MixedRealityPDF.Factory;

import java.awt.Image;

// Difference map given 2 of the same doccument modified and unmodified extract
// the difference between the 2.
public interface DifferenceMap {

  public Image findDifference(Image original, Image modified);
  public Image findDifference(String originalPDFFilepath, Image modified);
}
