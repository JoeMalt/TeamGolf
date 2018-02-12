package MixedRealityPDF.AnnotationProcessor.Identification;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class FeatureExtractor {

    public FeatureExtractor(){
    }

    /**
     * Analyses passed image inspecting ratio of whitespace to marking.
     * @param image annotation cropped out to be inspected. **/
    public double getCoverage(BufferedImage image){
        double coverage;
        int colour;
        double covered = 0;
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                colour = image.getRGB(x, y);
                if(colour != -1){
                    covered++;
                }
            }
        }
        coverage = covered/(double)(image.getWidth()*image.getHeight());
        return coverage;
    }

    // faster way of getting RGB but buggy
    // TODO(Aga): try to fix. DataBuffer can't be cast from Int to Byte but should be because BufferedImage should be in RBG mode.
    private int getFastRGB(BufferedImage image, int x, int y){
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int width = image.getWidth();
        boolean hasAlphaChannel = image.getAlphaRaster() != null;
        int pixelLength = 3;
        if (hasAlphaChannel)
        {
            pixelLength = 4;
        }

        int pos = (y * pixelLength * width) + (x * pixelLength);

        int argb = -16777216; // 255 alpha
        if (hasAlphaChannel)
        {
            argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
        }

        argb += ((int) pixels[pos++] & 0xff); // blue
        argb += (((int) pixels[pos++] & 0xff) << 8); // green
        argb += (((int) pixels[pos++] & 0xff) << 16); // red
        return argb;
    }
}
