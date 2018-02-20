package MixedRealityPDF.AnnotationProcessor;

import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.io.IOException;

public class ApplyAnnotationsTest {

  public static void main(String[] args) throws IOException {

    String path = "../Data/AnnotationTests";
    String type = "pdf";
    String inName = "01-Wikipedia-PDF" + "." + type;
    String outName = "highlight" + "." + type;

    PDDocument in = null;
    PDDocument doc = null;
    try{
      in = PDDocument.load(new File(path + "/" + inName));
      doc = new PDDocument();
      PDPage page = in.getPage(0);
      //PDPage page = new PDPage();

      PDRectangle mediabox = page.getMediaBox();
      float midY = mediabox.getHeight() / 2;
      float midX = mediabox.getWidth() / 2;
      float side = 72;

      Highlight hl = new Highlight(midX, midY, side, side, 0);
      UnderLine ul = new UnderLine(midX-20, midY+50, 100, 0);

      hl.applyAnnotation(page);
      ul.applyAnnotation(page);

      doc.addPage(page);
      doc.save(new File(path, outName));
    } finally {
      if (doc != null){
        doc.close();
      }
    }


  }


}
