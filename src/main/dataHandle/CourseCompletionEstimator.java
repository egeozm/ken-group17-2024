package src.main.dataHandle;

import java.util.*;

public class CourseCompletionEstimator {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();

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
        Set<Integer> thirdYearStudentIDs = findThirdYearStudents(currentStudentManager, thirdYearCourses, courses);

        System.out.println("Students likely in third year based on courses:");
        int count = 0;
        for (Integer studentID : thirdYearStudentIDs) {
            count++;
            System.out.println("Student ID: " + studentID);
        }
        System.out.println("Number of students in third year: " + count);


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

    //     Method to find students enrolled in third-year courses
    private static Set<Integer> findThirdYearStudents(CurrentStudentManager currentStudentManager,
                                                      List<Course> thirdYearCourses, List<Course> allCourses) {
        Set<Integer> thirdYearStudentIDs = new HashSet<>();
        Map<Integer, CurrentStudentRecord> studentRecords = currentStudentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCourseGrades();
            for (Course thirdYearCourse : thirdYearCourses) {
                int courseIndex = thirdYearCourse.getColumnIndex();
                if (courseIndex >= 0 && courseIndex < grades.size() && grades.get(courseIndex) != null) {
                    thirdYearStudentIDs.add(student.getStudentID());
                    break;
                }
            }
        }
        return thirdYearStudentIDs;
    }
}

