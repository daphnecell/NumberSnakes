import java.awt.*;
import java.util.Random;
import enigma.console.TextAttributes;
import enigma.core.Enigma;
import java.util.Timer;
import java.util.TimerTask;

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
        for (int i = 0; i < per_treasure1; i++) {
            input.addNode("1");
        }
        for (int i = 0; i < per_treasure2; i++) {
            input.addNode("2");
        }
        for (int i = 0; i < per_treasure3; i++) {
            input.addNode("3");
        }
        for (int i = 0; i < per_treasureAD; i++) {
            input.addNode("@");
        }
        for (int i = 0; i < per_treasureS; i++) {
            input.addNode("S");
        }

        Random shuffleRandom = new Random();
        int totalSize = input.size();

        for (int i = 0; i < totalSize; i++) {
            int randomIndex = shuffleRandom.nextInt(input.size());

            Node current = input.head;
            Node previous = null;

            for (int j = 0; j < randomIndex; j++) {
                previous = current;
                current = current.getLink();
            }

            inputQueue.enqueue((String) current.getData());

            if (previous == null) {
                input.head = current.getLink();
            } else {
                previous.setLink(current.getLink());
            }
        }
    }

    public static void printInputQueueToBoard(CircularQueue inputQueue, enigma.console.Console cn) {
        int size = inputQueue.size();
        int count = 1;
        int index = 0;
        cn.getTextWindow().setCursorPosition(57, 2);
        cn.getTextWindow().output("Input: ");
        cn.getTextWindow().setCursorPosition(57, 3);
        cn.getTextWindow().output("<<<<<<<<<<<<<<<", new TextAttributes(Color.WHITE));
        for (int i = 0; i < size; i++) {
            if (count <= 15) {
                String current = (String) inputQueue.dequeue();
                cn.getTextWindow().setCursorPosition(57 + index, 4);
                TextAttributes attr = new TextAttributes(Color.WHITE);
                if (current.equals("1") || current.equals("2") || current.equals("3")) {
                    attr = new TextAttributes(Color.YELLOW);
                } else if (current.equals("@")) {
                    attr = new TextAttributes(Color.CYAN);
                } else if (current.equals("S")) {
                    attr = new TextAttributes(Color.MAGENTA);
                }
                cn.getTextWindow().output(current, attr);
                inputQueue.enqueue(current);
                count++;
                index++;
            }
            else {
                inputQueue.enqueue(inputQueue.dequeue());
            }
        }
        cn.getTextWindow().setCursorPosition(57, 5);
        cn.getTextWindow().output("<<<<<<<<<<<<<<<", new TextAttributes(Color.WHITE));
    }

    public static void addElementFromQueue(CircularQueue inputQueue, SnakeManager snakeManager, enigma.console.Console cn) {
        if (inputQueue.isEmpty()) {
            return;
        }

        String element = (String) inputQueue.dequeue();

        if (element.equals("S")) {
            int[] pos = Maze.findRandomEmptyPosition();
            snakeManager.addSnake(pos[0], pos[1]);
        } else {
            int[] pos = Maze.findRandomEmptyPosition();
            Maze.placeElement(pos[0], pos[1], element.charAt(0));
        }

        inputQueue.enqueue(element);
    }

    private static boolean printTreasure(enigma.console.Console cn, CircularQueue inputQueue, java.util.Random random, char current, boolean snake_case) {
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
        current = dequeued.charAt(0);

        if (current != 'S') {
            Maze.maze[y][x] = current;
            cn.getTextWindow().setCursorPosition(x, y);

            TextAttributes attr = new TextAttributes(Color.WHITE);
            if (current == '1' || current == '2' || current == '3') {
                attr = new TextAttributes(Color.YELLOW);
            } else if (current == '@') {
                attr = new TextAttributes(Color.CYAN);
            }
            cn.getTextWindow().output(current, attr);
        } else {
            snake_case = true;
        }

        inputQueue.enqueue(dequeued);
        return snake_case;
    }

    public static boolean printTreasuresToBoard(enigma.console.Console cn, java.util.Random random, CircularQueue inputQueue, boolean snake_case) throws InterruptedException {
        char current = ' ';
        Thread.sleep(2000);
        return printTreasure(cn, inputQueue, random, current, snake_case);
    }

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        enigma.console.Console cn = Enigma.getConsole("inputqueue");
        SingleLinkedList input = new SingleLinkedList();
        CircularQueue inputQueue = new CircularQueue(100);
        createInputQueue(per_treasure1, per_treasure2, per_treasure3, per_treasureAD, per_treasureS, input, inputQueue);
        printInputQueueToBoard(inputQueue, cn);
        printTreasuresToBoard(cn, random, inputQueue, true);
    }
}