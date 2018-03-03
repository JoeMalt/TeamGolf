package MixedRealityPDF;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.Text;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.AnnotationProcessor.DBSCANClusterDetector;
import MixedRealityPDF.AnnotationProcessor.IClusterDetector;
import MixedRealityPDF.AnnotationProcessor.Identification.AnnotationIdentifier;
import MixedRealityPDF.AnnotationProcessor.Identification.BasicClassifier;
import MixedRealityPDF.AnnotationProcessor.Identification.EverythingIsText;
import MixedRealityPDF.AnnotationProcessor.Identification.IAnnotationIdentifier;
import MixedRealityPDF.ImageProcessor.Alignment.ImageWrapper;
import MixedRealityPDF.ImageProcessor.ColourRemoval.ColorExtractor;
import MixedRealityPDF.ImageProcessor.IAlignment;
import MixedRealityPDF.ImageProcessor.IDifferenceMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PDFPenAndPaper {

  Collection<Annotation> annotations = new ArrayList<>();

  private static IDifferenceMap imageDiff = new ColorExtractor();
  private static IClusterDetector clusterDetector = new DBSCANClusterDetector();
  //private static IAnnotationIdentifier annId = new AnnotationIdentifier();
  //private static IAnnotationIdentifier annId = null;


  //private static IAnnotationIdentifier annId = new EverythingIsText();

  private static IAnnotationIdentifier annId = new BasicClassifier();

  private static IAlignment alignment = new ImageWrapper();

  public PDFPenAndPaper(File pdfOriginalFile, File pdfScannedFile,
                        String outputFilePath) throws IOException{
    try(
            PDDocument original = PDDocument.load(pdfOriginalFile);
            PDDocument scanned  = PDDocument.load(pdfScannedFile);
            PDDocument copy     = copy(original);
    ){
      init(original, scanned);
      applyAnnotations(copy);
      copy.save(new File(outputFilePath));
    }
  }

  private PDDocument copy(PDDocument doc) throws IOException{
    PDDocument copy = new PDDocument();
    for(int i=0; i<doc.getNumberOfPages(); i++)
      copy.importPage(doc.getPage(i));
    return copy;
  }


  public PDFPenAndPaper(BufferedImage scannedImage, BufferedImage pdfPageImage)
          throws IOException {
    initSinglePage(pdfPageImage, scannedImage, 0);
  }

  private void initSinglePage(BufferedImage pdf, BufferedImage scan, int page){
    assert(annotations != null);
    try{
      File out = new File("Data/PDF.png");
      ImageIO.write(pdf, "png", out);
    } catch (Exception e){
      e.printStackTrace();
    }
    try{
      File out = new File("Data/scan.png");
      ImageIO.write(scan, "png", out);
    } catch (Exception e){
      e.printStackTrace();
    }
    scan = alignment.align(pdf, scan);
    try{
      File out = new File("Data/alignment.png");
      ImageIO.write(scan, "png", out);
    } catch (Exception e){
      e.printStackTrace();
    }
    scan = imageDiff.findDifference(pdf, scan);
    try{
      File out = new File("Data/diff.png");
      ImageIO.write(scan, "png", out);
    } catch (Exception e){
      e.printStackTrace();
    }

    Collection<AnnotationBoundingBox> clusterPoints;
    clusterPoints = clusterDetector.cluster(scan);
    annotations = annId.identifyAnnotations(scan, clusterPoints, page);
  }

  private void init(PDDocument original, PDDocument scan) throws  IOException{
    assert(original.getNumberOfPages() == scan.getNumberOfPages());
    PDFRenderer originalRenderer = new PDFRenderer(original);
    PDFRenderer scanRenderer = new PDFRenderer(scan);

    for(int i=0; i<original.getNumberOfPages(); i++){
      BufferedImage pdfPage;
      pdfPage = originalRenderer.renderImage(i, 1f, ImageType.RGB);
      BufferedImage scanImg;
      scanImg = scanRenderer.renderImage(i, 1f, ImageType.ARGB);
      initSinglePage(pdfPage, scanImg, i);
    }
  }

  public void applyAnnotations(PDDocument doc) throws IOException {
    for(Annotation ann : annotations){
      if(ann.getPageNumber() < doc.getNumberOfPages())
        ann.applyAnnotation(doc);
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

  public List<Text> getText() {
    return getAnnotations(Text.class);
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

  public static void
  setDefaultDifferenceMap(IDifferenceMap defaultDifferenceMap) {
    PDFPenAndPaper.imageDiff = defaultDifferenceMap;
  }

  public static IClusterDetector getDefaultClusterDetector() {
    return clusterDetector;
  }

  public static void
  setDefaultClusterDetector(IClusterDetector defaultClusterDetector) {
    PDFPenAndPaper.clusterDetector = defaultClusterDetector;
  }

  public static IAnnotationIdentifier getDefaultAnnotationIdentifier() {
    return annId;
  }

  public static void setDefaultAnnotationIdentifier(
          IAnnotationIdentifier defaultAnnotationIdentifier) {
    PDFPenAndPaper.annId = defaultAnnotationIdentifier;
  }
}
