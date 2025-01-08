package src.main.decisionTrees;

import src.main.dataHandle.TwoDimensionalArray;

public class Main {
    public static void main(String[] args) {
        String csvFile = "src/csvFiles/StudentInfo.csv";
        String[][] rawData = TwoDimensionalArray.readCsvInto2DArray(csvFile);


        double[][] features = DataLoader.loadFeaturesFromCsv(rawData);
        double[] targets = DataLoader.loadTargetFromCsv(rawData);

        DecisionTree tree = new DecisionTree(3);
        tree.fit(features, targets);

        double[] newSample = {2, 1, 4};
        System.out.println("Predicted Plasma Conductivity: " + tree.predict(newSample));
    }
}
