package src.main;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

public class GradeSpread {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        List<Course> courses = courseManager.getCourseRecords();

        String csvFile = "src/csvFiles/GraduateGrades.csv";
        String line;
        String csvSplitBy = ",";
        
        // Average grade for every course.
        for(Course course : courses){
            double averageGrade = course.getAverageGrade();
            System.out.println("Avg grd - " + course.getName() + " " + averageGrade);
        }

        System.out.println(" ");

        // Standard deviation for every course.
        for(Course course : courses){
            double standardDeviation = course.getStandardDeviation();
            System.out.println("Std dev - " + course.getName() + " " + standardDeviation);
        }

        // Grade spread.
        for(Course course : courses){
            List<Double> grades = new ArrayList<>();
            int courseIndex = courses.indexOf(course) + 1;    // +1 for the Student ID column
            try(BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                // Skip the header row.
                br.readLine();
                while((line = br.readLine()) != null){
                    String[] gradeData = line.split(csvSplitBy);
                
                    if(courseIndex < gradeData.length){
                        grades.add(Double.parseDouble(gradeData[courseIndex]));
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            if(!grades.isEmpty()){
                double maxGrade = Collections.max(grades);
                double minGrade = Collections.min(grades);
                double gradeSpread = maxGrade - minGrade;
                System.out.println("Grade spread - " + course.getName() + " " + gradeSpread);
            } else{
                System.out.println("No grades found for " + course.getName());
            }
        }
    }
}
