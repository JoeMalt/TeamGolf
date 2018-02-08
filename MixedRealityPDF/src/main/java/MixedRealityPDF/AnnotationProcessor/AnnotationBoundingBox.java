package MixedRealityPDF.AnnotationProcessor;

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