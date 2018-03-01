package MixedRealityPDF.AnnotationProcessor.Annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

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

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }

  @Override
  public void applyAnnotation(PDDocument doc) throws IOException{
    PDPage page = doc.getPage(getPageNumber());
    List<PDAnnotation> ann = page.getAnnotations();
    PDAnnotationTextMarkup highlight;
    highlight = new PDAnnotationTextMarkup(
            PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
    highlight.setColor(super.YELLOW);

    PDRectangle position = new PDRectangle();
    position.setLowerLeftX(getX());
    position.setLowerLeftY(getY());
    position.setUpperRightX(getX()+getWidth());
    position.setUpperRightY(getY()+getHeight());
    float[] quads = super.getQuads(position);
    // PDAnnotationTextMarkup needs both Rectangle and QuadPoints.
    highlight.setRectangle(position);
    highlight.setQuadPoints(quads);

    ann.add(highlight);
  }
}





