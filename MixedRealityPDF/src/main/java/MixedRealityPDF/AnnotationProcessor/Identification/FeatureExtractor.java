package MixedRealityPDF.AnnotationProcessor.Identification;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;
import java.util.Map;

public class FeatureExtractor {

    public FeatureExtractor(){
    }
// Highlight vs non highlight - train on binary decisions and put non-highlights into another tree
// Kaggle - ideas for data science solutions


    /**
     * Analyses passed image inspecting ratio of whitespace to marking.
     * @param image annotation cropped out to be inspected.
     * **/
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

    public double getHeight(BufferedImage image){
        return image.getHeight();
    }

    public double getWidth(BufferedImage image){
        return image.getWidth();
    }

    /**
     * Insects all pixels of passed image and counts number of occurences of each colour to return the most frequently
     * occurring one.
     * @param image cropped annotation to be annalysed
     * @return integer encoding of most frequent RGB value; -1 if no colour other than white**/
    public int getDominantColour(BufferedImage image){
        HashMap<Integer, Integer> colourCount = new HashMap<>();
        int colour;
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                colour = image.getRGB(x, y);
                if(colour != -1){
                    if(colourCount.containsKey(colour)){
                        colourCount.replace(colour, colourCount.get(colour)+1);
                    }
                    else {
                        colourCount.put(colour, 1);
                    }
                }
            }
        }

        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry : colourCount.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        return maxEntry != null ? maxEntry.getValue() : -1;
    }

    // faster way of getting RGB but buggy
    // TODO(Aga): try to fix. DataBuffer can't be cast from Int to Byte but should be because BufferedImage is in RBG mode.
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
