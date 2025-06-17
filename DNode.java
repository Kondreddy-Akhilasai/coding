class DNode {
    int data;
    DNode prev, next;
    DNode(int d) { data = d; }
}

public class DoublyLinkedList {
    DNode head;

    void insert(int val) {
        DNode n = new DNode(val);
        if(head == null)
            head = n;
        else {
            DNode temp = head;
            while(temp.next != null)
                temp = temp.next;
            temp.next = n;
            n.prev = temp;
        }
    }

    void display() {
        DNode temp = head;
        while(temp != null) {
            System.out.print(temp.data + " ");
            temp = temp.next;
        }
    }

    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();
        list.insert(5);
        list.insert(15);
        list.display();
    }
}
