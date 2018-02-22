package MixedRealityPDF.UserInterface;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MixedRealityPDFGUI extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    File originalFile;
    File scannedFile;

    private void setOriginalFile(File originalFile){
        this.originalFile = originalFile;
    }

    private void setScannedFile(File scannedFile){
        this.scannedFile = scannedFile;
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
        gp.setRowIndex(lblUpperText, 0);
        gp.setColumnSpan(lblUpperText, 2);
        gp.getChildren().add(lblUpperText);

        Label lblOriginalText = new Label("Original document");
        gp.setRowIndex(lblOriginalText, 1);
        gp.getChildren().add(lblOriginalText);

        Button btnChooseOriginalFile = new Button("Choose");
        gp.setRowIndex(btnChooseOriginalFile, 2);
        gp.getChildren().add(btnChooseOriginalFile);

        Label lblOriginalFilePath = new Label("No file selected");
        gp.setRowIndex(lblOriginalFilePath, 2);
        gp.setColumnIndex(lblOriginalFilePath, 1);
        gp.getChildren().add(lblOriginalFilePath);

        boolean originalFileValid = false;

        Label lblScannedText = new Label("Scanned document");
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
        gp.setColumnIndex(btnStart, 1);
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


        // set ColumnConstraints to make this scene fit to the window size
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        gp.getColumnConstraints().add(column1);
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(50);
        gp.getColumnConstraints().add(column2);

        // Set some padding
        gp.setHgap(10.0);
        gp.setVgap(10.0);

        Scene fileSelectionScene = new Scene(gp, 400, 300);
        stage.setScene(fileSelectionScene);
        stage.show();
    }


    @Override
    public void start(Stage primaryStage) {
        showFileSelectionScene(primaryStage);
    }


    private File chooseFile(FileChooser fc, Stage s){
        return fc.showOpenDialog(s);
    }
}
