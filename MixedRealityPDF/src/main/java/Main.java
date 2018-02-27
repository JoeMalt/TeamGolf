import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.NewLine;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.AnnotationProcessor.Identification.AnnotationIdentifier;
import MixedRealityPDF.PDFPenAndPaper;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Main {


  public static void main(String[] args) throws IOException{
    String imageFilepath = "";
    String pdfFilePath = "";
    PDFPenAndPaper document = new PDFPenAndPaper(imageFilepath, pdfFilePath);

    // full pdf image of difference (just annotations)
    Image fullImage = null;
    // list of bounding boxes from segmentation stage
    List<AnnotationBoundingBox> boundingBoxes = null;
    AnnotationIdentifier identifier = new AnnotationIdentifier();
    identifier.createTreeTrainingFile();

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
