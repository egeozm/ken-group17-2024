package src.main;

import java.util.List;

public class GradeSpread {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        List<Course> courses = courseManager.getCourseRecords();
        
        for (int i = 0; i < courses.size(); i++) {
        double averageGrade = courses.get(i).getAverageGrade();
        System.out.println(courses.get(i).getName() + " " + courses.get(i).getAverageGrade());
        }

}
}