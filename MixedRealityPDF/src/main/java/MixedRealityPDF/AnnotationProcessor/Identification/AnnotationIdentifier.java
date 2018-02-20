package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.Annotations.Annotation;
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
public class AnnotationIdentifier {
    private Collection<AnnotationBoundingBox> points;
    private FeatureExtractor featureExtractor;
    private String RELATIVE_PATH;
    private Image fullImage;

    public AnnotationIdentifier(Image fullImage, Collection<AnnotationBoundingBox> points) {
        this.points = points;
        this.fullImage = fullImage;
        this.featureExtractor = new FeatureExtractor();
        Path currentRelativePath = Paths.get("");
        this.RELATIVE_PATH = currentRelativePath.toAbsolutePath().toString();
    }

    public Collection<Annotation> identifyAnnotations(){
        ArrayList<BufferedImage> annotationImages = cropAnnotations((BufferedImage) fullImage);
        ArrayList<Annotation> identifiedAnnotations = new ArrayList<>();
        FileWriter writer = initializeFileWriter("predictionData.csv");

        for(BufferedImage annotationImage : annotationImages){
            saveFeaturesToCSV(annotationImage, writer, "");

            // TODO(koc): fix errors thrown by CL, adjust to different OSes
            try {
                // run python script with decision tree
                String pythonScriptPath = Paths.get(RELATIVE_PATH, "src", "main", "java", "MixedRealityPDF",
                        "AnnotationProcessing", "Identification", "decision_tree.py").toString();

                ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath);
                Process p = pb.start();

                // retrieve output from python script
                BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = bfr.readLine()) != null) {
                    System.out.println(line);
                }
            }catch(IOException ioe){
                System.err.println("Error executing python script from command line");
                ioe.printStackTrace();
            }
        }

        return identifiedAnnotations;
    }

    private void saveFeaturesToCSV(BufferedImage image, FileWriter fileWriter, String key){
        ArrayList<String> record = new ArrayList<>();
        record.add(Double.toString(featureExtractor.getCoverage(image)));
        record.add(Double.toString(featureExtractor.getDominantColour(image)));
        record.add(Double.toString(image.getWidth()));
        record.add(Double.toString(image.getHeight()));
        if(!key.isEmpty())  record.add(key);
        writeLineCSV(fileWriter, record);
    }

    private FileWriter initializeFileWriter(String filePath){
        String csvPath = Paths.get(RELATIVE_PATH, "Data", filePath).toString();
        FileWriter writer = null;
        try {
            System.out.println(csvPath);
            writer = new FileWriter(new File(csvPath));
        }catch(IOException ioe) {
            System.err.println("Error reading CSV file");
        }
        return writer;
    }

    public void createTreeTrainingFile(){
        // decided to go with the approach of passing data to Python via CSVs because Jython is too complicated and
        // slow compared with fileIO
        FileWriter writer = initializeFileWriter("trainingData.csv");

        Map<String, ArrayList<BufferedImage>> classImageMap = null;
        try{
            classImageMap = readClassImageMap();
        }catch(IOException ioe){
            System.err.println("Error reading images for training ");
        }

        ArrayList<String> firstLine = new ArrayList<>();
        // names of features
        firstLine.addAll(Arrays.asList("coverage", "colour", "width", "height", "key"));
        writeLineCSV(writer, firstLine);

        for (Map.Entry<String, ArrayList<BufferedImage>> entry : classImageMap.entrySet()) {
            ArrayList<BufferedImage> images = entry.getValue();
            for (BufferedImage image : images) {
                saveFeaturesToCSV(image, writer, entry.getKey());
            }
        }

        try{
            writer.flush();
            writer.close();
        }catch(IOException ioe){
            System.err.println("Error closing writer");
            ioe.printStackTrace();
        }

    }

    /**
     * Reads images in folders specified in dirNames array and puts them in a map according to which folder they came
     * from, indicating the type of image they are.
     * @return HashMap from Strings which are names of classes of annotation images to ArrayList<BufferedImage>
     *         which contains all images of that type.**/
    private Map<String, ArrayList<BufferedImage>> readClassImageMap() throws IOException{
        // Directory names become keys in the map
        String [] dirNames = new String[]{"text", "underline", "highlight"};
        Map<String, ArrayList<BufferedImage>> imagesInClasses = new HashMap<>();

        int numbOfFiles;
        for(String dirName : dirNames) {
            Path dirPath = Paths.get(Paths.get(RELATIVE_PATH, "Data", dirName).toString());
            numbOfFiles = (int) Files.list(dirPath).count();

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

    private ArrayList<BufferedImage> cropAnnotations(BufferedImage fullImage){
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
