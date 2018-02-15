package MixedRealityPDF.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Handler {

  private int height;
  private int width;
  private int[] pixels;

  // white, so that true luminosity of the image would be preserved.
  private static int THRESHOLD = 255*3;

  public Handler(BufferedImage img){
    height = img.getHeight();
    width = img.getWidth();
    pixels = img.getRGB(0, 0, getWidth(), getHeight(), null, 0, getWidth());
  }

  public BufferedImage getBlackAndWhite(){


    BufferedImage out = new BufferedImage(getWidth(), getHeight(),
            BufferedImage.TYPE_INT_RGB);

    int[] lum = new int[pixels.length];
    for(int i = 0; i <lum .length; i++)
      lum[i] = getLuminosity(pixels[i]);

    for(int x = 0; x < getWidth(); x++)
      for(int y =0; y < getHeight(); y++) {

        if (lum[getXY(x, y)] <= THRESHOLD) {
          out.setRGB(x, y, Color.BLACK.getRGB());
        } else {
          out.setRGB(x, y, Color.WHITE.getRGB());
          lum[getXY(x, y)] -= THRESHOLD;
        }

        if (lum[getXY(x, y)] > 0) {
          int cl = lum[getXY(x, y)];
          int cld = cl / 16;
          int clm = cl % 16;
          if(inBounds(x+1, y  )) {
            lum[getXY(x+1, y  )] += clm;
          } else if(inBounds(x  , y+1)) {
            lum[getXY(x  ,y+1)] += clm;
          }

          if(inBounds(x+1, y  )) lum[getXY(x+1, y  )] += cld * 7;
          if(inBounds(x+1, y+1)) lum[getXY(x+1, y+1)] += cld * 1;
          if(inBounds(x  , y+1)) lum[getXY(x  , y+1)] += cld * 5;
          if(inBounds(x-1, y+1)) lum[getXY(x-1, y+1)] += cld * 3;
        }
      }
    return out;
  }

  public int getLuminosity(int c){
    return getRed(c) + getGreen(c) + getBlue(c);
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

  private int getAlpha(int c){
    return (c & 0xFF000000) >> 6*4;
  }

  private int getRed(int c){
    return (c & 0xFF0000) >> 4*4;
  }

  private int getGreen(int c){
    return (c & 0xFF00) >> 2*4;
  }

  private int getBlue(int c){
    return (c & 0xFF);
  }
}
