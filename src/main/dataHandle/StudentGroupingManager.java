package src.main.dataHandle;

import java.util.ArrayList;
import java.util.List;

public class StudentGroupingManager {

    private final StudentInfoManager studentInfoManager;
    private final CurrentStudentManager currentStudentManager;

    public StudentGroupingManager(StudentInfoManager studentInfoManager, CurrentStudentManager currentStudentManager) {
        this.studentInfoManager = studentInfoManager;
        this.currentStudentManager = currentStudentManager;
    }

    public void displayPredictedGradesForGroupAndCourse(String attribute, String value, String courseName, CourseManager courseManager) {
        // Load predicted grades temporarily
        String[][] predictedGradesData = TwoDimensionalArray.readCsvInto2DArray("src/csvFiles/PredictedGradesDecisionStump.csv");
        if (predictedGradesData == null || predictedGradesData.length == 0) {
            System.out.println("Predicted grades file is empty or not found.");
            return;
        }

        // Get students grouped by the specified attribute and value
        List<Integer> studentIDs = getStudentIDsByAttribute(attribute, value);

        // Find the specific course
        Course course = courseManager.getCourseByName(courseName);
        if (course == null) {
            System.out.println("Course not found: " + courseName);
            return;
        }

        // Print grades for each student in the group
        System.out.println("Predicted Grades for students with " + attribute + " = " + value + " in course " + courseName + ":");
        for (Integer studentID : studentIDs) {
            int rowIndex = findStudentRowIndex(predictedGradesData, studentID);
            if (rowIndex != -1) {
                String gradeStr = predictedGradesData[rowIndex][course.getColumnIndex() + 1]; // +1 to skip StudentID column
                try {
                    Double grade = Double.parseDouble(gradeStr.trim());
                    System.out.println("StudentID: " + studentID + ", Grade: " + grade);
                } catch (NumberFormatException e) {
                    System.out.println("StudentID: " + studentID + ", Predicted Grade: No Grade");
                }
            } else {
                System.out.println("StudentID: " + studentID + ", Predicted Grade: Not Found");
            }
        }
    }

    private List<Integer> getStudentIDsByAttribute(String attribute, String value) {
        List<Integer> studentIDs = new ArrayList<>();

        for (StudentInfoRecord student : studentInfoManager.getAllStudents()) {
            if (getAttributeValue(student, attribute).equalsIgnoreCase(value)) {
                studentIDs.add(student.getStudentID());
            }
        }

        return studentIDs;
    }

    private String getAttributeValue(StudentInfoRecord student, String attribute) {
        switch (attribute.toLowerCase()) {
            case "neuro-synaptic interface level":
                return student.getNeuroSynapticInterfaceLevel();
            case "plasma conductivity quotient":
                return String.valueOf(student.getPlasmaConductivityQuotient());
            case "chrono adaptation rate":
                return String.valueOf(student.getChronoAdaptationRate());
            case "telepathic synchronisation index":
                return String.valueOf(student.getTelepathicSynchronisationIndex());
            case "aetheric resonance capacity":
                return String.valueOf(student.getAethericResonanceCapacity());
            default:
                throw new IllegalArgumentException("Invalid attribute: " + attribute);
        }
    }

    private int findStudentRowIndex(String[][] data, int studentID) {
        for (int i = 1; i < data.length; i++) { // Skip header row
            if (Integer.parseInt(data[i][0].trim()) == studentID) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        // Initialize managers
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        CourseManager courseManager = CourseManager.getInstance();

        // Load predicted grades into course manager
        courseManager.loadPredictedGrades(); // Ensure predicted grades are loaded

        // Initialize grouping manager
        StudentGroupingManager groupingManager = new StudentGroupingManager(studentInfoManager, currentStudentManager);

        // Display predicted grades for students in a specific group and course
        groupingManager.displayPredictedGradesForGroupAndCourse(
                "Neuro-Synaptic Interface Level",
                "Medium",
                "Arkonian Warfare Tactics", // Replace with your course name
                courseManager
        );
    }
}