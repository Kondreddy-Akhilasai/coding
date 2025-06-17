import java.util.Stack;
public class BalancedParens {
    public static void main(String[] args) {
        String expr = "(()())";
        Stack<Character> stack = new Stack<>();
        for(char ch : expr.toCharArray()) {
            if(ch == '(')
                stack.push(ch);
            else if(ch == ')') {
                if(stack.isEmpty()) {
                    System.out.println("Unbalanced");
                    return;
                }
                stack.pop();
            }
        }
        System.out.println(stack.isEmpty() ? "Balanced" : "Unbalanced");
    }
}
