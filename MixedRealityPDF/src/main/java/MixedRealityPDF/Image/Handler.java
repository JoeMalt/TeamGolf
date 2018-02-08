package MixedRealityPDF.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Handler {

  private int height;
  private int width;
  private Color[] pixels;

  private static int TRESHOLD = 128*3;

  Handler(BufferedImage img){
    height = img.getHeight();
    width = img.getWidth();
    int[] pixelInt = img.getRGB(0, 0, getWidth(), getHeight(),
            null, 0, getWidth());
    pixels = Arrays.stream(pixelInt)
                   .mapToObj(i -> new Color(i))
                   .toArray(size -> new Color[size]);
  }

  public BufferedImage getBlackAndWhite(){
    BufferedImage out = new BufferedImage(getWidth(), getHeight(),
            BufferedImage.TYPE_INT_RGB);

    int i = 0, j = 0;

    int[] lum = Arrays.stream(pixels)
                      .mapToInt(c -> getLuminosity(c)).toArray();

    for(int x = 0; x < getWidth(); x++)
      for(int y =0; y < getHeight(); y++) {

        if (lum[getXY(x, y)] < TRESHOLD) {
          out.setRGB(x, y, Color.BLACK.getRGB());
        } else {
          out.setRGB(x, y, Color.WHITE.getRGB());
          lum[getXY(x, y)] -= TRESHOLD;
        }

        if (lum[getXY(x, y)] > 0) {
          int cl = lum[getXY(x, y)];
          if(inBounds(x+1, y  )) lum[getXY(x+1, y  )] += cl * 7 / 16;
          if(inBounds(x+1, y+1)) lum[getXY(x+1, y+1)] += cl * 1 / 16;
          if(inBounds(x  , y+1)) lum[getXY(x  , y+1)] += cl * 5 / 16;
          if(inBounds(x-1, y+1)) lum[getXY(x-1, y+1)] += cl * 3 / 16;
        }
      }

    return out;
  }

  public int getLuminosity(Color c){
    return c.getBlue() + c.getGreen() + c.getRed();
  }

  private boolean inBounds(int x, int y){
    return x < width && y < height;
  }

  private int getXY(int x, int y){
    return y * width + x;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }
}
