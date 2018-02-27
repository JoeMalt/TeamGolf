import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.AnnotationProcessor.Identification.AnnotationIdentifier;
import MixedRealityPDF.DocumentProcessor.Document;
import MixedRealityPDF.PDFPenAndPaper;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {


  public static void main(String[] args) throws IOException{
    String imageFilepath = "";
    String pdfFilePath = "";

    BufferedImage scanImage = ImageIO.read(new File(imageFilepath));
    PDDocument pdf = PDDocument.load(new File(pdfFilePath));
    Document doc = new Document(pdf);

    PDFPenAndPaper document = new PDFPenAndPaper(scanImage, doc.getPageImage(0));

    // full pdf image of difference (just annotations)
    Image fullImage = null;
    // list of bounding boxes from segmentation stage
    List<AnnotationBoundingBox> boundingBoxes = null;
    AnnotationIdentifier identifier = new AnnotationIdentifier(fullImage, boundingBoxes);
    identifier.createTreeTrainingFile();

    List<Annotation> annotationList;
    document.getAnnotations();

    List<Highlight> highLightList;
    document.getHighlights();

    List<UnderLine> underlineList;
    document.getUnderlines();
  }
}
