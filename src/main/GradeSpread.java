package src.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GradeSpread {

    public static void main(String[] args) {
        String filePath = "src/csvFiles/GraduateGrades.csv";
        List<Double> grades = readGradesFromFile(filePath);

        if (grades.isEmpty()) {
            System.out.println("No grades found in the file.");
            return;
        }

        double averageGrade = calculateAverage(grades);
        double standardDeviation = calculateStandardDeviation(grades, averageGrade);

        Course courseInstance = new Course("Course Name", averageGrade, standardDeviation, grades);

        System.out.println("Average Grade: " + courseInstance.getAverageGrade());
        System.out.println("Standard Deviation: " + courseInstance.getStandardDeviation());
        System.out.println("Grade Spread: " + courseInstance.getStandardDeviation());
    }

    private static List<Double> readGradesFromFile(String filePath) {
        List<Double> grades = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] gradeStrings = line.split(",");
                for (String gradeString : gradeStrings) {
                    grades.add(Double.parseDouble(gradeString.trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grades;
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