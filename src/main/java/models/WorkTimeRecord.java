package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@Getter
public class WorkTimeRecord {
    private String issueKey;
    private String description;
    private Date date;
    private float workedHours;
    private String issueSummary;

    public String getDateWithFormat(String dateFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(this.date);
    }
}
