import MixedRealityPDF.Annotations.Annotation;
import MixedRealityPDF.Annotations.Highlight;
import MixedRealityPDF.Annotations.NewLine;
import MixedRealityPDF.Annotations.UnderLine;
import MixedRealityPDF.PDFPenAndPaper;

import java.io.IOException;
import java.util.List;

public class Main {


  public static void main(String[] args) throws IOException{
    String imageFilepath = "";
    String pdfFilePath = "";
    PDFPenAndPaper document = new PDFPenAndPaper(imageFilepath, pdfFilePath);

    List<Annotation> annotationList;
    document.getAnnotations();

    List<Highlight> highLightList;
    document.getHighlights();

    List<UnderLine> underlineList;
    document.getUnderlines();

    List<NewLine> newLineList;
    document.getNewLineAnnotations();
  }
}
