package experiments;

import org.opencv.core.*;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import static org.opencv.calib3d.Calib3d.findHomography;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.warpPerspective;

public class OpenCVExperiments
{
    public static void main( String[] args ) throws IOException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String original = "originalpdf.png";
        String scan = "scannedversion.png";

        // Read source image.
        Mat im_src = imread(original);

        // Read destination image.
        Mat im_dst = imread(scan);

        Point[] pts_src_array = {
                new Point(0, 0),
            new Point(100, 0),
            new Point(0, 100),
            new Point(100, 100)};

        Point[] pts_dst_array = {
                new Point(141, 131),
                new Point(480, 159),
                new Point(493, 159),
                new Point(64, 601)};



        MatOfPoint2f pts_src = new MatOfPoint2f(pts_src_array);
        MatOfPoint2f pts_dst = new MatOfPoint2f(pts_src_array);

        // Calculate Homography
        Mat h = findHomography(pts_src, pts_dst);

        // Output image
        Mat im_out = new Mat();

        // Warp source image to destination based on homography
        warpPerspective(im_src, im_out, h, im_dst.size());


        // Display images
        BufferedImage output_buffimage =toBufferedImage(toImage(im_out));


        // Write the output image to disk
        File outputfile = new File("opencv-output.png");
        ImageIO.write(output_buffimage, "png", outputfile);

    }

    public static void saveMatrixAsImage(Mat m, String path) throws IOException {
        Image im = toImage(m);
        BufferedImage bm = toBufferedImage(im);
        // Write the output image to disk
        File outputfile = new File(path);
        ImageIO.write(bm, "png", outputfile);

    }


    public static Image toImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;

    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }



    public static void showResult(Object to_show, String message) {
        JOptionPane.showMessageDialog(null, to_show, message, JOptionPane.PLAIN_MESSAGE);
    }
}