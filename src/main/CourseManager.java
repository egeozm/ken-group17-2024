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
            for (int i = 1; i < dataSet[0].length; i++) { // Skip the "StudentID" column
                String courseName = dataSet[0][i];
                List<Double> grades = new ArrayList<>();
                double sum = 0;
                int count = 0;

                for (int j = 1; j < dataSet.length; j++) { // Skip the header row
                    try {
                        double grade = Double.parseDouble(dataSet[j][i]);
                        grades.add(grade);
                        sum += grade; // for calculating average grade
                        count++; // for total
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid grade at row " + j + ", column " + i);
                    }
                }

                double averageGrade = (count > 0) ? (sum / count) : 0;
                double standardDeviation = calculateStandardDeviation(grades, averageGrade);
                courses.add(new Course(courseName, averageGrade, standardDeviation, grades));
            }
        }
    }


    // Method to calculate the standard deviation for a specific course column
    private double calculateStandardDeviation(List<Double> grades, double mean) {
        double sum = 0;
        for (double grade : grades) {
            sum += Math.pow(grade - mean, 2);
        }
        return (grades.size() > 0) ? Math.sqrt(sum / grades.size()) : 0;
    }


    public List<Course> getCourseRecords() {
        return courses;
    }

}
