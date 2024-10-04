package src.main;

import java.util.Arrays;

public class CourseDifficultyIndicator {

    public static void main(String[] args) {
        String csvFilePath = "src/main/GraduateGrades.csv";
        String[][] data = TwoDimensionalArray.readCsvInto2DArray(csvFilePath);

        System.out.println(Arrays.deepToString(sortCoursesByDifficulty(data)));
    }


    public static String[][] coursesAverageGrades (String[][] dataSet){
        String result[][] = new String[2][dataSet[0].length - 1];
        double sum = 0;
        int count = 0;
        for (int i = 1; i < dataSet [1].length; i++){
            for (int j = 1; j < dataSet.length; j++){
                sum = sum + Double.parseDouble(dataSet[j][i]);
                count ++;
            }
            result[0][i-1] = dataSet[0][i];
            result[1][i-1] = Double.toString(Math.round(sum/count * 1000.0) / 1000.0);
            sum = 0;
            count = 0;
        }
        return result;
    }



    public static String[][] sortCoursesByDifficulty (String[][] dataSet){
        
        String[][] gradesArray = coursesAverageGrades(dataSet);
        int n = gradesArray[0].length;
        String tempGrade;
        String tempCourse;

        for (int i = 0; i < n-1; i++){
            for(int j = 0; j < n-1; j++){
                if(Double.parseDouble(gradesArray[1][j]) > Double.parseDouble(gradesArray[1][j+1])){
                    tempGrade = gradesArray[1][j];
                    gradesArray[1][j] = gradesArray[1][j+1];
                    gradesArray[1][j+1] = tempGrade;
                    tempCourse = gradesArray[0][j];
                    gradesArray[0][j] = gradesArray[0][j+1];
                    gradesArray[0][j+1] = tempCourse;
                }
            }
        }

        System.out.println("The hardest course is: " + gradesArray[0][0] + "\nand the easiest course is: " + gradesArray[0][n-1]);
        return gradesArray;
    }
}
