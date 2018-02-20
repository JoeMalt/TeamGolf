package MixedRealityPDF.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {

  private BufferedImage image;
  private BufferedImage imageBNW;

  public ImageProcessor(BufferedImage image){
    this.image = image;
  }

  public BufferedImage getBlackAndWhiteImage() {
    if(imageBNW == null)
      imageBNW = computeBlackAndWhite(image);
    return imageBNW;
  }

  public BufferedImage getImage() {
    return image;
  }

  public static BufferedImage computeBlackAndWhite(BufferedImage image){
    // true white, so that true luminosity of the image would be preserved.
    int threshold = 255*3;

    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage imageBNW = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_RGB);

    int[] blackAndWhite = image.getRGB(0, 0, width, height, null, 0, width);
    for(int i = 0; i <blackAndWhite .length; i++)
      blackAndWhite[i] = getLuminosity(blackAndWhite[i]);

    for(int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int xy = y * width + x;

        if (blackAndWhite[xy] <= threshold) {
          imageBNW.setRGB(x, y, Color.BLACK.getRGB());
        } else {
          imageBNW.setRGB(x, y, Color.WHITE.getRGB());
          blackAndWhite[xy] -= threshold;
        }

        if (blackAndWhite[xy] > 0) {
          int cl = blackAndWhite[xy];
          int cld = cl / 16;
          int clm = cl % 16;
          if (x + 1 < width && y < height) {
              blackAndWhite[xy + 1] += clm;
            } else if (x < width && y + 1 < height) {
              blackAndWhite[xy + width] += clm;
            }

          if (x+1 < width) // (x+1, y)
            blackAndWhite[xy + 1] += cld * 7;
          if (x+1 < width && y+1 < height) // (x+1, y+1)
            blackAndWhite[xy + 1 + width] += cld * 1;
          if (y+1 < height) // (x, y+1)
            blackAndWhite[xy + width] += cld * 5;
          if (x - 1 >= 0 && y + 1 < height) // (x-1, y+1)
            blackAndWhite[xy - 1 + width] += cld * 3;
        }
      }
    }
    return imageBNW;
  }

  public static int getLuminosity(int c){
    return getRed(c) + getGreen(c) + getBlue(c);
  }

  // Alligns this imageProcessor to reference.
  // Works best for documents.
  public void allignDocumentTo(ImageProcessor reference){

  }

  private static int getRed(int c){
    return (c & 0xFF0000) >> 4*4;
  }

  private static int getGreen(int c){
    return (c & 0xFF00) >> 2*4;
  }

  private static int getBlue(int c){
    return (c & 0xFF);
  }

}
