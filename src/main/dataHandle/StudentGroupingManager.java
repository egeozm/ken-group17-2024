package src.main.dataHandle;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import java.util.*;
import java.util.stream.Collectors;

public class StudentGroupingManager {

    private final StudentInfoManager studentInfoManager;
    private final CurrentStudentManager currentStudentManager;

    public StudentGroupingManager(StudentInfoManager studentInfoManager, CurrentStudentManager currentStudentManager) {
        this.studentInfoManager = studentInfoManager;
        this.currentStudentManager = currentStudentManager;
    }

    // Group grades by a specific attribute and calculate averages
    public Map<String, Double> calculateAverageGradesByAttribute(Course course, String attribute) {
        Map<String, List<Double>> groupedGrades = new HashMap<>();

        // Group grades by the specified attribute
        for (StudentInfoRecord student : studentInfoManager.getAllStudents()) {
            String groupValue = getAttributeValue(student, attribute); // Get group value (e.g., "Full", "High")
            groupedGrades.computeIfAbsent(groupValue, k -> new ArrayList<>()); // Initialize group if not present

            // Get grades for the student
            CurrentStudentRecord studentRecord = currentStudentManager.getStudentRecord(student.getStudentID());
            if (studentRecord != null) {
                List<Double> grades = studentRecord.getCourseGrades();
                if (course.getColumnIndex() < grades.size()) {
                    Double grade = grades.get(course.getColumnIndex());
                    if (grade != null) {
                        groupedGrades.get(groupValue).add(grade);
                    }
                }
            }
        }

        // Calculate average grades for each group
        return groupedGrades.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Key: Group (e.g., "Full")
                        entry -> calculateAverage(entry.getValue()) // Value: Average grade for the group
                ));
    }

    // Get attribute value dynamically
    private String getAttributeValue(StudentInfoRecord student, String attribute) {
        switch (attribute.toLowerCase()) {
            case "nsil":
                return student.getNeuroSynapticInterfaceLevel();
            case "car":
                return String.valueOf(student.getChronoAdaptationRate());
            case "tsi":
                return String.valueOf(student.getTelepathicSynchronisationIndex());
            case "arc":
                return String.valueOf(student.getAethericResonanceCapacity());
            case "pcq":
                return String.valueOf(student.getPlasmaConductivityQuotient());
            default:
                return "Unknown";
        }
    }

    public Map<String, List<Double>> groupGradesByAttribute(Course course, String attribute) {
        Map<String, List<Double>> groupedGrades = new HashMap<>();

        // Iterate through all students
        for (StudentInfoRecord student : studentInfoManager.getAllStudents()) {
            // Get the value of the specified attribute for the student
            String groupValue = getAttributeValue(student, attribute);

            // Initialize the group in the map if not present
            groupedGrades.computeIfAbsent(groupValue, k -> new ArrayList<>());

            // Get the student's grades
            CurrentStudentRecord studentRecord = currentStudentManager.getStudentRecord(student.getStudentID());
            if (studentRecord != null) {
                List<Double> grades = studentRecord.getCourseGrades();
                // Ensure the grade exists for the course and is not null
                if (course.getColumnIndex() < grades.size()) {
                    Double grade = grades.get(course.getColumnIndex());
                    if (grade != null) {
                        groupedGrades.get(groupValue).add(grade);
                    }
                }
            }
        }

        return groupedGrades;
    }

    // Calculate the average of a list of grades
    private double calculateAverage(List<Double> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }
}