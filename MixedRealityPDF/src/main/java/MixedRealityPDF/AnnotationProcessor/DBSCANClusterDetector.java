package MixedRealityPDF.AnnotationProcessor;

/**
 * Created by joe on 08/02/18.
 */

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import java.awt.image.BufferedImage;
import java.util.Collection;

public class DBSCANClusterDetector implements IClusterDetector {
    public Collection<AnnotationBoundingBox> cluster(BufferedImage im){
        return null;
    }
}
