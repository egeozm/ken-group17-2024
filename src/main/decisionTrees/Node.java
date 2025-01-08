package src.main.decisionTrees;

public class Node {
    boolean isLeaf;
    double prediction;
    int splitFeature;
    double splitValue;
    Node left;
    Node right;

    // Constructor for leaf node
    public Node(boolean isLeaf, double prediction) {
        this.isLeaf = isLeaf;
        this.prediction = prediction;
    }

    // Constructor for decision node
    public Node(int splitFeature, double splitValue) {
        this.isLeaf = false;
        this.splitFeature = splitFeature;
        this.splitValue = splitValue;
    }
}
