public class CircularQueue {
    private Object[] elements;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        elements = new Object[capacity];
        front = 0;
        rear = 0;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public void enqueue(Object data) {
        if (isFull()) {
            System.out.println("Queue overflow");
            return;
        }
        elements[rear] = data;
        rear = (rear + 1) % capacity;
        size++;
    }

    public Object dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }
        Object removed = elements[front];
        elements[front] = null;
        front = (front + 1) % capacity;
        size--;
        return removed;
    }

    public Object peek() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }
        return elements[front];
    }

    public int size() {
        return size;
    }

    public void printQueue() {
        System.out.print("Queue: ");
        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;
            System.out.print(elements[index] + " ");
        }
        System.out.println();
    }
}
