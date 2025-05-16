import enigma.core.Enigma;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;



public class SnakeLinkedList {
    SnakeNode head;

    public void add(Object dataToAdd) {
        SnakeNode newNode = new SnakeNode(dataToAdd);

        if (head == null) { // Here list is empty
            head = newNode;
            return;
        }

        // Here we are adding the head 'S'
        if (dataToAdd instanceof Character && dataToAdd.equals('S')) {
            if (head.getData().equals('S')) {
                // the cae that S is already added as head
                System.out.println("'S' is already head. ");
                return;
            } else {
                // here we add S as head :))
                newNode.setLink(head);
                head = newNode;
                return;
            }
        }

        // Here we are adding the treasures.
        if (dataToAdd instanceof Integer) {
            Integer intDataToAdd = (Integer) dataToAdd;

            // Head is 'S'. Integers are sorted after 'S'. THAT IS THE CASE WE'RE LOOKING FOR
            if (head.getData() instanceof Character && head.getData().equals('S')) {
                SnakeNode current = head.getLink();
                SnakeNode previous = head; // 'S' is the previous node initially
                while (current != null && current.getData() instanceof Integer &&
                        intDataToAdd > (Integer) current.getData()) {
                    previous = current;
                    current = current.getLink();
                }
                newNode.setLink(previous.getLink()); // newNode.next = current
                previous.setLink(newNode);           // previous.next = newNode
                return;
            }
            // Head is an Integer. List is sorted Integers.
            else if (head.getData() instanceof Integer) {
                if (intDataToAdd < (Integer) head.getData()) { // inserting at front
                    newNode.setLink(head);
                    head = newNode;
                    return;
                }
                // Insert in middle or at the end (dataToAdd >= head.getData())
                SnakeNode current = head;
                SnakeNode previous = null;
                while (current != null && current.getData() instanceof Integer &&
                        intDataToAdd > (Integer) current.getData()) {
                    previous = current;
                    current = current.getLink();
                }

                if (previous != null) {
                    newNode.setLink(previous.getLink()); // newNode.next = current
                    previous.setLink(newNode);           // previous.next = newNode
                } else {

                    head = newNode;

                    newNode.setLink(head);
                    head = newNode;
                }
                return;
            }
            //  Head is not 'S' Integers sorted after it.
            else if (head.getData() instanceof Character) {
                SnakeNode current = head.getLink();
                SnakeNode previous = head;
                while (current != null && current.getData() instanceof Integer &&
                        intDataToAdd > (Integer) current.getData()) {
                    previous = current;
                    current = current.getLink();
                }
                newNode.setLink(previous.getLink());
                previous.setLink(newNode);
                return;
            }
        }


        // HERE WE HANDLE THE SITUATIONS THAT WE BEHAVE SILLY
        SnakeNode temp = head;
        while (temp.getLink() != null) {
            temp = temp.getLink();
        }
        temp.setLink(newNode);
    }

    public void delete(Object dataToDelete) {
        if (head == null) {
            System.out.println("Linked list is empty");
            return;
        }

        // deleting from start
        while (head != null && head.getData().equals(dataToDelete)) {
            head = head.getLink();
        }

        // null list
        if (head == null) {
            return;
        }

        // other deleting operations
        SnakeNode temp = head.getLink();
        SnakeNode previous = head;      // previous always holds the one thats before temp

        while (temp != null) {
            if (temp.getData().equals(dataToDelete)) {
                previous.setLink(temp.getLink());
                temp = temp.getLink();
            } else {
                previous = temp;
                temp = temp.getLink();
            }
        }
    }

    public void display() {
        if (head == null) {
            System.out.println("Linked list is empty");
        } else {
            SnakeNode temp = head;
            while (temp != null) {
                System.out.println(temp.getData());
                temp = temp.getLink();
            }
        }
    }

    public boolean searchItem(Object item) {
        boolean found = false;
        if (head == null) {
            return false;
        } else {
            SnakeNode temp = head;
            while (temp != null) {

                if (item.equals(temp.getData())) {
                    found = true;
                    break;
                }
                temp = temp.getLink();
            }
        }
        return found;
    }

    public int size() {
        int count = 0;
        if (head == null) {
            System.out.println("Linked list is empty");
        } else {
            SnakeNode temp = head;
            while (temp != null) {
                count++;
                temp = temp.getLink();
            }
        }
        return count;
    }


