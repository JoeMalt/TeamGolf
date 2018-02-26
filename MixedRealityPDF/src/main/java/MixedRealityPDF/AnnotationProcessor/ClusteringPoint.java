package MixedRealityPDF.AnnotationProcessor;

import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Created by joe on 08/02/18.
 */
public class ClusteringPoint implements Clusterable {
    private int x;
    private int y;

    public double[] getPoint(){
        double[] point = {x, y};
        return point;
    }

    public ClusteringPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
