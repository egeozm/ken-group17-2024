package src.main.dataHandle;

import java.util.*;

public class GraduatingStudentFinder {

    public static Set<Integer> findGraduatingStudents(CurrentStudentManager currentStudentManager,
                                                      Set<Integer> thirdYearStudentIDs, // Only check third-year students
                                                      List<Course> courses,
                                                      List<Course> notStartedCourses,
                                                      double passingGrade) {
        Set<Integer> graduatingStudents = new HashSet<>();
        Map<Integer, CurrentStudentRecord> studentRecords = currentStudentManager.getAllStudentRecords();

        // Find the indices of the not-started courses
        Set<Integer> notStartedIndices = new HashSet<>();
        for (Course course : notStartedCourses) {
            notStartedIndices.add(course.getColumnIndex());
        }

        // Only check third-year students to see if they can graduate
        for (Integer studentID : thirdYearStudentIDs) {
            CurrentStudentRecord student = studentRecords.get(studentID);
            if (student == null) continue;

            List<Double> grades = student.getCourseGrades();
            boolean hasFailed = false;
            List<String> failureReasons = new ArrayList<>();

            // Check for failures in all courses (third year and previous years)
            for (Course course : courses) {
                int courseIndex = course.getColumnIndex();
                if (courseIndex >= 0 && courseIndex < grades.size()) {
                    Double grade = grades.get(courseIndex);
                    if (notStartedIndices.contains(courseIndex)) continue;  // Skip not-started courses
                    if (grade != null && grade < passingGrade) {
                        hasFailed = true;
                        failureReasons.add(course.getName() + " | Grade: " + grade + " (Failed)");
                    }
                }
            }

            // Add the student to graduating list if they haven't failed
            if (!hasFailed) {
                graduatingStudents.add(student.getStudentID());
            } else {
                System.out.println("Student ID: " + student.getStudentID() + " will not graduate due to:");
                for (String reason : failureReasons) {
                    System.out.println(reason);
                }
                System.out.println();
            }
        }

        return graduatingStudents;
    }

}
