package src.main.predictGrades;

import java.util.ArrayList;

public class StudentAnalyzer {
    private String[][] data;
    ArrayList<GraduatedStudent> graduatedStudents = new ArrayList<>();
    public StudentAnalyzer(String[][] data) {
        this.data = data;

        for (int i = 1; i < data.length; i++) {
            ArrayList<Double> grades = new ArrayList<>();
            for (int j = 1; j < data[i].length; j++) {
                if (data[i][j].contentEquals("NG")) {
                    grades.add(null);
                }else {
                    grades.add(Double.parseDouble(data[i][j]));
                }
            }
            GraduatedStudent gStudent = new GraduatedStudent(Integer.parseInt(data[i][0]),grades);
            graduatedStudents.add(gStudent);
        }
    }
    public void findCumLaude () {
        int count = 0;
        System.out.println("Students who graduated Cum-Laude:");
        for (GraduatedStudent student : graduatedStudents) {
            if(student.getGPA() >= 8.25){
                count++;
                System.out.println("Student ID: " + student.getStudentID() + " | GPA: " + student.getGPA());
            }
        }
        System.out.println("There are " + count + " students who graduated cum-laude");
    }
}
