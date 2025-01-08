package src.main.decisionTrees;


import java.util.Arrays;

public class DataLoader {
    public static double[][] loadFeaturesFromCsv(String[][] rawData) {
        int rowCount = rawData.length - 1; // Exclude header row
        double[][] features = new double[rowCount][3]; // 3 feature columns

        for (int i = 1; i < rawData.length; i++) { // Start from 1 to skip the header
            features[i - 1][0] = DataPreprocessor.neuroSynapticMap.get(rawData[i][1]); // Neuro-Synaptic Interface Level
            features[i - 1][1] = DataPreprocessor.chronoAdaptationMap.get(rawData[i][3]); // Chrono-Adaptation Rate
            features[i - 1][2] = DataPreprocessor.telepathicSyncMap.get(rawData[i][4]); // Telepathic Synchronisation Index
        }

        return features;
    }

    public static double[] loadTargetFromCsv(String[][] rawData) {
        int rowCount = rawData.length - 1; // Exclude header row
        double[] targets = new double[rowCount];

        for (int i = 1; i < rawData.length; i++) { // Start from 1 to skip the header
            targets[i - 1] = Double.parseDouble(rawData[i][2]); // Plasma Conductivity Quotient
        }

        return targets;
    }
}

