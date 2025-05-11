public class SingleLinkedList {
    Node head;

    public void addNode(Object data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.getLink() != null) {
                temp = temp.getLink();
            }
            temp.setLink(newNode);
        }
    }

    public int size() {
        int count = 0;
        Node temp = head;
        while (temp != null) {
            count++;
            temp = temp.getLink();
        }
        return count;
    }

    public void display() {
        if (head == null) {
            System.out.println("Linked list is empty");
        } else {
            Node temp = head;
            while (temp != null) {
                System.out.println(temp.getData() + " ");
                temp = temp.getLink();
            }
        }
    }

    public int findVariance() {
        int sum = 0;
        int count = 0;
        Node temp = head;
        while (temp != null) {
            count++;
            sum += Integer.parseInt(temp.getData().toString());
            temp = temp.getLink();
        }
        if (count == 0) return 0;
        double average = (double) sum / count;
        temp = head;
        double sum2 = 0;
        while (temp != null) {
            sum2 += Math.pow(Integer.parseInt(temp.getData().toString()) - average, 2);
            temp = temp.getLink();
        }
        return (int) (sum2 / count);
    }

    public void printAddresses() {
        Node temp = head;
        while (temp != null) {
            System.out.println(temp);
            temp = temp.getLink();
        }
    }
}
