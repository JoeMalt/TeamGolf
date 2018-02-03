package MixedRealityPDF.Annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;

public final class Highligh extends Annotation {

  private final double width;
  private final double height;

  public Highligh(double x, double y, double width, double height){
    super(x, y);
    this.width = width;
    this.height = height;
  }

  public Highligh(Point2D.Double p, double width, double height){
    super(p.x, p.y);
    this.width = width;
    this.height = height;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  @Override
  public void applyAnnotation(PDPage doc) {
    // TODO IMPLEMENT.
  }
}
