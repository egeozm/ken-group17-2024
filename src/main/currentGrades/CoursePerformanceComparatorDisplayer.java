package src.main.currentGrades;

import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;
import src.main.dataHandle.CoursePerformanceComparator;
import src.main.dataHandle.CurrentStudentManager;
import src.main.dataHandle.StudentInfoManager;

import java.util.List;

public class CoursePerformanceComparatorDisplayer {
    public static void main(String[] args) {
        
    
CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();
        CoursePerformanceComparator comparator = new CoursePerformanceComparator(currentStudentManager, courses);
        comparator.comparePerformanceAcrossCourses("Vortex Quantum Mechanics", "Aether Resonance");
}}
