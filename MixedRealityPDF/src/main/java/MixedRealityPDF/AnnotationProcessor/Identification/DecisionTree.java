package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;

import java.awt.image.BufferedImage;
import java.util.Collection;

public class DecisionTree implements IAnnotationIdentifier {


  public DecisionTree(){}

  @Override
  public Collection<Annotation> identifyAnnotations(BufferedImage fullImage,
          Collection<AnnotationBoundingBox> points, int pageNumber) {
    return null;
  }
}
