package project1_1.main.currentGrades;

import project1_1.main.dataHandle.*;

import java.util.List;

import project1_1.main.dataHandle.PredictionManager2;
import project1_1.main.dataHandle.SimilarCourses;

public class PredictionManagerDisplayer {
    public static void main(String[] args) {
        // Create CourseManager for current data
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        courseManager.loadGraduateGrades();

        // Retrieve current and graduated course records
        List<Course> currentCourses = courseManager.getCurrentCourses();
        List<Course> graduatedCourses = courseManager.getGraduatedCourses();

        // Initialize SimilarCourses with the graduated data
        SimilarCourses similarCourses = new SimilarCourses(graduatedCourses);


        // Initialize PredictionManager with current data and other dependencies
        CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        PredictionManager2 predictionManager2 = new PredictionManager2(currentStudentManager, currentCourses, studentInfoManager, similarCourses);

        // Run the prediction using current data
        predictionManager2.findBestPropertyForUncompletedCourses(currentCourses);
    }
}