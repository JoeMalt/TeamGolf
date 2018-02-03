package MixedRealityPDF.Annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;

public final class NewLine extends Annotation {

  public NewLine(double x, double y){
    super(x,y);
  }

  public NewLine(Point2D.Double p){
    super(p.x, p.y);
  }

  @Override
  public void applyAnnotation(PDPage doc) {
    // TODO IMPLEMENT.
  }

}
