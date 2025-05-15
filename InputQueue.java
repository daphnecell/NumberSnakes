import enigma.core.Enigma;
import java.util.Random;

public class InputQueue {
    public static boolean game_over = false;

    Random random = new Random();

    public static int per_treasure1 = 50;
    static int per_treasure2 = 25;
    static int per_treasure3 = 13;
    static int per_treasureAD = 9;
    static int per_treasureS = 3;

    int playerscore_1 = 1;
    int playerscore_2 = 4;
    int playerscore_3 = 16;
    int playerscore_S = 200;

    int player_energy_1 = 50;
    int player_energy_2 = 150;
    int player_energy_3 = 250;
    int player_energy_S = 500;

    int AIscore_1 = 50;
    int AIscore_2 = 150;
    int AIscore_3 = 250;
    int AIscore_AD = 500;

    SingleLinkedList input = new SingleLinkedList();
    CircularQueue inputQueue = new CircularQueue(100);

    static void createInputQueue(int per_treasure1, int per_treasure2, int per_treasure3, int per_treasureAD, int per_treasureS, SingleLinkedList input, CircularQueue inputQueue) {

        String[] elements = new String[per_treasure1 + per_treasure2 + per_treasure3 + per_treasureAD + per_treasureS];
        int index = 0;


        for (int i = 0; i < per_treasure1; i++) {
            elements[index++] = "1";
        }
        for (int i = 0; i < per_treasure2; i++) {
            elements[index++] = "2";
        }
        for (int i = 0; i < per_treasure3; i++) {
            elements[index++] = "3";
        }
        for (int i = 0; i < per_treasureAD; i++) {
            elements[index++] = "@";
        }
        for (int i = 0; i < per_treasureS; i++) {
            elements[index++] = "S";
        }


        shuffle(elements);


        for (String element : elements) {
            input.addNode(element);
        }


        Node current = input.head;
        while (current != null) {
            inputQueue.enqueue(current.getData());
            current = current.getLink();
        }
    }


    private static void shuffle(String[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            String temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static void printInputQueueToBoard(CircularQueue inputQueue, enigma.console.Console cn) {
        int size = inputQueue.size();
        int count = 1;
        int index = 0;
        cn.getTextWindow().setCursorPosition(57, 2);
        cn.getTextWindow().output("Input: ");
        cn.getTextWindow().setCursorPosition(57, 3);
        cn.getTextWindow().output("<<<<<<<<<<<<<<<");
        for (int i = 0; i < size; i++) {
            if (count <= 15) {
                String current = (String) inputQueue.dequeue();
                cn.getTextWindow().setCursorPosition(57 + index, 4);
                cn.getTextWindow().output(current);
                inputQueue.enqueue(current);
                count++;
                index++;
            }
            else {
                inputQueue.enqueue(inputQueue.dequeue());
            }
        }
        cn.getTextWindow().setCursorPosition(57, 5);
        cn.getTextWindow().output("<<<<<<<<<<<<<<<");
    }

    private static void printTreasure(enigma.console.Console cn, CircularQueue inputQueue, java.util.Random random) {
        int x = 2;
        int y = 2;
        boolean coordination_is_empty = false;
        while (!coordination_is_empty) {
            x = random.nextInt(53) + 2;
            y = random.nextInt(21) + 2;
            if (Maze.maze[y][x] == ' ') {
                coordination_is_empty = true;
            }
        }
        String dequeued = (String) inputQueue.dequeue();
        char current = dequeued.charAt(0);

        Maze.maze[y][x] = current;


        cn.getTextWindow().setCursorPosition(x, y);
        cn.getTextWindow().output(current);

        inputQueue.enqueue(dequeued);
    }

    public static void printTreasuresToBoard(enigma.console.Console cn, java.util.Random random, CircularQueue inputQueue) throws InterruptedException {
        Thread.sleep(2000);
        printTreasure(cn, inputQueue, random);
    }

    public static void trapCase() {

    }

    public static void snakeCase() {

    }

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        enigma.console.Console cn = Enigma.getConsole("inputqueue");
        SingleLinkedList input = new SingleLinkedList();
        CircularQueue inputQueue = new CircularQueue(100);
        createInputQueue(per_treasure1, per_treasure2, per_treasure3, per_treasureAD, per_treasureS, input, inputQueue);
        printInputQueueToBoard(inputQueue, cn);
        printTreasuresToBoard(cn, random, inputQueue);
    }
}