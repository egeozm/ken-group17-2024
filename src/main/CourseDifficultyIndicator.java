package src.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CourseDifficultyIndicator {

    public static void main(String[] args) {
        String csvFilePath = "src/csvFiles/GraduateGrades.csv";
        String[][] data = TwoDimensionalArray.readCsvInto2DArray(csvFilePath);

        if (data != null) {
            List<CourseRecord> sortedCourses = sortCoursesByDifficulty(data);
            CourseRecord hardestCourse = sortedCourses.get(0);
            CourseRecord easiestCourse = sortedCourses.get(sortedCourses.size() - 1);
            System.out.printf("The hardest course is: %s, with average: %.3f, Std Dev: %.3f\n", hardestCourse.getName(), hardestCourse.getAverageGrade(), hardestCourse.getStandardDeviation());
            System.out.printf("The easiest course is: %s, with average: %.3f, Std Dev: %.3f\n", easiestCourse.getName(), easiestCourse.getAverageGrade(), easiestCourse.getStandardDeviation());

            System.out.println("\nSorted list of courses: (Hardest to easiest)\n");
            int count = 1;
            for (CourseRecord course : sortedCourses) {
                System.out.printf("Course %d: %s, with average: %.3f, Std Dev: %.3f\n", count, course.getName(), course.getAverageGrade(), course.getStandardDeviation());
                count++;
            }
        }
    }

    public static List<CourseRecord> coursesAverageGrades(String[][] dataSet) {
        List<CourseRecord> courses = new ArrayList<>();

        for (int i = 1; i < dataSet[0].length; i++) {
            double sum = 0;
            int count = 0;
            List<Double> grades = new ArrayList<>();

            for (int j = 1; j < dataSet.length; j++) {
                try {
                    double grade = Double.parseDouble(dataSet[j][i]);
                    grades.add(grade);
                    sum += grade;
                    count++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid grade at row " + j + ", column " + i + ": " + dataSet[j][i]);
                }
            }

            double average = (count > 0) ? (sum / count) : 0;
            double standardDeviation = calculateStandardDeviation(grades, average);
            courses.add(new CourseRecord(dataSet[0][i], average, standardDeviation));
        }
        return courses;
    }

    //Use standard deviation to measure the variability in student grades, providing insight into the consistency of course difficulty
    public static double calculateStandardDeviation(List<Double> grades, double mean) {
        double sum = 0;
        for (double grade : grades) {
            sum += Math.pow(grade - mean, 2);
        }
        return (grades.size() > 0) ? Math.sqrt(sum / grades.size()) : 0;
    }

    public static List<CourseRecord> sortCoursesByDifficulty(String[][] dataSet) {
        List<CourseRecord> courses = coursesAverageGrades(dataSet);
        /*
        Course::getAverageGrade: This is a method reference that points to the getAverageGrade method of the
        CourseRecord class.
        It's shorthand for saying, "Use each course’s average grade as the sorting key."
        Sorts in ASC.
         */
        courses.sort(Comparator.comparingDouble(CourseRecord::getAverageGrade));

//        int n = gradesArray[0].length;
//        String tempGrade;
//        String tempCourse;
//
//        for (int i = 0; i < n - 1; i++) {
//            for (int j = 0; j < n - 1; j++) {
//                if (Double.parseDouble(gradesArray[1][j]) > Double.parseDouble(gradesArray[1][j + 1])) {
//                    tempGrade = gradesArray[1][j];
//                    gradesArray[1][j] = gradesArray[1][j + 1];
//                    gradesArray[1][j + 1] = tempGrade;
//                    tempCourse = gradesArray[0][j];
//                    gradesArray[0][j] = gradesArray[0][j + 1];
//                    gradesArray[0][j + 1] = tempCourse;
//                }
//            }
//        }
//
//        System.out.println("The hardest course is: " + gradesArray[0][0] + "\and the easiest course is: "
//        + gradesArray[0][n - 1]);
//        return gradesArray;

        return courses;
    }
}
