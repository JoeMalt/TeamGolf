package MixedRealityPDF.AnnotationProcessor;

import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.Text;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ApplyAnnotationsTest {

  public static void main(String[] args) throws IOException {

    String path = "../Data/AnnotationTests/";
    String type = "pdf";
    String inName = "01-Wikipedia-PDF" + "." + type;
    String outName = "highlight" + "." + type;

    String imagePath = "Data/text/15.png";
    String imagePath2 = "Data/text/17.png";
    BufferedImage image = ImageIO.read(new File(imagePath));
    BufferedImage image2 = ImageIO.read(new File(imagePath2));
    //image.getScaledInstance();

    PDDocument in = null;
    PDDocument doc = null;
    try{
      in = PDDocument.load(new File(path + "/" + inName));
      doc = new PDDocument();
      PDPage page = in.getPage(0);
      doc.addPage(page);

      //PDPage page = new PDPage();

      PDRectangle mediabox = page.getMediaBox();
      float midY = mediabox.getHeight() / 2;
      float midX = mediabox.getWidth() / 2;
      float side = 72;

      Highlight hl = new Highlight(70, 680, 210, 16, 0);
      Highlight hl2 = new Highlight(130-16, 680-16, 70+32, 16, 0);
      UnderLine ul = new UnderLine(45, 680-16-16-2, 80, 0);
      Text txt = new Text(350, 680+16, image, 0);
      Text txt2 = new Text(259, 680-100, image2, 0);

      hl.applyAnnotation(doc);
      hl2.applyAnnotation(doc);
      ul.applyAnnotation(doc);
      txt.applyAnnotation(doc);
      txt2.applyAnnotation(doc);

      doc.save(new File(path, outName));
    } finally {
      if (doc != null){
        doc.close();
      }
    }


  }


}
