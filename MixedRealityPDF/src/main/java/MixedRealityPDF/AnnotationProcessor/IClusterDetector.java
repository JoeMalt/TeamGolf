package main.java.MixedRealityPDF.AnnotationProcessor;

import java.awt.image.BufferedImage;
import java.util.Collection;

// Given an image containing clusters find the likely coordinates of the clusters.
// k-means clustering ?
public interface IClusterDetector {
  Collection<AnnotationBoundingBox> cluster(BufferedImage image);
}
