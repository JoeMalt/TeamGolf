package MixedRealityPDF;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.AnnotationProcessor.DBSCANClusterDetector;
import MixedRealityPDF.AnnotationProcessor.IClusterDetector;
import MixedRealityPDF.AnnotationProcessor.Identification.IAnnotationIdentifier;
import MixedRealityPDF.ImageProcessor.Alignment.ImageWrapper;
import MixedRealityPDF.ImageProcessor.IAlignment;
import MixedRealityPDF.ImageProcessor.IDifferenceMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PDFPenAndPaper {

  Collection<Annotation> annotations = new ArrayList<>();

  // TODO(everyone) Initialize static variables
  private static IDifferenceMap imageDiff = new ImageWrapper();
  private static IClusterDetector clusterDetector = new DBSCANClusterDetector();
  private static IAnnotationIdentifier annId;
  private static IAlignment alignment = new ImageWrapper();

  public PDFPenAndPaper(File pdfOriginalFile, File pdfScannedFile,
                        File OutputFile) throws IOException{
    try(
            PDDocument original = PDDocument.load(pdfOriginalFile);
            PDDocument scanned  = PDDocument.load(pdfScannedFile);
    ){
      init(original, scanned);
      applyAnnotations(OutputFile);
    }
  }


  public PDFPenAndPaper(BufferedImage scannedImage, BufferedImage pdfPageImage)
          throws IOException {
    initSinglePage(pdfPageImage, scannedImage);
  }

  private void initSinglePage(BufferedImage pdfPage, BufferedImage scan){
    assert(annotations != null);
    scan = alignment.align(pdfPage, scan);
    scan = imageDiff.findDifference(pdfPage, scan);
    Collection<AnnotationBoundingBox> clusterPoints;
    clusterPoints = clusterDetector.cluster(scan);
    annotations = annId.identifyAnnotations(scan, clusterPoints);
  }

  private void init(PDDocument original, PDDocument scan) throws  IOException{
    assert(original.getNumberOfPages() == scan.getNumberOfPages());
    PDFRenderer originalRenderer = new PDFRenderer(original);
    PDFRenderer scanRenderer = new PDFRenderer(scan);

    for(int i=0; i<original.getNumberOfPages(); i++){
      BufferedImage pdfPage = originalRenderer.renderImage(i);
      BufferedImage scanImg = scanRenderer.renderImage(i);
      initSinglePage(pdfPage, scanImg);
    }
  }

  public void applyAnnotations(File pdfFile) throws IOException {
    try(PDDocument doc = PDDocument.load(pdfFile)){
      int pageNumber = doc.getNumberOfPages();
      for(Annotation ann : annotations){
        if(ann.getPageNumber() < pageNumber)
          ann.applyAnnotation(doc.getPage(ann.getPageNumber()));
      }
    }
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
