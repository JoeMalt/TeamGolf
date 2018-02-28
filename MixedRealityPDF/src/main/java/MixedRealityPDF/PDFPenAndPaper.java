package MixedRealityPDF;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.AnnotationProcessor.IClusterDetector;
import MixedRealityPDF.AnnotationProcessor.Identification.IAnnotationIdentifier;
import MixedRealityPDF.ImageProcessor.IDifferenceMap;
import MixedRealityPDF.ImageProcessor.ColourRemoval.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PDFPenAndPaper {

  private String imageFilepath;
  private String pdfFilePath;

  Collection<Annotation> annotations;

  // TODO Initialize static variables.
  private static IDifferenceMap defaultDifferenceMap;
  private static IClusterDetector defaultCusterDetector;
  private static IAnnotationIdentifier defaultAnnotationIdentifier;

  private IDifferenceMap differenceMap = defaultDifferenceMap;
  private IClusterDetector clusterDetector = defaultCusterDetector;
  private IAnnotationIdentifier annotationIdentifier = defaultAnnotationIdentifier;


  // To use the default non-path variables set them too null.
  public PDFPenAndPaper(String imageFilepath, String pdfFilepath,
                        IDifferenceMap differenceMap,
                        IClusterDetector clusterDetector,
                        IAnnotationIdentifier annotationIdentifier) {

    if(differenceMap != null)
      this.differenceMap = differenceMap;
    if(clusterDetector != null)
      this.clusterDetector = clusterDetector;
    if(annotationIdentifier != null)
      this.annotationIdentifier = annotationIdentifier;
  }

  public PDFPenAndPaper(String imageFilepath, String pdfFilepath)
          throws IOException {
    this.imageFilepath = imageFilepath;
    this.pdfFilePath = pdfFilepath;

    Image modifiedPDFImage = ImageIO.read(new File(imageFilepath));
    Image originalPDFImage = new PDFRenderer(pdfFilePath).getImage();
//    BufferedImage differenceMapImage = (BufferedImage) differenceMap.findDifference(originalPDFImage,
//            modifiedPDFImage);
//    Collection<AnnotationBoundingBox> clusterPoints = clusterDetector.cluster((BufferedImage) differenceMapImage);
//    annotations = annotationIdentifier.identifyAnnotations(differenceMapImage, clusterPoints, 0);
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

  public String getImageFilepath() {
    return imageFilepath;
  }

  public String getPdfFilePath() {
    return pdfFilePath;
  }

  public static IDifferenceMap getDefaultDifferenceMap() {
    return defaultDifferenceMap;
  }

  public static void setDefaultDifferenceMap(IDifferenceMap defaultDifferenceMap) {
    PDFPenAndPaper.defaultDifferenceMap = defaultDifferenceMap;
  }

  public static IClusterDetector getDefaultCusterDetector() {
    return defaultCusterDetector;
  }

  public static void setDefaultCusterDetector(IClusterDetector defaultCusterDetector) {
    PDFPenAndPaper.defaultCusterDetector = defaultCusterDetector;
  }

  public static IAnnotationIdentifier getDefaultAnnotationIdentifier() {
    return defaultAnnotationIdentifier;
  }

  public static void setDefaultAnnotationIdentifier(IAnnotationIdentifier defaultAnnotationIdentifier) {
    PDFPenAndPaper.defaultAnnotationIdentifier = defaultAnnotationIdentifier;
  }
}
