package src.main;

import java.util.Comparator;
import java.util.List;

public class CourseDifficultyIndicator {
    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        List<Course> sortedCourses = courseManager.getCourseRecords();
        sortedCourses.sort(Comparator.comparingDouble(Course::getAverageGrade));

        Course hardestCourse = sortedCourses.get(0);
        Course easiestCourse = sortedCourses.get(sortedCourses.size() - 1);
        System.out.printf("The hardest course is: %s, with average: %.3f, Std Dev: %.3f\n",
                hardestCourse.getName(), hardestCourse.getAverageGrade(), hardestCourse.getStandardDeviation());
        System.out.printf("The easiest course is: %s, with average: %.3f, Std Dev: %.3f\n",
                easiestCourse.getName(), easiestCourse.getAverageGrade(), easiestCourse.getStandardDeviation());

        // Display all sorted courses
        System.out.println("\nSorted list of courses: (Hardest to easiest)\n");
        int count = 1;
        for (Course course : sortedCourses) {
            System.out.printf("Course %d: %s, with average: %.3f, Std Dev: %.3f, most common grade: %.3f\n",
                    count, course.getName(), course.getAverageGrade(), course.getStandardDeviation(),course.getMostCommonGrade());
            count++;
        }
    }
}