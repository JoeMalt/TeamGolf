package MixedRealityPDF.AnnotationProcessor.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.io.IOException;

public abstract class Annotation {

  protected static PDColor BLACK = new PDColor(new float[]{0,0,0}, PDDeviceRGB.INSTANCE);
  protected static PDColor YELLOW =new PDColor(new float[]{247/255f,255/255f,0/255f}, PDDeviceRGB.INSTANCE);

  private final float x;
  private final float y;
  private final int pageNumber;

  // (x, y) == (lower-left corner, lower-left corner).
  public Annotation(float x, float y, int pageNumber){
    this.x = x;
    this.y = y;
    this.pageNumber = pageNumber;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public abstract void applyAnnotation(PDPage page) throws IOException;

  protected float[] getQuads(PDRectangle rectangle){
    float[] quads = new float[8];
    // x3, y3 --- x4, y4
    //    |          |
    // x1, y1 --- x2, y2
    // x1, y1
    quads[0] = rectangle.getLowerLeftX();
    quads[1] = rectangle.getLowerLeftY();
    // x2, y2
    quads[2] = rectangle.getUpperRightX();
    quads[3] = rectangle.getLowerLeftY();
    // x3, y3
    quads[4] = rectangle.getLowerLeftX();
    quads[5] = rectangle.getUpperRightY();
    // x4, y4
    quads[6] = rectangle.getUpperRightX();
    quads[7] = rectangle.getUpperRightY();
    return quads;
  }

}
