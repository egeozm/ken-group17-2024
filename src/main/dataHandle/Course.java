package src.main.dataHandle;

import java.util.List;

public class Course {
    private final String name;
    private final int columnIndex;  // Column index from the CSV file
    private final double averageGrade;
    private final double standardDeviation;
    private final List<Double> grades; // Add a list to store individual grades for each course
    private final double mostCommonGrade;


    public Course(String name, int columnIndex, double averageGrade, double standardDeviation, List<Double> grades, double mostCommonGrade) {
        this.name = name;
        this.columnIndex = columnIndex;
        this.averageGrade = averageGrade;
        this.standardDeviation = standardDeviation;
        this.grades = grades;
        this.mostCommonGrade = mostCommonGrade;
    }

    public String getName() {
        return name;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public List<Double> getGrades() {
        return grades;
    }

    public double getMostCommonGrade() {
        return mostCommonGrade;
    }

    public double getLowestGrade(List<Double> grades) {
        if (grades.isEmpty()) {
            return 0.0; // Return 0 or any default value for empty lists.
        }

        double minGrade = Double.MAX_VALUE;
        for (double grade : grades) {
            if (grade < minGrade) {
                minGrade = grade;
            }
        }
        return minGrade;
    }

    public double getHighestGrade(List<Double> grades) {
        if (grades.isEmpty()) {
            return 0.0; // Return 0 or any default value for empty lists.
        }

        double maxGrade = Double.MIN_VALUE;
        for (double grade : grades) {
            if (grade > maxGrade) {
                maxGrade = grade;
            }
        }
        return maxGrade;
    }


}

