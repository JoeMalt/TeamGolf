package MixedRealityPDF.AnnotationProcessor.Identification;

import MixedRealityPDF.AnnotationProcessor.AnnotationBoundingBox;
import MixedRealityPDF.AnnotationProcessor.ClusteringPoint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Test {
    static String RELATIVE_PATH;
    public static void main(String [] args){
        Path currentRelativePath = Paths.get("");
        RELATIVE_PATH = String.format("%s/%s", currentRelativePath.toAbsolutePath().toString(), "Data");
        String csvPath = Paths.get(RELATIVE_PATH,"trainingData.csv").toString();
        FileWriter writer = null;
        try {
            System.out.println(csvPath);
            writer = new FileWriter(new File(csvPath));
        }catch(IOException ioe){
            System.err.println("Error reading CSV file");
        }
        Map<String, ArrayList<BufferedImage>> classImageMap = readClassImageMap();
        FeatureExtractor featureExtractor = new FeatureExtractor();

        ArrayList<String> firstLine = new ArrayList<>();
        firstLine.addAll(Arrays.asList("coverage", "colour", "width", "height", "key"));

        try{
            writeLineCSV(writer, firstLine);
        }catch(IOException ioe){
            System.out.println("Error printing first line into CSV file");
            ioe.printStackTrace();
        }

        for(Map.Entry<String, ArrayList<BufferedImage>> entry : classImageMap.entrySet()){
            ArrayList<BufferedImage> images = entry.getValue();
            ArrayList<String> record;
            for(BufferedImage image : images){
                record = new ArrayList<>();
                record.add(Double.toString(featureExtractor.getCoverage(image)));
                record.add(Double.toString(featureExtractor.getDominantColour(image)));
                record.add(Double.toString(image.getWidth()));
                record.add(Double.toString(image.getHeight()));
                record.add(entry.getKey().equals("highlight") ? "highlight" : "non-highlight");

                try {
                    writeLineCSV(writer, record);
                }catch(IOException ioe){
                    System.err.println(String.format("Error writing record \"%s\" to CSV", record.toString()));
                    ioe.printStackTrace();
                }
            }
        }

        try {
            writer.flush();
            writer.close();
        }catch(IOException ioe){
            System.out.println("Error flushing/closing writer.");
            ioe.printStackTrace();
        }
    }

    private static BufferedImage readFullImage(String filename){
        BufferedImage fullImage = null;
        try {
            fullImage = ImageIO.read(new File(String.format("%s%s", RELATIVE_PATH, filename)));
        }catch(IOException ioe){
            System.err.println(String.format("IOException reading image %s", filename));
            ioe.printStackTrace();
        }
        return fullImage;
    }

    private static Map<String, ArrayList<BufferedImage>> readClassImageMap(){
        String [] dirNames = new String[]{"text", "underline", "highlight"};
        Map<String, ArrayList<BufferedImage>> imagesInClasses = new HashMap<>();

        int numbOfFiles = 0;
        for(String dirName : dirNames) {
            Path dirPath = Paths.get(Paths.get(RELATIVE_PATH, dirName).toString());
            try {
                numbOfFiles = (int) Files.list(dirPath).count();
            }catch (IOException ie){
                System.err.println("Error counting number of files.");
                ie.printStackTrace();
            }

            ArrayList<BufferedImage> imagesList = new ArrayList<>();
            BufferedImage image = null;
            for(int i = 0; i < numbOfFiles; i++){
                try {
                    image = ImageIO.read(new File(String.format("%s/%d.png", dirPath.toString(), i)));
                }catch(IOException ioe){
                    System.err.println(String.format("IOException reading image %d: ", i));
                    ioe.printStackTrace();
                }
                imagesList.add(image);
            }
            imagesInClasses.put(dirName, imagesList);
        }
        return imagesInClasses;
    }

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    private static void writeLineCSV(Writer w, List<String> values) throws IOException {
        writeLine(w, values, ',', ' ');
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

    private void testFullImage(String [] args){
        BufferedImage fullImage = readFullImage("test1_heavy");
        AnnotationBoundingBox boxWithNoText = new AnnotationBoundingBox(
                new ClusteringPoint(0, 0), new ClusteringPoint(10, 0),
                new ClusteringPoint(0, 10), new ClusteringPoint(10, 10));

        AnnotationBoundingBox boxWithLittleText = new AnnotationBoundingBox(
                new ClusteringPoint(100, 100), new ClusteringPoint(200, 100),
                new ClusteringPoint(100, 200), new ClusteringPoint(200, 200));

        AnnotationBoundingBox boxWithManyText = new AnnotationBoundingBox(
                new ClusteringPoint(500, 500), new ClusteringPoint(800, 500),
                new ClusteringPoint(500, 800), new ClusteringPoint(800, 800));

        ArrayList<AnnotationBoundingBox> list = new ArrayList<>();
        list.add(boxWithNoText);
        list.add(boxWithLittleText);
        list.add(boxWithManyText);

        AnnotationIdentifier annotationIdentifier = new AnnotationIdentifier(list);
        annotationIdentifier.identifyAnnotations(fullImage);
        annotationIdentifier.saveAnnotationBoxes();
    }

}
