package MixedRealityPDF.Annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;

public abstract class Annotation {

  private final double x;
  private final double y;

  // (x, y) == (lower-left corner, lower-left corner).
  public Annotation(double x, double y){
    this.x = x;
    this.y = y;
  }

  public Annotation(Point2D.Double p){
    this.x = p.x;
    this.y = p.y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public Point2D.Double getLocation(){
    return new Point2D.Double(x, y);
  }


  public abstract void applyAnnotation(PDPage page);

}
