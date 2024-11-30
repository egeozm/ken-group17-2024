package src.main.dataHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictedCurrentStudentManager {
    // Map student ID to their CurrentStudentRecord
    private final Map<Integer, CurrentStudentRecord> studentRecords;

    // Constructor: Automatically loads student records from the data source
    public PredictedCurrentStudentManager() {
        studentRecords = new HashMap<>();
        loadStudentRecords();  // Load student records from data source when instantiated
    }

    // Method to load student records from a data source
    private void loadStudentRecords() {
        String[][] data = TwoDimensionalArray.readCsvInto2DArray("src/csvFiles/PredictedGradesDecisionStump.csv");

        for (int i = 1; i < data.length; i++) {  // Skip header row
            int studentID = Integer.parseInt(data[i][0].trim());  // Student ID in the first column
            List<Double> courseGrades = parseGrades(data[i]);  // Parse the grades for each course
            studentRecords.put(studentID, new CurrentStudentRecord(studentID, courseGrades));  // Create a new CurrentStudentRecord
        }
    }

    // Helper method to parse grades from a row in the data source
    private List<Double> parseGrades(String[] row) {
        List<Double> grades = new ArrayList<>();
        for (int j = 1; j < row.length; j++) {  // Skip the first column, which is the student ID
            String gradeStr = row[j].trim();
            grades.add(Double.parseDouble(gradeStr));  // Add valid grades
        }
        return grades;
    }

    // Method to get a specific student's record by their ID
    public CurrentStudentRecord getStudentRecord(int studentID) {
        return studentRecords.get(studentID);
    }

    // Method to get all student records
    public Map<Integer, CurrentStudentRecord> getAllStudentRecords() {
        return studentRecords;
    }
}