    public void printByIndex(int index, int x, int y, enigma.console.Console cn) {
        if (head == null) {
            cn.getTextWindow().output("Linked list is empty.");
            return;
        }

        if (index < 0) {
            cn.getTextWindow().output("Negative index");
            return;
        }

        SnakeNode current = head;
        int count = 0;
        //the part that we print the index that we want
        //we return value as char to avoid problems (to avoid ClassCastException)
        while (current != null) {
            if (count == index) {
                cn.getTextWindow().setCursorPosition(x, y);
                Object data = current.getData();
                if (data instanceof Character) {
                    cn.getTextWindow().output(((Character) data).charValue());
                    Maze.maze[y][x] = ((Character) data).charValue();
                } else if (data instanceof Integer) {
                    String intAsString = Integer.toString(((Integer) data).intValue());
                    cn.getTextWindow().output(intAsString);
                    if (!intAsString.isEmpty()) {
                        Maze.maze[y][x] = intAsString.charAt(0);
                    } else {
                        Maze.maze[y][x] = '?';
                    }
                } else if (data != null) {
                    // this block shouldnt work normally. If it works that means that there must be an unexpected value in maze array. it prints ?
                    cn.getTextWindow().output('?');
                    Maze.maze[y][x] = '?';
                }
                return;
            }
            count++;
            current = current.getLink();
        }

        //if this part works that means that we are asking for a index that actually does not exist
        cn.getTextWindow().output("error: linked list out of bounds");
    }

    Random random = new Random();
    public int random_column;
    public int random_row;


    public char selectRandomWaffle() {
        boolean found = false;
        char treasure = ' ';
        while (!found) {
            random_column = random.nextInt(56);
            random_row = random.nextInt(24);
            treasure = Maze.maze[random_row][random_column];
            if (treasure == '1' || treasure == '2' || treasure == '3') {
                found = true;
            }
        }
        return treasure;
    }

    public boolean is_random_move_case(int current_x, int current_y, enigma.console.Console cn, String motion) {
        switch (motion) {
            case "left":
                if (Maze.maze[current_y][current_x - 1] != ' ') {

                    return true;
                }
                break;

            case "right":
                if (Maze.maze[current_y][current_x + 1] != ' ') {
                    return true;
                }
                break;

            case "up":
                if (Maze.maze[current_y + 1][current_x] != ' ') {
                    return true;
                }
                break;

            case "down":
                if (Maze.maze[current_y - 1][current_x] != ' ') {
                    return true;
                }
                break;

            default:
                return false;  // saçma bir şey girildiyse fonksiyondA bu çalışacak
        }
        return false;  // Hiçbir case true dönmediyse false dönsün her türlü diye
    }


