package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
import MixedRealityPDF.AnnotationProcessor.Annotations.Highlight;
import MixedRealityPDF.AnnotationProcessor.Annotations.Text;
import MixedRealityPDF.AnnotationProcessor.Annotations.UnderLine;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * Decides what sort of Annotation parts of document specified by bounding boxes are.
 * **/
public class AnnotationIdentifier implements IAnnotationIdentifier{
    private FeatureExtractor featureExtractor;
    private String relativePath;

    public AnnotationIdentifier(){
        featureExtractor = new FeatureExtractor();
        Path currentRelativePath = Paths.get("");
        relativePath = currentRelativePath.toAbsolutePath().toString();
        createTreeTrainingFile();
    }

    /**
     * Central method which is invoked by the main pipeline.
     * Crops out annotations from the full difference image of a PDF page according to their bounding boxes, analyses
     * the resulting images in a Python decision tree and outputs an identified collection of Annotation objects, specific
     * to their type
     * @param fullImage image of the full PDF page with its difference taken so that it's only full of annotations in colour
     * @param points collection of AnnotationBoundingBoxes which specify location of annotations on the page
     * @param pageNumber index of the PDF page
     * @return collection of identified annotations
     * **/
    public Collection<Annotation> identifyAnnotations(BufferedImage fullImage, Collection<AnnotationBoundingBox> points, int pageNumber) {
        ArrayList<BufferedImage> annotationImages = cropAnnotations(fullImage, points);
        FileWriter writer = initializeFileWriter("predictionData.csv");

        for (BufferedImage annotationImage : annotationImages) {
            saveFeaturesToCSV(annotationImage, writer, "");
        }

        closeWriter(writer);

        ArrayList<String> keys = runDecisionTree();
        return createAnnotationObjects(keys, points, annotationImages, pageNumber);
    }

    public Collection<Annotation> testIdentifyAnnotations(Collection<AnnotationBoundingBox> points, int pageNumber){
        ArrayList<BufferedImage> annotationImages = new ArrayList<>();

        // load test images
        try {
            int numbOfFiles;
            Path dirPath = Paths.get(Paths.get(relativePath, "Data", "test").toString());
            numbOfFiles = (int) Files.list(dirPath).count();

            BufferedImage image;
            File imagePath;
            for (int i = 0; i < numbOfFiles; i++) {
                imagePath = new File(String.format("%s/%d.png", dirPath.toString(), i));
                image = ImageIO.read(imagePath);
                annotationImages.add(image);
            }
        }catch(IOException ioe){
            System.out.println("Error while loading test images.");
            ioe.printStackTrace();
        }

        //save features from test data
        FileWriter writer = initializeFileWriter("predictionData.csv");

        for (BufferedImage annotationImage : annotationImages) {
            saveFeaturesToCSV(annotationImage, writer, "");
        }

        closeWriter(writer);

        // identify annotations
        ArrayList<String> keys = runDecisionTree();
        ArrayList<Annotation> identifiedAnnotations = createAnnotationObjects(keys, points, annotationImages, pageNumber);
        int i = 0;
        for(Annotation annotation : identifiedAnnotations){
            if(annotation instanceof Text){
                System.out.println(i + ": Text");
            }
            else if(annotation instanceof Highlight){
                System.out.println(i + ": High");
            }
            else if(annotation instanceof UnderLine){
                System.out.println(i + ": Under");
            }
            i++;
        }
        return identifiedAnnotations;

    }

