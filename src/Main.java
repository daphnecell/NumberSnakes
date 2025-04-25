import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        LinkedList<String> myList = new LinkedList<>();

        myList.add("A");
        myList.add("B");
        myList.add("C");

        System.out.println(myList); // [A, B, C]

        myList.addFirst("Start");
        myList.addLast("End");

        System.out.println(myList); // [Start, A, B, C, End]

        myList.remove("B");
        System.out.println(myList); // [Start, A, C, End]


    }
}