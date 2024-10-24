package src.main.currentGrades;

import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;
import src.main.dataHandle.CurrentStudentManager;
import src.main.dataHandle.PredictionManager;
import src.main.dataHandle.StudentInfoManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionManagerTester {
    public static void main(String[] args) {
        CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();
        PredictionManager predictionManager = new PredictionManager(currentStudentManager, courses, studentInfoManager);
        
        //Change the values below to check for a different course or property

        predictionManager.compareAverageGradeForProperty("Arkonian Warfare Tactics", "Neuro-Synaptic Interface Level", "low");
    }

}
