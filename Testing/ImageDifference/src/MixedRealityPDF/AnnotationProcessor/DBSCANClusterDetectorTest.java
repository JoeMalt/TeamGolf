package MixedRealityPDF.AnnotationProcessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class DBSCANClusterDetectorTest {

    public static void main(String[] args) {

        String pathname = "output_images_new/OUT-2018-02-26-13-13-40";
        String outputPath = pathname + "-withBBs";
        try {
            long startTime = System.nanoTime();
            BufferedImage im = ImageIO.read(new File(pathname));
            DBSCANClusterDetector cd = new DBSCANClusterDetector();
            Collection<AnnotationBoundingBox> s = cd.cluster(im);
            // draw the bounding boxes back onto the image

            Graphics2D g2d = im.createGraphics();
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
            ImageIO.write(im, "PNG", new File(pathname));
            System.out.println("Time taken: " + (double) (System.nanoTime() - startTime) / 1000000000L + " seconds");
        }
        catch (IOException e){

        }
    }
}
