package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Text;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;
import MixedRealityPDF.AnnotationProcessor.Identification.IAnnotationIdentifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class EverythingIsText implements IAnnotationIdentifier {

  public EverythingIsText(){}

  @Override
  public Collection<Annotation> identifyAnnotations(BufferedImage fullImage, Collection<AnnotationBoundingBox> points, int pageNumber) {
    ArrayList<BufferedImage> annImages = cropAnnotations(fullImage, points);

    System.out.println("points: " + points.size());

    ArrayList<Annotation> text = new ArrayList<>(annImages.size());
    Iterator<AnnotationBoundingBox> itp =  points.iterator();
    Iterator<BufferedImage> iti =  annImages.iterator();

    while(itp.hasNext() && iti.hasNext()){
      AnnotationBoundingBox box = itp.next();
      BufferedImage im = iti.next();

      int x = box.getBottomLeft().getX();
      int y = box.getBottomLeft().getY();
      y = Annotation.ImageYToPDFY(y, fullImage.getHeight());
      Text txt = new Text(x, y, im, pageNumber);
      text.add(txt);
    }

    return text;
  }

  private static ArrayList<BufferedImage> cropAnnotations(
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
