package src.main.dataHandle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoFailedStudents {
    String csvFile = "src/csvFiles/CurrentGrades.csv";
    String line;
    String csvSplitBy = ",";
    public static void main(String[] args) {
        NoFailedStudents noFailedStudents = new NoFailedStudents();
        noFailedStudents.findFailedStudents();
    }

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