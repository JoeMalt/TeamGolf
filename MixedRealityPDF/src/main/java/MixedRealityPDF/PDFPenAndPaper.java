package MixedRealityPDF;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.NewLine;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.AnnotationProcessor.IClusterDetector;
import MixedRealityPDF.AnnotationProcessor.Identification.IAnnotationIdentifier;
import MixedRealityPDF.DocumentProcessor.IDifferenceMap;
import MixedRealityPDF.ImageProcessor.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PDFPenAndPaper {

  Collection<Annotation> annotations;

  // TODO(everyone) Initialize static variables.
  private static IDifferenceMap imageDiff;
  private static IClusterDetector clusterDetector;
  private static IAnnotationIdentifier annId;


  public PDFPenAndPaper(BufferedImage scannedImage, BufferedImage pdfPageImage)
          throws IOException {
    init(scannedImage, pdfPageImage);
  }

  private void init(BufferedImage scan, BufferedImage pdfPage){
    BufferedImage difference = ImageProcessor.getDifference(pdfPage, scan);
    Collection<AnnotationBoundingBox> clusterPoints;
    clusterPoints = clusterDetector.cluster(difference);
    annotations = annId.identifyAnnotations(difference, clusterPoints);
  }

  public List<Annotation> getAnnotations() {
    return new ArrayList<>(annotations);
  }

  public List<Highlight> getHighlights() {
    return getAnnotations(Highlight.class);
  }

  public List<UnderLine> getUnderlines() {
    return getAnnotations(UnderLine.class);
  }

  public List<NewLine> getNewLineAnnotations() {
    return getAnnotations(NewLine.class);
  }

  public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
    int count = 0;
    for (Annotation ann : annotations) {
      if (ann.getClass().equals(type))
        count++;
    }

    List<T> filteredAnn = new ArrayList<>(count);
    for (Annotation ann : annotations) {
      if (ann.getClass().equals(type))
        filteredAnn.add((T) ann);
    }

    return filteredAnn;
  }

  public static IDifferenceMap getDefaultDifferenceMap() {
    return imageDiff;
  }

  public static void setDefaultDifferenceMap(IDifferenceMap defaultDifferenceMap) {
    PDFPenAndPaper.imageDiff = defaultDifferenceMap;
  }

  public static IClusterDetector getDefaultClusterDetector() {
    return clusterDetector;
  }

  public static void setDefaultClusterDetector(IClusterDetector defaultClusterDetector) {
    PDFPenAndPaper.clusterDetector = defaultClusterDetector;
  }

  public static IAnnotationIdentifier getDefaultAnnotationIdentifier() {
    return annId;
  }

  public static void setDefaultAnnotationIdentifier(IAnnotationIdentifier defaultAnnotationIdentifier) {
    PDFPenAndPaper.annId = defaultAnnotationIdentifier;
  }
}
