package MixedRealityPDF;

import MixedRealityPDF.Annotations.Annotation;
import MixedRealityPDF.Annotations.Highlight;
import MixedRealityPDF.Annotations.NewLine;
import MixedRealityPDF.Annotations.UnderLine;
import MixedRealityPDF.Factory.AnnotationIdentifier;
import MixedRealityPDF.Factory.ClusterDetector;
import MixedRealityPDF.Factory.DifferenceMap;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class PDFPenAndPaper {

  private String imageFilepath;
  private String pdfFilePath;

  Collection<Annotation> annotations;

  // TODO Initialize static variables.
  private static DifferenceMap defaultDifferenceMap;
  private static ClusterDetector defaultCusterDetector;
  private static AnnotationIdentifier defaultAnnotationIdentifier;

  private DifferenceMap differenceMap = defaultDifferenceMap;
  private ClusterDetector clusterDetector = defaultCusterDetector;
  private AnnotationIdentifier annotationIdentifier = defaultAnnotationIdentifier;


  // To use the default non-path variables set the too null.
  public PDFPenAndPaper(String imageFilepath, String pdfFilepath,
                        DifferenceMap differenceMap,
                        ClusterDetector clusterDetector,
                        AnnotationIdentifier annotationIdentifier) {

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
    Image differenceMapImage = differenceMap.findDifference(pdfFilepath,
            modifiedPDFImage);
    Collection<Point2D.Double> clusterPoints = clusterDetector.cluster(
            differenceMapImage);
    annotations = annotationIdentifier.identifyAnnotations(differenceMapImage,
            clusterPoints);
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

  public String getImageFilepath() {
    return imageFilepath;
  }

  public String getPdfFilePath() {
    return pdfFilePath;
  }

  public static DifferenceMap getDefaultDifferenceMap() {
    return defaultDifferenceMap;
  }

  public static void setDefaultDifferenceMap(DifferenceMap defaultDifferenceMap) {
    PDFPenAndPaper.defaultDifferenceMap = defaultDifferenceMap;
  }

  public static ClusterDetector getDefaultCusterDetector() {
    return defaultCusterDetector;
  }

  public static void setDefaultCusterDetector(ClusterDetector defaultCusterDetector) {
    PDFPenAndPaper.defaultCusterDetector = defaultCusterDetector;
  }

  public static AnnotationIdentifier getDefaultAnnotationIdentifier() {
    return defaultAnnotationIdentifier;
  }

  public static void setDefaultAnnotationIdentifier(AnnotationIdentifier defaultAnnotationIdentifier) {
    PDFPenAndPaper.defaultAnnotationIdentifier = defaultAnnotationIdentifier;
  }
}
