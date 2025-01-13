package src.main.predictGrades;

public class TreeNode {
    private String splitProperty;   // Feature used for splitting
    private Object splitPoint;       // Threshold or category for the split
    private double predictedValue;   // Predicted value for leaf nodes (Mean of the grades in the leaf)
    private TreeNode left;           // Left child
    private TreeNode right;          // Right child
    private boolean isLeaf;          // Is this a leaf node?

    // Constructor for internal nodes
    public TreeNode(String splitAttribute, Object splitValue) {
        this.splitProperty = splitAttribute;
        this.splitPoint = splitValue;
        this.isLeaf = false;
    }

    // Constructor for leaf nodes
    public TreeNode(double predictedValue) {
        this.predictedValue = predictedValue;
        this.isLeaf = true;
    }

    // Getters and setters
    public String getSplitProperty() { return splitProperty; }
    public Object getSplitPoint() { return splitPoint; }
    public double getPredictedValue() { return predictedValue; }
    public TreeNode getLeft() { return left; }
    public TreeNode getRight() { return right; }
    public boolean isLeaf() { return isLeaf; }

    public void setLeft(TreeNode left) { this.left = left; }
    public void setRight(TreeNode right) { this.right = right; }
}

