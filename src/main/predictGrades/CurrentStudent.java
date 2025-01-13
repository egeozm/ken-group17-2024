package src.main.predictGrades;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class CurrentStudent {

    private final int studentID;
    private final boolean faildCourse;
    private final ArrayList<Integer> coursesEnrolled;
    private final ArrayList<Double> grades;
    private final Double GPA;
    private int yearOfStudy;
    private final String NSI; //Neuro-Synaptic Interface Level
    private final int PCQ; //Plasma Conductivity Quotient
    private final int CAR; //Chrono-Adaptation Rate
    private final String TSI; //Telepathic Synchronisation Index
    private final Double ARC; //Aetheric Resonance Capacity
    public ArrayList<Double> predictedGrades;


    public CurrentStudent(int studentID, ArrayList<Double> grades, ArrayList<String> traits){
        this.studentID = studentID;
        this.grades = grades;
        this.GPA = calculateGPA(grades);
        this.faildCourse = hasFailedCourse(grades);
        this.coursesEnrolled = findCoursesEnrolled(grades);
        this.NSI = traits.get(0);
        this.PCQ = Integer.parseInt(traits.get(1));
        this.CAR = Integer.parseInt(traits.get(2).substring(0,1));
        this.TSI = traits.get(3);
        this.ARC = Double.parseDouble(traits.get(4).substring(0,3));
    }

    public boolean hasFailedCourse(ArrayList<Double> grades){
        boolean hasFailedCourse = false;
        for (int i = 0; i < grades.size(); i++){
            if (grades.get(i) == null)
                continue;
            if (grades.get(i) < 6){
                hasFailedCourse = true;
                break;
            }
        }
        return hasFailedCourse;
    }

    public ArrayList<Double> getGrades(){
        return grades;
    }

    public ArrayList<Double> getPredictedGrades(){
        return predictedGrades;
    }

    public void setPredictedGrades(ArrayList<Double> predictedGrades) {
        this.predictedGrades = new ArrayList<>(predictedGrades); // Ensure a new copy is created
        System.out.println("Predicted grades set for student: " + this.studentID + " -> " + this.predictedGrades);
    }


    public double calculateGPA(ArrayList<Double> grades) {
        int counter = 0;
        double sum = 0;
        for(int i = 0; i < grades.size(); i++){
            if (grades.get(i) == null){
                continue;
            }
            sum += grades.get(i);
            counter++;
        }
        BigDecimal bd = new BigDecimal(sum / counter).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public ArrayList<Integer> findCoursesEnrolled(ArrayList<Double> grades){
        ArrayList<Integer> coursesEnrolled = new ArrayList<>();
        for (Double grade : grades){
            if (grade == null) {
                continue;
            }else {
                coursesEnrolled.add(grades.indexOf(grade));
            }
        }
        return coursesEnrolled;
    }

    public int getStudentID() {
        return studentID;
    }
    public int getYearOfStudy() {
        return yearOfStudy;
    }
    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
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
    public Double getARC() {
        return ARC;
    }

}
