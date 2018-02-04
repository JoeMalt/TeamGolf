package MixedRealityPDF.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

public final class Highlight extends Annotation {

  private final float width;
  private final float height;

  public Highlight(float x, float y, float width, float height, int pageNumber){
    super(x, y, pageNumber);
    this.width = width;
    this.height = height;
  }

  public Highlight(Point2D.Float p, float width, float height, int pageNumber){
    super(p.x, p.y, pageNumber);
    this.width = width;
    this.height = height;
  }

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }

  @Override
  public void applyAnnotation(PDPage doc) throws IOException{
    List<PDAnnotation> ann = doc.getAnnotations();
    PDAnnotationTextMarkup highlight;
    highlight = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
    PDColor yellow = new PDColor(new float[]{1,1,204/255F}, PDDeviceRGB.INSTANCE);
    highlight.setColor(yellow);
    ann.add(highlight);
  }
}





