package src.main;

public class GrauatedStudentRecord {
    private int studentID;
    private double GPA;

    public GrauatedStudentRecord(int studentID, double GPA) {
        this.studentID = studentID;
        this.GPA = GPA;

    }

    public int getStudentID() {
        return studentID;
    }

    public double getGPA() {
        return GPA;
    }
}
