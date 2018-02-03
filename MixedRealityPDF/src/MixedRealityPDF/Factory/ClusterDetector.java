package MixedRealityPDF.Factory;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.Collection;

// Given an image containing clusters find the likely coordinates of the clusters.
// k-means clustering ?
public interface ClusterDetector {
  Collection<Point2D.Double> cluster(Image image);
}