    /**
     * Loops through all images to create their objects depending on class determined by key from python output.
     * This requires data for images to be in parallel across all data structures so that iterating over them gives
     * data corresponding to the same annotation with each step: image with index i in annotationImages[i] will have its
     * bounding box at points[i] and its key at decisionTreeOutput[i]
     *
     * @param keys output from decision tree in the form of strings: "highlight", "text" and "underline"
     * @param points bounding boxes for annotations, necessary to get their x-y coordinates
     * @param annotationImages annotation images, necessary to get their dimentions
     * @param pageNumber index of PDF page
     * @return collection of annotation objects, each of specific type according to their key
     * **/
    private ArrayList<Annotation> createAnnotationObjects(Collection<String> keys, Collection<AnnotationBoundingBox> points, Collection<BufferedImage> annotationImages, int pageNumber){
        ArrayList<Annotation> identifiedAnnotations = new ArrayList<>();
        Iterator<String> keyIt = keys.iterator();
        Iterator<AnnotationBoundingBox> boxIt = points.iterator();
        Iterator<BufferedImage> imageIt = annotationImages.iterator();
        int x, y, width, height;
        String key;
        AnnotationBoundingBox currentBox;
        BufferedImage annotationImage;

        while(keyIt.hasNext() && boxIt.hasNext() && imageIt.hasNext()){
            key = keyIt.next();
            currentBox = boxIt.next();
            annotationImage = imageIt.next();

            x = currentBox.getBottomLeft().getX();
            y = currentBox.getBottomLeft().getY();
            // Converting imgY to pdfY. - no need to convert x.
            y = Annotation.ImageYToPDFY(y, annotationImage.getHeight());
            width = annotationImage.getWidth();
            height = annotationImage.getHeight();
            switch (key) {
                case "highlight":
                    identifiedAnnotations.add(new Highlight(x, y, width, height, pageNumber));
                    break;
                case "text":
                    identifiedAnnotations.add(new Text(x, y, annotationImage, pageNumber));
                    break;
                case "underline":
                    identifiedAnnotations.add(new UnderLine(x, y, width, pageNumber));
                    break;
            }
        }
        return identifiedAnnotations;
    }

