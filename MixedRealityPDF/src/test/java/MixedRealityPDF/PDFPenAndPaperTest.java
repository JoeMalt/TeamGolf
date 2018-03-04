package MixedRealityPDF;

import java.io.File;

public class PDFPenAndPaperTest {

  public static void main(String[] args) throws Exception {
    String pathOriginal = "Data/PDFs/doc2_original.pdf";
    String pathScanned = "Data/PDFs/doc2_scanned.pdf";
    String pathOut = "Data/PDFs/doc2_out.pdf";

    File original = new File(pathOriginal);
    File scanned = new File(pathScanned );

    PDFPenAndPaper ppap = new PDFPenAndPaper(original, scanned, pathOut);
  }

}
