package src.main;

import java.util.ArrayList;
import java.util.List;

public class GraduatedCumLaudeStudents {

    public static void main(String[] args) {
        String csvFilePath = "src/main/GraduateGrades.csv";
        String[][] csvData = TwoDimensionalArray.readCsvInto2DArray(csvFilePath);

        if (csvData != null) {
            List<GraduatedStudentRecord> studentRecords = new ArrayList<>();

            for (int i = 1; i < csvData.length; i++) {
                try {
                    int studentID = Integer.parseInt(csvData[i][0]);
                    List<Double> courseGrades = new ArrayList<>();

                    for (int j = 1; j < csvData[i].length; j++) {
                        try {
                            courseGrades.add(Double.parseDouble(csvData[i][j]));
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing grade at row " + i + ", column " + j);
                        }
                    }

                    studentRecords.add(new GraduatedStudentRecord(studentID, courseGrades));
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing student ID at row " + i);
                }
            }

            int cumLaudeNumber = 0;
            int totalNumberStudent = studentRecords.size();
            for (GraduatedStudentRecord record : studentRecords) {
                if (record.getGPA() >= 8.25) {
                    cumLaudeNumber++;
                    System.out.printf("Student ID: %d, GPA: %.2f%n", record.getStudentID(), record.getGPA());
                }
            }
            System.out.println("Number of students that cum-laude: " + cumLaudeNumber);
            System.out.println("Total number of students: " + totalNumberStudent);
            System.out.printf("Percentage of a student cum-laude: %.2f%%%n", ((double) cumLaudeNumber / totalNumberStudent) * 100);

        }
    }
}
