package project1_1.main.graduatedGrades;

import project1_1.main.dataHandle.Course;
import project1_1.main.dataHandle.CourseManager;
import project1_1.main.dataHandle.GraduatedStudentRecord;

import java.util.List;
import java.util.ArrayList;

public class GraduatedCumLaudeStudentsDisplayer {

    public static void main(String[] args) {
        // Use CourseManager to load data
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadGraduateGrades();  // Load graduate grades from CSV

        List<Course> courses = courseManager.getGraduatedCourses();
        List<GraduatedStudentRecord> studentRecords = new ArrayList<>();

        // Assuming the first column of each course contains the student ID
        int numStudents = courses.get(0).getGrades().size(); // Get number of students based on the first course's grades

        // Iterate through the students (rows)
        for (int i = 0; i < numStudents; i++) {
            int studentID = (int) courses.get(0).getGrades().get(i).doubleValue();  // Assuming student IDs are stored as doubles

            List<Double> studentGrades = new ArrayList<>();
            for (Course course : courses) {
                List<Double> grades = course.getGrades();
                // Skip non-graded values
                if (i < grades.size()) {
                    studentGrades.add(grades.get(i));
                }
            }

            // Add each student record
            studentRecords.add(new GraduatedStudentRecord(studentID, studentGrades));
        }

        // Count students who graduated cum laude (GPA >= 8.25)
        int cumLaudeNumber = 0;
        int totalNumberStudent = studentRecords.size();
        for (GraduatedStudentRecord record : studentRecords) {
            if (record.getGPA() >= 8.25) {
                cumLaudeNumber++;
                System.out.printf("Student ID: %d, GPA: %.2f%n", record.getStudentID(), record.getGPA());
            }
        }

        // Output summary statistics
        System.out.println("Number of students that cum-laude: " + cumLaudeNumber);
        System.out.println("Total number of students: " + totalNumberStudent);
        System.out.printf("Percentage of students cum-laude: %.2f%%%n", ((double) cumLaudeNumber / totalNumberStudent) * 100);
    }
}
