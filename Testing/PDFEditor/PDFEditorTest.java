import java.io.IOException;

public class PDFEditorTest {

    public static void main(String[] args) throws IOException{
        PDFEditor pdfEditor = new PDFEditor("/original_documents/original1.pdf", "/test/annotate1.pdf");
        pdfEditor.loadOriginalFile();
        pdfEditor.annotateDocument("Test Comment 1", "This is a test comment to check if annotations are working.");
        pdfEditor.saveAndClose();
    }
}
