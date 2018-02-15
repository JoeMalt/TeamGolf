package MixedRealityPDF.DocumentProcessor;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class DiffMap implements IDifferenceMap {

    static IColorDistance d = new Euclidian();

    protected static BufferedImage myGreyscale(BufferedImage image) {
        BufferedImage greyscale = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x=0; x<image.getWidth(); x++) {
            for (int y=0; y<image.getHeight(); y++) {
                final Color orPixel = new Color( image.getRGB(x, y) );
//                if (!orPixel.equals(Color.WHITE)) {
//                    System.out.println("Non-white!");
//                }
                final double orDist = d.apply(orPixel, Color.BLACK);
                int outComp = (int) Math.round( (orDist / d.max() ) * 255 );
                Color greyPixel = new Color(outComp, outComp, outComp);
                greyscale.setRGB(x, y, greyPixel.getRGB());
            }
        }
        return greyscale;
    }

    protected static BufferedImage myPureBW(BufferedImage image) {
        BufferedImage pureBW = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x=0; x<image.getWidth(); x++) {
            for (int y=0; y<image.getHeight(); y++) {
                final Color orPixel = new Color( image.getRGB(x, y) );
//                if (!orPixel.equals(Color.WHITE)) {
//                    System.out.println("Non-white!");
//                }
                final double orDist = d.apply(orPixel, Color.BLACK);
                int outComp = (int) Math.round( orDist / d.max() ) * 255;
                Color greyPixel = new Color(outComp, outComp, outComp);
                pureBW.setRGB(x, y, greyPixel.getRGB());
            }
        }
        return pureBW;
    }

    protected static BufferedImage greyscale(BufferedImage image) {
        double[] weightsRGB = {1.0, 1.0, 1.0};
        BufferedImage greyscale = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x=0; x<image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                final Color orPixel = new Color( image.getRGB(x, y) );
                int intensity = (int) Math.round( ( weightsRGB[0]*orPixel.getRed() + weightsRGB[1]*orPixel.getGreen() + weightsRGB[2]*orPixel.getBlue() ) /3.0 );
                greyscale.setRGB(x, y, new Color(intensity, intensity, intensity).getRGB() );
            }
        }
        return greyscale;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage td1Or = ImageIO.read(
                Paths.get("C:\\Users\\Gideon\\Git\\TeamGolf\\Testing\\TestDocs\\Clean Scans\\CleanScan1.png").toFile()
        );
        BufferedImage td1An = ImageIO.read(
                Paths.get("C:\\Users\\Gideon\\Git\\TeamGolf\\Testing\\TestDocs\\Annotated Scans\\AnnotatedScan1.png").toFile()

        );
//        System.out.println("Subtracting original from annotated");
//        ImageIO.write(
//                subtract(td1An, td1Or),
//                "png",
//                new File("TestDoc1_Extracted.png")
//        );
//        System.out.println("Converting original to greyscale (ColorSpace method)");
//        ImageIO.write(
//                greyscale(td1Or),
//                "png",
//                new File("TestDoc1_Scan_Grey.png")
//        );
//        System.out.println("Converting original to greyscale (my method)");
//        ImageIO.write(
//                myGreyscale(td1Or),
//                "png",
//                new File("TestDoc1_Scan_MyGrey.png")
//        );
//        System.out.println("Converting original to pure black/white");
//        ImageIO.write(
//                myPureBW(td1Or),
//                "png",
//                new File("TestDoc1_Scan_MyBW.png")
//        );
    }
}
