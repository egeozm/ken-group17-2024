package src.main.graduatedGrades;

import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;
import src.main.dataHandle.SimilarCourses;

import java.util.List;

public class SimilarCoursesTester {

    public static void main(String[] args) {
        
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadGraduateGrades();

        SimilarCourses similarCoursesFinder = new SimilarCourses(courseManager);

        
        double similarityBaseValue = 0.0; 

        
        double highestSimilarity = 0.0;
        Course courseWithHighestSimilarity1 = null;
        Course courseWithHighestSimilarity2 = null;

        
        List<Course> courses = courseManager.getCourseRecords();
        for (Course targetCourse : courses) {
            System.out.printf("\nSimilarities for course '%s':\n", targetCourse.getName());

            
            Course mostSimilarCourse = similarCoursesFinder.findMostSimilarCourse(targetCourse);
            if (mostSimilarCourse != null) {
                double similarity = SimilarCourses.pearsonCorrelation(targetCourse.getGrades(), mostSimilarCourse.getGrades());

                
                if (similarity > highestSimilarity) {
                    highestSimilarity = similarity;
                    courseWithHighestSimilarity1 = targetCourse;
                    courseWithHighestSimilarity2 = mostSimilarCourse;
                }
            } else {
                System.out.println("  No similar course found.");
            }

            
            List<Course> similarCourses = similarCoursesFinder.findSimilarCourses(targetCourse, similarityBaseValue);
            if (similarCourses.isEmpty()) {
                System.out.println("  No courses found.");
            } else {
                for (Course similarCourse : similarCourses) {
                    double similarity = SimilarCourses.pearsonCorrelation(targetCourse.getGrades(), similarCourse.getGrades());
                    System.out.println(similarCourse.getName() + " " + similarity);
                }
            }
        }

       
        if (courseWithHighestSimilarity1 != null && courseWithHighestSimilarity2 != null) {
            System.out.println("\nHighest similarity found is between" + courseWithHighestSimilarity1.getName() + "and" + courseWithHighestSimilarity2.getName() +  "with a similarity  of: " + highestSimilarity);
        } else {
            System.out.println("\nNo courses with similarity found.");
        }
    }
}
