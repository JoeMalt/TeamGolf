package MixedRealityPDF.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

public final class UnderLine extends Annotation{

  private final float length;

  public UnderLine(float x, float y, float length, int pageNumber){
    super(x,y,pageNumber);
    this.length = length;
  }

  public UnderLine(Point2D.Float p, float length, int pageNumber){
    super(p.x, p.y, pageNumber);
    this.length = length;
  }

  public float getLength(){
    return length;
  }

  // Such annotation will only be visible in a document.
  // It will not be visible if we create an image from PDPage.
  @Override
  public void applyAnnotation(PDPage doc) throws IOException{
    // TODO fix Might not work yet.
    List<PDAnnotation> ann = doc.getAnnotations();
    PDAnnotationTextMarkup underline;
    underline = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_UNDERLINE);
    underline.setRectangle(new PDRectangle(getX(), getY(), getLength(), 100f));
    //underline.
    PDColor yellow = new PDColor(new float[]{1,1,204/255F}, PDDeviceRGB.INSTANCE);
    underline.setColor(yellow);
    ann.add(underline);
  }
}
