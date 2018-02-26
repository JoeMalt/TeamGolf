import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Class dedicated to editing, creating and extracting information from PDFs.
 * Based on the PDFBox library.
 * **/
public class PDFEditor {
    private Path currentRelativePath = Paths.get("");
    private String RELATIVE_PATH = String.format("%s/Testing/PDFEditor/"currentRelativePath.toAbsolutePath().toString();
    private String loadFilePath;
    private String outFilePath;
    private PDDocument document;

    /**
     * PDFEditor constructor.
     * Args:
     *  @param loadFilePath relative path to the original document.
     *  @param outFilePath relative path to the output document.
     ***/
    public PDFEditor(String loadFilePath, String outFilePath){
        this.loadFilePath = loadFilePath;
        this.outFilePath = outFilePath;
    }

    // Example how to get text positions:
    // https://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/util/PrintTextLocations.java?revision=1792647&view=markup

    /**
     * Add markup + annotation to arbitrary point on PDF.
     * @param content text to be displayed in annotation box
     * @param title title of annotation box
     * **/
    public void annotateDocument(String title, String content){
        // get first page and annotiations
        PDPage page0 = document.getPage(0);
        List<PDAnnotation> annotations = new ArrayList<>();
        try {
            annotations = page0.getAnnotations();
        }catch(IOException e){
            System.err.println("IOException while fetching annotations currently on the document. Stack trace:");
            e.printStackTrace();
        }

        //generate instanse for annotation
        PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);

        //set the rectangle
        PDRectangle position = createRectangle(170, 125, 195, 140);
        txtMark.setRectangle(position);

        //set the quadpoint
        txtMark.setQuadPoints(getQuadPoints(position));

        // set the annotation up
        txtMark.setContents(content);
        txtMark.setAnnotationName((UUID.randomUUID().toString()));
        txtMark.setCreationDate((new GregorianCalendar()));
        txtMark.setTitlePopup(title);

        // make comment more visible
        PDColor blue = new PDColor(new float[] { 0, 0, 1 }, PDDeviceRGB.INSTANCE);
        txtMark.setColor(blue);
        txtMark.setConstantOpacity((float)0.2); // 20% transparent

        // add annotations
        annotations.add(txtMark);
        page0.setAnnotations(annotations);
    }

    /** Rectangle specifying contents to be modified **/
    private PDRectangle createRectangle(int leftX, int leftY, int rightX, int rightY){
        PDRectangle rectangle = new PDRectangle();
        rectangle.setLowerLeftX(leftX);
        rectangle.setLowerLeftY(leftY);
        rectangle.setUpperRightX(rightX);
        rectangle.setUpperRightY(rightY);
        return rectangle;
    }

    /**
     * Create array of points wrapping around annotation.
     * @param position PDRectangle object specifying coordinates.
     * @return array of points.
     * **/
    private float[] getQuadPoints(PDRectangle position){
        float[] quads = new float[8];
        //x1,y1
        quads[0] = position.getLowerLeftX();
        quads[1] = position.getUpperRightY()-2;
        //x2,y2
        quads[2] = position.getUpperRightX();
        quads[3] = quads[1];
        //x3,y3
        quads[4] = quads[0];
        quads[5] = position.getLowerLeftY()-2;
        //x4,y4
        quads[6] = quads[2];
        quads[7] = quads[5];
        return quads;
    }

    /** Load PDF as specified by loadFilePath. **/
    public void loadOriginalFile() {
        File file = new File(RELATIVE_PATH + loadFilePath);
        try {
            document = PDDocument.load(file);
        }catch(IOException e){
            System.err.println("IOException when trying to open the original file. Stack trace: ");
            e.printStackTrace();
        }
    }

    /**
     * Add text at the end of document.
     * Args:
     *  @param text: text to be appended.
     **/
    public void appendText(String text) throws IOException {
        PDPage page = document.getPage(0);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true, true);

        contentStream.beginText();
        //contentStream.newLineAtOffset(25, 700);
        contentStream.setFont(PDType1Font.COURIER, 12);

        contentStream.showText(text);
        contentStream.endText();

        contentStream.close();
    }

    /**
     * Retrieve all text from PDF.
     * @return all text as single String.
     * **/
    public String getText() throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        return pdfStripper.getText(document);
    }
    /**
     * Create new blank document with one page and save it.
     * @return the newly created PDDocument reference.
     * **/
    public PDDocument createAndSave() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage();
        document.addPage(page1);
        saveAndClose();
        return document;
    }

    /** Save and close the document**/
    public void saveAndClose() throws IOException {
        document.save(RELATIVE_PATH + outFilePath);
        document.close();
    }

    /** Setters and getters **/
    public void setLoadFilePath(String f){
        loadFilePath = f;
    }

    public String getLoadFilePath(){
        return loadFilePath;
    }

    public void setOutFilePath(String f){
        outFilePath = f;
    }

    public String getOutFilePath(){
        return outFilePath;
    }
}
