package src.main;

import java.util.Arrays;
import java.util.List;

public class GradeSpread {

    public static void main(String[] args) {
        String name = "Course Name";
        List<Double> grades = Arrays.asList(85.0, 90.0, 78.0, 92.0, 88.0);
        
        // Average and standard deviation
        double averageGrade = calculateAverage(grades);
        double standardDeviation = calculateStandardDeviation(grades, averageGrade);
        double mostCommonGrade = calculateAverage(grades);

        // Course instance
        Course courseInstance = new Course(name, averageGrade, standardDeviation, grades, mostCommonGrade);

        // Access the values
        double average = courseInstance.getAverageGrade();
        double stdDev = courseInstance.getStandardDeviation();

        System.out.println("Average Grade: " + average);
        System.out.println("Standard Deviation: " + stdDev);
        System.out.println("Grade Spread: " + stdDev);
    }

    private static double calculateAverage(List<Double> grades) {
        double sum = 0.0;
        for (double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }

    private static double calculateStandardDeviation(List<Double> grades, double average) {
        double sum = 0.0;
        for (double grade : grades) {
            sum += Math.pow(grade - average, 2);
        }
        return Math.sqrt(sum / grades.size());
    }
}