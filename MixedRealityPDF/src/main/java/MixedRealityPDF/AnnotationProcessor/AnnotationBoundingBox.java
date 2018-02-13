package MixedRealityPDF.AnnotationProcessor;

import org.apache.commons.math3.ml.clustering.Cluster;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by joe on 08/02/18.
 */
public class AnnotationBoundingBox {
    private ClusteringPoint top_left;
    private ClusteringPoint top_right;
    private ClusteringPoint bottom_left;
    private ClusteringPoint bottom_right;

    public AnnotationBoundingBox(ClusteringPoint top_left, ClusteringPoint top_right, ClusteringPoint bottom_left, ClusteringPoint bottom_right){
        this.top_left = top_left;
        this.top_right = top_right;
        this.bottom_left = bottom_left;
        this.bottom_right = bottom_right;
    }

    public AnnotationBoundingBox(Cluster<ClusteringPoint> cluster, double scale){
        // Generate a bounding box aligned to the x-y axes by finding the highest and lowest, and leftmost and rightmost, points in the cluster.
        List<Integer> xValues = cluster.getPoints().stream().map(ClusteringPoint::getX).collect(Collectors.toList());
        List<Integer> yValues = cluster.getPoints().stream().map(ClusteringPoint::getY).collect(Collectors.toList());
        System.out.println("Cluster has " + xValues.size() + " points");

        this.top_left = new ClusteringPoint((int) (Collections.min(xValues) * scale), (int) (Collections.min(yValues) * scale));
        this.top_right = new ClusteringPoint((int) (Collections.max(xValues) * scale), (int) (Collections.min(yValues) * scale));
        this.bottom_left = new ClusteringPoint((int) (Collections.min(xValues) * scale), (int) (Collections.max(yValues) * scale));
        this.bottom_right = new ClusteringPoint((int) (Collections.max(xValues) * scale), (int) (Collections.max(yValues) * scale));

    }

    public ClusteringPoint getTopLeft() {
        return top_left;
    }

    public ClusteringPoint getTopRight() {
        return top_right;
    }

    public ClusteringPoint getBottomLeft() {
        return bottom_left;
    }

    public ClusteringPoint getBottomRight() {
        return bottom_right;
    }
}