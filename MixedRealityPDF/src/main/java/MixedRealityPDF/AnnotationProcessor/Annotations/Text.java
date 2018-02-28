package MixedRealityPDF.AnnotationProcessor.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public final class Text extends Annotation {

    private final BufferedImage image;

    public Text(float x, float y, BufferedImage image, int pageNumber){
        super(x, y, pageNumber);
        this.image = image;
    }

    @Override
    public void applyAnnotation(PDPage doc) throws IOException{
        //TODO: Implement
    }
}





