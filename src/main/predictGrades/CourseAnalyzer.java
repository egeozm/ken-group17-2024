package src.main.predictGrades;

import src.main.dataHandle.TwoDimensionalArray;

import java.util.*;

public class CourseAnalyzer {
    private String[][] data;
    private String[] headers;
    ArrayList<Course> courses = new ArrayList<>();
    ArrayList<Course> graduatedCourses = new ArrayList<>();
    TwoDimensionalArray fileLoader = new TwoDimensionalArray();


    public CourseAnalyzer(String[][] data, String[] headers) {
        this.data = data;
        this.headers = headers;

        for (int i = 1; i < data[0].length; i++){
            ArrayList<Double> gradesList = new ArrayList<>();
            for (int j = 1; j < data.length; j++){
                if (data[j][i].contentEquals("NG")){
                    gradesList.add(null);
                }else {
                    gradesList.add(Double.parseDouble(data[j][i]));
                }
            }
            Course c = new Course(i-1, data[0][i], gradesList);
            courses.add(c);
        }
    }

    // Calculating the course difficulty
    public void sortByDifficulty(){
        ArrayList<Course> sortedCourses = new ArrayList<>(courses);
        sortedCourses = courses;
        Collections.sort(sortedCourses, Comparator.comparingDouble(Course::getAverageGrade));
        System.out.println("List of the courses, sorted by difficulty:");
        for (int i = 0; i < sortedCourses.size(); i++){
            System.out.println("Course "+ (i+1) +":" + sortedCourses.get(i).getCourseName() + "| Avg: " + sortedCourses.get(i).getAverageGrade());
        }
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

    public ArrayList<Course> getAllCourses() {
        return courses;
    }

    public ArrayList<Course> getCoursesByYear(int year) {
        ArrayList<Course> result = new ArrayList<>();
        for (Course course : courses) {
            if (course.getYearOfTheCourse() == year) {
                result.add(course);
            }
        }
        return result;
    }

    public Course getCourseByName(String courseName) {
        for (Course c : courses) {
            if (c.getCourseName().equals(courseName)) {
                return c;
            }
        }
        return null;
    }

    // Find the most similar course, using Pearson Correlation
    public int findSimilarCoursesByCorrelation(Course c){
        double highestSimilarity = 0;
        Course mostSimilarCourse = null;
        for(Course c1 : courses){
            if (!(c1.getCourseID() == c.getCourseID())){
                double similarity = calculateCorrelation(c, c1);
                if (similarity > highestSimilarity){
                    highestSimilarity = similarity;
                    mostSimilarCourse = c1;
                }
            }
        }
        System.out.println("The most similar course to the course " + c.getCourseName() + ": " + mostSimilarCourse.getCourseName() + " | Pearson Correlation coefficient: " + highestSimilarity);
        c.setSimilarCourse(mostSimilarCourse);
        return mostSimilarCourse.getCourseID();
    }
    public int findSimilarCoursesByCorrelation(int courseID){
        double highestSimilarity = 0;
        Course mostSimilarCourse = null;
        for(Course c1 : courses){
            if (!(c1.getCourseID() == courseID)){
                double similarity = calculateCorrelation(courses.get(courseID), c1);
                if (similarity > highestSimilarity){
                    highestSimilarity = similarity;
                    mostSimilarCourse = c1;
                }
            }
        }
        return mostSimilarCourse.getCourseID();
    }
    // Find the most similar course for each course
    public void findSimilarCourseForEachCourse (){
        for (Course c : courses){
            findSimilarCoursesByCorrelation(c);
        }
    }

    public Course getCourseByID(int courseID) {
        for (Course c : courses){
            if (c.getCourseID() == courseID){
                return c;
            }
        }
        return null;
    }

    // Predicting

    // Finding the best (Property + boundary) and the variance reduction
    public ArrayList<String> findBestProperty(Course c, String[][] studentInfo){
        if (c.getStudentsCount() == 0){
            int similarCourseID = findSimilarCoursesByCorrelation(c.getCourseID());
            ArrayList<Double> similarCourseGrades = new ArrayList<>();

            Course similarCourse = new Course(c.getCourseID(), c.getCourseName(), similarCourseGrades);
            return findBestProperty(similarCourse, studentInfo);
        }
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
            System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the boundary: " + bestBoundary + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if(maxVarianceReduction == variances.get(1)){
            bestProperty = "PCQ";
            bestThreshold = Double.parseDouble(findBestThresholdForInt(PCQBoundaries, c.getGrades()).get(0));
            result.add(bestProperty);
            result.add(Double.toString(bestThreshold));
            result.add(Double.toString(maxVarianceReduction));
            System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the threshold: " + bestThreshold + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if(maxVarianceReduction == variances.get(2)){
            bestProperty = "CAR";
            bestThreshold = Double.parseDouble(findBestThresholdForInt(CARBoundaries, c.getGrades()).get(0));
            result.add(bestProperty);
            result.add(Double.toString(bestThreshold));
            result.add(Double.toString(maxVarianceReduction));
            System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the threshold: " + bestThreshold + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if (maxVarianceReduction == variances.get(3)){
            bestProperty = "TSI";
            bestBoundary = findBestThresholdForString(TSIBoundaries, c.getGrades()).get(0);
            result.add(bestProperty);
            result.add(bestBoundary);
            result.add(Double.toString(maxVarianceReduction));
            System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the boundary: " + bestBoundary + " and variance reduction: " + maxVarianceReduction + ", the total Variance: " + calculateVariance(c.getGrades()));
        }else if (maxVarianceReduction == variances.get(4)){
            bestProperty = "ARC";
            bestThreshold = Double.parseDouble(findBestThresholdForDouble(ARCBoundaries, c.getGrades()).get(0));
            result.add(bestProperty);
            result.add(Double.toString(bestThreshold));
            result.add(Double.toString(maxVarianceReduction));
            System.out.println("Best property for the course " + c.getCourseName() + ": " + bestProperty + " With the threshold: " + bestThreshold + " and variance reduction: " + maxVarianceReduction + ", total variance: " + calculateVariance(c.getGrades()));
        }
        return result; // returning a String ArrayList: {propertyName, boundary, varianceReduction}
    }

