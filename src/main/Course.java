package src.main;

import java.util.List;

public class Course {
    private final String name;
    private final double averageGrade;
    private final double standardDeviation;
    private final List<Double> grades; // Add a list to store individual grades for each course
    private final double mostCommonGrade;



    public Course(String name, double averageGrade, double standardDeviation, List<Double> grades, double mostCommonGrade) {
        this.name = name;
        this.averageGrade = averageGrade;
        this.standardDeviation = standardDeviation;
        this.grades = grades;
        this.mostCommonGrade = mostCommonGrade;
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

    public List<Double> getGrades() {
        return grades;
    }

    public double getMostCommonGrade() {
        return mostCommonGrade;
    }

}

