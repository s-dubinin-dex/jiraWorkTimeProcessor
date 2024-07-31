import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;

public class Controller {
    @FXML
    private TextField csvTimeSheetSourceFile;
    @FXML
    private TextField xlsxTimeSheetDestinationFolder;
    @FXML
    private Button chooseCSVSourceFile;

    @FXML
    public void initialize(){
        chooseCSVSourceFile.requestFocus();
    }

    @FXML
    private void handlerChooseCSVSourceFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null){
            csvTimeSheetSourceFile.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handlerChooseXLSXDestinationFolder(){
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File directory = directoryChooser.showDialog(null);
        if (directory != null){
            xlsxTimeSheetDestinationFolder.setText(directory.getAbsolutePath());
        }
    }

    @FXML
    private void handlerProcessFile(){
        String csvTimeSheetSourcePath = csvTimeSheetSourceFile.getText();
        String xlsxTimeSheetDestinationFolderPath = xlsxTimeSheetDestinationFolder.getText();
        String fileName = "output.xlsx";

        if (csvTimeSheetSourcePath == null || csvTimeSheetSourcePath.isEmpty()){
            WorKTimeApplication.showAlert(Alert.AlertType.ERROR, "Ошибка", "Не выбран CSV файл");
            return;
        }

        if (xlsxTimeSheetDestinationFolderPath == null || xlsxTimeSheetDestinationFolderPath.isEmpty()){
            WorKTimeApplication.showAlert(Alert.AlertType.ERROR, "Ошибка", "Не выбран путь сохранения");
            return;
        }

        WorkTimeProcessor workTimeProcessor = new WorkTimeProcessor(csvTimeSheetSourcePath, xlsxTimeSheetDestinationFolderPath, fileName);
        workTimeProcessor.processCSVFile();

        WorKTimeApplication.showAlert(Alert.AlertType.WARNING, "Успешно", "Файл успешно обработан");
    }
}
