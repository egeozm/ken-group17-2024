package src.main.predictGrades;

import src.main.dataHandle.TwoDimensionalArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Predictor {
    public static ArrayList<Course> graduateCourses = new ArrayList<>();
    public static ArrayList<Course> currentCourses = new ArrayList<>();
    public Predictor() {
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


        for (int i = 1; i < graduateData[0].length; i++){
            ArrayList<Double> gradesList = new ArrayList<>();
            for (int j = 1; j < graduateData.length; j++){
                if (graduateData[j][i].contentEquals("NG")){
                    gradesList.add(null);
                }else {
                    gradesList.add(Double.parseDouble(graduateData[j][i]));
                }
            }
            Course c = new Course(i-1, graduateData[0][i], gradesList);
            graduateCourses.add(c);
        }
        for (int i = 1; i < currentStudentData[0].length; i++){
            ArrayList<Double> gradesList = new ArrayList<>();
            for (int j = 1; j < currentStudentData.length; j++){
                if (currentStudentData[j][i].contentEquals("NG")){
                    gradesList.add(null);
                }else {
                    gradesList.add(Double.parseDouble(currentStudentData[j][i]));
                }
            }
            Course c = new Course(i-1, currentStudentData[0][i], gradesList);
            currentCourses.add(c);
        }
    }

    public ArrayList<String> findBestProperty(Course c, String[][] studentInfo){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> NSIBoundaries = new ArrayList<>();
        ArrayList<Integer> PCQBoundaries = new ArrayList<>();
        ArrayList<Integer> CARBoundaries = new ArrayList<>();
        ArrayList<String> TSIBoundaries = new ArrayList<>();
        ArrayList<Double> ARCBoundaries = new ArrayList<>();
        for (int i = 1; i < studentInfo[0].length; i++){
            for (int j = 1; j < studentInfo.length; j++){
                if (i == 1)
                    NSIBoundaries.add(studentInfo[j][i]);
                else if (i == 2)
                    PCQBoundaries.add(Integer.parseInt(studentInfo[j][i]));
                else if (i == 3)
                    CARBoundaries.add(Integer.parseInt(studentInfo[j][i].substring(0,1)));
                else if (i == 4)
                    TSIBoundaries.add(studentInfo[j][i]);
                else if (i == 5)
                    ARCBoundaries.add(Double.parseDouble(studentInfo[j][i].substring(0,3)));
            }
        }
        ArrayList<Double> variances = new ArrayList<>();
        variances.add(Double.parseDouble(findBestThresholdForString(NSIBoundaries , c.getGrades()).get(1)));
        variances.add(Double.parseDouble(findBestThresholdForInt(PCQBoundaries, c.getGrades()).get(1)));
        variances.add(Double.parseDouble(findBestThresholdForInt(CARBoundaries, c.getGrades()).get(1)));
        variances.add(Double.parseDouble(findBestThresholdForString(TSIBoundaries , c.getGrades()).get(1)));
        variances.add(Double.parseDouble(findBestThresholdForDouble(ARCBoundaries, c.getGrades()).get(1)));
        double maxVarianceReduction = Collections.max(variances);
        String bestProperty;
        String bestBoundary;
        double bestThreshold;
        if (maxVarianceReduction == variances.get(0)){
            bestProperty = "NSI";
            bestBoundary = findBestThresholdForString(NSIBoundaries, c.getGrades()).get(0);
            result.add(bestProperty);
            result.add(bestBoundary);
            result.add(Double.toString(maxVarianceReduction));
            // System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the boundary: " + bestBoundary + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if(maxVarianceReduction == variances.get(1)){
            bestProperty = "PCQ";
            bestThreshold = Double.parseDouble(findBestThresholdForInt(PCQBoundaries, c.getGrades()).get(0));
            result.add(bestProperty);
            result.add(Double.toString(bestThreshold));
            result.add(Double.toString(maxVarianceReduction));
            //System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the threshold: " + bestThreshold + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if(maxVarianceReduction == variances.get(2)){
            bestProperty = "CAR";
            ArrayList<String> res = findBestThresholdForInt(CARBoundaries,c.getGrades());
            bestThreshold = Double.parseDouble(res.get(0));
            result.add(bestProperty);
            result.add(Double.toString(bestThreshold));
            result.add(Double.toString(maxVarianceReduction));
            result.add(res.get(2));
            result.add(res.get(3));
            //  System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the threshold: " + bestThreshold + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if (maxVarianceReduction == variances.get(3)){
            bestProperty = "TSI";
            bestBoundary = findBestThresholdForString(TSIBoundaries, c.getGrades()).get(0);
            result.add(bestProperty);
            result.add(bestBoundary);
            result.add(Double.toString(maxVarianceReduction));
            //  System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the boundary: " + bestBoundary + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if (maxVarianceReduction == variances.get(4)){
            bestProperty = "ARC";
            bestThreshold = Double.parseDouble(findBestThresholdForDouble(ARCBoundaries, c.getGrades()).get(0));
            result.add(bestProperty);
            result.add(Double.toString(bestThreshold));
            result.add(Double.toString(maxVarianceReduction));
            //  System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the threshold: " + bestThreshold + " and variance reduction: " + maxVarianceReduction + ", total variance: " + calculateVariance(c.getGrades()));
        }
        return result; // returning a String ArrayList: {propertyName, boundary, varianceReduction}
    }



    // Finding the best threshold (boundary) for the given property => 3 different methods for double, int, string
    public ArrayList<String> findBestThresholdForInt(ArrayList<Integer> boundaries, ArrayList<Double> grades){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Integer> sortedBoundaries = new ArrayList<>(boundaries);
        Collections.sort(sortedBoundaries);
        double bestThreshold = 0;
        double bestVarianceReduction = 0;
        double initialVariance = calculateVariance(grades);
        Set<Integer> uniqueBoundariesSet = new HashSet<>(sortedBoundaries);
        ArrayList<Integer> sortedUniqueBoundaries = new ArrayList<>(uniqueBoundariesSet);
        double leftMean = 0;
        double rightMean = 0;


        for (int i = 1; i < sortedUniqueBoundaries.size(); i++){
            double threshold = ((double)sortedUniqueBoundaries.get(i) + (double)sortedUniqueBoundaries.get(i - 1)) / 2;
            ArrayList<Double> leftGroup = new ArrayList<>();
            ArrayList<Double> rightGroup = new ArrayList<>();
            for (int j = 0; j < boundaries.size(); j++){
                if (boundaries.get(j) <= threshold){
                    leftGroup.add(grades.get(j));
                }else{
                    rightGroup.add(grades.get(j));
                }
            }
            leftMean = calculateMean(leftGroup);
            rightMean = calculateMean(rightGroup);
            double weightedVariance = (calculateVariance(leftGroup) * leftGroup.size() + calculateVariance(rightGroup) * rightGroup.size()) / grades.size();
            double varianceReduction = initialVariance - weightedVariance;
            if (varianceReduction > bestVarianceReduction){
                bestVarianceReduction = varianceReduction;
                bestThreshold = threshold;
            }
        }
        result.add(Double.toString(bestThreshold));
        result.add(Double.toString(bestVarianceReduction));
        result.add(Double.toString(leftMean));
        result.add(Double.toString(rightMean));

        System.out.println("The best threshold is: " + bestThreshold + " | variance reduction: " + bestVarianceReduction + ", the total Variance: " + initialVariance);
        return result;
    }
    public ArrayList<String> findBestThresholdForDouble(ArrayList<Double> boundaries, ArrayList<Double> grades){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Double> sortedBoundaries = new ArrayList<>(boundaries);
        Collections.sort(sortedBoundaries);
        double bestThreshold = 0;
        double bestVarianceReduction = 0;
        double initialVariance = calculateVariance(grades);
        Set<Double> uniqueBoundariesSet = new HashSet<>(sortedBoundaries);
        ArrayList<Double> sortedUniqueBoundaries = new ArrayList<>(uniqueBoundariesSet);
        double leftMean = 0;
        double rightMean = 0;

        for (int i = 1; i < sortedUniqueBoundaries.size(); i++){
            double threshold = ((double)sortedUniqueBoundaries.get(i) + (double)sortedUniqueBoundaries.get(i - 1)) / 2;
            ArrayList<Double> leftGroup = new ArrayList<>();
            ArrayList<Double> rightGroup = new ArrayList<>();

            for (int j = 0; j < boundaries.size(); j++){
                if (boundaries.get(j) <= threshold){
                    leftGroup.add(grades.get(j));
                }else{
                    rightGroup.add(grades.get(j));
                }
            }
            leftMean = calculateMean(leftGroup);
            rightMean = calculateMean(rightGroup);
            double weightedVariance = (calculateVariance(leftGroup) * leftGroup.size() + calculateVariance(rightGroup) * rightGroup.size()) / grades.size();
            double varianceReduction = initialVariance - weightedVariance;
            if (varianceReduction > bestVarianceReduction){
                bestVarianceReduction = varianceReduction;
                bestThreshold = threshold;
            }
        }
        result.add(Double.toString(bestThreshold));
        result.add(Double.toString(bestVarianceReduction));
        result.add(Double.toString(leftMean));
        result.add(Double.toString(rightMean));
        System.out.println("The best threshold is: " + bestThreshold + " | variance reduction: " + bestVarianceReduction + ", the total Variance: " + initialVariance);
        return result;
    }

    public ArrayList<String> findBestThresholdForString(ArrayList<String> boundaries, ArrayList<Double> grades){
        ArrayList<String> result = new ArrayList<>();
        Set<String> bounderiesSet = new HashSet<>(boundaries);
        ArrayList<String> uniqueBounderies = new ArrayList<>(bounderiesSet);
        double initialVariance = calculateVariance(grades);
        String bestBoundary = null;
        double bestVarianceReduction = 0;
        double leftMean = 0;
        double rightMean = 0;
        for (String boundary : uniqueBounderies){
            ArrayList<Double> leftGroup = new ArrayList<>();
            ArrayList<Double> rightGroup = new ArrayList<>();
            for (int j = 0; j < boundaries.size(); j++){
                if (boundaries.get(j).contentEquals(boundary)){
                    leftGroup.add(grades.get(j));
                }else{
                    rightGroup.add(grades.get(j));
                }
            }

            leftMean = calculateMean(leftGroup);
            rightMean = calculateMean(rightGroup);
            double weightedVariance = (calculateVariance(leftGroup) * leftGroup.size() + calculateVariance(rightGroup) * rightGroup.size()) / grades.size();
            double varianceReduction = initialVariance - weightedVariance;
            if (varianceReduction > bestVarianceReduction){
                bestVarianceReduction = varianceReduction;
                bestBoundary = boundary;
            }
        }

        result.add(bestBoundary);
        result.add(Double.toString(bestVarianceReduction));
        result.add(Double.toString(leftMean));
        result.add(Double.toString(rightMean));
        System.out.println("The best boundary is: " + bestBoundary + " | variance reduction: " + bestVarianceReduction + ", the total Variance: " + initialVariance);
        return result;
    }

    public Course findSimilarCoursesByCorrelation(int courseID){
        double highestSimilarity = 0;
        Course mostSimilarCourse = null;
        for(Course c1 : graduateCourses){
            if (!(c1.getCourseID() == courseID)){
                double similarity = calculateCorrelation(graduateCourses.get(courseID), c1);
                if (similarity > highestSimilarity){
                    highestSimilarity = similarity;
                    mostSimilarCourse = c1;
                }
            }
        }
        return mostSimilarCourse;
    }

    public double calculateCorrelation(Course c1, Course c2){
        ArrayList<Double> grades1 = c1.getGrades();
        ArrayList<Double> grades2 = c2.getGrades();
        double mean1 = c1.getAverageGrade();
        double mean2 = c2.getAverageGrade();
        double sumXY = 0;
        double sumX2 = 0;
        double sumY2 = 0;
        for (int i = 0; i < grades1.size(); i++){
            double g1 = grades1.get(i);
            double g2 = grades2.get(i);
            double x = g1 - mean1;
            double y = g2 - mean2;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }
        if(sumXY > 0 && sumX2 > 0 && sumY2 > 0){
            return sumXY / Math.sqrt(sumX2 * sumY2);
        }else{
            return 0;
        }
    }
    public void predictNG (){
        CSVWriter writer = new CSVWriter();
        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        String[][] graduateData = fileLoader.readCsvInto2DArray("src/csvFiles/GraduateGrades.csv");
        String[][] currentStudentData = fileLoader.readCsvInto2DArray("src/csvFiles/CurrentGrades.csv");
        String[][] studentInfo = fileLoader.readCsvInto2DArray("src/csvFiles/StudentInfo.csv");
        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(currentStudentData, studentInfo);
        ArrayList<CurrentStudent> currentStudents = new ArrayList<>();currentStudentAnalyzer.getAllCurrentStudents();
        currentStudents = currentStudentAnalyzer.getAllCurrentStudents();
        CourseAnalyzer courseAnalyzer = new CourseAnalyzer(currentStudentData, currentStudentData[0]);
        int row = 0;
        for (CurrentStudent student: currentStudents){
            ArrayList<Double> grades = new ArrayList<>();
            grades = student.getGrades();
            for (int i = 0; i < grades.size(); i++){
                if (grades.get(i) == null){
                    ArrayList<String> property = findBestProperty(courseAnalyzer.getCourseByID(i),studentInfo);
                    switch (property.get(0)){
                        case "CAR":
                            if (student.getCAR() <= Double.parseDouble(property.get(1))){
                                grades.set(i,(double)(Math.round(Double.parseDouble(property.get(3)))));
                            }else{
                                grades.set(i, (double)(Math.round(Double.parseDouble(property.get(4)))));
                            }
                            break;
                    }
                }
            }
            grades.set(8, grades.get(21));
            grades.set(13, grades.get(15));
            grades.set(31, grades.get(30));

            student.setPredictedGrades(grades);
            writer.writeToCSV(row ,grades);
            row++;
        }
    }

    public void predictNGForestStump(){
        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        String[][] graduateData = fileLoader.readCsvInto2DArray("src/csvFiles/GraduateGrades.csv");
        String[][] currentStudentData = fileLoader.readCsvInto2DArray("src/csvFiles/CurrentGrades.csv");
        String[][] studentInfo = fileLoader.readCsvInto2DArray("src/csvFiles/StudentInfo.csv");
        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(currentStudentData, studentInfo);
        ArrayList<CurrentStudent> currentStudents = new ArrayList<>();
        currentStudentAnalyzer.getAllCurrentStudents();
        currentStudents = currentStudentAnalyzer.getAllCurrentStudents();
        CourseAnalyzer courseAnalyzer = new CourseAnalyzer(currentStudentData, currentStudentData[0]);
        int row = 0;

        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> NSIBoundaries = new ArrayList<>();
        ArrayList<Integer> PCQBoundaries = new ArrayList<>();
        ArrayList<Integer> CARBoundaries = new ArrayList<>();
        ArrayList<String> TSIBoundaries = new ArrayList<>();
        ArrayList<Double> ARCBoundaries = new ArrayList<>();
        for (int i = 1; i < studentInfo[0].length; i++){
            for (int j = 1; j < studentInfo.length; j++){
                if (i == 1)
                    NSIBoundaries.add(studentInfo[j][i]);
                else if (i == 2)
                    PCQBoundaries.add(Integer.parseInt(studentInfo[j][i]));
                else if (i == 3)
                    CARBoundaries.add(Integer.parseInt(studentInfo[j][i].substring(0,1)));
                else if (i == 4)
                    TSIBoundaries.add(studentInfo[j][i]);
                else if (i == 5)
                    ARCBoundaries.add(Double.parseDouble(studentInfo[j][i].substring(0,3)));
            }
        }
        ArrayList<Double> variances = new ArrayList<>();
        ArrayList<String> thresholds = new ArrayList<>();
        ArrayList<Double> means = new ArrayList<>();

        String[][] predictedGrades = new String[currentStudentData.length][currentStudentData[0].length];

        double overallGPA = 0;
        int counter = 0;
        double sum = 0;


        for (int i = 0; i < currentStudentData.length; i++) {
            for (int j = 0; j < currentStudentData[i].length; j++) {
                predictedGrades[i][j] = currentStudentData[i][j];
                if (i > 0 && j > 0 && !predictedGrades[i][j].contentEquals("NG")){
                    sum += Double.parseDouble(predictedGrades[i][j]);
                    counter++;
                }
            }
        }
        overallGPA = sum / counter;
//        for (Course c: currentCourses) {
//            ArrayList<Integer> sortedProperties = new ArrayList<>();
//            ArrayList<String> p0 = new ArrayList<>(findBestThresholdForString(NSIBoundaries, c.getGrades()));
//            variances.add(Double.parseDouble((p0.get(1))));
//            ArrayList<String> p1 = new ArrayList<>(findBestThresholdForInt(PCQBoundaries, c.getGrades()));
//            variances.add(Double.parseDouble(p1.get(1)));
//            ArrayList<String> p2 = new ArrayList<>(findBestThresholdForInt(CARBoundaries, c.getGrades()));
//            variances.add(Double.parseDouble(p2.get(1)));
//            ArrayList<String> p3 = new ArrayList<>(findBestThresholdForString(TSIBoundaries, c.getGrades()));
//            variances.add(Double.parseDouble(p3.get(1)));
//            ArrayList<String> p4 = new ArrayList<>(findBestThresholdForDouble(ARCBoundaries, c.getGrades()));
//            variances.add(Double.parseDouble(p4.get(1)));
//            sortedProperties = getSortedIndices(variances);

        //           ArrayList<Double> decisionStumpsGrades = new ArrayList<>();
        for (int i = 1; i < currentStudentData[0].length; i++){

            ArrayList<Integer> sortedProperties = new ArrayList<>();
            ArrayList<String> p0 = new ArrayList<>(findBestThresholdForString(NSIBoundaries, currentCourses.get(i-1).getGrades()));
            variances.add(Double.parseDouble((p0.get(1))));
            ArrayList<String> p1 = new ArrayList<>(findBestThresholdForInt(PCQBoundaries, currentCourses.get(i-1).getGrades()));
            variances.add(Double.parseDouble(p1.get(1)));
            ArrayList<String> p2 = new ArrayList<>(findBestThresholdForInt(CARBoundaries, currentCourses.get(i-1).getGrades()));
            variances.add(Double.parseDouble(p2.get(1)));
            ArrayList<String> p3 = new ArrayList<>(findBestThresholdForString(TSIBoundaries, currentCourses.get(i-1).getGrades()));
            variances.add(Double.parseDouble(p3.get(1)));
            ArrayList<String> p4 = new ArrayList<>(findBestThresholdForDouble(ARCBoundaries, currentCourses.get(i-1).getGrades()));
            variances.add(Double.parseDouble(p4.get(1)));
            sortedProperties = getSortedIndices(variances);
            ArrayList<Double> decisionStumpsGrades = new ArrayList<>();



            for (int j = 1; j < currentStudentData.length; j++) {
                if (currentStudentData[j][i].contentEquals("NG")) {
                    for (int k = 0; k < sortedProperties.size(); k++) {
                        switch (sortedProperties.get(k)) {
                            case 0:
                                if (p0.get(0) == null || studentInfo[j][1].contentEquals(p0.get(0))){
                                    decisionStumpsGrades.add(Double.parseDouble(p0.get(2)));
                                }else{
                                    decisionStumpsGrades.add(Double.parseDouble(p0.get(3)));
                                }
                                break;

                            case 1:
                                if (Double.parseDouble(studentInfo[j][2]) <= Double.parseDouble(p1.get(0))){
                                    decisionStumpsGrades.add(Double.parseDouble(p1.get(2)));
                                }else {
                                    decisionStumpsGrades.add(Double.parseDouble(p1.get(3)));
                                }
                                break;

                            case 2:
                                if (Double.parseDouble(studentInfo[j][3].substring(0,1)) <= Double.parseDouble(p2.get(0))){
                                    decisionStumpsGrades.add(Double.parseDouble(p2.get(2)));
                                }else {
                                    decisionStumpsGrades.add(Double.parseDouble(p2.get(3)));
                                }
                                break;

                            case 3:


                                if (p3.get(0) == null || studentInfo[j][4].equals(p3.get(0))){
                                    decisionStumpsGrades.add(Double.parseDouble(p3.get(2)));
                                }else {
                                    decisionStumpsGrades.add(Double.parseDouble(p3.get(3)));
                                }
                                break;

                            case 4:
                                if (Double.parseDouble(studentInfo[j][5].substring(0,3)) <= Double.parseDouble(p4.get(0))){
                                    decisionStumpsGrades.add(Double.parseDouble(p4.get(2)));
                                }else {
                                    decisionStumpsGrades.add(Double.parseDouble(p4.get(3)));
                                }
                                break;
                        }
                    }
                    double g = (50 * decisionStumpsGrades.get(0)) + (25 * decisionStumpsGrades.get(1)) + (15 * decisionStumpsGrades.get(2)) + (10 * decisionStumpsGrades.get(3)) + (5 * decisionStumpsGrades.get(4));
                    predictedGrades[j][i] = String.valueOf(Math.round(g/105));
                }
            }
        }
        for (int i = 1; i<predictedGrades.length; i++){
            predictedGrades[i][9] = predictedGrades [i][22];
            predictedGrades[i][14] = predictedGrades [i][16];
            predictedGrades[i][32] = predictedGrades [i][31];
        }

        //final stump: adjusting the grade by comparing the student's gpa with overall gpa
        for (int i = 1; i < currentStudentData.length; i++){
            double studentGPA = 0;
            double count = 0;
            double gradesSum = 0;
            for (int j = 1; j < currentStudentData[0].length; j++){
                if (currentStudentData[i][j].contentEquals("NG")){
                    continue;
                }else {
                    gradesSum += Double.parseDouble(predictedGrades[i][j]);
                    count++;
                }
            }
            studentGPA = gradesSum / count;
            double differanceGPA = studentGPA - overallGPA;
            for (int j = 1; j < currentStudentData[0].length; j++) {
                if (currentStudentData[i][j].contentEquals("NG")) {
                    predictedGrades[i][j] = String.valueOf(Math.round(Double.parseDouble(predictedGrades[i][j]) + differanceGPA));
                }
            }
        }

        CSVWriter writer = new CSVWriter();
        writer.writeArrayToCSV(predictedGrades);

    }




    //}

    //helper method
    public ArrayList<Integer> getSortedIndices(ArrayList<Double> values) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            indices.add(i);
        }
        indices.sort((i, j) -> Double.compare(values.get(j), values.get(i)));
        return indices;
    }

    public double calculateMean (ArrayList<Double> grades){
        ArrayList<Double> noNGValues = new ArrayList<>();
        for (int i = 0; i < grades.size(); i++) {
            if (grades.get(i) == null) {
                continue;
            }
            noNGValues.add(grades.get(i));
        }
        double sum = 0;
        double mean = 0;
        int count = 0;
        for (Double value : noNGValues) {
            sum += value;
            count++;
        }
        mean = sum / count;
        return mean;
    }

    public double calculateVariance(ArrayList<Double> values) {
        ArrayList<Double> noNGValues = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }
            noNGValues.add(values.get(i));
        }
        double sum = 0;
        double mean = 0;
        int count = 0;
        for (Double value : noNGValues) {
            sum += value;
            count++;
        }
        mean = sum / count;
        double result;
        double sigma = 0;
        for (Double value : noNGValues) {
            sigma += Math.pow((value - mean), 2);

        }
        result = sigma / count;
        return result;
    }

    public Double[] predictPassRate (){
        TwoDimensionalArray twoDimensionalArray = new TwoDimensionalArray();
        String predictedFileName = "src/csvFiles/PredictedGradesDecisionStump.csv";
        String [][] predictedData = twoDimensionalArray.readCsvInto2DArray(predictedFileName);
        Double[] passRates = new Double[predictedData[0].length - 1];
        for (int i = 1; i < predictedData[0].length; i++) {
            double passRate = 0;
            double counter = 0;
            for (int j = 1; j < predictedData.length; j++) {
                if (Double.parseDouble(predictedData[j][i]) >= 6.0) {
                    counter++;
                }
            }
            passRate = Math.round((counter / (predictedData.length - 1) * 100)) - 1;
            passRates[i - 1] = passRate;
        }
        return passRates;
    }

}
