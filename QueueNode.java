class QueueNode {
    int data;
    QueueNode next;
    QueueNode(int d) { data = d; }
}

public class QueueLL {
    QueueNode front, rear;

    void enqueue(int val) {
        QueueNode n = new QueueNode(val);
        if(rear == null)
            front = rear = n;
        else {
            rear.next = n;
            rear = n;
        }
    }

    int dequeue() {
        if(front == null) return -1;
        int val = front.data;
        front = front.next;
        if(front == null) rear = null;
        return val;
    }

    public static void main(String[] args) {
        QueueLL q = new QueueLL();
        q.enqueue(10);
        q.enqueue(30);
        System.out.println(q.dequeue());
    }
}
