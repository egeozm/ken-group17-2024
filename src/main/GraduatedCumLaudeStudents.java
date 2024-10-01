package src.main;

public class GraduatedCumLaudeStudents {

    public static void main(String[] args) {
        String csvFilePath = "src/main/GraduateGrades.csv";
        String[][] csvData = TwoDimensionalArray.readCsvInto2DArray(csvFilePath);

        if (csvData != null) {
            GrauatedStudentRecord[] studentMeanData = new GrauatedStudentRecord[csvData.length - 1]; // Exclude header row

            for (int i = 1; i < csvData.length; i++) {
                int studentID = Integer.parseInt(csvData[i][0]);
                double meanGrade = calculateMean(csvData[i]);

                studentMeanData[i - 1] = new GrauatedStudentRecord(studentID, meanGrade);

            }

            int cumLaudeNumber = 0;
            int totalNumberStudent = csvData.length - 1;
            for (GrauatedStudentRecord record : studentMeanData) {
                if (record.getGPA() >= 8.25) {
                    cumLaudeNumber++;
                    System.out.printf("Student ID: %d, GPA: %.2f%n", record.getStudentID(), record.getGPA());
                }
            }
            System.out.println("Number of students that cum-laude: " + cumLaudeNumber);
            System.out.println("Total number of students: " + totalNumberStudent);
            System.out.printf("Percentage of a student cum-laude: %.2f%%%n", ((double) cumLaudeNumber / totalNumberStudent) * 100);

        }
    }

    public static double calculateMean(String[] studentData) {
        double sum = 0;
        int count = 0;

        for (int i = 1; i < studentData.length; i++) {
            try {
                sum += Double.parseDouble(studentData[i]);
                count++;
            } catch (NumberFormatException e) {
                System.out.println("Error passing grade: " + studentData[i]);
            }
        }

        return (count > 0) ? (sum / count) : 0.0;
    }
}
