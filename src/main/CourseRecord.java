package src.main;

public class CourseRecord {
    private String name;
    private double averageGrade;
    private double standardDeviation;

    public CourseRecord(String name, double averageGrade, double standardDeviation) {
        this.name = name;
        this.averageGrade = averageGrade;
        this.standardDeviation = standardDeviation;
    }

    public String getName() {
        return name;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }


}

