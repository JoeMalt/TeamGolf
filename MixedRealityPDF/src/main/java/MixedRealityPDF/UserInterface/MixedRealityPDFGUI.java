package MixedRealityPDF.UserInterface;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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

        // TODO: call the code that actually does something
        // Assume this to be blocking


        // Show the original scene and a dialog indicating that processing is complete
    }


    @Override
    public void start(Stage primaryStage) {
        showFileSelectionScene(primaryStage);
    }
}
