package MixedRealityPDF.AnnotationProcessor;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class DBSCANClusterDetector implements IClusterDetector {

    // Parameters for DBSCAN
    // epsilon: how close 2 points must be to be considered part of the same cluster, in millimetres
    // this is converted to pixels based on the width of the image
    // Empirically, about 3mm works well
    private static double DBSCAN_EPSILON_MM = 3.0;
    // min-points: the minimum number of points in a cluster. Useful for excluding noise.
    // Empirically, about 5 works well
    private static int DBSCAN_MINPTS = 5;

    // If an image is wider than this value, scale it down to this width before clustering. Higher values
    // may give slightly more accurate bounding boxes (and catch tiny annotations), but are slower.
    private static int SCALED_IMAGE_WIDTH = 500;

    /**
     *
     * @param im: BufferedImage on which to perform the clustering
     * @return Collection of AnnotationBoundingBox, one per annotation detected
     */
    public Collection<AnnotationBoundingBox> cluster(BufferedImage im){

        // If the image is more than 500 pixels wide, scale it down
        double scale = 1.0d;
        BufferedImage scaledImage;
        if (im.getWidth() > SCALED_IMAGE_WIDTH){
            scaledImage = getScaledImage(im, SCALED_IMAGE_WIDTH);
            scale = ((double) im.getWidth()) / SCALED_IMAGE_WIDTH;
        }
        else{
            scaledImage = im;
        }

        // Calculate epsilon in pixels (as opposed to millimetres)
        // Assumes the document is A4 (210mm wide)
        int epsilon = (int) ((DBSCAN_EPSILON_MM / 210.0) * scaledImage.getWidth());

        // Get a list of the pixels that are not white / transparent
        Set<ClusteringPoint> pixelsToCluster = getNonBlankPixels(scaledImage);

        // Get the clusters using DBSCAN
        List<Cluster<ClusteringPoint>> clusters = DBSCAN(pixelsToCluster, epsilon);

        // Generate a set of bounding boxes, one for each cluster

        Set<AnnotationBoundingBox> annotationSet = new HashSet<>();
        for (Cluster<ClusteringPoint> c : clusters){
            annotationSet.add(new AnnotationBoundingBox(c, scale));
        }
        return annotationSet;
    }

    private static BufferedImage getScaledImage(BufferedImage im, int width){
        Image scaledImage = im.getScaledInstance(width, -1, Image.SCALE_DEFAULT);
        BufferedImage imScaled = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferedGraphics2D = imScaled.createGraphics();
        bufferedGraphics2D.drawImage(scaledImage, 0, 0, null);
        bufferedGraphics2D.dispose();

        return imScaled;
    }

    private Set<ClusteringPoint> getNonBlankPixels(BufferedImage im){
        /*
         Iterate through all the pixels in im, and add the coordinates of any that are not white/transparent to a set.
         */
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

    private static List<Cluster<ClusteringPoint>> DBSCAN(Set<ClusteringPoint> points, int epsilon){
        DBSCANClusterer<ClusteringPoint> clusterer = new DBSCANClusterer<ClusteringPoint>(epsilon, DBSCAN_MINPTS);
        return clusterer.cluster(points);
    }
}
