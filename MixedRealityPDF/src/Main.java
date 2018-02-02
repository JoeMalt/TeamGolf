import MixedRealityPDF.Annotation;
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

    List<Annotation> highLightList;
    document.getHighlights();

    List<Annotation> underlineList;
    document.getUnderlines();

    List<Annotation> newLineList;
    document.getNewLineAnnotations();



  }

}
