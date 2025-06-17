class TreeNode {
    int data;
    TreeNode left, right;
    TreeNode(int d) { data = d; }
}

public class CheckBST {
    boolean isBST(TreeNode root, int min, int max) {
        if(root == null) return true;
        if(root.data <= min || root.data >= max) return false;
        return isBST(root.left, min, root.data) && isBST(root.right, root.data, max);
    }

    public static void main(String[] args) {
        CheckBST c = new CheckBST();
        TreeNode root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.right = new TreeNode(15);
        System.out.println(c.isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE));
    }
}
