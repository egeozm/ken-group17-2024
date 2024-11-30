package src.main.predictGrades;

import src.main.dataHandle.TwoDimensionalArray;

import java.util.ArrayList;

public class PredictionAnalyzer {
//    private String [][] predictedGradesData;
//    private String [][] infoData;
//    ArrayList<CurrentStudent> predictedStudents = new ArrayList<>();
//    public PredictionAnalyzer(String [][] predictedGradesData, String [][] infoData, String[][] gradesData) {
//        this.predictedGradesData = predictedGradesData;
//        this.infoData = infoData;
//        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(gradesData, infoData);
//
//        for (int i = 1; i < predictedGradesData.length; i++) {
//            ArrayList<Double> grades = new ArrayList<>();
//            for (int j = 0; j < predictedGradesData[i].length; j++) {
//                if (predictedGradesData[i][j].contentEquals("NG")) {
//                    grades.add(null);
//                }else {
//                    grades.add(Double.parseDouble(predictedGradesData[i][j]));
//                }
//            }
//            ArrayList<String> traits = new ArrayList<>();
//            for (int k = 1; k < infoData[i].length; k++) {
//                traits.add(infoData[i][k]);
//            }
//            CurrentStudent cStudent = new CurrentStudent(Integer.parseInt(infoData[i][0]), grades, traits);
//            cStudent.setYearOfStudy(Integer.parseInt(infoData[i][0]));
//            predictedStudents.add(cStudent);
//        }
//    }
    private String [][] predictedGrades;
    private String [][] infoData;
    ArrayList<CurrentStudent> predictedStudents = new ArrayList<>();
    public PredictionAnalyzer(String [][] predictedGrades, String [][] infoData) {
        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(fileLoader.readCsvInto2DArray("src/csvFiles/CurrentGrades.csv"),fileLoader.readCsvInto2DArray("src/csvFiles/StudentInfo.csv"));
        this.predictedGrades = predictedGrades;
        this.infoData = infoData;


        // Extracting grades from the CurrentStudent.csv file
        for (int i = 1; i < predictedGrades.length; i++) {
            ArrayList<Double> grades = new ArrayList<>();
            for (int j = 1; j < predictedGrades[i].length; j++) {
                if (predictedGrades[i][j].contentEquals("NG")) {
                    grades.add(null);
                }else {
                    grades.add(Double.parseDouble(predictedGrades[i][j]));
                }
            }
            ArrayList<String> traits = new ArrayList<>();
            for (int k = 1; k < infoData[i].length; k++) {
                traits.add(infoData[i][k]);
            }
            CurrentStudent cStudent = new CurrentStudent(Integer.parseInt(predictedGrades[i][0]), grades, traits);
            cStudent.setYearOfStudy(currentStudentAnalyzer.findYearOfStudy(grades));
            predictedStudents.add(cStudent);
        }
    }

    public ArrayList<Double> predictPassRate (){
        ArrayList<Double> passRates = new ArrayList<>();
        for (int i = 0; i < predictedStudents.get(i).getGrades().size(); i++) {
            double passCounter = 0;
            for (int j = 0; j < predictedStudents.size(); j++) {
                if (predictedStudents.get(j).getGrades().get(i) >= 6) {
                    passCounter++;
                }
            }
            double passRate = passCounter / predictedStudents.size();
            passRates.add(passRate);
        }
        return passRates;
    }



    public ArrayList<CurrentStudent> getPredictedStudents() {
        return predictedStudents;
    }


}
