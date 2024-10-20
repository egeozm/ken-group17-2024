package src.main.dataHandle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CourseCompletionEstimator {
    // From NoFailedStudents.java
    String csvFile = "src/csvFiles/CurrentGrades.csv";
    String line;
    String csvSplitBy = ",";

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();

        // From NoFailedStudents.java
        NoFailedStudents noFailedStudents = new NoFailedStudents();
        noFailedStudents.findFailedStudents();

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

    // From NoFailedStudents.java
    public void findFailedStudents() {
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = br.readLine();
            String[] courses = headerLine.split(csvSplitBy);

            List<String> failedStudentsList = new ArrayList<>();
            List<List<String>> failedCoursesList = new ArrayList<>();

            while((line = br.readLine()) != null){
                String[] values = line.split(csvSplitBy);
                String studentID = values[0];
                List<String> failedCourses = new ArrayList<>();

                for(int i = 1; i < values.length; i++){
                    if("NG".equals(values[i])){
                        failedCourses.add(courses[i]);
                    }
                }
                if(!failedCourses.isEmpty()){
                    failedStudentsList.add(studentID);
                    failedCoursesList.add(failedCourses);
                }
            }
            int numberOfFailedStudents = failedStudentsList.size();

            // Print the results.
            System.out.println("(!) List of students who failed at least one course: " + failedStudentsList);
            System.out.println("(!) Number of entries: " + numberOfFailedStudents);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}

