public class PostorderTree {
    void postorder(TreeNode root) {
        if(root != null) {
            postorder(root.left);
            postorder(root.right);
            System.out.print(root.data + " ");
        }
    }

    public static void main(String[] args) {
        PostorderTree t = new PostorderTree();
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(3);
        root.right = new TreeNode(8);
        t.postorder(root);
    }
}
