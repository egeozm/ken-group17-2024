package project1_1.main.graduatedGrades;

import project1_1.main.dataHandle.Course;
import project1_1.main.dataHandle.CourseManager;

import java.util.List;
import java.util.Collections;

public class GradeSpreadDisplayer {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadGraduateGrades();
        List<Course> courses = courseManager.getGraduatedCourses();

        // Iterate through each course and use the preloaded grades
        for (Course course : courses) {
            List<Double> grades = course.getGrades();

            if (!grades.isEmpty()) {
                // Sort grades for easier calculations of percentiles
                Collections.sort(grades);
                int size = grades.size();
                int quarterSize = size / 4;

                // Calculate the first and last quarter's median
                List<Double> firstQuarter = grades.subList(0, quarterSize);
                double medianFirstQuarter = calculateMedian(firstQuarter);

                List<Double> lastQuarter = grades.subList(size - quarterSize, size);
                double medianLastQuarter = calculateMedian(lastQuarter);

                // Calculate the mean of the middle 50%
                List<Double> middleHalf = grades.subList(quarterSize, size - quarterSize);
                double meanMiddleHalf = calculateMean(middleHalf);

                // Print course details
                System.out.println("Course: " + course.getName());
                System.out.printf("Grade spread: %.2f\n", course.getHighestGrade(grades) - course.getLowestGrade(grades));
                System.out.printf("Median of first 25%%: %.2f\n", medianFirstQuarter);
                System.out.printf("Median of last 25%%: %.2f\n", medianLastQuarter);
                System.out.printf("Mean of middle 50%%: %.2f\n\n", meanMiddleHalf);
            } else {
                System.out.println("No grades found for course: " + course.getName());
            }
        }
    }

    private static double calculateMedian(List<Double> grades) {
        int size = grades.size();
        if (size % 2 == 0) {
            return (grades.get(size / 2 - 1) + grades.get(size / 2)) / 2.0;
        } else {
            return grades.get(size / 2);
        }
    }

    private static double calculateMean(List<Double> grades) {
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }
}
