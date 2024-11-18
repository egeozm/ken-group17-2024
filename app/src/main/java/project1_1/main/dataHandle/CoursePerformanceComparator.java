package project1_1.main.dataHandle;

import java.util.*;

public class CoursePerformanceComparator {

    private final CurrentStudentManager studentManager;
    private final List<Course> courses;

    public CoursePerformanceComparator(CurrentStudentManager studentManager, List<Course> courses) {
        this.studentManager = studentManager;
        this.courses = courses;
    }

    // Method to compare student performance across multiple courses
    public void comparePerformanceAcrossCourses(String course1Name, String course2Name) {
        Course course1 = findCourseByName(course1Name);
        Course course2 = findCourseByName(course2Name);

        if (course1 == null || course2 == null) {
            System.out.println("One or both courses not found: " + course1Name + ", " + course2Name);
            return;
        }

        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        List<Double> course1Grades = new ArrayList<>();
        List<Double> course2Grades = new ArrayList<>();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCompletedCourseGrades();

            // Get grade for course1 and course2
            int course1Index = course1.getColumnIndex();
            int course2Index = course2.getColumnIndex();

            if (course1Index >= 0 && course1Index < grades.size() && course2Index >= 0 && course2Index < grades.size()) {
                Double course1Grade = grades.get(course1Index);
                Double course2Grade = grades.get(course2Index);

                if (course1Grade != null && course2Grade != null) {
                    course1Grades.add(course1Grade);
                    course2Grades.add(course2Grade);
                }
            }
        }


        displayComparison(course1Name, course1Grades, course2Name, course2Grades);
    }

    private Course findCourseByName(String courseName) {
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                return course;
            }
        }
        return null;
    }

    private void displayComparison(String course1Name, List<Double> course1Grades, String course2Name, List<Double> course2Grades) {
        double course1Average = calculateAverage(course1Grades);
        double course2Average = calculateAverage(course2Grades);
        double course1StdDev = calculateStandardDeviation(course1Grades, course1Average);
        double course2StdDev = calculateStandardDeviation(course2Grades, course2Average);

        System.out.printf("Comparison between %s and %s:\n", course1Name, course2Name);
        System.out.printf("%s - Average Grade: %.2f | Std Dev: %.2f | Students: %d\n", course1Name, course1Average, course1StdDev, course1Grades.size());
        System.out.printf("%s - Average Grade: %.2f | Std Dev: %.2f | Students: %d\n", course2Name, course2Average, course2StdDev, course2Grades.size());
        System.out.printf(" - Average difference: %.2f\n", Math.abs(course1Average - course2Average));
        System.out.printf(" - Std Dev difference: %.2f\n", Math.abs(course1StdDev - course2StdDev));
    }

    private double calculateAverage(List<Double> grades) {
        double sum = 0;
        for (Double grade : grades) {
            sum += grade;
        }
        return (!grades.isEmpty()) ? sum / grades.size() : 0.0;
    }

    private double calculateStandardDeviation(List<Double> grades, double mean) {
        double sum = 0;
        for (Double grade : grades) {
            sum += Math.pow(grade - mean, 2);
        }
        return (!grades.isEmpty()) ? Math.sqrt(sum / grades.size()) : 0.0;
    }
}

/*
    Usage:
        CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();
        CoursePerformanceComparator comparator = new CoursePerformanceComparator(currentStudentManager, courses);
        comparator.comparePerformanceAcrossCourses("Vortex Quantum Mechanics", "Aether Resonance");
 */
