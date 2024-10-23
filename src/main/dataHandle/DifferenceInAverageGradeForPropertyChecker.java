package src.main.dataHandle;

import java.util.*;

public class DifferenceInAverageGradeForPropertyChecker {

    private StudentInfoManager studentInfoManager; 
    private List<StudentInfoRecord> students; 

    public DifferenceInAverageGradeForPropertyChecker() {
        
        studentInfoManager = new StudentInfoManager();
        studentInfoManager.loadStudentsFromSSV(); 
        students = studentInfoManager.getAllStudents(); 
    }

    public static void main(String[] args) {
        
        DifferenceInAverageGradeForPropertyChecker checker = new DifferenceInAverageGradeForPropertyChecker();
        
        
        ArrayList<ArrayList<String>> studentIDHolder = new ArrayList<ArrayList<String>>();
        int totalLow = 0;
        int totalMedium = 0;
        int totalHigh = 0;
        int totalFull = 0;
        int totalNothing = 0;

        for (StudentInfoRecord student : checker.students) {
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("low")) {
                
                ArrayList<String> studendIDlow = new ArrayList<String>();
                studendIDlow.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDlow);
                totalLow++;
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("medium")) {
                
                ArrayList<String> studendIDmedium = new ArrayList<String>();
                studendIDmedium.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDmedium);
                totalMedium++;
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("high")) {
                
                ArrayList<String> studendIDhigh = new ArrayList<String>();
                studendIDhigh.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDhigh);
                totalHigh++;
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("full")) {
                
                ArrayList<String> studendIDfull = new ArrayList<String>();
                studendIDfull.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDfull);
                totalFull++;
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("nothing")) {
                
                ArrayList<String> studendIDnothing = new ArrayList<String>();
                studendIDnothing.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDnothing);
                totalNothing++;
            }
            
        }
        System.out.println("The total amount of students with NeuroSynapticInterfaceLevel 'low' is " + totalLow);
        System.out.println("The total amount of students with NeuroSynapticInterfaceLevel 'medium' is " + totalMedium);
        System.out.println("The total amount of students with NeuroSynapticInterfaceLevel 'high' is " + totalHigh);
        System.out.println("The total amount of students with NeuroSynapticInterfaceLevel 'full' is " + totalFull);
        System.out.println("The total amount of students with NeuroSynapticInterfaceLevel 'nothing' is " + totalNothing);
    }
}
