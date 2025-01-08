package src.main.decisionTrees;

public class Split {
    int featureIndex;
    double threshold;
    double mse;

    public Split(int featureIndex, double threshold, double mse) {
        this.featureIndex = featureIndex;
        this.threshold = threshold;
        this.mse = mse;
    }
}
