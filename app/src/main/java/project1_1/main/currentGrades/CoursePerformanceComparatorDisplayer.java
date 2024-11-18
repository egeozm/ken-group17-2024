package project1_1.main.currentGrades;

import project1_1.main.dataHandle.Course;
import project1_1.main.dataHandle.CourseManager;
import project1_1.main.dataHandle.CoursePerformanceComparator;
import project1_1.main.dataHandle.CurrentStudentManager;
import project1_1.main.dataHandle.StudentInfoManager;

import java.util.List;

public class CoursePerformanceComparatorDisplayer {
    public static void main(String[] args) {
        
    
CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getGraduatedCourses();
        CoursePerformanceComparator comparator = new CoursePerformanceComparator(currentStudentManager, courses);
        comparator.comparePerformanceAcrossCourses("Vortex Quantum Mechanics", "Aether Resonance");
}}
