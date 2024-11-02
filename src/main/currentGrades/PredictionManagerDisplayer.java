package src.main.currentGrades;

import src.main.dataHandle.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionManagerDisplayer {
    public static void main(String[] args) {
        CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = new ArrayList<>();
        SimilarCourses similarCourses = new SimilarCourses(courseManager);
        courses = courseManager.getCourseRecords();
        //PredictionManager predictionManager = new PredictionManager(currentStudentManager, courses, studentInfoManager, similarCourses);
        PredictionManager2 predictionManager2 = new PredictionManager2(currentStudentManager, courses, studentInfoManager, similarCourses);
        //predictionManager2.compareAverageGradeForProperty("Vortex Quantum Mechanics", "Telepathic Synchronisation Index", "B");
        //predictionManager.compareAverageGradeUsingOtherCourses("Vortex Quantum Mechanics", "Cybernetic Ethics", "Telepathic Synchronisation Index", "B");
        //predictionManager.compareCourseToAllOtherCourses("Vortex Quantum Mechanics", "Telepathic Synchronisation Index", "A");
//        predictionManager.findBestPropertyForCourse("Arkonian Warfare Tactics");
//        predictionManager.findBestPropertyForCourse("ExoGenetics Evolution");
//        predictionManager.findBestPropertyForCourse("Transdimensional Navigation");
       System.out.println("AAAAAAAAAA");
       //System.out.println(courses);
        //predictionManager.predictGradeForUncompletedCourse("Vortex Quantum Mechanics", 212055);
      //  predictionManager.findBestPropertyOrCombinationForCourse("Vortex Quantum Mechanics");
        //predictionManager2.compareAverageGradeForProperty("Transdimensional Navigation","Neuro-Synaptic Interface Level", "medium");
        predictionManager2.findBestPropertyForUncomplitedCourses(courses);
    }

}
