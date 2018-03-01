package MixedRealityPDF.AnnotationProcessor.Annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.IOException;

public final class Text extends Annotation {

  private final BufferedImage image;

  public Text(float x, float y, BufferedImage image, int pageNumber){
    super(x, y, pageNumber);
    this.image = image;
  }

  public BufferedImage getImage() {
    return image;
  }

  @Override
  public void applyAnnotation(PDDocument doc) throws IOException{
    PDPage page = doc.getPage(getPageNumber());
    PDImageXObject image = JPEGFactory.createFromImage(doc, getImage());
    PDPageContentStream contents;
    contents = new PDPageContentStream(doc, page, true, true, true);
    contents.drawImage(image, getX(), getY());
    contents.close();
  }
}





