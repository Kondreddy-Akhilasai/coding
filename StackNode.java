class StackNode {
    int data;
    StackNode next;
    StackNode(int d) { data = d; }
}

public class StackLL {
    StackNode top;

    void push(int val) {
        StackNode n = new StackNode(val);
        n.next = top;
        top = n;
    }

    int pop() {
        if(top == null) return -1;
        int val = top.data;
        top = top.next;
        return val;
    }

    public static void main(String[] args) {
        StackLL s = new StackLL();
        s.push(10);
        s.push(20);
        System.out.println(s.pop());
    }
}
