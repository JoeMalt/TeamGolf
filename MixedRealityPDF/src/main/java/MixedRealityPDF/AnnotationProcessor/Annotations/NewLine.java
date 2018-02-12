package MixedRealityPDF.AnnotationProcessor.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;

// Might not be implemented.
public final class NewLine extends Annotation {

  public NewLine(float x, float y, int pageNumber){
    super(x,y,pageNumber);
  }

  public NewLine(Point2D.Float p, int pageNumber){
    super(p.x, p.y, pageNumber);
  }

  @Override
  public void applyAnnotation(PDPage doc) {
    // TODO IMPLEMENT MAYBE.
  }

}
