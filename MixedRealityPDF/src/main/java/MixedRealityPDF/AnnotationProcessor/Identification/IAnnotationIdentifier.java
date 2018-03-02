package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

// Given an image and a set of coordinates classify the Annotations.
public interface IAnnotationIdentifier {
  Collection<Annotation> identifyAnnotations(
          BufferedImage fullImage,
          Collection<AnnotationBoundingBox> points, int pageNumber);

  /**
   * Crops out annotations out of the full image of the PDF difference
   * according to bounding boxes passed from segmentation stage. **/
  static ArrayList<BufferedImage> cropAnnotations(
          BufferedImage fullImage, Collection<AnnotationBoundingBox> points){
    BufferedImage image;
    ClusteringPoint topLeft;
    int width;
    int height;
    ArrayList<BufferedImage> annotationImages = new ArrayList<>();
    for(AnnotationBoundingBox boundingBox : points) {
      topLeft = boundingBox.getTopLeft();
      width = boundingBox.getTopRight().getX() - topLeft.getX();
      height = boundingBox.getBottomLeft().getY() - topLeft.getY();
      BufferedImage subImage = fullImage.getSubimage(
              topLeft.getX(), topLeft.getY(), width, height);

      // get a copy of image because .getSubimage operates on the original
      image = new BufferedImage(subImage.getWidth(), subImage.getHeight(),
              BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.createGraphics();
      g.drawImage(subImage, 0, 0, null);

      annotationImages.add(image);
    }
    return annotationImages;
  }
}
