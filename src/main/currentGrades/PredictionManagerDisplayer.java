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
        courseManager.loadGraduateGrades();
        SimilarCourses similarCourses = new SimilarCourses(courseManager);
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();
        PredictionManager2 predictionManager2 = new PredictionManager2(currentStudentManager, courses, studentInfoManager, similarCourses);
        predictionManager2.findBestPropertyForUncomplitedCourses(courses);

    }

}
