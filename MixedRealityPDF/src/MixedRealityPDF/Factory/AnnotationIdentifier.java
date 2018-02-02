package MixedRealityPDF.Factory;

import MixedRealityPDF.Annotation;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

// Given an image and a set of coordinates classify the Annotations.
public interface AnnotationIdentifier {

  public Collection<Annotation>
  identifyAnnotations(Image image, Collection<Point2D.Double> points);

}
