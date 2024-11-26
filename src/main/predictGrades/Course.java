package src.main.predictGrades;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class Course {
    private final int courseID;
    private final String courseName;
    private Double averageGrade;
    private ArrayList<Double> grades;
    private Double standardDeviation;
    private Double mostCommonGrade;
    private final int studentsCount;
    private final int yearOfTheCourse;
    private Course similarCourse;

    public Course(int courseID, String courseName, ArrayList<Double> grades) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.grades = grades;
        this.averageGrade = calculateAverageGrade(grades);
        this.standardDeviation = calculateStandardDeviation(grades, averageGrade);
        this.mostCommonGrade = findMostCommonGrade(grades);
        this.studentsCount = calculateStudentsCount(grades);
        this.yearOfTheCourse = findYearOfTheCourse(studentsCount);
    }

    public int getStudentsCount (){
        return studentsCount;
    }

    public int calculateStudentsCount(ArrayList<Double> grades) {
        int count = 0;
        for(Double grade : grades){
            if (grade != null){
                count ++;
            }
        }
        return count;
    }

    public Double calculateAverageGrade(ArrayList<Double> grades) {
        Double avg;
        double sum = 0;
        int count = 0;
        for (Double grade : grades) {
            if (grade != null) {
                sum += grade;
                count++;
            }
        }
        if (count == 0) {
            return null;
        }else {
            avg = sum / count;
        }
        BigDecimal bd = new BigDecimal(avg).setScale(3, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Double calculateStandardDeviation(ArrayList<Double> grades, Double avg) {
        double stdDev;
        int count = 0;
        double sum = 0;
        for (Double grade : grades) {
            if (grade != null) {
                sum += Math.pow((avg - grade), 2);
                count++;
            }
        }
        if (count == 0) {
            return null;
        }else {
            stdDev = Math.sqrt(sum / count);
            BigDecimal bd = new BigDecimal(stdDev).setScale(3, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }

    public double findMostCommonGrade(ArrayList<Double> grades) {
        if (grades.isEmpty()){
            return 0;
        }
        int[] frequency = new int [11]; // 0 - 11
        for (Double grade : grades) {
            if (grade != null) {
                int index =  grade.intValue();
                if (grade >= 0 && grade <= 10) {
                    frequency[index] ++;
                }
            }
        }
        int mostCommonGrade = 0;
        int maxFrequency = 0;
        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] > maxFrequency) {
                maxFrequency = frequency[i];
                mostCommonGrade = i;
            }
        }
        return mostCommonGrade;
    }

    public int findYearOfTheCourse(int studentsCount) {
        if (studentsCount <= 340)
            return 3;
        if (studentsCount <=760)
            return 2;

        return 1;
    }

    public void setSimilarCourse(Course similarCourse) {
        this.similarCourse = similarCourse;
    }
    public Course getSimilarCourse() {
        return similarCourse;
    }
    public int getCourseID() {
        return courseID;
    }
    public ArrayList<Double> getGrades() {
        return grades;
    }
    public String getCourseName() {
        return courseName;
    }
    public String getCourseNameByID (int courseID) {
        return courseName;
    }

    public double getAverageGrade() {
        if (averageGrade == null) {
            return 0;
        }
        return averageGrade;
    }
    public double getStandardDeviation() {
        return standardDeviation;
    }
    public int getYearOfTheCourse(){
        return yearOfTheCourse;
    }

    public String toString(){
        return "\n" + courseName + " | Year: " + yearOfTheCourse + " | Students: " + studentsCount ;
    }
}
