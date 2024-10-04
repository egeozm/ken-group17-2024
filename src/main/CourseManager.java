package src.main;

import java.util.ArrayList;
import java.util.List;

public class CourseManager {
    private static CourseManager instance;
    private final List<Course> courses;

    private CourseManager() {
        courses = new ArrayList<>();
        loadCoursesFromCsv();
    }

    // Singleton method to get the single instance of CourseManager
    public static CourseManager getInstance() {
        if (instance == null) {
            instance = new CourseManager();
        }
        return instance;
    }

    // Method to load courses and create CourseRecord objects
    private void loadCoursesFromCsv() {
        // Assuming you use this method to load your CSV file
        String[][] dataSet = TwoDimensionalArray.readCsvInto2DArray("src/csvFiles/GraduateGrades.csv");

        if (dataSet != null && dataSet.length > 0) {
            // Extract course names and calculate average and standard deviation
            for (int i = 1; i < dataSet[0].length; i++) { // Start from index 1 to skip "StudentID"
                String courseName = dataSet[0][i];
                double averageGrade = calculateAverageGrade(dataSet, i);
                double standardDeviation = calculateStandardDeviation(dataSet, i, averageGrade);
                Course course = new Course(courseName, averageGrade, standardDeviation);
                courses.add(course);
            }
        }
    }

    // Method to calculate the average grade for a specific course column
    private double calculateAverageGrade(String[][] dataSet, int columnIndex) {
        double sum = 0;
        int count = 0;

        for (int i = 1; i < dataSet.length; i++) { // Start from 1 to skip header
            try {
                sum += Double.parseDouble(dataSet[i][columnIndex]);
                count++;
            } catch (NumberFormatException e) {
                System.out.println("Invalid grade at row " + i + ", column " + columnIndex + ": " + dataSet[i][columnIndex]);
            }
        }

        return (count > 0) ? (sum / count) : 0.0;
    }

    // Method to calculate the standard deviation for a specific course column
    private double calculateStandardDeviation(String[][] dataSet, int columnIndex, double mean) {
        double sum = 0;
        int count = 0;

        for (int i = 1; i < dataSet.length; i++) { // Start from 1 to skip header
            try {
                double grade = Double.parseDouble(dataSet[i][columnIndex]);
                sum += Math.pow(grade - mean, 2);
                count++;
            } catch (NumberFormatException ignored) {
                // Ignore invalid grades
            }
        }

        return (count > 0) ? Math.sqrt(sum / count) : 0.0;
    }


    public List<Course> getCourseRecords() {
        return courses;
    }

    public Course getCourseRecordByName(String name) {
        for (Course record : courses) {
            if (record.getName().equals(name)) {
                return record;
            }
        }
        return null;
    }
}
