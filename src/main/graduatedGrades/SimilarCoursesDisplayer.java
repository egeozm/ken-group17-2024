package src.main.graduatedGrades;

import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;

import java.util.List;

public class SimilarCoursesDisplayer {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadGraduateGrades();
        List<Course> courses = courseManager.getCourseRecords();

        int similarCoursesCount = 0;
        double similarityHolder = 0;
        String[] courseNameHolder = new String[2];

        int numOfCourses = courses.size();

        for (int i = 0; i < numOfCourses; ++i) {
            for (int j = i + 1; j < numOfCourses; ++j) {
                double similarity = pearsonCorrelation(courses.get(i).getGrades(), courses.get(j).getGrades());

                if (similarity > 0.7) {
                    similarCoursesCount += 1;
                    System.out.printf("Similarity between %s and %s: %.3f%n", courses.get(i).getName(), courses.get(j).getName(), similarity);
                }

                if (similarity > similarityHolder) {
                    similarityHolder = similarity;
                    courseNameHolder[0] = courses.get(i).getName();
                    courseNameHolder[1] = courses.get(j).getName();
                }
            }
        }
        System.out.println("Number of similar courses: " + similarCoursesCount);
        System.out.printf("The most similar courses based on Pearson correlation are: %s and %s with correlation: %.3f%n",
                courseNameHolder[0], courseNameHolder[1], similarityHolder);
    }

    public static double pearsonCorrelation(List<Double> x, List<Double> y) {
/*
    While other similarity measures, such as cosine similarity or Euclidean distance, could also be used,
    Pearson correlation has specific advantages in the context of comparing course grades:

    Accounts for Mean Differences:
    Unlike some other measures, the Pearson correlation considers the mean and standard deviation of the data sets.
    This means it focuses on how students' relative performance in one course corresponds to their relative performance
    in another, not just the raw scores.

    Insensitive to Scale:
    The Pearson correlation is scale-invariant.This means that it does not matter if one course is graded on a
    different scale than another; it only looks at the relative differences between student grades in each course.
    This is particularly useful when comparing courses that might have different grading practices.

    A Pearson correlation coefficient ranges from -1 to 1

    1 indicates a perfect positive linear relationship
    0 indicates no linear relationship.
    -1 indicates a perfect negative linear relationship
*/

        int n = x.size();
        if (n != y.size() || n == 0) {
            return 0; // Return 0 if the lists are not of equal size or empty
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < n; i++) {
            double xi = x.get(i);
            double yi = y.get(i);

            sumX += xi;
            sumY += yi;
            sumXY += xi * yi;
            sumX2 += xi * xi;
            sumY2 += yi * yi;
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        return denominator != 0 ? numerator / denominator : 0; // Handle division by zero
    }

}
