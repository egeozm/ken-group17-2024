package src.main.predictGrades;

import src.main.dataHandle.TwoDimensionalArray;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String graduateGradesFileName = "src/csvFiles/GraduateGrades.csv";
        String currentGradesFileName = "src/csvFiles/CurrentGrades.csv";
        String studentInfoFileName = "src/csvFiles/StudentInfo.csv";


        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        String[][] graduateData = fileLoader.readCsvInto2DArray(graduateGradesFileName);
        String[][] currentStudentData = fileLoader.readCsvInto2DArray(currentGradesFileName);
        String[][] studentInfo = fileLoader.readCsvInto2DArray(studentInfoFileName);

        CourseAnalyzer graduateCourseAnalyzer = new CourseAnalyzer(graduateData, graduateData[0]);
        CourseAnalyzer currentCourseAnalyzer = new CourseAnalyzer(currentStudentData, currentStudentData[0]);
        StudentAnalyzer studentAnalyzer = new StudentAnalyzer(graduateData);

        // Find the Hardest and Easiest course
        graduateCourseAnalyzer.sortByDifficulty();

        // Find the cum-laude students
        studentAnalyzer.findCumLaude();

        //Find the most similar course for each course, using pearson correlation
        graduateCourseAnalyzer.findSimilarCourseForEachCourse();

        //Analyzing Current Students
        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(currentStudentData, studentInfo);

        // Printing the list of the courses and their information
        System.out.println(currentCourseAnalyzer.courses);

//        System.out.println("First-year Students: " + currentStudentAnalyzer.getCurrentStudentsByYear(1).size());
//        System.out.println("Second-year Students: " + currentStudentAnalyzer.getCurrentStudentsByYear(2).size());
//        System.out.println("Third-year Students: " + currentStudentAnalyzer.getCurrentStudentsByYear(3).size());
//        System.out.println(graduateCourseAnalyzer.findSimilarCoursesByCorrelation(graduateCourseAnalyzer.getCourseByName("Quantum Neuro-Hacking")));



       //Predicting the NG Grades
//        findBestPropertyForEachCourse(studentInfo);
//        Predictor predictor = new Predictor();
//        predictor.predictNG();
//        CurrentStudentAnalyzer pStudentAnalyzer = new CurrentStudentAnalyzer(currentStudentData, studentInfo);
//        ArrayList<CurrentStudent> r = new ArrayList<>();
//        r = pStudentAnalyzer.getAllCurrentStudents();




        Predictor predictor = new Predictor();
        predictor.predictNGForestStump();


        //predicting the passrate for open courses
//        String predictedFileName = "src/csvFiles/PredictedGradesDecisionStump.csv";
//        String [][] predictedData = fileLoader.readCsvInto2DArray(predictedFileName);
//        PredictionAnalyzer predictionAnalyzer = new PredictionAnalyzer(predictedData, studentInfo, currentStudentData);
//        ArrayList<CurrentStudent> pStudents = new ArrayList<>();
//        pStudents = predictionAnalyzer.getPredictedStudents();
//        ArrayList<Double> predictedPassRates = new ArrayList<>();
//        predictedPassRates = predictionAnalyzer.predictPassRate();
//        Double[] a = predictedPassRates.toArray(new Double[predictedPassRates.size()]);
//        System.out.println(Arrays.deepToString(a));

        Double[] a = predictor.predictPassRate();
        System.out.println(Arrays.deepToString(a));






    }
    public static void findBestPropertyForEachCourse(String[][] studentInfo){
        Predictor predictor = new Predictor();

        for (Course course: predictor.currentCourses){
            ArrayList<String> result = new ArrayList<>();
            int courseID = course.getCourseID();
            if (course.getStudentsCount() == 0){
                Course similar = predictor.findSimilarCoursesByCorrelation(courseID);
                result = predictor.findBestProperty(predictor.currentCourses.get(similar.getCourseID()), studentInfo);
            }else {
                //ArrayList<String> list = findBestProperty(course, studentInfo);
                result = predictor.findBestProperty(course, studentInfo);
            }
            System.out.println("Best property for the course " + course.getCourseName() + ": " + result.get(0) + " With the threshold: " + result.get(1) + " and variance reduction: " + result.get(2) + ", the total Variance: " + predictor.calculateVariance(course.getGrades()));
        }
    }
}