    //if its random move case this function will work
    public void random_move(int current_x, int current_y, enigma.console.Console cn, String motion) {
        int count = 0;

        String[] motions = {"left", "right", "up", "down"};

        while (count < 25) {
            //in every move there isa %20 possibility to change direction
            int n = random.nextInt(5);
            if (n == 0) {
                int k = random.nextInt(4);
                motion = motions[k];
            }

            switch (motion) {
                case "left":
                    if ((Maze.maze[current_y - 1][current_x] == ' ') && (Maze.maze[current_y + 1][current_x] == ' ')) {
                        int which = random.nextInt(2);
                        if (which == 0) {
                            printByIndex(0, current_x, current_y - 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (which == 1) {
                            printByIndex(0, current_x, current_y + 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    } else if (Maze.maze[current_y - 1][current_x] == ' ') {
                        printByIndex(0, current_x, current_y - 1, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if (Maze.maze[current_y + 1][current_x] == ' ') {
                        printByIndex(0, current_x, current_y + 1, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if ((Maze.maze[current_y - 1][current_x] != ' ') && (Maze.maze[current_y + 1][current_x] != ' ')) {
                        //reverse movement case
                    } else {
                        int dir = random.nextInt(4);
                        if (dir == 0) {
                            printByIndex(0, current_x, current_y + 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 1) {
                            printByIndex(0, current_x, current_y - 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 2) {
                            printByIndex(0, current_x + 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 3) {
                            printByIndex(0, current_x - 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    }
                case "right":
                    if ((Maze.maze[current_y - 1][current_x] == ' ') && (Maze.maze[current_y + 1][current_x] == ' ')) {
                        int which = random.nextInt(2);
                        if (which == 0) {
                            printByIndex(0, current_x, current_y - 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (which == 1) {
                            printByIndex(0, current_x, current_y + 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    } else if (Maze.maze[current_y - 1][current_x] == ' ') {
                        printByIndex(0, current_x, current_y - 1, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if (Maze.maze[current_y + 1][current_x] == ' ') {
                        printByIndex(0, current_x, current_y + 1, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if ((Maze.maze[current_y - 1][current_x] != ' ') && (Maze.maze[current_y + 1][current_x] != ' ')) {
                        //reverse movement case
                    } else {
                        int dir = random.nextInt(4);
                        if (dir == 0) {
                            printByIndex(0, current_x, current_y + 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 1) {
                            printByIndex(0, current_x, current_y - 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 2) {
                            printByIndex(0, current_x + 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 3) {
                            printByIndex(0, current_x - 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    }

                case "down":
                    if ((Maze.maze[current_y][current_x - 1] == ' ') && (Maze.maze[current_y][current_x + 1] == ' ')) {
                        int which = random.nextInt(2);
                        if (which == 0) {
                            printByIndex(0, current_x - 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (which == 1) {
                            printByIndex(0, current_x + 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    } else if (Maze.maze[current_y][current_x - 1] == ' ') {
                        printByIndex(0, current_x - 1, current_y, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if (Maze.maze[current_y][current_x + 1] == ' ') {
                        printByIndex(0, current_x + 1, current_y, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if ((Maze.maze[current_y][current_x - 1] != ' ') && (Maze.maze[current_y][current_x + 1] != ' ')) {
                        //reverse movement case
                    } else {
                        int dir = random.nextInt(4);
                        if (dir == 0) {
                            printByIndex(0, current_x, current_y + 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 1) {
                            printByIndex(0, current_x, current_y - 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 2) {
                            printByIndex(0, current_x + 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 3) {
                            printByIndex(0, current_x - 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    }
                case "up":
                    if ((Maze.maze[current_y][current_x - 1] == ' ') && (Maze.maze[current_y][current_x + 1] == ' ')) {
                        int which = random.nextInt(2);
                        if (which == 0) {
                            printByIndex(0, current_x - 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (which == 1) {
                            printByIndex(0, current_x + 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    } else if (Maze.maze[current_y][current_x - 1] == ' ') {
                        printByIndex(0, current_x - 1, current_y, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if (Maze.maze[current_y][current_x + 1] == ' ') {
                        printByIndex(0, current_x + 1, current_y, cn);
                        Maze.maze[current_y][current_x] = ' ';
                        cn.getTextWindow().setCursorPosition(current_x, current_y);
                        cn.getTextWindow().output(' ');
                    } else if ((Maze.maze[current_y][current_x - 1] != ' ') && (Maze.maze[current_y][current_x + 1] != ' ')) {
                        //reverse movement case
                    } else {
                        int dir = random.nextInt(4);
                        if (dir == 0) {
                            printByIndex(0, current_x, current_y + 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 1) {
                            printByIndex(0, current_x, current_y - 1, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 2) {
                            printByIndex(0, current_x + 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        } else if (dir == 3) {
                            printByIndex(0, current_x - 1, current_y, cn);
                            Maze.maze[current_y][current_x] = ' ';
                            cn.getTextWindow().setCursorPosition(current_x, current_y);
                            cn.getTextWindow().output(' ');
                        }
                    }

            }

            count++;
        }
    }


    //NOT: büyük ihtimalle sonradan her if içerisine snake'in diğer indeksleri için başka bir detay daha eklememiz gerekecek
    public void search(int random_row, int random_column, int initial_S_x, int initial_S_y, enigma.console.Console cn) throws InterruptedException {
        int current_x = initial_S_x;
        int current_y = initial_S_y;
        boolean search_completed = false;

        while (!search_completed) {
            // Here we check if the search is complete or not
            if (current_x == random_column && current_y == random_row) {
                search_completed = true;
                continue;
            }

            // Here we clear the ex position
            Maze.maze[current_y][current_x] = ' ';
            cn.getTextWindow().setCursorPosition(current_x, current_y);
            cn.getTextWindow().output(' ');

            // first horizontal motion
            if (current_x < random_column) {
                if (is_random_move_case(current_x, current_y, cn, "right")) {
                    random_move(current_x, current_y, cn, "right");
                }
                current_x = current_x + 1;  // moving right
            } else if (current_x > random_column) {
                current_x = current_x - 1;  // moving left
                if (is_random_move_case(current_x, current_y, cn, "left")) {
                    random_move(current_x, current_y, cn, "left");
                }
            }
            // then vertical motion
            else if (current_y < random_row) {
                current_y = current_y + 1;  // moving down
                if (is_random_move_case(current_x, current_y, cn, "down")) {
                    random_move(current_x, current_y, cn, "down");
                }
            } else if (current_y > random_row) {
                current_y = current_y - 1;  // moving up
                if (is_random_move_case(current_x, current_y, cn, "up")) {
                    random_move(current_x, current_y, cn, "up");
                }
            }

            // writing to the next position
            printByIndex(0, current_x, current_y, cn);

            Thread.sleep(400); //the period of snake:))
        }
    }






    public static void main(String[] args) throws InterruptedException {


        Maze maze = new Maze("/Users/isikdefneerdemgil/IdeaProjects/NumberSnakes0/src/maze.txt");
        enigma.console.Console cn = Enigma.getConsole("snaketest");
        SnakeLinkedList test = new SnakeLinkedList();
        Maze.printMaze();
        test.add('S');

        test.printByIndex(1, 3, 5, cn);
        Maze.printMaze();
        test.search(16, 16, 5, 5, cn);

    }
}
