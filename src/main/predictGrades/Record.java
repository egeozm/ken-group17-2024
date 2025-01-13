package src.main.predictGrades;

public class Record {
    private int id;
    private double grade;
    private String NSI;
    private int PCQ;
    private int CAR;
    private String TSI;
    private double ARC;

    public Record(int id, double grade, String NSI, int PCQ, int CAR, String TSI, double arc) {
        this.id = id;
        this.grade = grade;
        this.NSI = NSI;
        this.PCQ = PCQ;
        this.CAR = CAR;
        this.TSI = TSI;
        this.ARC = arc;
    }

    public int getId() {
        return id;
    }
    public double getGrade() {
        return grade;
    }
    public String getNSI() {
        return NSI;
    }
    public int getPCQ() {
        return PCQ;
    }
    public int getCAR() {
        return CAR;
    }
    public String getTSI() {
        return TSI;
    }
    public double getARC() {
        return ARC;
    }
}
