package MixedRealityPDF.AnnotationProcessor.Identification;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

public interface IFeatureExtractor {
  Collection<String> getFeatureNames();
  List<String> extractFeatures(BufferedImage img);
}
