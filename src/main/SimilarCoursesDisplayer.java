package src.main;

public class SimilarCoursesDisplayer {

    public static void main(String[] args) {
        String csvFilePath = "src/main/GraduateGrades.csv";
        String[][] csvData = TwoDimensionalArray.readCsvInto2DArray(csvFilePath);
        int numOfStudents = csvData.length;
        int numOfCourses = csvData[0].length;

        double[][] grades = new double[numOfStudents][numOfCourses]; 

        for (int i = 1; i < numOfStudents; ++i){
            for (int j = 1; j < numOfCourses; ++j){
                grades[i][j] = Double.parseDouble(csvData[i][j]); // Array with grades only and changed to double type.
            }
        }


        int similarCoursesCount = 0;
        double similarityHolder = 0;
        String [] courseNameHolder = new String[2];
        if (csvData != null) {
            for (int i = 1; i < csvData[0].length; ++i){     // Exluding Student ID (i = 1)
                for (int j = 1; j < csvData[0].length; ++j){ // Exluding First Row (j = 1)
                    double similarity = pearsonCorrelation(courseGrades(grades, i), courseGrades(grades, j)); //Calculate similarity between courses
                    if (similarity == 1){   //Excludes combinations of the same courses.
                        continue;
                    }else if (similarity > 0.7){
                        similarCoursesCount += 1;
                        System.out.println("Similarity between " + String.join(",", csvData[0][i]) + " and " + String.join(",", csvData[0][j])+": " + similarity);
                    }
                    if (similarity > similarityHolder){
                        similarityHolder = similarity;
                        courseNameHolder[0] = csvData[0][i];
                        courseNameHolder[1] = csvData[0][j];
                    }
                }
            }
            System.out.println("Number of similar courses: " + similarCoursesCount);
            System.out.println("The most similar courses based on Pearson correlation are: " + courseNameHolder[0] + " and " + courseNameHolder[1] + " with correlation:" + similarityHolder);
            
        }


    }
    public static double pearsonCorrelation(double[] x, double[] y) {

        //Pearson Correlation calculate correlation between two courses.
        //Output is given between 1 and 0. A score closer to 0 means there is a little or no similarity.

        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < n; i++) {

            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        return numerator / denominator;
    }
    
    public static double[] courseGrades(double[][] grades, int courseIndex){

        //Returns all grades for each course.

        double[] courseGrades = new double[grades.length]; //Create array for grades
        for (int i = 0; i < grades.length; ++i){ 
            courseGrades[i] = grades[i][courseIndex]; //Go through every grade and add it to array
        }
        return courseGrades;
    }



}
