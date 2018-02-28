package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

// Given an image and a set of coordinates classify the Annotations.
public interface IAnnotationIdentifier {
  Collection<Annotation> identifyAnnotations(BufferedImage image, Collection<AnnotationBoundingBox> points, int pageNumber);
}
