package MixedRealityPDF.UserInterface;

import MixedRealityPDF.PDFPenAndPaper;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MixedRealityPDFGUI extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    File originalFile;
    File scannedFile;
    File outputFile;

    private void setOriginalFile(File originalFile){
        this.originalFile = originalFile;
    }

    private void setScannedFile(File scannedFile){
        this.scannedFile = scannedFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getOriginalFile() {
        return originalFile;
    }

    public File getScannedFile() {
        return scannedFile;
    }

    private void showFileSelectionScene(Stage stage){
        // A GridPane is used for layout
        GridPane gp = new GridPane();
        FileChooser fc = new FileChooser();

        Label lblUpperText = new Label("To get started, select your documents.");
        lblUpperText.getStyleClass().add("heading");
        gp.setRowIndex(lblUpperText, 0);
        gp.setColumnSpan(lblUpperText, 2);
        gp.getChildren().add(lblUpperText);

        Label lblOriginalText = new Label("Original document");
        lblOriginalText.getStyleClass().add("bold");
        gp.setRowIndex(lblOriginalText, 1);
        gp.getChildren().add(lblOriginalText);

        Button btnChooseOriginalFile = new Button("Choose");
        gp.setRowIndex(btnChooseOriginalFile, 2);
        gp.getChildren().add(btnChooseOriginalFile);

        Label lblOriginalFilePath = new Label("No file selected");
        gp.setRowIndex(lblOriginalFilePath, 2);
        gp.setColumnIndex(lblOriginalFilePath, 1);
        gp.getChildren().add(lblOriginalFilePath);

        Label lblScannedText = new Label("Scanned document");
        lblScannedText.getStyleClass().add("bold");
        gp.setRowIndex(lblScannedText, 3);
        gp.getChildren().add(lblScannedText);

        Button btnChooseScannedFile = new Button("Choose");
        gp.setRowIndex(btnChooseScannedFile, 4);
        gp.getChildren().add(btnChooseScannedFile);

        Label lblScannedFilePath = new Label("No file selected");
        gp.setRowIndex(lblScannedFilePath, 4);
        gp.setColumnIndex(lblScannedFilePath, 1);
        gp.getChildren().add(lblScannedFilePath);

        Label lblStatus = new Label("");
        gp.setRowIndex(lblStatus, 5);
        gp.getChildren().add(lblStatus);

        Button btnStart = new Button("Start");
        gp.setRowIndex(btnStart, 5);
        gp.setColumnIndex(btnStart, 0);
        btnStart.setDisable(true); //disable this button until valid PDFs are selected
        gp.getChildren().add(btnStart);

        btnChooseOriginalFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setOriginalFile(fc.showOpenDialog(stage));
                if (getOriginalFile() != null && getOriginalFile().isFile() && getOriginalFile().getName().endsWith(".pdf")){
                    lblOriginalFilePath.setText(getOriginalFile().getName());
                    if (lblScannedFilePath.getText() != "No file selected"){
                        btnStart.setDisable(false);
                    }
                }
                else if (getOriginalFile() != null){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "It looks like this file is not a PDF. Please try again.", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        btnChooseScannedFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setScannedFile(fc.showOpenDialog(stage));
                if (getScannedFile() != null && getScannedFile().isFile() && getScannedFile().getName().endsWith(".pdf")){
                    lblScannedFilePath.setText(getScannedFile().getName());
                    if (lblOriginalFilePath.getText() != "No file selected"){
                        btnStart.setDisable(false);
                    }
                }
                else if (getScannedFile() != null){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "It looks like this file is not a PDF. Please try again.", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showProgressScene(stage);
            }
        });

        // set ColumnConstraints to make this scene fit to the window size
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        gp.getColumnConstraints().add(column1);
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(50);
        gp.getColumnConstraints().add(column2);

        // Set some padding between elements
        gp.setHgap(10.0);
        gp.setVgap(10.0);

        gp.getStyleClass().add("region-padded");
        Scene fileSelectionScene = new Scene(gp, 400, 300);
        fileSelectionScene.getStylesheets().add("css/UIStyleSheet.css");
        stage.setScene(fileSelectionScene);
        stage.show();
    }

    private void showProgressScene(Stage s){
        GridPane gp = new GridPane();
        FileChooser fc = new FileChooser();

        // Open up a file chooser to ask the user where to save the output
        setOutputFile(fc.showSaveDialog(s));

        // For the time being, we do not show a progress bar or any output
        // This is because the UI is deliberately basic
        // Our client has indicated that the processing logic is more useful to him than the UI

        Label lblProgress = new Label("Document processing in progress");
        lblProgress.getStyleClass().add("heading");
        gp.getChildren().add(lblProgress);
        gp.getStyleClass().add("region-padded");
        Scene progressScene = new Scene(gp, 400, 300);
        progressScene.getStylesheets().add("css/UIStyleSheet.css");
        s.setScene(progressScene);
        s.show();

        // Creating a new PDFPenAndPaper starts the pipeline
        // This blocks until processing is complete
        try {
            // Create the output file if it doesn't exist
            new PDFPenAndPaper(originalFile, scannedFile, outputFile.getAbsolutePath());
        }
        catch(IOException e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        // Show the original scene and a dialog indicating that processing is complete
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Processing complete!", ButtonType.OK);
        alert.show();
        showViewScene(s, 0, 1); //TODO: support showing more than just the first page

    }

    private Image getIntermediateImage(PipelineStageOutput pso, int page){
        String path = "";
        switch(pso){
            case ALIGNED:
                path = "Data/intermediates/pages/" + page + "/aligned.png";
                break;
            case EXTRACTED:
                path = "Data/intermediates/pages/" + page + "/extracted.png";
                break;
            case ORIGINAL:
                path = "Data/intermediates/pages/" + page + "/original.png";
                break;
            case OUTPUT:
                path = "Data/intermediates/pages/" + page + "/output.png";
                break;
            case SCANNED:
                path = "Data/intermediates/pages/" + page + "/scanned.png";
                break;
        }
        return new Image(new File(path).toURI().toString());
    }

    private List<Image> getAnnotations(int page){
        List<Image> annotations = new ArrayList<>();
        String path = "Data/intermediates/pages/" + page + "/annotations/";
        File annotationDirectory = new File(path);
        File[] annotationDirectoryListing = annotationDirectory.listFiles();
        Arrays.sort(annotationDirectoryListing); // Sort into lexicographic (in this case numerical) order
        if (annotationDirectoryListing != null){
            for (File f : annotationDirectoryListing){
                if (f.getName().endsWith(".png")){
                    annotations.add(new Image(f.toURI().toString()));
                }
            }
        }

        return annotations;
    }

    // Load the classification of each annotation, to display alongside the annotation
    // Generating the list of classifications hasn't yet been implemented, so this is disabled for now
    private List<String> getAnnotationClassifications(int page){
        // Read the file "classification.txt"
        List<String> output = new ArrayList<>();
        try {
            output = Files.readAllLines(new File("Data/intermediates/pages/" + page + "/annotations/classification.txt").toPath(), Charset.defaultCharset());
        }
        catch(IOException e){
        }

        return output;
    }


    private void showViewScene(Stage s, int page, int total_pages){

        int total_annotations = getAnnotations(page).size();

        TabPane tpViewSceneContent = new TabPane();

        // Buttons to switch page, used in all views
        BorderPane pageToggleControlsPane = new BorderPane();
        Button btnPageTogglePrev = new Button("Previous");
        Label lblPageToggleStatus = new Label ("Page m of n");
        Button btnPageToggleNext = new Button("Next");

        pageToggleControlsPane.setLeft(btnPageTogglePrev);
        pageToggleControlsPane.setCenter(lblPageToggleStatus);
        pageToggleControlsPane.setRight(btnPageToggleNext);


        // Contents of the Original Image tab
        GridPane originalPane = new GridPane();
        ImageView ivOriginalImage = new ImageView(getIntermediateImage(PipelineStageOutput.ORIGINAL, page));
        ivOriginalImage.setPreserveRatio(true);
        ivOriginalImage.setFitHeight(640.0);
        originalPane.getChildren().add(ivOriginalImage);

        // Contents of the Scanned Image tab
        GridPane scannedPane = new GridPane();
        ImageView ivScannedImage = new ImageView(getIntermediateImage(PipelineStageOutput.SCANNED, page));
        ivScannedImage.setPreserveRatio(true);
        ivScannedImage.setFitHeight(640.0);
        scannedPane.getChildren().add(ivScannedImage);

        // Contents of the AlignedImage tab
        GridPane alignedPane = new GridPane();
        ImageView ivAlignedImage = new ImageView(getIntermediateImage(PipelineStageOutput.ALIGNED, page));
        ivAlignedImage.setPreserveRatio(true);
        ivAlignedImage.setFitHeight(640.0);
        alignedPane.getChildren().add(ivAlignedImage);

        // Contents of the extraction tab
        GridPane extractedPane = new GridPane();
        ImageView ivExtractedImage = new ImageView(getIntermediateImage(PipelineStageOutput.EXTRACTED, page));
        ivExtractedImage.setPreserveRatio(true);
        ivExtractedImage.setFitHeight(640.0);
        extractedPane.getChildren().add(ivExtractedImage);


        // Contents of the segmentation / identification tab
        AnnotationDisplayStateManager adsm = new AnnotationDisplayStateManager(0, total_annotations);

        BorderPane segmentationPane = new BorderPane();
        Button segmentationAnnotationTogglePrev = new Button("Previous");
        Button segmentationAnnotationToggleNext = new Button("Next");
        GridPane segmentationContentPane = new GridPane();
        ImageView ivAnnotation = new ImageView(getAnnotations(page).get(adsm.getCurrent()));
        ivAnnotation.setPreserveRatio(true);
        ivAnnotation.setFitWidth(300.0);

        //Label lblClassification = new Label("Classification: " + getAnnotationClassifications(page).get(adsm.getCurrent()));
        segmentationContentPane.getChildren().add(ivAnnotation);
        //segmentationContentPane.getChildren().add(lblClassification);
        //segmentationContentPane.setRowIndex(lblClassification, 2);

        segmentationPane.setLeft(segmentationAnnotationTogglePrev);
        segmentationPane.setRight(segmentationAnnotationToggleNext);
        segmentationPane.setCenter(segmentationContentPane);

        // Make the next and previous buttons work


        // next button
        segmentationAnnotationToggleNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ivAnnotation.setImage(getAnnotations(page).get(adsm.getNext()));
                //lblClassification.setText("Classification: " + getAnnotationClassifications(page).get(adsm.getCurrent()));
            }
        });

        segmentationAnnotationTogglePrev.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ivAnnotation.setImage(getAnnotations(page).get(adsm.getPrev()));
                //lblClassification.setText("Classification: " + getAnnotationClassifications(page).get(adsm.getCurrent()));
            }
        });

        // Define the tabs and their contents

        Tab originalTab = new Tab();
        originalTab.setText("Original page");
        originalTab.setClosable(false);
        originalTab.setContent(originalPane);
        tpViewSceneContent.getTabs().add(originalTab);

        Tab scannedTab = new Tab();
        scannedTab.setText("Scanned page");
        scannedTab.setClosable(false);
        scannedTab.setContent(scannedPane);
        tpViewSceneContent.getTabs().add(scannedTab);

        Tab alignmentTab = new Tab();
        alignmentTab.setText("Aligned page");
        alignmentTab.setClosable(false);
        alignmentTab.setContent(alignedPane);
        tpViewSceneContent.getTabs().add(alignmentTab);

        Tab extractionTab = new Tab();
        extractionTab.setText("Extracted annotations");
        extractionTab.setClosable(false);
        extractionTab.setContent(extractedPane);
        tpViewSceneContent.getTabs().add(extractionTab);

        Tab segmentationTab = new Tab();
        segmentationTab.setText("Segmentation / Identification");
        segmentationTab.setClosable(false);
        segmentationTab.setContent(segmentationPane);
        tpViewSceneContent.getTabs().add(segmentationTab);


        // The view consists of a BorderPane which contains a TabPane for content and a BorderPane for the page controls
        BorderPane bpViewScene = new BorderPane();
        // bpViewScene.setBottom(pageToggleControlsPane); // TODO: add support for viewing more than just Page 1, and re-enable these controls
        bpViewScene.setCenter(tpViewSceneContent);

        Scene viewScene = new Scene(bpViewScene, 900, 700);
        s.setScene(viewScene);
        s.show();
    }


    @Override
    public void start(Stage primaryStage) {
        showFileSelectionScene(primaryStage);
    }

    // Inner class to manage state in the Annotation Display tab in ViewScene
    class AnnotationDisplayStateManager{
        int current_annotation_display;
        int total_annotations;

        public AnnotationDisplayStateManager(int c, int t){
            current_annotation_display = c;
            total_annotations = t;
        }

        int getNext(){
            if (current_annotation_display + 1 < total_annotations){
                current_annotation_display++;
            }
            return current_annotation_display;
        }

        int getPrev(){
            if (current_annotation_display > 0){
                current_annotation_display--;
            }
            return current_annotation_display;
        }

        int getCurrent(){
            return current_annotation_display;
        }
    }
}
