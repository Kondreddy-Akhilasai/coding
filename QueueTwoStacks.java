import java.util.Stack;
public class QueueTwoStacks {
    Stack<Integer> s1 = new Stack<>();
    Stack<Integer> s2 = new Stack<>();

    void enqueue(int x) {
        s1.push(x);
    }

    int dequeue() {
        if(s2.isEmpty())
            while(!s1.isEmpty())
                s2.push(s1.pop());
        return s2.isEmpty() ? -1 : s2.pop();
    }

    public static void main(String[] args) {
        QueueTwoStacks q = new QueueTwoStacks();
        q.enqueue(10);
        q.enqueue(20);
        System.out.println(q.dequeue());
    }
}
