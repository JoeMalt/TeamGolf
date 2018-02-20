package MixedRealityPDF.DocumentProcessor;

import MixedRealityPDF.ImageProcessor.ImageProcessor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Document {

  private PDDocument pdfDocument;
  private int pages;
  private ImageProcessor[] images;


  public Document(String path) throws IOException{
    pdfDocument = PDDocument.load(new File(path));
    init(pdfDocument);
  }

  public Document(PDDocument pdfDocument) throws IOException {
    init(pdfDocument);
  }

  private void init(PDDocument pdfDocument) throws IOException{
    pages = pdfDocument.getNumberOfPages();
    PDFRenderer renderer = new PDFRenderer(pdfDocument);

    images = new ImageProcessor[pages];
    for(int i = 0; i < pages; i++){
      images[i] = new ImageProcessor(renderer.renderImage(i));
    }

  }

  public PDDocument getDocument() {
    return pdfDocument;
  }

  public PDPage getPage(int page) {
    return pdfDocument.getPage(page);
  }

  public BufferedImage getPageImage(int page){
    return images[page].getImage();
  }

  public BufferedImage getPageImageBNW(int page){
    return images[page].getBlackAndWhiteImage();
  }

  public ImageProcessor getPageImageProcessor(int page){
    return images[page];
  }
}
