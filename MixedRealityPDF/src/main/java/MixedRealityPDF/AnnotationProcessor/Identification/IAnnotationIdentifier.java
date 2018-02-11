package main.java.MixedRealityPDF.AnnotationProcessor.Identification;

import main.java.MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import main.java.MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;

import java.awt.*;
import java.util.Collection;

// Given an image and a set of coordinates classify the Annotations.
public interface IAnnotationIdentifier {
  Collection<Annotation> identifyAnnotations(Image image, Collection<AnnotationBoundingBox> points);
}
