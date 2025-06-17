public class QueueArray {
    int[] q = new int[5];
    int front = 0, rear = -1;

    void enqueue(int val) {
        if(rear < 4)
            q[++rear] = val;
    }

    int dequeue() {
        if(front <= rear)
            return q[front++];
        return -1;
    }

    public static void main(String[] args) {
        QueueArray q = new QueueArray();
        q.enqueue(10);
        q.enqueue(20);
        System.out.println(q.dequeue());
    }
}
