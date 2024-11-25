package src.main.dataHandle;

import java.util.ArrayList;
import java.util.List;

public class CourseManager {
    private static CourseManager instance;
    private final List<Course> currentCourses;
    private final List<Course> graduatedCourses;


    private CourseManager() {
        currentCourses = new ArrayList<>();
        graduatedCourses = new ArrayList<>();
    }

    // Singleton method to get the single instance of CourseManager
    public static CourseManager getInstance() {
        if (instance == null) {
            instance = new CourseManager();
        }
        return instance;
    }

    // Method to load courses from a specific CSV file
    public void loadCoursesFromCsv(String csvFilePath, boolean isGraduated) {
        // Clear the existing courses if switching between datasets
        List<Course> courseList = isGraduated ? graduatedCourses : currentCourses;
        courseList.clear();

        String[][] dataSet = TwoDimensionalArray.readCsvInto2DArray(csvFilePath);

        if (dataSet != null && dataSet.length > 0) {
            for (int i = 1; i < dataSet[0].length; i++) { // Skip the "StudentID" column
                String courseName = dataSet[0][i].trim();
                List<Double> grades = new ArrayList<>();
                double sum = 0;
                int count = 0;

                for (int j = 1; j < dataSet.length; j++) { // Skip the header row
                    String gradeStr = dataSet[j][i].trim(); // Trim to remove any extra spaces
                    if (gradeStr.equalsIgnoreCase("NG") || gradeStr.isEmpty()) {
                        grades.add(null);
                        continue;
                    }
                    try {
                        double grade = Double.parseDouble(gradeStr);
                        grades.add(grade);
                        sum += grade; // for calculating average grade
                        count++;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid grade at row " + j + ", column " + i);
                        grades.add(null);
                    }
                }

                double averageGrade = (count > 0) ? (sum / count) : 0;
                double standardDeviation = calculateStandardDeviation(grades, averageGrade);
                double mostCommonGrade = calculateMostCommonGrade(grades);

                courseList.add(new Course(courseName, i - 1, averageGrade, standardDeviation, grades, mostCommonGrade));
            }
        }
    }

    // Switch to load Current Grades
    public void loadCurrentGrades() {
        loadCoursesFromCsv("src/csvFiles/CurrentGrades.csv", false);
    }

    // Switch to load Graduate Grades
    public void loadGraduateGrades() {
        loadCoursesFromCsv("src/csvFiles/GraduateGrades.csv", true);
    }

    // Method to calculate the standard deviation for a specific course column
    private double calculateStandardDeviation(List<Double> grades, double mean) {
        double sum = 0;
        int count = 0;
        for (Double grade : grades) {
            if (grade != null) {
                sum += Math.pow(grade - mean, 2);
                count++;
            }
        }
        return (grades.size() > 0) ? Math.sqrt(sum / count) : 0;
    }

    public static double calculateMostCommonGrade(List<Double> grades) {
        if (grades.isEmpty()) {
            return 0;
        }

        int[] frequency = new int[11]; // the grades are between 0-10

        for (Double grade : grades) {
            if (grade != null) {
                int index = grade.intValue();
                if (index >= 0 && index <= 10) {
                    frequency[index]++;
                }
            }
        }

        int mostCommonGrade = 0;
        int maxFrequency = 0;

        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] > maxFrequency) {
                maxFrequency = frequency[i];
                mostCommonGrade = i;
            }
        }

        return mostCommonGrade;
    }

    // Getters for course records
    public List<Course> getCurrentCourses() {
        return currentCourses;
    }

    public Course getCourseByName(String name) {
        for (Course course : graduatedCourses) {
            if (course.getName().equalsIgnoreCase(name)) {
                return course;
            }
        }
        return null;


    }

    public List<Course> getGraduatedCourses() {
        return graduatedCourses;
    }
}
