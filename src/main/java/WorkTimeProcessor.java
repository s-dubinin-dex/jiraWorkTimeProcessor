import javafx.scene.control.Alert;
import models.WorkTimeRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class WorkTimeProcessor {

    private final String csvSourceFilePath;
    private final String xlsxDestinationFilePath;
    private final String fileName;

    private List<WorkTimeRecord> workSheetWorkTimeRecords = new ArrayList<>();

    public WorkTimeProcessor(String csvSourceFilePath, String xlsxDestinationFilePath, String fileName) {
        this.csvSourceFilePath = csvSourceFilePath;
        this.xlsxDestinationFilePath = xlsxDestinationFilePath;
        this.fileName = fileName;
    }

    public void processCSVFile() {
        workSheetWorkTimeRecords = readCSVFile();
        if (workSheetWorkTimeRecords == null){
            // TODO: проверить алерт
            WorkTimeApplication.showAlert(Alert.AlertType.ERROR, "Ошибка", "CSV файл не содержит данных для обработки");
            return;
        }
        workSheetWorkTimeRecords.sort(Comparator.comparing(WorkTimeRecord::getDate));
        generateXLSX();
    }

    private List<WorkTimeRecord> readCSVFile(){
        List<WorkTimeRecord> workSheetWorkTimeRecords = new ArrayList<>();

        SimpleDateFormat dateFormatterFromCSV = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        try (Reader reader = new FileReader(csvSourceFilePath)) {

            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            for (CSVRecord csvRecord : csvParser) {
                String issueKey = csvRecord.get("issueKey");
                String description = csvRecord.get("description");
                Date date = dateFormatterFromCSV.parse(csvRecord.get("date"));
                float workedHours = (float) Integer.parseInt(csvRecord.get("spentTime")) / (60 * 60);
                String issueSummary = csvRecord.get("issueSummary");

                workSheetWorkTimeRecords.add(new WorkTimeRecord(issueKey, description, date, workedHours, issueSummary));
            }
            return workSheetWorkTimeRecords;
        } catch (ParseException | IOException e) {
            //TODO: Добавить алерт, почему не удалось обработать файл
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void generateXLSX(){

        String filePath = xlsxDestinationFilePath + "\\" + fileName;

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Sheet 1");
        sheet.setColumnWidth(0, 100 * 256);
        sheet.setColumnWidth(1, 5 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 10 * 256);
        sheet.setColumnWidth(4, 10 * 256);

        Row header = sheet.createRow(0);
        //TODO: сделать заголовки жирными
        //TODO: настроить ширину заголовков
        //TODO: обработать исключение того, что файл занят другим процессом
        header.createCell(0, CellType.STRING).setCellValue("Название задачи");
        header.createCell(1, CellType.STRING).setCellValue("Часы");
        header.createCell(2, CellType.STRING).setCellValue("Дата");
        header.createCell(3, CellType.STRING).setCellValue("IssueKey");
        header.createCell(3, CellType.STRING).setCellValue("IssueSummary");


        int rowDataIndex = 1;
        for (WorkTimeRecord workTimeRecord : this.workSheetWorkTimeRecords){
            Row row = sheet.createRow(rowDataIndex);
            row.createCell(0, CellType.STRING).setCellValue(workTimeRecord.getDescription());
            row.createCell(1, CellType.NUMERIC).setCellValue(workTimeRecord.getWorkedHours());
            row.createCell(2, CellType.STRING).setCellValue(workTimeRecord.getDateWithFormat("dd.MM.yyy"));
            row.createCell(3, CellType.STRING).setCellValue(workTimeRecord.getIssueKey());
            row.createCell(4, CellType.STRING).setCellValue(workTimeRecord.getIssueSummary());
            rowDataIndex++;
        }

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}