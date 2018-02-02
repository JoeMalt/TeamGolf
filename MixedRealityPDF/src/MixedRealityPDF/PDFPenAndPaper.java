package MixedRealityPDF;

import MixedRealityPDF.Factory.AnnotationIdentifier;
import MixedRealityPDF.Factory.ClusterDetector;
import MixedRealityPDF.Factory.DifferenceMap;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.geom.Point2D;
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
  private static DifferenceMap differenceMap;
  private static ClusterDetector clusterDetector;
  private static AnnotationIdentifier annotationIdentifier;

  public PDFPenAndPaper(String imageFilepath, String pdfFilepath)
          throws IOException{
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

  public List<Annotation> getAnnotations(){
    return new ArrayList<>(annotations);
  }

  public List<Annotation> getHighlights(){
    int count = 0;
    for(Annotation ann : annotations)
      if(ann.getType().equals(Annotation.Type.HIGHLIGHT))
        count++;

    ArrayList<Annotation> highlights = new ArrayList<>(count);
    for(Annotation ann : annotations)
      if(ann.getType().equals(Annotation.Type.HIGHLIGHT))
        highlights.add(ann);

    return highlights;
  }

  public List<Annotation> getUnderlines(){
    int count = 0;
    for(Annotation ann : annotations)
      if(ann.getType().equals(Annotation.Type.UNDERLINE))
        count++;

    ArrayList<Annotation> underlines = new ArrayList<>(count);
    for(Annotation ann : annotations)
      if(ann.getType().equals(Annotation.Type.UNDERLINE))
        underlines.add(ann);

    return underlines;
  }

  public List<Annotation> getNewLineAnnotations(){
    int count = 0;
    for(Annotation ann : annotations)
      if(ann.getType().equals(Annotation.Type.NEW_LINE))
        count++;

    ArrayList<Annotation> newLines = new ArrayList<>(count);
    for(Annotation ann : annotations)
      if(ann.getType().equals(Annotation.Type.NEW_LINE))
        newLines.add(ann);

    return newLines;
  }

  public String getImageFilepath() {
    return imageFilepath;
  }

  public String getPdfFilePath() {
    return pdfFilePath;
  }
}
