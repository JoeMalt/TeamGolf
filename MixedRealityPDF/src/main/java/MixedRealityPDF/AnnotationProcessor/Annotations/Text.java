package MixedRealityPDF.AnnotationProcessor.Annotations;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

import java.io.IOException;
import java.util.List;

public final class Text extends Annotation {

    private final float width;
    private final float height;

    public Text(float x, float y, float width, float height, int pageNumber){
        super(x, y, pageNumber);
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public void applyAnnotation(PDPage doc) throws IOException{
        //TODO: implement if possible
    }
}





