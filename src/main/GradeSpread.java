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
        
        // Read the header row to get the course names
        List<String> courseNames = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            if((line = br.readLine()) != null){
                String[] headers = line.split(csvSplitBy);
                for(int i = 1; i < headers.length; i++){
                    courseNames.add(headers[i].trim());
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }

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

        // Grade spread, median for first and last 25%, and mean of middle 50%.
        for(Course course : courses){
            List<Double> grades = new ArrayList<>();
            int courseIndex = courseNames.indexOf(course.getName()) + 1;    // +1 to account for the Student ID column
            try(BufferedReader br = new BufferedReader(new FileReader(csvFile))){
                // Skip the header row
                br.readLine();
                while((line = br.readLine()) != null){
                    String[] gradeData = line.split(csvSplitBy);
                    // Assuming the grade is in the column corresponding to the course index
                    if (courseIndex < gradeData.length) {
                        grades.add(Double.parseDouble(gradeData[courseIndex]));
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
            }

            if(!grades.isEmpty()){
                Collections.sort(grades);
                int size = grades.size();
                int quarterSize = size / 4;

                // Median for the first 25%
                List<Double> firstQuarter = grades.subList(0, quarterSize);
                double medianFirstQuarter = calculateMedian(firstQuarter);

                // Median for the last 25%
                List<Double> lastQuarter = grades.subList(size - quarterSize, size);
                double medianLastQuarter = calculateMedian(lastQuarter);

                // Mean of the middle 50%
                List<Double> middleHalf = grades.subList(quarterSize, size - quarterSize);
                double meanMiddleHalf = calculateMean(middleHalf);

                System.out.println("Course: " + course.getName());
                System.out.println("Grade spread: " + (Collections.max(grades) - Collections.min(grades)));
                System.out.println("Median of first 25%: " + medianFirstQuarter);
                System.out.println("Median of last 25%: " + medianLastQuarter);
                System.out.println("Mean of middle 50%: " + meanMiddleHalf);
                System.out.println();
            } else {
                System.out.println("No grades found for course: " + course.getName());
            }
        }
    }

    private static double calculateMedian(List<Double> grades){
        int size = grades.size();
        if(size % 2 == 0){
            return(grades.get(size / 2 - 1) + grades.get(size / 2)) / 2.0;
        } else{
            return grades.get(size / 2);
        }
    }

    private static double calculateMean(List<Double> grades){
        double sum = 0;
        for(double grade : grades){
            sum += grade;
        }
        return sum / grades.size();
    }
}
