package MixedRealityPDF.AnnotationProcessor.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

public final class UnderLine extends Annotation{

  private final float length;
  private float thickness = 10;

  public UnderLine(float x, float y, float length, int pageNumber){
    super(x,y,pageNumber);
    this.length = length;
  }

  public UnderLine(float x, float y, float length, float thickness,
                   int pageNumber){
    super(x,y,pageNumber);
    this.length = length;
  }

  public UnderLine(Point2D.Float p, float length, int pageNumber){
    super(p.x, p.y, pageNumber);
    this.length = length;
  }

  public UnderLine(Point2D.Float p, float length, float thickness,
                   int pageNumber){
    super(p.x, p.y, pageNumber);
    this.length = length;
  }

  public float getLength(){
    return length;
  }

  private float getThickness() {
    return thickness;
  }

  @Override
  public void applyAnnotation(PDPage doc) throws IOException{
    List<PDAnnotation> ann = doc.getAnnotations();
    PDAnnotationTextMarkup underline = new PDAnnotationTextMarkup(
            PDAnnotationTextMarkup.SUB_TYPE_UNDERLINE);
    underline.setRectangle(new PDRectangle(getX(), getY(), getLength(), 100f));
    underline.setColor(super.BLACK);

    PDRectangle position = new PDRectangle();
    position.setLowerLeftX(getX());
    position.setLowerLeftY(getY());
    position.setUpperRightX(getX()+getLength());
    position.setUpperRightY(getY()+getThickness());
    underline.setRectangle(position);
    float[] quads = super.getQuads(position);
    underline.setQuadPoints(quads);

    ann.add(underline);
  }
}
