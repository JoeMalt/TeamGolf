package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.ImageProcessor.ImgHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FeatureExtractorV2 implements IFeatureExtractor {

  public FeatureExtractorV2(){}

  @Override
  public Collection<String> getFeatureNames() {
    return Arrays.asList("height", "color", "cover");
  }

  @Override
  public List<String> extractFeatures(BufferedImage img) {
    if(img == null){
      return null;
    }
    BufferedImage blackNWhite = ImgHelper.computeBlackAndWhite(img);
    ArrayList<String> vector = new ArrayList<>();
    float invArea = 1 / (float)(img.getHeight() * img.getWidth());

    float height = height(blackNWhite) * invArea;
    float color = color(img) * invArea;
    float cover = countBlack(blackNWhite) * invArea;
    vector.add(Float.toString(height));
    vector.add(Float.toString(color));
    vector.add(Float.toString(cover));
    return vector;
  }

  public int height(BufferedImage blackAndWhite){
    int maxH = 0;
    for(int x=0; x<blackAndWhite.getWidth(); x++){

      int lo = 0;
      int hi = 0;
      for(int y=0; y<blackAndWhite.getHeight(); y++)
        if(blackAndWhite.getRGB(x, y) == Color.BLACK.getRGB()){
          lo = y;
          break;
        }

      for(int y=blackAndWhite.getHeight()-1; y>=0; y--)
        if(blackAndWhite.getRGB(x, y) == Color.BLACK.getRGB()){
          hi = y;
          break;
        }

      maxH = Math.max(maxH, hi-lo);
    }
    return maxH;
  }

  public int color(BufferedImage image){
    int count = 0;
    for(int x=0; x<image.getWidth(); x++){
      for(int y=0; y<image.getHeight(); y++){
        if(ImgHelper.isColor(image.getRGB(x, y)))
          count++;
      }
    }
    return count;
  }

  public int countBlack(BufferedImage blackAndWhite){
    int count = 0;
    for(int x=0; x<blackAndWhite.getWidth(); x++){
      for(int y=0; y<blackAndWhite.getHeight(); y++){
        if(blackAndWhite.getRGB(x, y) == Color.BLACK.getRGB())
          count++;
      }
    }
    return count;
  }
}
