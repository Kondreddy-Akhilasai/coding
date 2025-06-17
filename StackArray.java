public class StackArray {
    int[] stack = new int[5];
    int top = -1;

    void push(int val) {
        if(top < 4)
            stack[++top] = val;
    }

    int pop() {
        if(top >= 0)
            return stack[top--];
        return -1;
    }

    public static void main(String[] args) {
        StackArray s = new StackArray();
        s.push(10);
        s.push(20);
        System.out.println(s.pop());
    }
}
