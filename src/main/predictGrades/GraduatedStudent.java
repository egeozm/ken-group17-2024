package src.main.predictGrades;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class GraduatedStudent {
    private int studentID;
    private Double GPA;
    private ArrayList<Double> grades;
    public GraduatedStudent(int studentID, ArrayList<Double> grades) {
        this.studentID = studentID;
        this.grades = grades;
        this.GPA = calculateGPA(grades);
    }

    public double getGPA() {
        return GPA;
    }
    public int getStudentID() {
        return studentID;
    }
    public double calculateGPA(ArrayList<Double> grades){
        int counter = 0;
        double sum = 0;
        for(double grade : grades){
            sum += grade;
            counter++;
        }
        BigDecimal bd = new BigDecimal(sum / counter).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
