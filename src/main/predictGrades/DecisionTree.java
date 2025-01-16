package src.main.predictGrades;

import src.main.dataHandle.TwoDimensionalArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DecisionTree {
    public static ArrayList<Course> graduateCourses = new ArrayList<>();
    public static ArrayList<Course> currentCourses = new ArrayList<>();
    ArrayList<String> NSIBoundaries = new ArrayList<>();
    ArrayList<Integer> PCQBoundaries = new ArrayList<>();
    ArrayList<Integer> CARBoundaries = new ArrayList<>();
    ArrayList<String> TSIBoundaries = new ArrayList<>();
    ArrayList<Double> ARCBoundaries = new ArrayList<>();



    //Constructor
    public DecisionTree(){

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
    }

    public static void main(String[] args) {
        String graduateGradesFileName = "src/csvFiles/GraduateGrades.csv";
        String currentGradesFileName = "src/csvFiles/CurrentGrades.csv";
        String studentInfoFileName = "src/csvFiles/StudentInfo.csv";
        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        String[][] graduateData = fileLoader.readCsvInto2DArray(graduateGradesFileName);
        String[][] currentStudentData = fileLoader.readCsvInto2DArray(currentGradesFileName);
        String[][] studentInfo = fileLoader.readCsvInto2DArray(studentInfoFileName);



        ArrayList<Record> records = new ArrayList<>();
        int courseId = 1;
        for (int i = 1; i < currentStudentData.length; i++){
            if (!(currentStudentData[i][courseId].equals("NG"))){
                records.add(new Record(Integer.parseInt(currentStudentData[i][0]), Double.parseDouble(currentStudentData[i][courseId]),
                        studentInfo[i][1], Integer.parseInt(studentInfo[i][2]), Integer.parseInt(studentInfo[i][3].substring(0,1)), studentInfo[i][4], Double.parseDouble(studentInfo[i][5].substring(0,3))));
            }
        }
        System.out.println(records.size());
    }



    public TreeNode buildTree(ArrayList<Record> data, int maxDepth){
        ArrayList<Double> grades = new ArrayList<>();
        for (Record record : data){
            grades.add(record.getGrade());
        }

        //Create a leaf node
        if (maxDepth == 0 || isPure(grades) || data.size() <= 8){
            double predictedValue = calculateMean(grades);
            return new TreeNode(predictedValue);
        }

        //Find the split
        SplitResult splitResult = split(data);

        //Create an internal node
        TreeNode node = new TreeNode(splitResult.getProperty(), splitResult.getSplitPoint());

        //Build child nodes
        node.setLeft(buildTree(splitResult.getLeftRecords(), maxDepth-1));
        node.setRight(buildTree(splitResult.getRightRecords(), maxDepth-1));
        return node;
    }



    //Method that split the data on each node, returns a SplitResult object, containing leftgroup records, rightgroup records, the property that the split is done on that, and the split point
    //gets an arraylist of records containing attributes and grade for the course
    public SplitResult split (ArrayList<Record> records){
        ArrayList<Double> gradesList = new ArrayList<>();
        ArrayList<String> NSIList = new ArrayList<>();
        ArrayList<Integer> PCQList = new ArrayList<>();
        ArrayList<Integer> CARList = new ArrayList<>();
        ArrayList<String> TSIList = new ArrayList<>();
        ArrayList<Double> ARCList = new ArrayList<>();
        for (int i = 0; i < records.size(); i++){
            gradesList.add(records.get(i).getGrade());
            NSIList.add(records.get(i).getNSI());
            PCQList.add(records.get(i).getPCQ());
            CARList.add(records.get(i).getCAR());
            TSIList.add(records.get(i).getTSI());
            ARCList.add(records.get(i).getARC());
        }
        ArrayList<Double> mseValues = new ArrayList<>();
        mseValues.add(Double.parseDouble(findBestSplitString(NSIList , gradesList).get(1)));
        mseValues.add(Double.parseDouble(findBestSplitInt(PCQList, gradesList).get(1)));
        mseValues.add(Double.parseDouble(findBestSplitInt(CARList, gradesList).get(1)));
        mseValues.add(Double.parseDouble(findBestSplitString(TSIList , gradesList).get(1)));
        mseValues.add(Double.parseDouble(findBestSplitDouble(ARCList, gradesList).get(1)));
        double maxMseReduction = Collections.max(mseValues);
        ArrayList<String> splitData = new ArrayList<>();
        ArrayList<Record> leftGroup = new ArrayList<>();
        ArrayList<Record> rightGroup = new ArrayList<>();

        if (maxMseReduction == mseValues.get(0)){
            splitData.add("NSI");
            splitData.add(findBestSplitString(NSIList , gradesList).get(0));
            for (Record record : records){
                if (record.getNSI().equals(splitData.get(1))){
                    leftGroup.add(record);
                }else{
                    rightGroup.add(record);
                }
            }

        } else if (maxMseReduction == mseValues.get(1)) {
            splitData.add("PCQ");
            splitData.add(findBestSplitInt(PCQList , gradesList).get(0));
            for (Record record : records){
                if (record.getPCQ() <= Double.parseDouble(splitData.get(1))){
                    leftGroup.add(record);
                }else{
                    rightGroup.add(record);
                }
            }
        } else if (maxMseReduction == mseValues.get(2)) {
          splitData.add("CAR");
          splitData.add(findBestSplitInt(CARList , gradesList).get(0));
            for (Record record : records){
                if (record.getCAR() <= Double.parseDouble(splitData.get(1))){
                    leftGroup.add(record);
                }else{
                    rightGroup.add(record);
                }
            }
        } else if (maxMseReduction == mseValues.get(3)) {
            splitData.add("TSI");
            splitData.add(findBestSplitString(TSIList , gradesList).get(0));
            for (Record record : records){
                if (record.getTSI().equals(splitData.get(1))){
                    leftGroup.add(record);
                }else{
                    rightGroup.add(record);
                }
            }
        } else if (maxMseReduction == mseValues.get(4)) {
            splitData.add("ARC");
            splitData.add(findBestSplitDouble(ARCList , gradesList).get(0));
            for (Record record : records){
                if (record.getARC() <= Double.parseDouble(splitData.get(1))){
                    leftGroup.add(record);
                }else{
                    rightGroup.add(record);
                }
            }
        }

        SplitResult splitResult = new SplitResult(leftGroup, rightGroup, splitData.get(0), splitData.get(1));
        return splitResult;
    }

    public ArrayList<String> findBestSplitDouble(ArrayList<Double> boundaries, ArrayList<Double> grades){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Double> sortedBoundaries = new ArrayList<>(boundaries);
        Collections.sort(sortedBoundaries);
        double bestThreshold = 0;
        double bestMseReduction = 0;
        double mse;
        if (grades.size() == 0){
            mse = 0;
        } else {
            mse = calculateMSE(grades);
        }
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

//            if (leftGroup.size() <=8 || rightGroup.size() <= 8){
//                continue;
//            }

            double weightedMSE = (calculateMSE(leftGroup) * leftGroup.size() + calculateMSE(rightGroup) * rightGroup.size()) / grades.size();
            double mseReduction = mse - weightedMSE;
            if (mseReduction > bestMseReduction){
                bestMseReduction = mseReduction;
                bestThreshold = threshold;
            }
        }
        result.add(Double.toString(bestThreshold));
        result.add(Double.toString(bestMseReduction));
        result.add(Double.toString(leftMean));
        result.add(Double.toString(rightMean));
        System.out.println("The best threshold is: " + bestThreshold + " | MSE reduction: " + bestMseReduction + ", the total MSE: " + mse);
        return result;
    }

    public ArrayList<String> findBestSplitInt(ArrayList<Integer> boundaries, ArrayList<Double> grades){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Integer> sortedBoundaries = new ArrayList<>(boundaries);
        Collections.sort(sortedBoundaries);
        double bestThreshold = 0;
        double bestMseReduction = 0;
        double mse;
        if (grades.size() == 0){
            mse = 0;
        } else {
            mse = calculateMSE(grades);
        }
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

//            if (leftGroup.size() <=8 || rightGroup.size() <= 8){
//                continue;
//            }

            double weightedMSE = (calculateMSE(leftGroup) * leftGroup.size() + calculateMSE(rightGroup) * rightGroup.size()) / grades.size();
            double mseRediction = mse - weightedMSE;
            if (mseRediction > bestMseReduction){
                bestMseReduction = mseRediction;
                bestThreshold = threshold;
            }
        }
        result.add(Double.toString(bestThreshold));
        result.add(Double.toString(bestMseReduction));
        result.add(Double.toString(leftMean));
        result.add(Double.toString(rightMean));
        System.out.println("The best split is: " + bestThreshold + " | MSE reduction: " + bestMseReduction + ", the total MSE: " + mse);
        return result;
    }

    public ArrayList<String> findBestSplitString (ArrayList<String> boundaries, ArrayList<Double> grades) {
        ArrayList<String> result = new ArrayList<>();
        Set<String> bounderiesSet = new HashSet<>(boundaries);
        ArrayList<String> uniqueBounderies = new ArrayList<>(bounderiesSet);

        double mse;
        if (grades.size() == 0){
            mse = 0;
        } else {
            mse = calculateMSE(grades);
        }
        String bestSplit = null;
        double bestMseReduction = 0;
        double leftMean = 0;
        double rightMean = 0;
        for (String boundary : uniqueBounderies) {
            ArrayList<Double> leftGroup = new ArrayList<>();
            ArrayList<Double> rightGroup = new ArrayList<>();
            for (int j = 0; j < boundaries.size(); j++) {
                if (boundaries.get(j).contentEquals(boundary)) {
                    leftGroup.add(grades.get(j));
                } else {
                    rightGroup.add(grades.get(j));
                }
            }
            leftMean = calculateMean(leftGroup);
            rightMean = calculateMean(rightGroup);
            if (leftGroup.isEmpty() || rightGroup.isEmpty()){
                continue;
            }
//            if (leftGroup.size() <=8 || rightGroup.size() <= 8){
//                continue;
//            }
            double weightedMSE = (calculateMSE(leftGroup) * leftGroup.size() + calculateMSE(rightGroup) * rightGroup.size()) / grades.size();
            double mseReducion = mse - weightedMSE;
            if (mseReducion > bestMseReduction) {
                bestMseReduction = mseReducion;
                bestSplit = boundary;
            }
        }
        result.add(bestSplit);
        result.add(Double.toString(bestMseReduction));
        result.add(Double.toString(leftMean));
        result.add(Double.toString(rightMean));
        System.out.println("The best boundary is: " + bestSplit + " | MSE reduction: " + bestMseReduction + ", the total MSE: " + mse);
        return result;
    }



    public Double calculateMSE (ArrayList<Double> values){
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("The list of values cannot be null or empty.");
        }
        double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }
        double mean = sum / values.size();

        double squaredErrorSum = 0.0;
        for (Double value : values) {
            squaredErrorSum += Math.pow(value - mean, 2);
        }
        double mse = squaredErrorSum / values.size();
        return mse;
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

    public boolean isPure(ArrayList<Double> grades){
        HashSet<Double> gradesSet = new HashSet<>(grades);
        if (gradesSet.size()==1){
            return true;
        }else {
            return false;
        }
    }

}
