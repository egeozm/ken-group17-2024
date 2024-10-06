package src.main;

import java.util.List;

public class GraduatedStudentRecord {
    private final int studentID;
    private final double GPA;
    private final List<Double> courseGrades;

    public GraduatedStudentRecord(int studentID, List<Double> courseGrades) {
        this.studentID = studentID;
        this.courseGrades = courseGrades;
        this.GPA = calculateGPA();
    }

    private double calculateGPA() {
        double sum = 0;
        int count = courseGrades.size();

        for (double grade : courseGrades) {
            sum += grade;
        }

        return (count > 0) ? (sum / count) : 0.0;
    }

    public int getStudentID() {
        return studentID;
    }

    public double getGPA() {
        return GPA;
    }

}
