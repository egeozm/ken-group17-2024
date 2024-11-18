package project1_1.main.dataHandle;

import java.util.List;


public class CurrentStudentRecord {
    private final int studentID;
    private final List<Double> courseGrades;
    private final double GPA;

    public CurrentStudentRecord(int studentID, List<Double> courseGrades) {
        this.studentID = studentID;
        this.courseGrades = courseGrades;
        this.GPA = calculateGPA();
    }

    private double calculateGPA() {
        double sum = 0;
        int count = 0;

        for (Double grade : courseGrades) {
            if (grade != null) {  // Only include non-null grades
                sum += grade;
                count++;
            }
        }
        return (count > 0) ? (sum / count) : 0.0;
    }

    // Method to get the list of completed courses (non-null grades)
    public List<Double> getCompletedCourseGrades() {
        return courseGrades.stream().filter(grade -> grade != null).toList();
    }

    // Getters for student ID, GPA, and course grades
    public int getStudentID() {
        return studentID;
    }

    public double getGPA() {
        return GPA;
    }

    public List<Double> getCourseGrades() {
        return courseGrades;
    }
}
