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
        Maze maze = new Maze("/Users/isikdefneerdemgil/IdeaProjects/NumberSnakes0/src/maze.txt");

        klis = new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }


            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_W) upPressed = true;
                if (code == KeyEvent.VK_S) downPressed = true;
                if (code == KeyEvent.VK_A) leftPressed = true;
                if (code == KeyEvent.VK_D) rightPressed = true;
            }


            public void keyReleased(KeyEvent e) {

            }
        };
        cn.getTextWindow().addKeyListener(klis);

        Maze.printMaze();
        Random random = new Random();

        //Here I've created Input Queue and printed it on the enigma console
        LinkedList<String> input = new LinkedList<>();
        CircularQueue inputQueue = new CircularQueue(100);
        InputQueue.createInputQueue(InputQueue.per_treasure1,InputQueue.per_treasure2,InputQueue.per_treasure3, InputQueue.per_treasureAD, InputQueue.per_treasureS, input, inputQueue);
        InputQueue.printInputQueueToBoard(inputQueue, cn);



        Maze.printMaze();

        int tickCounter = 0;
        while (true) {
            if (currentDircection != ' ') {
                maze.movePlayer(currentDircection);
            }

            //Here I print treasure to the board in every 2 second, and update the current state of input queue on console.
            tickCounter++;
            if (tickCounter % 50 == 0) {
                InputQueue.printTreasuresToBoard(cn, random, inputQueue);
                InputQueue.printInputQueueToBoard(inputQueue, cn);
            }
            Thread.sleep(20);
        }


    }

    public static void main(String[] args) throws Exception {
        new Main();
    }
}