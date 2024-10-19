package src.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwoDimensionalArray {

    public static String[][] readCsvInto2DArray(String csvFile) {

        List<String[]> recordList = new ArrayList<>();
        String delimiter = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String currentLine;


            // Read lines and split them into arrays
            while ((currentLine = br.readLine()) != null) {
                String[] data = currentLine.split(delimiter); // Determine max number of fields
                recordList.add(data);

            }

            int recordCount = recordList.size();
            int maxFields = recordList.get(0).length;
            String[][] arrayToReturn = new String[recordCount][maxFields];

            // Populate the 2D array
            for (int i = 0; i < recordCount; i++) {
                String[] data = recordList.get(i);
                for (int j = 0; j < data.length; j++) {
                    arrayToReturn[i][j] = data[j];
                }
            }

            return arrayToReturn;

        } catch (IOException e) {
            System.out.println("Error reading the CSV file: " + e.getMessage());
            return null;

        }
    }
}