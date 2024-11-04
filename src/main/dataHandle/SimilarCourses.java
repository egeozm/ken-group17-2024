package src.main.dataHandle;

import java.util.ArrayList;
import java.util.List;

public class SimilarCourses {

    private final List<Course> graduatedCourses;

    public SimilarCourses(List<Course> graduatedCourses) {
        this.graduatedCourses = graduatedCourses;

    }

    // Method to find and return the most similar course to the given course
    public Course findMostSimilarCourse(Course targetCourse) {
        double highestSimilarity = 0;
        Course mostSimilarCourse = null;

        for (Course course : graduatedCourses) {
            if (!course.getName().equals(targetCourse.getName())) {
                double similarity = pearsonCorrelation(targetCourse.getGrades(), course.getGrades());
//                System.out.printf("Comparing %s with %s: Similarity = %.4f\n", targetCourse.getName(), course.getName(), similarity);


                if (similarity > highestSimilarity) {
                    highestSimilarity = similarity;
                    mostSimilarCourse = course;
                }
            }
        }
        if (mostSimilarCourse == null) {
            System.out.println("No similar course found for: " + targetCourse.getName());
        } else {
            System.out.printf("Most similar course to %s is %s with similarity %.4f\n",
                    targetCourse.getName(), mostSimilarCourse.getName(), highestSimilarity);
        }

        return mostSimilarCourse;
    }

    // Method to return a list of courses that are similar to the target course
    public List<Course> findSimilarCourses(Course targetCourse, double threshold) {
        List<Course> similarCourses = new ArrayList<>();

        for (Course course : graduatedCourses) {
            if (!course.equals(targetCourse)) {
                double similarity = pearsonCorrelation(targetCourse.getGrades(), course.getGrades());

                if (similarity > threshold) {
                    similarCourses.add(course);
                }
            }
        }

        return similarCourses;
    }


    public static double pearsonCorrelation(List<Double> x, List<Double> y) {
        int n = x.size();
        if (n != y.size() || n == 0) {
            return 0; // Return 0 if the lists are not of equal size or empty
        }

        // Filter out any null values and only keep pairs of valid (non-null) grades
        List<Double> filteredX = new ArrayList<>();
        List<Double> filteredY = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            Double xi = x.get(i);
            Double yi = y.get(i);

            if (xi != null && yi != null) { // Only add non-null pairs
                filteredX.add(xi);
                filteredY.add(yi);
            }
        }

        // If there's no valid data left after filtering, return 0
        if (filteredX.isEmpty() || filteredY.isEmpty()) {
            return 0;
        }

        // Now compute the Pearson correlation on the filtered data
        int filteredSize = filteredX.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < filteredSize; i++) {
            double xi = filteredX.get(i);
            double yi = filteredY.get(i);

            sumX += xi;
            sumY += yi;
            sumXY += xi * yi;
            sumX2 += xi * xi;
            sumY2 += yi * yi;
        }

        double numerator = filteredSize * sumXY - sumX * sumY;
        double denominator = Math.sqrt((filteredSize * sumX2 - sumX * sumX) * (filteredSize * sumY2 - sumY * sumY));

        return denominator != 0 ? numerator / denominator : 0; // Handle division by zero
    }


}
