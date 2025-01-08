package src.main.decisionTrees;

public class Partition {
    double[][] leftFeatures;
    double[] leftTargets;
    double[][] rightFeatures;
    double[] rightTargets;

    public Partition(double[][] leftFeatures, double[] leftTargets, double[][] rightFeatures, double[] rightTargets) {
        this.leftFeatures = leftFeatures;
        this.leftTargets = leftTargets;
        this.rightFeatures = rightFeatures;
        this.rightTargets = rightTargets;
    }
}
