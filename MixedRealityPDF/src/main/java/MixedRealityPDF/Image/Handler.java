package MixedRealityPDF.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Handler {

  private int height;
  private int width;
  private int[] pixels;

  private static int TRESHOLD = 235*3;

  Handler(BufferedImage img){
    height = img.getHeight();
    width = img.getWidth();
    pixels = img.getRGB(0, 0, getWidth(), getHeight(),
            null, 0, getWidth());
  }

  public BufferedImage getBlackAndWhite(){
    BufferedImage out = new BufferedImage(getWidth(), getHeight(),
            BufferedImage.TYPE_INT_RGB);

    int[] lum = pixels;
    for(int i = 0; i <lum .length; i++)
      lum[i] = getLuminosity(lum[i]);

    for(int x = 0; x < getWidth(); x++)
      for(int y =0; y < getHeight(); y++) {

        if (lum[getXY(x, y)] <= TRESHOLD) {
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

  public int getLuminosity(int c){
    Color cc = new Color(c);
    return cc.getRed() + cc.getGreen() + cc.getBlue();
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
