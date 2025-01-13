package src.main.decisionTrees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecisionTrees {
    private Node root;
    private int maxDepth;

    public DecisionTrees(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    // Train the tree
    public void fit(double[][] features, double[] targets) {
        this.root = buildTree(features, targets, 0);
    }

    // Predict for a single instance
    public double predict(double[] instance) {
        Node node = root;
        while (!node.isLeaf) {
            if (instance[node.splitFeature] <= node.splitValue) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.prediction;
    }

    // Build the tree recursively
    private Node buildTree(double[][] features, double[] targets, int depth) {
        if (depth >= maxDepth || isPure(targets)) {
            return new Node(true, calculateMean(targets));
        }

        Split bestSplit = findBestSplit(features, targets);

        if (bestSplit == null) {
            return new Node(true, calculateMean(targets));
        }

        Partition partition = partitionData(features, targets, bestSplit.featureIndex, bestSplit.threshold);

        Node node = new Node(bestSplit.featureIndex, bestSplit.threshold);
        node.left = buildTree(partition.leftFeatures, partition.leftTargets, depth + 1);
        node.right = buildTree(partition.rightFeatures, partition.rightTargets, depth + 1);

        return node;
    }

    // Check if all targets are the same
    private boolean isPure(double[] targets) {
        for (int i = 1; i < targets.length; i++) {
            if (targets[i] != targets[0]) {
                return false;
            }
        }
        return true;
    }

    // Calculate the mean of targets
    private double calculateMean(double[] targets) {
        double sum = 0;
        for (double target : targets) {
            sum += target;
        }
        return sum / targets.length;
    }

    // Find the best split for the dataset
    public static Split findBestSplit(double[][] features, double[] targets) {
        Split bestSplit = null;
        double bestMSE = Double.MAX_VALUE;

        for (int featureIndex = 0; featureIndex < features[0].length; featureIndex++) {
            // Extract and sort unique feature values
            int finalFeatureIndex = featureIndex;
            double[] uniqueValues = Arrays.stream(features)
                    .mapToDouble(row -> row[finalFeatureIndex])
                    .distinct()
                    .sorted()
                    .toArray();

            // Calculate midpoints between unique values
            for (int i = 1; i < uniqueValues.length; i++) {
                double threshold = (uniqueValues[i - 1] + uniqueValues[i]) / 2;

                // Partition data based on the threshold
                Partition partition = partitionData(features, targets, featureIndex, threshold);

                // Calculate Weighted MSE for the split
                double weightedMSE = calculateWeightedMSE(partition);

                // Update best split if the current one is better
                if (weightedMSE < bestMSE) {
                    bestMSE = weightedMSE;
                    bestSplit = new Split(featureIndex, threshold, bestMSE);
                }
            }
        }
        return bestSplit;
    }

    // Partition data into left and right subsets based on a threshold
    private static Partition partitionData(double[][] features, double[] targets, int featureIndex, double threshold) {
        List<double[]> leftFeaturesList = new ArrayList<>();
        List<Double> leftTargetsList = new ArrayList<>();
        List<double[]> rightFeaturesList = new ArrayList<>();
        List<Double> rightTargetsList = new ArrayList<>();

        for (int i = 0; i < features.length; i++) {
            if (features[i][featureIndex] <= threshold) {
                leftFeaturesList.add(features[i]);
                leftTargetsList.add(targets[i]);
            } else {
                rightFeaturesList.add(features[i]);
                rightTargetsList.add(targets[i]);
            }
        }

        return new Partition(
                leftFeaturesList.toArray(new double[0][0]),
                leftTargetsList.stream().mapToDouble(Double::doubleValue).toArray(),
                rightFeaturesList.toArray(new double[0][0]),
                rightTargetsList.stream().mapToDouble(Double::doubleValue).toArray()
        );
    }

    // Calculate Weighted MSE for a partition
    private static double calculateWeightedMSE(Partition partition) {
        double leftMSE = calculateMSE(partition.leftTargets);
        double rightMSE = calculateMSE(partition.rightTargets);

        int totalSize = partition.leftTargets.length + partition.rightTargets.length;
        return (partition.leftTargets.length / (double) totalSize) * leftMSE
                + (partition.rightTargets.length / (double) totalSize) * rightMSE;
    }

    // Calculate MSE for a subset of targets
    private static double calculateMSE(double[] targets) {
        double mean = Arrays.stream(targets).average().orElse(0);
        double mse = 0;

        for (double target : targets) {
            mse += Math.pow(target - mean, 2);
        }

        return mse / targets.length;
    }
}
