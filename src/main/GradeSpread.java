package src.main;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GradeSpread {

    public static void main(String[] args) {
        CourseManager courseManager = CourseManager.getInstance();
        List<Course> courses = courseManager.getCourseRecords();

        String csvFile = "src/csvFiles/GraduateGrades.csv";
        String line;
        String csvSplitBy = ",";
        
        // Average grade for every course.
        for(int i = 0; i < courses.size(); i++) {
        double averageGrade = courses.get(i).getAverageGrade();
        System.out.println("(!) Avg Grd - " + courses.get(i).getName() + " " + courses.get(i).getAverageGrade());
        }

        System.out.println(" ");

        // Standard deviation for every course.
        for(int i = 0; i < courses.size(); i++) {
            double standardDeviation = courses.get(i).getStandardDeviation();
            System.out.println("(!) Std Dev - " + courses.get(i).getName() + " " + courses.get(i).getStandardDeviation());
            }

        //19354 graduated students.

        // The highest and the lowest grade for each row.
        for(int i = 0; i < GraduateGrades.length(); i++){
            
        }
}
}
}