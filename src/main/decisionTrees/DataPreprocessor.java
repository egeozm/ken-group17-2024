package src.main.decisionTrees;

import java.util.HashMap;

public class DataPreprocessor {
    static final HashMap<String, Integer> neuroSynapticMap = new HashMap<>();
    static final HashMap<String, Integer> chronoAdaptationMap = new HashMap<>();
    static final HashMap<String, Integer> telepathicSyncMap = new HashMap<>();

    static {
        // Mapping categorical features to numerical values
        neuroSynapticMap.put("low", 0);
        neuroSynapticMap.put("medium", 1);
        neuroSynapticMap.put("high", 2);
        neuroSynapticMap.put("full", 3);
        neuroSynapticMap.put("nothing", 4);

        chronoAdaptationMap.put("1 tau", 0);
        chronoAdaptationMap.put("2 tau", 1);
        chronoAdaptationMap.put("3 tau", 2);

        telepathicSyncMap.put("A", 0);
        telepathicSyncMap.put("B", 1);
        telepathicSyncMap.put("C", 2);
        telepathicSyncMap.put("D", 3);
        telepathicSyncMap.put("E", 4);
        telepathicSyncMap.put("F", 5);
    }

    public static double[][] preprocessFeatures(String[][] rawFeatures) {
        double[][] processedFeatures = new double[rawFeatures.length][3];

        for (int i = 0; i < rawFeatures.length; i++) {
            processedFeatures[i][0] = neuroSynapticMap.get(rawFeatures[i][0]);
            processedFeatures[i][1] = chronoAdaptationMap.get(rawFeatures[i][1]);
            processedFeatures[i][2] = telepathicSyncMap.get(rawFeatures[i][2]);
        }

        return processedFeatures;
    }

    public static double[] preprocessTarget(String[] rawTarget) {
        double[] target = new double[rawTarget.length];
        for (int i = 0; i < rawTarget.length; i++) {
            target[i] = Double.parseDouble(rawTarget[i]);
        }
        return target;
    }
}
