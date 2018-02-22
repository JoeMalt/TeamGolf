package experiments;

public class PDFs {



    public static String ORIGINAL =  "original_pdfs/originalpdf.png";
    public static String ORIGINAL_BLUR_SATURATED =  "original_pdfs/originalpdf_blurred_saturated.png";
    public static String ORIGINAL_BLUR_1 =  "original_pdfs/originalpdfblur1.png";
    public static String ORIGINAL_BLUR_2 =  "original_pdfs/originalpdfblur2.png";




    public static String THRESHOLDED_SCAN = "thresholding_tests/out4.png";

    // scaled original pdf
    // convert originalpdf.png -scale 1116x1578 -gravity center -background white -extent 1240x1753 originalpdf_scaled.png
    public static String ORIGINAL_SCALED_TRANSLATED = "modified_pdfs/originalpdf_scaled.png";



    // Original scaled+blurred
    public static String ORIGINAL_SCALED_BLURRED_FOR_ALIGNMENT = "modified_pdfs/scaled_blurred.png";
    public static String ORIGINAL_NOT_SCALED_BUT_BLURRED_FOR_ALIGNMENT = "original_pdfs/original_blurred.png";

    // Original + scan blurred
    public static String TEST_3_ORIGINAL_BLURRED = "original_pdfs/test_3_original_blurred.png";
    public static String TEST_3_SCAN_BLURRED = "scanned_pdfs/test3modifiedblurred.png";


    // SINGLE COLUMN OF TEXT, PT 12
    public static String TEST_1_TEXT_ORIGINAL = "original_pdfs/Test 1 original.pdf.png";
    public static String TEST_1_TEXT_NO_ANNOTATION = "scanned_pdfs/Test 1 no annotations.pdf.png";
    public static String TEST_1_TEXT_LIGHT_ANNOTATION = "scanned_pdfs/Test 1 light annotations.pdf.png";
    public static String TEST_1_TEXT_HEAVY_ANNOTATION = "scanned_pdfs/Test 1 heavy annotations.pdf.png";




    // IMAGES, VARIED FORMATTING
    public static String TEST_3_TEXT_ORIGINAL = "original_pdfs/Test 3 original.pdf.png";
    public static String TEST_3_TEXT_NO_ANNOTATION = "scanned_pdfs/Test 3 no annotations.pdf.png";
    public static String TEST_3_TEXT_LIGHT_ANNOTATION = "scanned_pdfs/Test 3 light annotations.pdf.png";
    public static String TEST_3_TEXT_HEAVY_ANNOTATION = "scanned_pdfs/Test 3 heavy annotations.pdf.png";




    // convert -rotate 3 -compress lossless originalpdf.png original_pdf_rotated.png
    public static String ORIGINAL_ROTATED_10_DEGREES = "original_pdfs/original_pdf_rotated.png";



    public static String MODIFIED_ORIGINAL_CORRECT_PERSPECTIVE =  "modified_pdfs/original_modified_correct_perspective.png";


    public static String SCAN_PURE =  "scanned_pdfs/scan_pure.png";
    public static String SCAN_THRESHOLDED =  "scanned_pdfs/scan_thresholded.jpg";
    public static String SCAN_THRESHOLD_TRANSLATED =  "scanned_pdfs/scan_thresholded_translated.png";
    public static String SCAN_TRANSFORMED =  "scanned_pdfs/scan_transformed.png";




    // New idea experiments.PDFs
    public static String ORIGINAL_NEW = "originals_new/original.png";
    public static String TRANSFORMED_NEW = "originals_new/modified.png";


    public static String P1 = "originals_new/1.png";
    public static String P2 = "originals_new/2.png";




}
