package src.main.predictGrades;

import java.util.ArrayList;

public class SplitResult {
    private ArrayList<Record> leftRecords;
    private ArrayList<Record> rightRecords;
    private String property;
    private String splitPoint;

    public SplitResult(ArrayList<Record> leftRecords, ArrayList<Record> rightRecords, String property, String splitPoint) {
        this.leftRecords = leftRecords;
        this.rightRecords = rightRecords;
        this.property = property;
        this.splitPoint = splitPoint;
    }

    public ArrayList<Record> getLeftRecords() {
        return leftRecords;
    }
    public ArrayList<Record> getRightRecords() {
        return rightRecords;
    }
    public String getProperty() {
        return property;
    }
    public String getSplitPoint() {
        return splitPoint;
    }
}
