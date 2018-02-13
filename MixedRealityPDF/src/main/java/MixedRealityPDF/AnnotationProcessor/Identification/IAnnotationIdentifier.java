package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;

import java.awt.*;
import java.util.Collection;

// Given an image and a set of coordinates classify the Annotations.
public interface IAnnotationIdentifier {
  Collection<Annotation> identifyAnnotations(Image image, Collection<AnnotationBoundingBox> points);
}
