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
    public PredictionAnalyzer() {
        TwoDimensionalArray fileLoader = new TwoDimensionalArray();
        CurrentStudentAnalyzer currentStudentAnalyzer = new CurrentStudentAnalyzer(fileLoader.readCsvInto2DArray("src/csvFiles/CurrentGrades.csv"),fileLoader.readCsvInto2DArray("src/csvFiles/StudentInfo.csv"));
        ArrayList<CurrentStudent> currentStudents = new ArrayList<>();
        currentStudents = currentStudentAnalyzer.getAllCurrentStudents();
        this.predictedGrades = fileLoader.readCsvInto2DArray("src/csvFiles/PredictedGradesDecisionStump.csv");
        this.infoData = fileLoader.readCsvInto2DArray("src/csvFiles/StudentInfo.csv");


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
            CurrentStudent pStudent = new CurrentStudent(Integer.parseInt(predictedGrades[i][0]), grades, traits);
            predictedStudents.add(pStudent);
        }
        for (int i = 0; i < predictedStudents.size(); i++) {
            predictedStudents.get(i).setYearOfStudy(currentStudents.get(i).getYearOfStudy());
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


    public void predictNumberOfGraduating(){
        int counter = 0;
        for (CurrentStudent student : predictedStudents) {
            if (student.getYearOfStudy() == 3) {
                if (student.hasFailedCourse(student.getGrades())){
                    counter++;
                }
            }
        }
        System.out.println("Around " + counter + " students are going to graduate this year!");
    }


    public void testGPA(int p){
        double avg = 0;
        int count = 0;
        double sum = 0;
        for (int i = 1; i < predictedGrades.length; i++) {
            sum += Double.parseDouble(predictedGrades[i][p]);
            count++;
        }
        avg = sum / count;
        System.out.println("Average grade is " + avg);
    }


}