    /**
     * Invokes python script which holds the main decision tree mechanism by calling it from Command Line.**/
    private ArrayList<String> runDecisionTree(){
        String annotationKey;
        ArrayList<String> decisionTreeOutput = new ArrayList<>();

        // run python script with decision tree
        String pythonScriptPath = Paths.get(relativePath, "src", "main", "java", "MixedRealityPDF",
                "AnnotationProcessor", "Identification", "decision_tree.py").toString();
        try {
            // start Python script
            ProcessBuilder builder = new ProcessBuilder("python3", pythonScriptPath);
            Process process = builder.start();

            // read in each line of python output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            while ((annotationKey = reader.readLine()) != null) {
                decisionTreeOutput.add(annotationKey);
            }
            reader.close();
        }catch(IOException ie){
            System.err.println("Error executing python script from command line");
            ie.printStackTrace();
        }
        return decisionTreeOutput;
    }

    /**
     * Analyse image passed to get its features and dimentions and save them all into CSV format.**/
    private void saveFeaturesToCSV(BufferedImage image, FileWriter fileWriter, String key){
        ArrayList<String> record = new ArrayList<>();
        record.add(Double.toString(featureExtractor.getCoverage(image)));
        record.add(Double.toString(featureExtractor.getDominantColour(image)));
        record.add(Double.toString(image.getWidth()));
        record.add(Double.toString(image.getHeight()));
        // save the key field only in case it's passed in = it's training data
        if(!key.isEmpty()) record.add(key);
        writeLineCSV(fileWriter, record);
    }

    private FileWriter initializeFileWriter(String filePath){
        String csvPath = Paths.get(relativePath, "Data", filePath).toString();
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(csvPath));
        }catch(IOException ioe) {
            System.err.println("Error reading CSV file");
        }
        return writer;
    }

    /**
     * Creates CSV file used for training decision tree in Python.
     * This method needs to be run only once so the file is created, but the decision tree itself is recreated each
     * time the script is called.
     * The file is in MixedRealityPDF/Data directory and uses training data acquired from readClassImageMap() method.
     * Names of individual features corresponding to FeatureExtractor classes are written as the first line (stored in
     * firstLine ArrayList).
     * **/
    private void createTreeTrainingFile(){
        // decided to go with the approach of passing data to Python via CSVs because Jython is too complicated and
        // slow compared with fileIO
        FileWriter writer = initializeFileWriter("trainingData.csv");

        // get the images to train with
        Map<String, ArrayList<BufferedImage>> classImageMap = null;
        try{
            classImageMap = readClassImageMap();
        }catch(IOException ioe){
            System.err.println("Error reading images for training ");
        }

        // write the first line of CSV file with column names (names of features)
        ArrayList<String> firstLine = new ArrayList<>();
        firstLine.addAll(Arrays.asList("coverage", "colour", "width", "height", "key"));
        writeLineCSV(writer, firstLine);

        // extract features from each image and save them to CSV file
        for (Map.Entry<String, ArrayList<BufferedImage>> entry : classImageMap.entrySet()) {
            ArrayList<BufferedImage> images = entry.getValue();
            for (BufferedImage image : images) {
                saveFeaturesToCSV(image, writer, entry.getKey());
            }
        }
        closeWriter(writer);

    }

    /**
     * Reads images in folders specified in dirNames array and puts them in a map according to which folder they came
     * from, indicating the type of image they are.
     * @throws IOException if there is an error when reading images for training
     * @return HashMap from Strings which are names of classes of annotation images to ArrayList<BufferedImage>
     *         which contains all images of that type.**/
    private Map<String, ArrayList<BufferedImage>> readClassImageMap() throws IOException{
        // Directory names become keys in the map
        String [] dirNames = new String[]{"text", "underline", "highlight"};
        Map<String, ArrayList<BufferedImage>> imagesInClasses = new HashMap<>();

        int numbOfFiles;
        for(String dirName : dirNames) {
            Path dirPath = Paths.get(Paths.get(relativePath, "Data", dirName).toString());
            numbOfFiles = (int) Files.list(dirPath).count();

            // load each image and add it to the output list
            ArrayList<BufferedImage> imagesList = new ArrayList<>();
            BufferedImage image;
            File imagePath;
            for(int i = 0; i < numbOfFiles; i++){
                imagePath = new File(String.format("%s/%d.png", dirPath.toString(), i));
                image = ImageIO.read(imagePath);
                imagesList.add(image);
            }
            imagesInClasses.put(dirName, imagesList);
        }
        return imagesInClasses;
    }

    private void closeWriter(FileWriter writer){
        try{
            writer.flush();
            writer.close();
        }catch(IOException ioe){
            System.err.println("Error closing writer");
            ioe.printStackTrace();
        }
    }

    /**
     * Crops out annotations out of the full image of the PDF difference according to bounding boxes passed from
     * segmentation stage. **/
    private static ArrayList<BufferedImage> cropAnnotations(BufferedImage fullImage, Collection<AnnotationBoundingBox> points){
        BufferedImage image;
        ClusteringPoint topLeft;
        int width;
        int height;
        ArrayList<BufferedImage> annotationImages = new ArrayList<>();
        for(AnnotationBoundingBox boundingBox : points) {
            topLeft = boundingBox.getTopLeft();
            width = boundingBox.getTopRight().getX() - topLeft.getX();
            height = boundingBox.getBottomLeft().getY() - topLeft.getY();
            BufferedImage subImage = fullImage.getSubimage(topLeft.getX(), topLeft.getY(), width, height);

            // get a copy of image because .getSubimage operates on the original
            image = new BufferedImage(subImage.getWidth(), subImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(subImage, 0, 0, null);

            annotationImages.add(image);
        }
        return annotationImages;
    }

    /* -------------- CSV writing methods -------------- */
    private static String followCVSformat(String value) {
        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    private static void writeLineCSV(Writer w, List<String> values){
        try {
            writeLine(w, values, ',', ' ');
        }catch(IOException ioe){
            System.err.println(String.format("Error writing to CSV: %s", values.toString()));
            ioe.printStackTrace();
        }
    }

    private static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            sb.append(followCVSformat(value));
            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    /* -------------- Debugging helper functions -------------- */
    /**
     * Debugging function saving annotation cutouts from the big image**/
    public void saveAnnotationBoxes(ArrayList<BufferedImage> annotationImages){
        Path currentRelativePath = Paths.get("");
        String RELATIVE_PATH = String.format("%s/Data/", currentRelativePath.toAbsolutePath().toString());
        int i = 0;
        for(BufferedImage image : annotationImages) {
            String filename = String.format("annotation_box%d.jpg", i);
            try {
                ImageIO.write(image, "png", new File(RELATIVE_PATH + filename));
            } catch (IOException e) {
                System.err.println("IOException when writing image: ");
                e.printStackTrace();
            }
            i++;
        }


    }
}
