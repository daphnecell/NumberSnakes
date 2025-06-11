import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.Random;

public class SnakeLinkedList {
    SnakeNode head;

    public void add(Object dataToAdd) {
        SnakeNode newNode = new SnakeNode(dataToAdd);

        if (head == null) {
            head = newNode;
            return;
        }

        if (dataToAdd instanceof Character && dataToAdd.equals('S')) {
            if (head.getData().equals('S')) {
                return;
            } else {
                newNode.setLink(head);
                head = newNode;
                return;
            }
        }

        if (dataToAdd instanceof Integer) {
            Integer intDataToAdd = (Integer) dataToAdd;

            if (head.getData() instanceof Character && head.getData().equals('S')) {
                SnakeNode afterHead = head.getLink();
                newNode.setLink(afterHead);
                head.setLink(newNode);
                return;
            }
            else if (head.getData() instanceof Integer) {
                if (intDataToAdd < (Integer) head.getData()) {
                    newNode.setLink(head);
                    head = newNode;
                    return;
                }

                SnakeNode current = head;
                SnakeNode previous = null;

                while (current != null && current.getData() instanceof Integer &&
                        intDataToAdd > (Integer) current.getData()) {
                    previous = current;
                    current = current.getLink();
                }

                newNode.setLink(current);
                if (previous != null) {
                    previous.setLink(newNode);
                } else {
                    head = newNode;
                }
                return;
            }
        }

        SnakeNode temp = head;
        while (temp.getLink() != null) {
            temp = temp.getLink();
        }
        temp.setLink(newNode);
    }

    public void delete(Object dataToDelete) {
        if (head == null) {
            return;
        }

        while (head != null && head.getData().equals(dataToDelete)) {
            head = head.getLink();
        }

        if (head == null) {
            return;
        }

        SnakeNode current = head.getLink();
        SnakeNode previous = head;

        while (current != null) {
            if (current.getData().equals(dataToDelete)) {
                previous.setLink(current.getLink());
                current = current.getLink();
            } else {
                previous = current;
                current = current.getLink();
            }
        }
    }

    public void display() {
        if (head == null) {
            System.out.println("Snake is empty");
        } else {
            SnakeNode temp = head;
            while (temp != null) {
                System.out.print(temp.getData() + " ");
                temp = temp.getLink();
            }
            System.out.println();
        }
    }

    public boolean searchItem(Object item) {
        if (head == null) {
            return false;
        }

        SnakeNode temp = head;
        while (temp != null) {
            if (item.equals(temp.getData())) {
                return true;
            }
            temp = temp.getLink();
        }
        return false;
    }

    public int size() {
        int count = 0;
        SnakeNode temp = head;
        while (temp != null) {
            count++;
            temp = temp.getLink();
        }
        return count;
    }

    public Object getDataAtIndex(int index) {
        if (head == null || index < 0) {
            return null;
        }

        SnakeNode current = head;
        int count = 0;

        while (current != null) {
            if (count == index) {
                return current.getData();
            }
            count++;
            current = current.getLink();
        }
        return null;
    }

    public void debugDisplay() {
    }

    public void printByIndex(int index, int x, int y, enigma.console.Console cn) {
        Object data = getDataAtIndex(index);

        if (data == null) {
            cn.getTextWindow().output(x, y, '?');
            return;
        }

        cn.getTextWindow().setCursorPosition(x, y);

        if (data instanceof Character) {
            char ch = (Character) data;
            TextAttributes attr = (ch == 'S') ? new TextAttributes(Color.MAGENTA) : new TextAttributes(Color.WHITE);
            cn.getTextWindow().output(ch, attr);
            if (Maze.isValidPosition(y, x)) {
                Maze.maze[y][x] = ch;
            }
        } else if (data instanceof Integer) {
            String intAsString = Integer.toString((Integer) data);
            cn.getTextWindow().output(intAsString, new TextAttributes(Color.YELLOW));
            if (!intAsString.isEmpty() && Maze.isValidPosition(y, x)) {
                Maze.maze[y][x] = intAsString.charAt(0);
            }
        } else {
            cn.getTextWindow().output('?');
        }
    }

    public void reverse() {
        if (head == null || head.getLink() == null) {
            return;
        }

        SnakeLinkedList tempList = new SnakeLinkedList();
        SnakeNode current = head;

        while (current != null) {
            if (current.getData() instanceof Integer) {
                tempList.addToEnd(current.getData());
            }
            current = current.getLink();
        }

        head = new SnakeNode('S');

        SnakeNode tempCurrent = tempList.head;
        Object[] treasures = new Object[tempList.size()];
        int index = 0;

        while (tempCurrent != null) {
            treasures[index++] = tempCurrent.getData();
            tempCurrent = tempCurrent.getLink();
        }

        for (int i = treasures.length - 1; i >= 0; i--) {
            if (treasures[i] != null) {
                addToEnd(treasures[i]);
            }
        }
    }

    private void addToEnd(Object data) {
        SnakeNode newNode = new SnakeNode(data);
        if (head == null) {
            head = newNode;
            return;
        }

        SnakeNode temp = head;
        while (temp.getLink() != null) {
            temp = temp.getLink();
        }
        temp.setLink(newNode);
    }

    public SnakeLinkedList splitAt(int index) {
        if (index <= 0 || head == null) {
            return null;
        }

        SnakeNode current = head;
        int count = 0;

        while (current != null && count < index - 1) {
            current = current.getLink();
            count++;
        }

        if (current == null || current.getLink() == null) {
            return null;
        }

        SnakeLinkedList newSnake = new SnakeLinkedList();
        newSnake.head = current.getLink();
        current.setLink(null);

        return newSnake;
    }

    public void joinWith(SnakeLinkedList other) {
        if (other == null || other.head == null) {
            return;
        }

        if (head == null) {
            head = other.head;
            return;
        }

        SnakeNode temp = head;
        while (temp.getLink() != null) {
            temp = temp.getLink();
        }

        temp.setLink(other.head);
    }

    public int getTotalTreasureValue() {
        int total = 0;
        SnakeNode temp = head;

        while (temp != null) {
            if (temp.getData() instanceof Integer) {
                int value = (Integer) temp.getData();
                if (value == 1) total += 1;
                else if (value == 2) total += 4;
                else if (value == 3) total += 16;
            }
            temp = temp.getLink();
        }

        return total;
    }

    public boolean hasHead() {
        return head != null && head.getData() instanceof Character && head.getData().equals('S');
    }

    public void clear() {
        head = null;
    }
}