package MixedRealityPDF.Factory;

import MixedRealityPDF.Annotations.Annotation;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.Collection;

// Given an image and a set of coordinates classify the Annotations.
public interface AnnotationIdentifier {
  Collection<Annotation> identifyAnnotations(Image image, Collection<Point2D.Double> points);
}
