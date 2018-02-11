import main.java.MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import main.java.MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import main.java.MixedRealityPDF.AnnotationProcessor.Annotations.NewLine;
import main.java.MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import main.java.MixedRealityPDF.PDFPenAndPaper;

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
