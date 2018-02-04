package MixedRealityPDF.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;
import java.io.IOException;

public abstract class Annotation {

  private final float x;
  private final float y;
  private final int pageNumber;

  // (x, y) == (lower-left corner, lower-left corner).
  public Annotation(float x, float y, int pageNumber){
    this.x = x;
    this.y = y;
    this.pageNumber = pageNumber;
  }

  public Annotation(Point2D.Float p, int pageNumber){
    this.x = p.x;
    this.y = p.y;
    this.pageNumber = pageNumber;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public Point2D.Float getLocation(){
    return new Point2D.Float(x, y);
  }

  public abstract void applyAnnotation(PDPage page) throws IOException;

}
