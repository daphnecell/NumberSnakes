public class SingleLinkedList {
    Node head;

    public void addNode(Object data) {
        if (head == null) {
            Node newNode = new Node(data);
            head = newNode;
        }
        else{
            Node temp = head;
            while (temp.getLink()!=null){
                temp = temp.getLink();
            }
            Node newNode = new Node(data);
            temp.setLink(newNode);
        }
    }

    public int size(){
        if (head == null){
            return 0;
        }
        else{
            int count = 0;
            Node temp = head;
            while (temp!=null){
                temp=temp.getLink();
                count++;
            }
            return count;
        }
    }

    public void display(){
        if (head == null){
            System.out.println("Linked list is empty");
        }
        else{
            Node temp = head;
            while (temp.getLink()!=null){
                System.out.println(temp.getData() + " ");
                temp=temp.getLink();
            }
        }
    }

    public int findVarience(){
        int sum = 0;
        int count = 0;

        Node temp = head;
        while (temp.getLink()!=null){
            count++;
            sum += Integer.parseInt(temp.getData().toString());
            temp=temp.getLink();
        }
        int average = sum/count;
        temp=head;

        int sum2 = 0;
        while (temp.getLink()!=null){
            count++;
            sum2 += Math.pow(Integer.parseInt(temp.getData().toString()) - average,2);
            temp=temp.getLink();
        }
        int variance = sum2/count;
        return variance;
    }

    public void printAddress(){
        Node temp = head;
        System.out.print(temp);
    }
}
