package MixedRealityPDF.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;

public final class UnderLine extends Annotation{

  public UnderLine(double x, double y){
    super(x,y);
  }

  public UnderLine(Point2D.Double p){
    super(p.x, p.y);
  }

  @Override
  public void applyAnnotation(PDPage doc) {
    // TODO IMPLEMENT.
  }
}
