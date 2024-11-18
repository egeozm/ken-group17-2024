package project1_1.main.graduatedGrades;

import project1_1.main.dataHandle.Course;
import project1_1.main.dataHandle.CourseManager;
import project1_1.main.dataHandle.SimilarCourses;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimilarCoursesDisplayer {

    public static void main(String[] args) {

        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadGraduateGrades();
        List<Course> graduatedCourses = courseManager.getGraduatedCourses();
        SimilarCourses similarCoursesFinder = new SimilarCourses(graduatedCourses);
        double similarityBaseValue = 0.0;
        double highestSimilarity = 0.0;
        Course courseWithHighestSimilarity1 = null;
        Course courseWithHighestSimilarity2 = null;

        for (Course targetCourse : graduatedCourses) {
            System.out.printf("\nSimilarities for course '%s':\n", targetCourse.getName());

            // List to store course similarities
            List<CourseSimilarity> courseSimilarities = new ArrayList<>();

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

            // Populate the list with similar courses and their similarities
            List<Course> similarCourses = similarCoursesFinder.findSimilarCourses(targetCourse, similarityBaseValue);
            if (similarCourses.isEmpty()) {
                System.out.println("  No courses found.");
            } else {
                for (Course similarCourse : similarCourses) {
                    double similarity = SimilarCourses.pearsonCorrelation(targetCourse.getGrades(), similarCourse.getGrades());
                    courseSimilarities.add(new CourseSimilarity(similarCourse, similarity));
                }

                // Sort the course similarities in descending order
                courseSimilarities.sort(Comparator.comparingDouble(CourseSimilarity::getSimilarity).reversed());

                // Print the sorted similarities
                for (CourseSimilarity courseSimilarity : courseSimilarities) {
                    System.out.printf("  %s: %.6f\n", courseSimilarity.getCourse().getName(), courseSimilarity.getSimilarity());
                }
            }
        }

        if (courseWithHighestSimilarity1 != null && courseWithHighestSimilarity2 != null) {
            System.out.printf("\nHighest similarity found is between %s and %s with a similarity of: %.6f\n",
                    courseWithHighestSimilarity1.getName(), courseWithHighestSimilarity2.getName(), highestSimilarity);
        } else {
            System.out.println("\nNo courses with similarity found.");
        }
    }

    // Helper class to store course and similarity information
    private static class CourseSimilarity {
        private final Course course;
        private final double similarity;

        public CourseSimilarity(Course course, double similarity) {
            this.course = course;
            this.similarity = similarity;
        }

        public Course getCourse() {
            return course;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}