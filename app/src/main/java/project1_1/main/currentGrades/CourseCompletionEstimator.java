package project1_1.main.currentGrades;

import project1_1.main.dataHandle.*;

import java.util.*;

public class CourseCompletionEstimator {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCurrentCourses();

        CurrentStudentManager currentStudentManager = new CurrentStudentManager();


        // Lists to categorize courses into different years
        List<Course> firstYearCourses = new ArrayList<>();
        List<Course> secondYearCourses = new ArrayList<>();
        List<Course> thirdYearCourses = new ArrayList<>();

        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course c1, Course c2) {
                // Compare by the number of completed students (non-null grades)
                return Integer.compare(countCompleted(c1), countCompleted(c2)); // Descending order
            }
        });

        // Print out the results
        System.out.println("Course completion estimates (from most completed to least):\n");
        int rank = 1;
        for (Course course : courses) {
            int completedCount = countCompleted(course);
            System.out.printf("Rank %d: Course: %s, Completed by: %d students\n",
                    rank, course.getName(), completedCount);
            rank++;
        }

        for (Course course : courses) {
            int completedCount = countCompleted(course);
            if (completedCount >= 1317) {
                firstYearCourses.add(course);
            } else if (completedCount >= 718) {
                secondYearCourses.add(course);
            } else {
                thirdYearCourses.add(course);
            }
        }
        System.out.println();
        printCoursesByYear("1 Year", firstYearCourses);
        printCoursesByYear("2 Year", secondYearCourses);
        printCoursesByYear("3 Year", thirdYearCourses);

        // Find students taking third-year courses
        Set<Integer> thirdYearStudentIDs = ThirdYearStudentFinder.findThirdYearStudents(currentStudentManager, thirdYearCourses);

        // Define not-started courses
        List<Course> notStartedCourses = getNotStartedCourses(courses);

        System.out.println("Students likely in third year based on courses:");
        int count = 0;
        for (Integer studentID : thirdYearStudentIDs) {
            count++;
            System.out.println("Student ID: " + studentID);
        }
        System.out.println("Number of students in third year: " + count);

        System.out.println("\nNot started courses:");
        for (Course course : notStartedCourses) {
            System.out.println(course.getName());
        }

        // Find the students who will likely graduate (with a pass threshold of 6.00)
        Set<Integer> graduatingStudents = GraduatingStudentFinder.findGraduatingStudents(currentStudentManager, thirdYearStudentIDs, courses, notStartedCourses, 6.0);

        // Output graduating students
        System.out.println("\nStudents likely to graduate:");
        for (Integer studentID : graduatingStudents) {
            System.out.println("Student ID: " + studentID);
        }
        System.out.println("Total students likely to graduate: " + graduatingStudents.size());

        Course mostFailedCourse = findMostFailedCourse(courses, notStartedCourses, currentStudentManager, 6.0);
        Course mostPassedCourse = findMostPassedCourse(courses, notStartedCourses, currentStudentManager, 6.0);

        System.out.printf("\nCourse most people fail: %s, Average Grade: %.2f, Std: %.2f",
                mostFailedCourse.getName(),
                mostFailedCourse.getAverageGrade(),
                mostFailedCourse.getStandardDeviation());

        System.out.printf("\n Highest grade: %.2f, Lowest grade: %.2f\n",
                mostFailedCourse.getHighestGrade(mostFailedCourse.getGrades()),
                mostFailedCourse.getLowestGrade(mostFailedCourse.getGrades()));

        System.out.printf("\nCourse most people pass: %s, Average Grade: %.2f, Std: %.2f",
                mostPassedCourse.getName(),
                mostPassedCourse.getAverageGrade(),
                mostPassedCourse.getStandardDeviation());

        System.out.printf("\n Highest grade: %.2f, Lowest grade: %.2f",
                mostPassedCourse.getHighestGrade(mostPassedCourse.getGrades()),
                mostPassedCourse.getLowestGrade(mostPassedCourse.getGrades()));

    }

    // Method to count how many students completed the course (non-null grades)
    private static int countCompleted(Course course) {
        int completedCount = 0;
        for (Double grade : course.getGrades()) {
            if (grade != null) {
                completedCount++;
            }
        }
        return completedCount;
    }

    // Method to print the courses for each year
    private static void printCoursesByYear(String yearLabel, List<Course> courses) {
        System.out.println(yearLabel + ":");
        int count = 0;
        for (Course course : courses) {
            count++;
            System.out.printf("Course: %s\n", course.getName());
        }
        System.out.println("Number of classes: " + count);
        System.out.println();
    }

    private static List<Course> getNotStartedCourses(List<Course> courses) {
        List<Course> notStartedCourses = new ArrayList<>();
        for (Course course : courses) {
            boolean allGradeNull = true;
            for (Double grade : course.getGrades()) {
                if (grade != null) {
                    allGradeNull = false;
                    break;
                }
            }
            if (allGradeNull) {
                notStartedCourses.add(course);
            }
        }
        return notStartedCourses;
    }

    private static Course findMostFailedCourse(List<Course> courses, List<Course> notStartedCourses, CurrentStudentManager studentManager, double passingGrade) {
        Map<Integer, CurrentStudentRecord> studentRecord = studentManager.getAllStudentRecords();
        Course mostFailedCourse = null;
        int maxFailures = 0;

        Set<Integer> notStartedIndices = new HashSet<>();
        for (Course course : notStartedCourses) {
            notStartedIndices.add(course.getColumnIndex());
        }
        for (Course course : courses) {
            if (notStartedIndices.contains(course.getColumnIndex())) {
                continue;
            }
            int failureCount = 0;
            for (CurrentStudentRecord student : studentRecord.values()) {
                List<Double> grades = student.getCourseGrades();
                int courseIndex = course.getColumnIndex();
                if (courseIndex >= 0 && courseIndex < grades.size()) {
                    Double grade = grades.get(courseIndex);
                    if (grade != null && grade < passingGrade) {
                        failureCount++;
                    }
                }
            }
            if (failureCount > maxFailures) {
                maxFailures = failureCount;
                mostFailedCourse = course;
            }
        }
        return mostFailedCourse;
    }

    private static Course findMostPassedCourse(List<Course> allCourses, List<Course> notStartedCourses, CurrentStudentManager studentManager, double passingGrade) {
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();
        Course mostPassedCourse = null;
        int maxPasses = 0;

        Set<Integer> notStartedIndices = new HashSet<>();
        for (Course course : notStartedCourses) {
            notStartedIndices.add(course.getColumnIndex());
        }


        for (Course course : allCourses) {
            if (notStartedIndices.contains(course.getColumnIndex())) {
                continue;
            }

            int passCount = 0;
            for (CurrentStudentRecord student : studentRecords.values()) {
                List<Double> grades = student.getCourseGrades();
                int courseIndex = course.getColumnIndex();
                if (courseIndex >= 0 && courseIndex < grades.size()) {
                    Double grade = grades.get(courseIndex);

                    if (grade != null && grade >= passingGrade) {
                        passCount++;
                    }
                }
            }

            if (passCount > maxPasses) {
                maxPasses = passCount;
                mostPassedCourse = course;
            }
        }

        return mostPassedCourse;
    }
}

