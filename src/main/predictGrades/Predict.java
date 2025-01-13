package src.main.predictGrades;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
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
        for (int i = 1; i < currentStudentData.length; i++) {
            if (!(currentStudentData[i][courseId].equals("NG"))) {
                records.add(new Record(Integer.parseInt(currentStudentData[i][0]), Double.parseDouble(currentStudentData[i][courseId]),
                        studentInfo[i][1], Integer.parseInt(studentInfo[i][2]), Integer.parseInt(studentInfo[i][3].substring(0, 1)), studentInfo[i][4], Double.parseDouble(studentInfo[i][5].substring(0, 3))));
            }
        }

        ArrayList<Record> trainingSet = new ArrayList<>();
        ArrayList<Record> testingSet = new ArrayList<>();
        List<ArrayList<Record>> shuffle = new ArrayList<>();
        shuffle = splitData(records, 0.8);
        trainingSet = shuffle.get(0);
        testingSet = shuffle.get(1);

        DecisionTree decisionTree = new DecisionTree();

        TreeNode d = decisionTree.buildTree(trainingSet, 6);
        ArrayList<Record> rec = new ArrayList<>();
        for (int i = 1; i < currentStudentData.length; i++) {
            if (currentStudentData[i][courseId].equals("NG")) {
                rec.add(new Record(Integer.parseInt(currentStudentData[i][0]), 0,
                        studentInfo[i][1], Integer.parseInt(studentInfo[i][2]), Integer.parseInt(studentInfo[i][3].substring(0, 1)), studentInfo[i][4], Double.parseDouble(studentInfo[i][5].substring(0, 3))));
            }
        }
        ArrayList<Double> actualGrades = new ArrayList<>();
        ArrayList<Double> predictedGrades = new ArrayList<>();
        for (Record r : testingSet) {
            double predicted = predictNG(d, r);
            System.out.println("ID: " + r.getId() + " => " + predicted + " | " + currentStudentAnalyzer.getCurrentStudentByID(r.getId()).getGrades().get(0));

            //for calculating the accuracy
            predictedGrades.add(predicted);
            actualGrades.add(currentStudentAnalyzer.getCurrentStudentByID(r.getId()).getGrades().get(0));

        }
        System.out.println("Accuracy by MSE: " + calculateAccuracy(predictedGrades, actualGrades) + " Average error: " + Math.sqrt(calculateAccuracy(predictedGrades, actualGrades)));
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

    public static double predictNG(TreeNode root, Record record) {
        TreeNode currentNode = root;
        while (!currentNode.isLeaf()) {
            String splitProperty = currentNode.getSplitProperty();
            String splitPoint = currentNode.getSplitPoint().toString();

            if (splitProperty.equals("NSI")) {
                String value = record.getNSI();
                if (value.equals(splitPoint)) {
                    currentNode = currentNode.getLeft();
                } else {
                    currentNode = currentNode.getRight();
                }
            } else if (splitProperty.equals("PCQ")) {
                double value = record.getPCQ();
                if (value <= Double.parseDouble(splitPoint)) {
                    currentNode = currentNode.getLeft();
                } else {
                    currentNode = currentNode.getRight();
                }
            } else if (splitProperty.equals("CAR")) {
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
            } else if (splitProperty.equals("ARC")) {
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


    public boolean isNumerical(String property) {
        if (property.equals("NSI") || property.equals("TSI")) {
            return false;
        }
        return true;
    }

    public static String getGradeForStudent(String studentId, String courseName) {
        // Load data
        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        String[][] currentGrades = fileLoader.readCsvInto2DArray("src/csvFiles/CurrentGrades.csv");
        String[][] studentInfo = fileLoader.readCsvInto2DArray("src/csvFiles/StudentInfo.csv");

        // Retrieve course ID from CourseAnalyzer
        CourseAnalyzer courseAnalyzer = new CourseAnalyzer(currentGrades, currentGrades[0]);
        Course selectedCourse = courseAnalyzer.getCourseByName(courseName);
        if (selectedCourse == null) {
            return "Course not found.";
        }
        int courseId = selectedCourse.getCourseID();

        // Locate student and check grade
        for (String[] student : currentGrades) {
            if (student[0].equals(studentId)) { // Student found
                String grade = student[courseId + 1]; // Offset for header row

                if (grade.equals("NG")) {
                    // Build a record for the student
                    CurrentStudentAnalyzer analyzer = new CurrentStudentAnalyzer(currentGrades, studentInfo);
                    CurrentStudent studentData = analyzer.getCurrentStudentByID(Integer.parseInt(studentId));

                    Record record = new Record(
                            studentData.getStudentID(),
                            0, // Placeholder for grade
                            studentData.getNSI(),
                            studentData.getPCQ(),
                            studentData.getCAR(),
                            studentData.getTSI(),
                            studentData.getARC()
                    );

                    // Build decision tree and predict grade
                    DecisionTree decisionTree = new DecisionTree();
                    ArrayList<Record> records = loadTrainingData(currentGrades, studentInfo, courseId);
                    TreeNode root = decisionTree.buildTree(records, 6); // Depth is adjustable
                    double predictedGrade = Predict.predictNG(root, record);

                    return "Predicted grade for " + courseName + ": " + predictedGrade;
                } else {
                    return "Student already has a grade for " + courseName + ": " + grade;
                }
            }
        }
        return "Student ID not found.";
    }

    // Helper method to load training data for a course
    private static ArrayList<Record> loadTrainingData(String[][] gradesData, String[][] studentInfo, int courseId) {
        ArrayList<Record> records = new ArrayList<>();
        for (int i = 1; i < gradesData.length; i++) {
            if (!gradesData[i][courseId + 1].equals("NG")) { // Only include known grades
                records.add(new Record(
                        Integer.parseInt(gradesData[i][0]),
                        Double.parseDouble(gradesData[i][courseId + 1]),
                        studentInfo[i][1], // NSI
                        Integer.parseInt(studentInfo[i][2]), // PCQ
                        Integer.parseInt(studentInfo[i][3].substring(0, 1)), // CAR
                        studentInfo[i][4], // TSI
                        Double.parseDouble(studentInfo[i][5].substring(0, 3)) // ARC
                ));
            }
        }
        return records;
    }

    public static Scene visualizeDecisionTree(String courseName, int studentId) {
        // Load data
        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        String[][] currentGrades = fileLoader.readCsvInto2DArray("src/csvFiles/CurrentGrades.csv");
        String[][] studentInfo = fileLoader.readCsvInto2DArray("src/csvFiles/StudentInfo.csv");

        // Retrieve course ID and build tree
        CourseAnalyzer courseAnalyzer = new CourseAnalyzer(currentGrades, currentGrades[0]);
        Course selectedCourse = courseAnalyzer.getCourseByName(courseName);
        if (selectedCourse == null) {
            return null;
        }
        int courseId = selectedCourse.getCourseID();

        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(currentGrades, studentInfo);
        CurrentStudent student = currentStudentAnalyzer.getCurrentStudentByID(studentId);
        if (student == null) {
            return null; // Handle invalid student ID
        }

        Record studentRecord = new Record(
                student.getStudentID(),
                0, // Placeholder for grade (as it's NG)
                student.getNSI(),
                student.getPCQ(),
                student.getCAR(),
                student.getTSI(),
                student.getARC()
        );

        ArrayList<Record> records = loadTrainingData(currentGrades, studentInfo, courseId);
        DecisionTree decisionTree = new DecisionTree();
        TreeNode root = decisionTree.buildTree(records, 6); // Depth adjustable

        // Display student attributes
        Label studentInfoLabel = new Label("Student Information:");
        studentInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label studentAttributes = new Label(
                "ID: " + student.getStudentID() + "\n" +
                        "TSI: " + student.getTSI() + "\n" +
                        "NSI: " + student.getNSI() + "\n" +
                        "PCQ: " + student.getPCQ() + "\n" +
                        "CAR: " + student.getCAR() + "\n" +
                        "ARC: " + student.getARC()
        );
        studentAttributes.setStyle("-fx-font-size: 12px;");

        // Wrap student attributes in a VBox
        VBox studentInfoBox = new VBox(studentInfoLabel, studentAttributes);
        studentInfoBox.setSpacing(10); // Add spacing between elements
        studentInfoBox.setPadding(new Insets(10)); // Add padding around the box

        // Generate JavaFX TreeView
        TreeItem<String> rootItem = new TreeItem<>("Decision Tree");
        buildTreeVisualization(root, rootItem, studentRecord);

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(true);

        // Add a ScrollPane for the tree view
        ScrollPane scrollPane = new ScrollPane(treeView);
        scrollPane.setFitToWidth(true); // Fit tree view to the width of the scroll pane

        // Combine student info and tree view into a VBox
        VBox layout = new VBox(studentInfoBox, scrollPane);
        layout.setSpacing(20); // Add spacing between student info and tree view
        layout.setPadding(new Insets(20)); // Add padding around the layout

        // Set the scene size
        return new Scene(layout, 500, 800); // Adjust height as needed
    }


    // Recursive method to build TreeView
    private static void buildTreeVisualization(TreeNode node, TreeItem<String> treeItem, Record student) {
        if (node.isLeaf()) {
            treeItem.getChildren().add(new TreeItem<>("Leaf (Predicted Grade): " + node.getPredictedValue()));
        } else {
            String conditionLeft = "If " + node.getSplitProperty() + " <= " + node.getSplitPoint();
            String conditionRight = "If " + node.getSplitProperty() + " > " + node.getSplitPoint();
            TreeItem<String> leftChild = new TreeItem<>(conditionLeft);
            TreeItem<String> rightChild = new TreeItem<>(conditionRight);

            // Highlight the path for the student
            if (studentMatchesCondition(node.getSplitProperty(), node.getSplitPoint(), student, true)) {
                leftChild.setValue(conditionLeft + " (Path Taken)");
            } else if (studentMatchesCondition(node.getSplitProperty(), node.getSplitPoint(), student, false)) {
                rightChild.setValue(conditionRight + " (Path Taken)");
            }

            treeItem.getChildren().add(leftChild);
            treeItem.getChildren().add(rightChild);

            buildTreeVisualization(node.getLeft(), leftChild, student);
            buildTreeVisualization(node.getRight(), rightChild, student);
        }
    }

    private static boolean studentMatchesCondition(String property, Object splitPoint, Record student, boolean isLeft) {
        try {
            switch (property) {
                case "NSI":
                case "TSI":
                    // Handle String properties
                    String stringValue = splitPoint.toString(); // Ensure splitPoint is treated as String
                    if (isLeft) {
                        return student.getNSI().equals(stringValue) || student.getTSI().equals(stringValue);
                    } else {
                        return !student.getNSI().equals(stringValue) && !student.getTSI().equals(stringValue);
                    }
                case "PCQ":
                case "CAR":
                case "ARC":
                    // Handle numerical properties
                    double numericValue = Double.parseDouble(splitPoint.toString()); // Convert splitPoint to double
                    if (isLeft) {
                        if (property.equals("PCQ")) {
                            return student.getPCQ() <= numericValue;
                        } else if (property.equals("CAR")) {
                            return student.getCAR() <= numericValue;
                        } else if (property.equals("ARC")) {
                            return student.getARC() <= numericValue;
                        }
                    } else {
                        if (property.equals("PCQ")) {
                            return student.getPCQ() > numericValue;
                        } else if (property.equals("CAR")) {
                            return student.getCAR() > numericValue;
                        } else if (property.equals("ARC")) {
                            return student.getARC() > numericValue;
                        }
                    }
                default:
                    return false;
            }
        } catch (Exception e) {
            System.err.println("Error in studentMatchesCondition: " + e.getMessage());
            return false;
        }
    }


}
