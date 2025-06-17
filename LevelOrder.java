import java.util.*;
public class LevelOrder {
    void levelOrder(TreeNode root) {
        if(root == null) return;
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        while(!q.isEmpty()) {
            TreeNode temp = q.poll();
            System.out.print(temp.data + " ");
            if(temp.left != null) q.add(temp.left);
            if(temp.right != null) q.add(temp.right);
        }
    }

    public static void main(String[] args) {
        LevelOrder t = new LevelOrder();
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        t.levelOrder(root);
    }
}
