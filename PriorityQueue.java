import java.util.LinkedList;


public class PriorityQueue<T extends Comparable<T>> {
    private LinkedList<T> elements;

    public PriorityQueue() {
        elements = new LinkedList<>();
    }


    public void add(T item) {
        int i = 0;

        while (i < elements.size() && item.compareTo(elements.get(i)) > 0) {
            i++;
        }
        elements.add(i, item);
    }


    public T poll() {
        if (isEmpty()) {
            return null;
        }
        return elements.removeFirst();
    }


    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return elements.getFirst();
    }


    public boolean isEmpty() {
        return elements.isEmpty();
    }


    public int size() {
        return elements.size();
    }


    public void clear() {
        elements.clear();
    }
}