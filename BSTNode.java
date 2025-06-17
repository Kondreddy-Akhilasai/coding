class BSTNode {
    int data;
    BSTNode left, right;
    BSTNode(int d) { data = d; }
}

public class BST {
    BSTNode root;

    BSTNode insert(BSTNode node, int val) {
        if(node == null) return new BSTNode(val);
        if(val < node.data) node.left = insert(node.left, val);
        else node.right = insert(node.right, val);
        return node;
    }

    void inorder(BSTNode node) {
        if(node != null) {
            inorder(node.left);
            System.out.print(node.data + " ");
            inorder(node.right);
        }
    }

    public static void main(String[] args) {
        BST t = new BST();
        t.root = t.insert(t.root, 50);
        t.insert(t.root, 30);
        t.insert(t.root, 70);
        t.inorder(t.root);
    }
}
