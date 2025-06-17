public class LCA {
    TreeNode lca(TreeNode root, int n1, int n2) {
        if(root == null) return null;
        if(n1 < root.data && n2 < root.data) return lca(root.left, n1, n2);
        if(n1 > root.data && n2 > root.data) return lca(root.right, n1, n2);
        return root;
    }

    public static void main(String[] args) {
        LCA obj = new LCA();
        TreeNode root = new TreeNode(20);
        root.left = new TreeNode(10);
        root.right = new TreeNode(30);
        TreeNode res = obj.lca(root, 10, 30);
        System.out.println(res.data);
    }
}
