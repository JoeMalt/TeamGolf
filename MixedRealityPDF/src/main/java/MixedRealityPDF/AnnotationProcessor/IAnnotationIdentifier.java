package MixedRealityPDF.AnnotationProcessor;

import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collection;

// Given an image and a set of coordinates classify the Annotations.
public interface IAnnotationIdentifier {
  Collection<Annotation> identifyAnnotations(Image image, Collection<Point2D.Double> points);
}
