package src.main;

public class SimilarCoursesDisplayer {

    public static void main(String[] args) {
        String csvFilePath = "src/csvFiles/GraduateGrades.csv";
        String[][] csvData = TwoDimensionalArray.readCsvInto2DArray(csvFilePath);

        if (csvData == null) {
            System.out.println("Empty CSV data.");
            return;
        }

        int numOfStudents = csvData.length - 1;
        int numOfCourses = csvData[0].length - 1;

        double[][] grades = new double[numOfStudents][numOfCourses];

        for (int i = 1; i < csvData.length; ++i) {
            for (int j = 1; j < csvData[i].length; ++j) {
                try {
                    grades[i - 1][j - 1] = Double.parseDouble(csvData[i][j]); // Array with grades only and changed to a double type.
                } catch (NumberFormatException e) {
                    System.out.println("Invalid grade at row " + i + ", column " + j + ": " + csvData[i][j]);
                }

            }
        }

        int similarCoursesCount = 0;
        double similarityHolder = 0;
        String[] courseNameHolder = new String[2];

        for (int i = 0; i < numOfCourses; ++i) {
            for (int j = i + 1; j < numOfCourses; ++j) { // Only calculate upper triangle to avoid duplicates i.e. we do not to calculate (A, A) / (A, B), (B, A)
                double similarity = pearsonCorrelation(courseGrades(grades, i), courseGrades(grades, j));

                if (similarity > 0.7) {
                    similarCoursesCount += 1;
                    System.out.printf("Similarity between %s and %s: %.3f%n", csvData[0][i + 1], csvData[0][j + 1], similarity);
                }

                if (similarity > similarityHolder) {
                    similarityHolder = similarity;
                    courseNameHolder[0] = csvData[0][i + 1];
                    courseNameHolder[1] = csvData[0][j + 1];
                }
            }
        }
        System.out.println("Number of similar courses: " + similarCoursesCount);
        System.out.printf("The most similar courses based on Pearson correlation are: %s and %s with correlation: %.3f%n", courseNameHolder[0], courseNameHolder[1], similarityHolder);


    }

    public static double pearsonCorrelation(double[] x, double[] y) {
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

        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        return denominator != 0 ? numerator / denominator : 0; // Handle division by zero
    }

    public static double[] courseGrades(double[][] grades, int courseIndex) {
        double[] courseGrades = new double[grades.length]; //Create an array for grades
        for (int i = 0; i < grades.length; ++i) {
            courseGrades[i] = grades[i][courseIndex]; //Go through every grade and add it to array
        }
        return courseGrades;
    }
}
