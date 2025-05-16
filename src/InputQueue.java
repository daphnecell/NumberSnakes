import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import enigma.console.TextAttributes;
import enigma.core.Enigma;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class InputQueue {
    public static boolean game_over = false;

    Random random = new Random();

    public static int per_treasure1 = 40;
    static int per_treasure2 = 25;
    static int per_treasure3 = 13;
    static int per_treasureAD = 9;
    static int per_treasureS = 13;

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

    List<String> input = new ArrayList<>();
    CircularQueue inputQueue = new CircularQueue(100);

    static void createInputQueue(int per_treasure1,int per_treasure2,int per_treasure3,int per_treasureAD,int per_treasureS, LinkedList<String> input, CircularQueue inputQueue) {
        for (int i = 0; i < per_treasure1; i++) {
            input.add("1");
        }
        for (int i = 0; i < per_treasure2; i++) {
            input.add("2");
        }
        for (int i = 0; i < per_treasure3; i++) {
            input.add("3");
        }
        for (int i = 0; i < per_treasureAD; i++) {
            input.add("@");
        }
        for (int i = 0; i < per_treasureS; i++) {
            input.add("S");
        }

        Collections.shuffle(input);

        int size = input.size();
        while (!input.isEmpty()){
            inputQueue.enqueue(input.remove());
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
            if (count <= 15) { //only the first 15 elements on queue will show up on console

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
            else { // to not break the order of input queue.
                inputQueue.enqueue(inputQueue.dequeue());
            }
        }
        cn.getTextWindow().setCursorPosition(57, 5);
        cn.getTextWindow().output("<<<<<<<<<<<<<<<", new TextAttributes(Color.WHITE));
    }

    public static int initial_snake_x = 0;
    public static int initial_snake_y = 0;
    enigma.console.Console cn;
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
        // Maze içeriğini güncelle
        Maze.maze[y][x] = current;

        // Sadece ekranda o konumu yaz
        cn.getTextWindow().setCursorPosition(x, y);
        cn.getTextWindow().output(current);
        inputQueue.enqueue(dequeued);

        //SNAKE CASE
        if (current == 'S') {
            Maze.maze[y][x] = ' ';
            snake_case = true;
            initial_snake_x = x;
            initial_snake_y = y;

        }
        return snake_case;
    }

    public static boolean printTreasuresToBoard(enigma.console.Console cn,java.util.Random random , CircularQueue inputQueue, boolean snake_case) throws InterruptedException {
        char current = ' ';
        Thread.sleep(2000);
        printTreasure(cn,inputQueue,random, current, snake_case);
        return snake_case;
    }

    public static void trapCase(){

    }

    public static void snakeCase(){

    }



    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        enigma.console.Console cn = Enigma.getConsole("inputqueue");
        LinkedList<String> input = new LinkedList<>();
        CircularQueue inputQueue = new CircularQueue(100);
        createInputQueue(per_treasure1,per_treasure2,per_treasure3, per_treasureAD, per_treasureS, input, inputQueue);
        printInputQueueToBoard(inputQueue, cn);
        printTreasuresToBoard(cn,random,inputQueue,true);

    }

}
