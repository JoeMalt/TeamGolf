package experiments;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.xfeatures2d.SURF;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Double.max;
import static org.opencv.features2d.Features2d.drawMatches;

class FindObject {

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new FindObject().run("originalpdf.png", "originalpdf.png", "output.png");

    }

    public void run(String pathObject, String pathScene, String pathResult) throws IOException {

        System.out.println("\nRunning FindObject");

        Mat img_1 = Imgcodecs.imread("".concat(pathObject), 0); //0 = CV_LOAD_IMAGE_GRAYSCALE
        Mat img_2 = Imgcodecs.imread("".concat(pathScene), 0);

        int minHessian = 400;

        // TODO: figure out parameters
        SURF detector = SURF.create(600, 15, 6, false, false);

        MatOfKeyPoint keypoints_1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints_2 = new MatOfKeyPoint();

        detector.detect( img_1, keypoints_1 );
        detector.detect( img_2, keypoints_2 );

        //-- Step 2: Calculate descriptors (feature vectors)
        SURF extractor = SURF.create();

        Mat descriptors_1 = new Mat();
        Mat descriptors_2 = new Mat();

        extractor.compute( img_1, keypoints_1, descriptors_1 );
        extractor.compute( img_2, keypoints_2, descriptors_2 );

        //-- Step 3: Matching descriptor vectors using FLANN matcher
        FlannBasedMatcher matcher = FlannBasedMatcher.create();



        MatOfDMatch matches = new MatOfDMatch();
        matcher.match( descriptors_1, descriptors_2, matches);

        double max_dist = 0; double min_dist = 100;

        //-- Quick calculation of max and min distances between keypoints
        for( int i = 0; i < descriptors_1.rows(); i++ )
        {


            double dist = matches.get(i, 0)[0];;
            if( dist < min_dist ) min_dist = dist;
            if( dist > max_dist ) max_dist = dist;
        }

        System.out.println("-- Max dist : "+ max_dist );
        System.out.println("-- Min dist : "+ min_dist );

        //-- Draw only "good" matches (i.e. whose distance is less than 2*min_dist,
        //-- or a small arbitary value ( 0.02 ) in the event that min_dist is very
        //-- small)
        //-- PS.- radiusMatch can also be used here.

        MatOfDMatch good_matches = new MatOfDMatch();


        for( int i = 0; i < descriptors_1.rows(); i++ )
        { if( matches.get(i, 0)[0] <= max(2*min_dist, 2) )
        { good_matches.push_back(matches.row(i)); }
        }

        //-- Draw only "good" matches
        Mat im_out = img_1.clone();

        Features2d.drawMatches(img_1, keypoints_1, img_2, keypoints_2, good_matches, im_out);



        //-- Show detected matches
        saveMatrixAsImage(im_out, "goodmatches.png");




        /*


        FeatureDetector detector = FeatureDetector.create(4); //4 = SURF

        MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
        MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();

        detector.detect(img_object, keypoints_object);
        detector.detect(img_scene, keypoints_scene);

        DescriptorExtractor extractor = DescriptorExtractor.create(2); //2 = SURF;

        Mat descriptor_object = new Mat();
        Mat descriptor_scene = new Mat();

        extractor.compute(img_object, keypoints_object, descriptor_object);
        extractor.compute(img_scene, keypoints_scene, descriptor_scene);

        DescriptorMatcher matcher = DescriptorMatcher.create(1); // 1 = FLANNBASED
        MatOfDMatch matches = new MatOfDMatch();

        matcher.match(descriptor_object, descriptor_scene, matches);
        List<DMatch> matchesList = matches.toList();

        Double max_dist = 0.0;
        Double min_dist = 100.0;

        for (int i = 0; i < descriptor_object.rows(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < min_dist) min_dist = dist;
            if (dist > max_dist) max_dist = dist;
        }

        System.out.println("-- Max dist : " + max_dist);
        System.out.println("-- Min dist : " + min_dist);

        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        MatOfDMatch gm = new MatOfDMatch();

        for (int i = 0; i < descriptor_object.rows(); i++) {
            if (matchesList.get(i).distance < 3 * min_dist) {
                good_matches.addLast(matchesList.get(i));
            }
        }

        gm.fromList(good_matches);

        Mat img_matches = new Mat();
        Features2d.drawMatches(
                img_object,
                keypoints_object,
                img_scene,
                keypoints_scene,
                gm,
                img_matches,
                new Scalar(255, 0, 0),
                new Scalar(0, 0, 255),
                new MatOfByte(),
                2);

        LinkedList<Point> objList = new LinkedList<Point>();
        LinkedList<Point> sceneList = new LinkedList<Point>();

        List<KeyPoint> keypoints_objectList = keypoints_object.toList();
        List<KeyPoint> keypoints_sceneList = keypoints_scene.toList();

        for (int i = 0; i < good_matches.size(); i++) {
            objList.addLast(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
            sceneList.addLast(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
        }

        MatOfPoint2f obj = new MatOfPoint2f();
        obj.fromList(objList);

        MatOfPoint2f scene = new MatOfPoint2f();
        scene.fromList(sceneList);

        Mat H = Calib3d.findHomography(obj, scene);

        LinkedList<Point> cornerList = new LinkedList<Point>();
        cornerList.add(new Point(0, 0));
        cornerList.add(new Point(img_object.cols(), 0));
        cornerList.add(new Point(img_object.cols(), img_object.rows()));
        cornerList.add(new Point(0, img_object.rows()));

        MatOfPoint obj_corners = new MatOfPoint();
        obj_corners.fromList(cornerList);

        MatOfPoint scene_corners = new MatOfPoint();

//ERROR HERE :
//OpenCV Error: Assertion failed (scn + 1 == m.cols && (depth == CV_32F || depth == CV_64F)) in unknown function, file ..\..\..\src\opencv\modules\core\src\matmul.cpp, line 1926
        Core.perspectiveTransform(obj_corners, scene_corners, H);
*/
//Draw the lines... later, when the homography will work
/*
Core.line(img_matches, new Point(), new Point(), new Scalar(0,255,0), 4);
Core.line(img_matches, new Point(), new Point(), new Scalar(0,255,0), 4);
Core.line(img_matches, new Point(), new Point(), new Scalar(0,255,0), 4);
Core.line(img_matches, new Point(), new Point(), new Scalar(0,255,0), 4);
*/

//Sauvegarde du résultat
        //System.out.println(String.format("Writing %s", pathResult));
        //Imgcodecs.imwrite(pathResult, img_matches);

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
}