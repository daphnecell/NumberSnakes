public class Stack {
    private int top;
    private Object [] elements;

    public Stack(int capacity){
        elements = new Object[capacity];
        top = -1;
    }
    boolean isEmpty() {
        return top == -1;
    }

    boolean isFull() {
        return (top+1 == elements.length);
    }

    void push(Object data) {
        if (isFull())
            System.out.println("Stack is overflow");
        else {
            top++;
            elements[top] = data;
        }
    }
    Object pop() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return null;
        }
        else {
            Object retData = elements[top];
            top--;
            return retData;

        }
    }
    Object peek() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return null;
        }
        else {
            return elements[top];
        }

    }

    int size() {
        return top+1;
    }


}
