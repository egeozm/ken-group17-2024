package src.main.predictGrades;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVComparator {

    public static void main(String[] args) {
        String file1 = "src/csvFiles/PredictedGradesDecisionStump.csv";
        String file2 = "src/csvFiles/PredictedGradesDecisionStump2.csv";

        try {
            List<String[]> differences = compareCSVFiles(file1, file2);

            if (differences.isEmpty()) {
                System.out.println("No differences found between the two CSV files.");
            } else {
                System.out.println("Rows with differences:");
                for (String[] row : differences) {
                    System.out.println(String.join(",", row));
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while processing the files: " + e.getMessage());
        }
    }

    public static List<String[]> compareCSVFiles(String file1Path, String file2Path) throws IOException {
        List<String[]> differences = new ArrayList<>();

        try (BufferedReader br1 = new BufferedReader(new FileReader(file1Path));
             BufferedReader br2 = new BufferedReader(new FileReader(file2Path))) {

            String line1;
            String line2;
            int rowNumber = 0;

            while ((line1 = br1.readLine()) != null & (line2 = br2.readLine()) != null) {
                rowNumber++;

                // Split the lines into arrays of values.
                String[] values1 = line1.split(",");
                String[] values2 = line2.split(",");

                // Check if the rows have different lengths.
                if (values1.length != values2.length) {
                    differences.add(new String[]{"Row " + rowNumber, "Structure Mismatch"});
                    continue;
                }

                // Compare the values in each row.
                boolean rowHasDifference = false;
                for (int i = 0; i < values1.length; i++) {
                    if (!values1[i].equals(values2[i])) {
                        rowHasDifference = true;
                        break;
                    }
                }

                if (rowHasDifference) {
                    differences.add(new String[]{"Row " + rowNumber, "\n" , line1, "\n", line2});
                }
            }

            // Check for extra rows in either file.
            while ((line1 = br1.readLine()) != null) {
                rowNumber++;
                differences.add(new String[]{"Row " + rowNumber, line1, "[No corresponding row in file2]"});
            }

            while ((line2 = br2.readLine()) != null) {
                rowNumber++;
                differences.add(new String[]{"Row " + rowNumber, "[No corresponding row in file1]", line2});
            }
        }

        return differences;
    }
}
