package src.main.predictGrades;

import src.main.dataHandle.TwoDimensionalArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Predict {
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
        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(currentStudentData, studentInfo);



        ArrayList<Record> records = new ArrayList<>();
        int courseId = 1;
        for (int i = 1; i < currentStudentData.length; i++){
            if (!(currentStudentData[i][courseId].equals("NG"))){
                records.add(new Record(Integer.parseInt(currentStudentData[i][0]), Double.parseDouble(currentStudentData[i][courseId]),
                        studentInfo[i][1], Integer.parseInt(studentInfo[i][2]), Integer.parseInt(studentInfo[i][3].substring(0,1)), studentInfo[i][4], Double.parseDouble(studentInfo[i][5].substring(0,3))));
            }
        }

        ArrayList<Record> trainingSet = new ArrayList<>();
        ArrayList<Record> testingSet = new ArrayList<>();
        List<ArrayList<Record>> shuffle = new ArrayList<>();
        shuffle = splitData(records,0.8);
        trainingSet = shuffle.get(0);
        testingSet = shuffle.get(1);

        DecisionTree decisionTree = new DecisionTree();

        TreeNode d = decisionTree.buildTree(trainingSet, 6);
        ArrayList<Record> rec = new ArrayList<>();
        for (int i = 1; i < currentStudentData.length; i++){
            if (currentStudentData[i][courseId].equals("NG")){
                rec.add(new Record(Integer.parseInt(currentStudentData[i][0]),0,
                        studentInfo[i][1], Integer.parseInt(studentInfo[i][2]), Integer.parseInt(studentInfo[i][3].substring(0,1)), studentInfo[i][4], Double.parseDouble(studentInfo[i][5].substring(0,3))));
            }
        }
        ArrayList<Double> actualGrades = new ArrayList<>();
        ArrayList<Double> predictedGrades = new ArrayList<>();
        for(Record r : testingSet){
            double predicted = predictNG(d,r);
            System.out.println("ID: " + r.getId() + " => " + predicted + " | " + currentStudentAnalyzer.getCurrentStudentByID(r.getId()).getGrades().get(0));

            //for calculating the accuracy
            predictedGrades.add(predicted);
            actualGrades.add(currentStudentAnalyzer.getCurrentStudentByID(r.getId()).getGrades().get(0));

        }
        System.out.println("Accuracy by MSE: " + calculateAccuracy(predictedGrades,actualGrades) + " Average error: " + Math.sqrt(calculateAccuracy(predictedGrades,actualGrades)));
    }

    public static double calculateAccuracy(ArrayList<Double> predicted, ArrayList<Double> actual) {
        if (predicted.size() != actual.size()) {
            throw new IllegalArgumentException("Predicted and actual lists must have the same size.");
        }

        double sumSquaredError = 0.0;
        for (int i = 0; i < predicted.size(); i++) {
            double error = predicted.get(i) - actual.get(i);
            sumSquaredError += error * error;
        }
        return sumSquaredError / predicted.size();
    }

    public static List<ArrayList<Record>> splitData(ArrayList<Record> records, double trainingRatio) {
        // Shuffle the original list randomly
        Collections.shuffle(records);

        // Calculate the split index
        int splitIndex = (int) (records.size() * trainingRatio);

        // Create the training and testing sets
        ArrayList<Record> trainingSet = new ArrayList<>(records.subList(0, splitIndex));
        ArrayList<Record> testingSet = new ArrayList<>(records.subList(splitIndex, records.size()));

        // Return both sets as a list of ArrayLists
        List<ArrayList<Record>> result = new ArrayList<>();
        result.add(trainingSet);
        result.add(testingSet);
        return result;
    }

    public static double predictNG (TreeNode root, Record record){
        TreeNode currentNode = root;
        while (!currentNode.isLeaf()){
            String splitProperty = currentNode.getSplitProperty();
            String splitPoint = currentNode.getSplitPoint().toString();

            if (splitProperty.equals("NSI")){
                String value = record.getNSI();
                if (value.equals(splitPoint)){
                    currentNode = currentNode.getLeft();
                }else {
                    currentNode = currentNode.getRight();
                }
            } else if (splitProperty.equals("PCQ")) {
                double value = record.getPCQ();
                if (value <= Double.parseDouble(splitPoint)){
                    currentNode = currentNode.getLeft();
                }else {
                    currentNode = currentNode.getRight();
                }
            }else if (splitProperty.equals("CAR")) {
                double value = record.getCAR();
                if (value <= Double.parseDouble(splitPoint)) {
                    currentNode = currentNode.getLeft();
                } else {
                    currentNode = currentNode.getRight();
                }
            } else if (splitProperty.equals("TSI")) {
                String value = record.getTSI();
                if (value.equals(splitPoint)) {
                    currentNode = currentNode.getLeft();
                } else {
                    currentNode = currentNode.getRight();
                }
            }else if (splitProperty.equals("ARC")) {
                double value = record.getARC();
                if (value <= Double.parseDouble(splitPoint)) {
                    currentNode = currentNode.getLeft();
                } else {
                    currentNode = currentNode.getRight();
                }
            }
        }
        return currentNode.getPredictedValue();
    }


    public boolean isNumerical(String property){
        if (property.equals("NSI") || property.equals("TSI")){
            return false;
        }
        return true;
    }
}
