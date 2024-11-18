package src.main.dataHandle;

import java.util.*;

public class ThirdYearStudentFinder {

    public static Set<Integer> findThirdYearStudents(CurrentStudentManager currentStudentManager, List<Course> thirdYearCourses) {
        Set<Integer> thirdYearStudentIDs = new HashSet<>();
        Map<Integer, CurrentStudentRecord> studentRecords = currentStudentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCourseGrades();
            for (Course thirdYearCourse : thirdYearCourses) {
                int courseIndex = thirdYearCourse.getColumnIndex();
                if (courseIndex >= 0 && courseIndex < grades.size() && grades.get(courseIndex) != null) {
                    thirdYearStudentIDs.add(student.getStudentID());
                    break;  // Student identified as third-year
                }
            }
        }
        return thirdYearStudentIDs;
    }
}
