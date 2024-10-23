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
    

    
    public ArrayList<ArrayList<String>> gatherStudentsByNeuroSynapticLevel(){
        DifferenceInAverageGradeForPropertyChecker checker = new DifferenceInAverageGradeForPropertyChecker();
        
        
        ArrayList<ArrayList<String>> studentIDHolder = new ArrayList<ArrayList<String>>();
       

        for (StudentInfoRecord student : checker.students) {
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("low")) {
                
                ArrayList<String> studendIDlow = new ArrayList<>();
                studendIDlow.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDlow);
                
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("medium")) {
                
                ArrayList<String> studendIDmedium = new ArrayList<String>();
                studendIDmedium.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDmedium);
                
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("high")) {
                
                ArrayList<String> studendIDhigh = new ArrayList<String>();
                studendIDhigh.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDhigh);
                
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("full")) {
                
                ArrayList<String> studendIDfull = new ArrayList<String>();
                studendIDfull.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDfull);
               
            }
            if (student.getNeuroSynapticInterfaceLevel().equalsIgnoreCase("nothing")) {
                
                ArrayList<String> studendIDnothing = new ArrayList<String>();
                studendIDnothing.add(String.valueOf(student.getStudentID()));
                studentIDHolder.add(studendIDnothing);
                
            }
            
        
        
     }
     return studentIDHolder;
    }
}
