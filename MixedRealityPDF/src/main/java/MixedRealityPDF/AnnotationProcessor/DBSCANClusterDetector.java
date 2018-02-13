package MixedRealityPDF.AnnotationProcessor;

/**
 * Created by joe on 08/02/18.
 */


import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class DBSCANClusterDetector implements IClusterDetector {

    private static double DBSCAN_EPSILON = 20.0;
    private static int DBSCAN_MINPTS = 50;

    public Collection<AnnotationBoundingBox> cluster(BufferedImage im){

        // If the image is more than 500 pixels wide, scale it down
        double scale = 1.0d;
        BufferedImage scaledImage;
        if (im.getWidth() > 500){
            scaledImage = getScaledImage(im, 500);
            scale = im.getWidth() / 500;
        }
        else{
            scaledImage = im;
        }

        // Get a list of the pixels that are not white / transparent
        Set<ClusteringPoint> pixelsToCluster = getNonBlankPixels(scaledImage);

        // Get the clusters using DBSCAN
        List<Cluster<ClusteringPoint>> clusters = DBSCAN(pixelsToCluster);

        // Generate a set of bounding boxes, one for each cluster

        Set<AnnotationBoundingBox> annotationSet = new HashSet<>();
        for (Cluster<ClusteringPoint> c : clusters){
            annotationSet.add(new AnnotationBoundingBox(c));
        }
        return annotationSet;
    }

    private BufferedImage getScaledImage(BufferedImage im, int width){
        Image scaledImage = im.getScaledInstance(width, -1, Image.SCALE_DEFAULT);
        BufferedImage imScaled = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferedGraphics2D = imScaled.createGraphics();
        bufferedGraphics2D.drawImage(scaledImage, 0, 0, null);
        bufferedGraphics2D.dispose();

        return imScaled;
    }

    private Set<ClusteringPoint> getNonBlankPixels(BufferedImage im){
        Set<ClusteringPoint> nonBlankPixels = new HashSet<>();
        for (int h = 0; h < im.getHeight(); h++){
            for (int w = 0; w < im.getWidth(); w++){
                int px = im.getRGB(w, h);
                if (px != -1 && px != 0){ // -1 is white, 0 is transparent
                    nonBlankPixels.add(new ClusteringPoint(w, h));
                }
            }
        }
        return nonBlankPixels;
    }

    private static List<Cluster<ClusteringPoint>> DBSCAN(Set<ClusteringPoint> points){
        DBSCANClusterer<ClusteringPoint> clusterer = new DBSCANClusterer<ClusteringPoint>(DBSCAN_EPSILON, DBSCAN_MINPTS);
        return clusterer.cluster(points);
    }
}
