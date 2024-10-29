package src.main.currentGrades;

import src.main.dataHandle.*;

import java.util.Arrays;
import java.util.List;

public class PredictionManagerDisplayer {
    public static void main(String[] args) {
        CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();
        SimilarCourses similarCourses = new SimilarCourses(courseManager);
        PredictionManager predictionManager = new PredictionManager(currentStudentManager, courses, studentInfoManager, similarCourses);
        //predictionManager.compareAverageGradeForProperty("Vortex Quantum Mechanics", "Telepathic Synchronisation Index", "B");
        //predictionManager.compareAverageGradeUsingOtherCourses("Vortex Quantum Mechanics", "Cybernetic Ethics", "Telepathic Synchronisation Index", "B");
        //predictionManager.compareCourseToAllOtherCourses("Vortex Quantum Mechanics", "Telepathic Synchronisation Index", "A");
//        predictionManager.findBestPropertyForCourse("Arkonian Warfare Tactics");
//        predictionManager.findBestPropertyForCourse("ExoGenetics Evolution");
//        predictionManager.findBestPropertyForCourse("Transdimensional Navigation");
        predictionManager.findBestPropertyForCourse("Cybernetic Ethics");

        //predictionManager.predictGradeForUncompletedCourse("ExoGenetics Evolution", 212055);

    }

}
