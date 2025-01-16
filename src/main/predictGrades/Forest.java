package src.main.predictGrades;

import java.util.ArrayList;

public class Forest {
    ArrayList<TreeNode> trees;

    public Forest(ArrayList<TreeNode> trees) {
        this.trees = trees;
    }


    public ArrayList<TreeNode> getTrees() {
        return trees;
    }

}
