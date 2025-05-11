import enigma.core.Enigma;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

public class Main {
    public enigma.console.Console cn = Enigma.getConsole("Snake");
    private KeyListener klis;

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private char currentDircection = ' ';

    public Main() throws Exception {
        Maze maze = new Maze("src\\maze.txt");

        klis = new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }


            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_W) currentDircection = 'W';
                if (code == KeyEvent.VK_S) currentDircection = 'S';
                if (code == KeyEvent.VK_A) currentDircection = 'A';
                if (code == KeyEvent.VK_D) currentDircection = 'D';
            }


            public void keyReleased(KeyEvent e) {

            }
        };
        cn.getTextWindow().addKeyListener(klis);

        Maze.printMaze(cn);
        Random random = new Random();

        //Here I've created Input Queue and printed it on the enigma console
        LinkedList<String> input = new LinkedList<>();
        CircularQueue inputQueue = new CircularQueue(100);
        InputQueue.createInputQueue(InputQueue.per_treasure1,InputQueue.per_treasure2,InputQueue.per_treasure3, InputQueue.per_treasureAD, InputQueue.per_treasureS, input, inputQueue);
        InputQueue.printInputQueueToBoard(inputQueue, cn);



        Maze.printMaze(cn);

        //some timing shit
        int tickCounter = 0;
        long lastPlayerMove = System.currentTimeMillis();
        long lastInputUpdate = System.currentTimeMillis();


        while (true) {
            long now = System.currentTimeMillis();

            // player moves (in every 200 ms)
            if (now - lastPlayerMove >= 120) {
                if (currentDircection != ' ') {
                    maze.movePlayer(currentDircection);
                }
                lastPlayerMove = now;
            }

            // Input queue and treasure operations (in every 20 ms)
            if (now - lastInputUpdate >= 20) {
                tickCounter++;
                if (tickCounter % 50 == 0) {
                    InputQueue.printTreasuresToBoard(cn, random, inputQueue);
                    InputQueue.printInputQueueToBoard(inputQueue, cn);
                }
                lastInputUpdate = now;
            }

             // a little time to avoid fucking CPU
        }


    }

    public static void main(String[] args) throws Exception {
        new Main();
    }
}