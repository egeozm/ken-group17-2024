package src.main.predictGrades;

import java.io.*;
import java.util.ArrayList;

public class CSVWriter {
    private static final String FILE_PATH = "src/csvFiles/PredictedGrades.csv";
    private static final String HEADERS = "Arkonian Warfare Tactics,ExoGenetics Evolution,Transdimensional Navigation,Warp Field Theory,Dark Matter Biophysics,Zarnithian Philosophy,Drakthon Linguistics,Xynthium Material Sciences,Hyperspace Topology,Helio-Bio Interface,Luminarian Art Theory,Stellar Cartography,Chrono-Kinetics,Technotronic Linguistic Fusion,Cybernetic Ethics,Nebulon Astrophysics,Quasar Dynamics,Glacial Holo-Architecture,Aether Resonance,Gravix Planetary Studies,Vortex Quantum Mechanics,Holo-Temporal Engineering,Psi-Energy Manipulation,Plasmawave Analysis,Neutronia Metallurgy,Syntho-Chemical Engineering,Sublight Propulsion Systems,Krythos Biomechanics,Flux Capacitor Management,Xyloprax Computation,Yridium Power Systems,Quantum Neuro-Hacking,Zyglon Neurology";

    public void writeToCSV(int rowNumber, ArrayList<Double> data) {
        try {
            File file = new File(FILE_PATH);
            // Initialize storage for the new content
            ArrayList<String> fileContent = new ArrayList<>();

            // Read existing lines if the file exists
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContent.add(line);
                    }
                }
            }

            // Add the headers as the first row if the file is empty
            if (fileContent.isEmpty()) {
                fileContent.add(HEADERS);
            }

            // Ensure the file has enough lines to accommodate the rowNumber
            // Adjust for skipping the first row
            while (fileContent.size() <= rowNumber + 1) { // +1 to account for skipping the first row
                fileContent.add("");
            }

            // Prepare the row to update
            StringBuilder row = new StringBuilder();
            for (Double value : data) {
                if (row.length() > 0) {
                    row.append(",");
                }
                row.append(value != null ? value.toString() : "");
            }

            // Update the specified row, skipping the first row
            fileContent.set(rowNumber + 1, row.toString()); // +1 to skip the first row

            // Write back to the file
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                for (String line : fileContent) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Data written successfully to row " + (rowNumber + 1));

        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }
    public  void writeArrayToCSV(String[][] data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/csvFiles/PredictedGradesDecisionStump.csv"))) {
            for (String[] row : data) {
                String line = String.join(",", row); // Join elements in the row with commas
                writer.write(line);
                writer.newLine(); // Move to the next line
            }
            System.out.println("CSV file created successfully: " + "csvFiles/PredictedGradesDecisionStump.csv");
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }


}
