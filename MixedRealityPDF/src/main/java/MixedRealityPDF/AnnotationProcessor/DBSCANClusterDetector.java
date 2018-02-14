package MixedRealityPDF.AnnotationProcessor;

/**
 * Created by joe on 08/02/18.
 */


import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class DBSCANClusterDetector implements IClusterDetector {

    private static double DBSCAN_EPSILON = 20.0;
    private static int DBSCAN_MINPTS = 50;
    private static int SCALED_IMAGE_WIDTH = 500;

    // TO DELETE
    // draw the bounding box on the image
    public static void main(String[] args) {
        try {
            BufferedImage im = ImageIO.read(new File("/home/joe/IBGroupProject/Stage2_Test1_no_text.png"));
            DBSCANClusterDetector cd = new DBSCANClusterDetector();
            Collection<AnnotationBoundingBox> s = cd.cluster(im);
            // draw the bounding boxes back onto the image

            Graphics2D g2d = im.createGraphics(); //TODO draw on original image
            g2d.setBackground(Color.WHITE);
            g2d.setColor(Color.RED);

            for (AnnotationBoundingBox boundingBox : s) {
                // draw the bounding box on the image
                System.out.println("bounding box with top left at (" + boundingBox.getTopLeft().getX() + "," + boundingBox.getTopLeft().getY() + ").");

                g2d.drawLine(boundingBox.getTopLeft().getX(), boundingBox.getTopLeft().getY(), boundingBox.getTopRight().getX(), boundingBox.getTopRight().getY());
                g2d.drawLine(boundingBox.getTopLeft().getX(), boundingBox.getTopLeft().getY(), boundingBox.getBottomLeft().getX(), boundingBox.getBottomLeft().getY());
                g2d.drawLine(boundingBox.getBottomLeft().getX(), boundingBox.getBottomLeft().getY(), boundingBox.getBottomRight().getX(), boundingBox.getBottomRight().getY());
                g2d.drawLine(boundingBox.getTopRight().getX(), boundingBox.getTopRight().getY(), boundingBox.getBottomRight().getX(), boundingBox.getBottomRight().getY());



            }
            ImageIO.write(im, "PNG", new File("/home/joe/IBGroupProject/test_out_3.png"));
        }
        catch (IOException e){

        }

    }

    // END

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

        // Get a list of the pixels that are not white / transparent
        Set<ClusteringPoint> pixelsToCluster = getNonBlankPixels(scaledImage);

        // Get the clusters using DBSCAN
        List<Cluster<ClusteringPoint>> clusters = DBSCAN(pixelsToCluster);

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