    public void findBestPropertyForEachCourse(String[][] studentInfo){
        for (Course course: courses){
            //ArrayList<String> list = findBestProperty(course, studentInfo);
            findBestProperty(course, studentInfo);
        }
    }

    // Finding the best threshold (boundary) for the given property => 3 different methods for double, int, string
    public ArrayList<String> findBestThresholdForInt(ArrayList<Integer> boundaries, ArrayList<Double> grades){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Integer> sortedBoundaries = new ArrayList<>(boundaries);
        Collections.sort(sortedBoundaries);
        double bestThreshold = 0;
        double bestVarianceReduction = 0;
        double initialVariance = calculateVariance(grades);

        for (int i = 1; i < sortedBoundaries.size(); i++){
            double threshold = ((double)sortedBoundaries.get(i) + (double)sortedBoundaries.get(i - 1)) / 2;
            ArrayList<Double> leftGroup = new ArrayList<>();
            ArrayList<Double> rightGroup = new ArrayList<>();
            for (int j = 0; j < boundaries.size(); j++){
                if (boundaries.get(j) <= threshold){
                    leftGroup.add(grades.get(j));
                }else{
                    rightGroup.add(grades.get(j));
                }
            }
            double weightedVariance = (calculateVariance(leftGroup) * leftGroup.size() + calculateVariance(rightGroup) * rightGroup.size()) / grades.size();
            double varianceReduction = initialVariance - weightedVariance;
            if (varianceReduction > bestVarianceReduction){
                bestVarianceReduction = varianceReduction;
                bestThreshold = threshold;
            }
        }
        result.add(Double.toString(bestThreshold));
        result.add(Double.toString(bestVarianceReduction));
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

        for (int i = 1; i < sortedBoundaries.size(); i++){
            double threshold = ((double)sortedBoundaries.get(i) + (double)sortedBoundaries.get(i - 1)) / 2;
            ArrayList<Double> leftGroup = new ArrayList<>();
            ArrayList<Double> rightGroup = new ArrayList<>();
            for (int j = 0; j < boundaries.size(); j++){
                if (boundaries.get(j) <= threshold){
                    leftGroup.add(grades.get(j));
                }else{
                    rightGroup.add(grades.get(j));
                }
            }
            double weightedVariance = (calculateVariance(leftGroup) * leftGroup.size() + calculateVariance(rightGroup) * rightGroup.size()) / grades.size();
            double varianceReduction = initialVariance - weightedVariance;
            if (varianceReduction > bestVarianceReduction){
                bestVarianceReduction = varianceReduction;
                bestThreshold = threshold;
            }
        }
        result.add(Double.toString(bestThreshold));
        result.add(Double.toString(bestVarianceReduction));
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

            double weightedVariance = (calculateVariance(leftGroup) * leftGroup.size() + calculateVariance(rightGroup) * rightGroup.size()) / grades.size();
            double varianceReduction = initialVariance - weightedVariance;
            if (varianceReduction > bestVarianceReduction){
                bestVarianceReduction = varianceReduction;
                bestBoundary = boundary;
            }
        }

        result.add(bestBoundary);
        result.add(Double.toString(bestVarianceReduction));
        System.out.println("The best boundary is: " + bestBoundary + " | variance reduction: " + bestVarianceReduction + ", the total Variance: " + initialVariance);
        return result;
    }


    private double calculateVariance(ArrayList<Double> values) {
        ArrayList<Double> noNGValues = new ArrayList<>();
        for (int i = 0; i < values.size(); i++){
            if (values.get(i) == null){
                continue;
            }
            noNGValues.add(values.get(i));
        }
        double sum = 0;
        double mean = 0;
        int count = 0;
        for (Double value : noNGValues){
            sum += value;
            count++;
        }
        mean = sum / count;
        double result;
        double sigma = 0;
        for (Double value : noNGValues){
            sigma += Math.pow((value - mean), 2);

        }
        result = sigma / count;
        return result;
//
        //---------------------------------------------------------------------
//        ArrayList<Double> noNGValues = new ArrayList<>();
//        for (int i = 0; i < values.size(); i++){
//            if (values.get(i) == null){
//                continue;
//            }
//            noNGValues.add(values.get(i));
//        }
//        double mean = noNGValues.stream().mapToDouble(a -> a).average().orElse(0);
//        double variance = noNGValues.stream().mapToDouble(a -> Math.pow(a - mean, 2)).sum() / noNGValues.size();
//        return variance;
    }

}