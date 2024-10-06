package src.main;

import java.util.List;

public class GradeSpread {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        List<Course> courses = courseManager.getCourseRecords();
        System.out.println("Grade Spread for Each Course:\n");
        for (Course course : courses) {
            System.out.println(course.getName() + ": " + course.getMostCommonGrade());
        }

}
}