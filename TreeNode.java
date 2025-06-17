class TreeNode {
    int data;
    TreeNode left, right;
    TreeNode(int d) { data = d; }
}

public class InorderTree {
    void inorder(TreeNode root) {
        if(root != null) {
            inorder(root.left);
            System.out.print(root.data + " ");
            inorder(root.right);
        }
    }

    public static void main(String[] args) {
        InorderTree t = new InorderTree();
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        t.inorder(root);
    }
}
