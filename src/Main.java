import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main {
    public enigma.console.Console cn = Enigma.getConsole("Number Snakes");
    private KeyListener klis;


    private boolean gameRunning = true;
    public static int playerScore = 0;
    public static int computerScore = 0;
    private int playerEnergy = 500;
    private int playerLife = 1000;
    private int playerTraps = 0;


    private long gameStartTime;
    private int elapsedSeconds = 0;
    private int tickCounter = 0;
    private int robotMoveCounter = 0;
    private int trapDuration = 100;


    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean quickMovement = true;


    private Player player;
    private CRobot cRobot;
    private Random random = new Random();
    private List<int[]> activeTrapLocations = new LinkedList<>();
    private List<Integer> trapTimers = new LinkedList<>();
    private CircularQueue inputQueue;


    public Main() throws Exception {

        Maze maze = new Maze("src/maze.txt");


        setupKeyListener();



        LinkedList<String> input = new LinkedList<>();
        this.inputQueue = new CircularQueue(100);
        InputQueue.createInputQueue(InputQueue.per_treasure1, InputQueue.per_treasure2, InputQueue.per_treasure3, InputQueue.per_treasureAD, InputQueue.per_treasureS, input, inputQueue);
        InputQueue.printInputQueueToBoard(inputQueue, cn);
        InputQueue.printTreasuresToBoard(cn,random,inputQueue,true);


        placePlayerAndRobotRandomly();


        Maze.printMaze();



        updateGameInfoDisplay();


        gameStartTime = System.currentTimeMillis();

        int tickCounter = 0;
        long lastPlayerMove = System.currentTimeMillis();
        long lastInputUpdate = System.currentTimeMillis();
        long lastSnakeUpdate = System.currentTimeMillis();
        while (gameRunning) {

            if (gameStartTime - lastInputUpdate >= 20) {
                tickCounter++;
                if (tickCounter % 50 == 0) {
                    boolean newSnake = InputQueue.printTreasuresToBoard(cn, random, inputQueue, false);
                    InputQueue.printInputQueueToBoard(inputQueue, cn);

                }
                lastInputUpdate = gameStartTime;
            }




            tickCounter++;


            robotMoveCounter++;
            if (robotMoveCounter >= 4) {
                moveCRobot();
                robotMoveCounter = 0;
            }


            updateTrapTimers();


            if (tickCounter % 20 == 0) {
                addElementFromInputQueue();
            }


            updateElapsedTime();


            updateGameInfoDisplay();


            if (playerLife <= 0) {
                gameRunning = false;
                showGameOver();
            }


            Thread.sleep(100);
        }
    }


    private void setupKeyListener() {
        klis = new KeyListener() {
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_UP) upPressed = true;
                if (code == KeyEvent.VK_DOWN) downPressed = true;
                if (code == KeyEvent.VK_LEFT) leftPressed = true;
                if (code == KeyEvent.VK_RIGHT) rightPressed = true;
                if (code == KeyEvent.VK_SPACE) spacePressed = true;
            }

            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_UP) upPressed = false;
                if (code == KeyEvent.VK_DOWN) downPressed = false;
                if (code == KeyEvent.VK_LEFT) leftPressed = false;
                if (code == KeyEvent.VK_RIGHT) rightPressed = false;
                if (code == KeyEvent.VK_SPACE) spacePressed = false;
            }
        };
        cn.getTextWindow().addKeyListener(klis);
    }










    private void placePlayerAndRobotRandomly() {

        int[] playerPos = Maze.findRandomEmptyPosition();
        player = new Player(playerPos[0], playerPos[1]);
        Maze.placeElement(playerPos[0], playerPos[1], 'P');


        int[] robotPos;
        do {
            robotPos = Maze.findRandomEmptyPosition();
        } while (Math.abs(robotPos[0] - playerPos[0]) + Math.abs(robotPos[1] - playerPos[1]) < 10);

        cRobot = new CRobot(robotPos[0], robotPos[1], cn);
    }


    private void placeInitialTreasures() {
        for (int i = 0; i < 30; i++) {
            addElementFromInputQueue();
        }
    }


    private void processPlayerInput() {

        int moveInterval = (quickMovement && playerEnergy > 0) ? 1 : 2;


        if (tickCounter % moveInterval == 0) {
            int newX = player.getX();
            int newY = player.getY();


            if (upPressed) newX--;
            if (downPressed) newX++;
            if (leftPressed) newY--;
            if (rightPressed) newY++;


            if (Maze.isValidPosition(newX, newY) && Maze.maze[newX][newY] != '#' &&
                    Maze.maze[newX][newY] != 'C' && Maze.maze[newX][newY] != 'S') {


                Maze.clearPosition(player.getX(), player.getY());


                collectItem(newX, newY);


                player.setX(newX);
                player.setY(newY);


                Maze.placeElement(newX, newY, 'P');


                if (quickMovement && playerEnergy > 0) {
                    playerEnergy--;
                }
            }


            if (spacePressed && playerTraps > 0) {
                placeTrap();
                spacePressed = false;
            }
        }
    }


    private void collectItem(int x, int y) {
        char item = Maze.maze[x][y];

        switch (item) {
            case '1':
                playerScore += 1;
                playerEnergy += 50;
                break;
            case '2':
                playerScore += 4;
                playerEnergy += 150;
                break;
            case '3':
                playerScore += 16;
                playerEnergy += 250;
                break;
            case '@':
                playerTraps++;
                break;
        }
    }


    private void placeTrap() {
        int trapX = player.getX();
        int trapY = player.getY();


        if (upPressed) trapX--;
        else if (downPressed) trapX++;
        else if (leftPressed) trapY--;
        else if (rightPressed) trapY++;
        else return;


        if (Maze.isValidPosition(trapX, trapY) && Maze.maze[trapX][trapY] == ' ') {

            Maze.placeElement(trapX, trapY, '=');


            activeTrapLocations.add(new int[]{trapX, trapY});
            trapTimers.add(trapDuration);


            playerTraps--;
        }
    }


    private void updateTrapTimers() {
        for (int i = 0; i < trapTimers.size(); i++) {
            trapTimers.set(i, trapTimers.get(i) - 1);


            if (trapTimers.get(i) <= 0) {
                int[] pos = activeTrapLocations.get(i);
                Maze.clearPosition(pos[0], pos[1]);

                activeTrapLocations.remove(i);
                trapTimers.remove(i);
                i--;
            }
        }
    }


    private void moveCRobot() {

        if (!cRobot.hasTarget()) {

            cRobot.findRandomTarget();
        }


        if (cRobot.isAdjacentToPlayer(player.getX(), player.getY())) {
            playerLife -= 30;
        }


        cRobot.move();
    }


    private void addElementFromInputQueue() {
        if (inputQueue.isEmpty()) {
            return;
        }


        String element = (String) inputQueue.dequeue();


        int[] pos = Maze.findRandomEmptyPosition();


        Maze.placeElement(pos[0], pos[1], element.charAt(0));


        InputQueue.printInputQueueToBoard(inputQueue, cn);
    }


    private void updateElapsedTime() {
        long currentTime = System.currentTimeMillis();
        elapsedSeconds = (int) ((currentTime - gameStartTime) / 1000);
    }


    private void updateGameInfoDisplay() {

        cn.getTextWindow().setCursorPosition(57, 7);
        cn.getTextWindow().output("Time    : " + String.format("%4d", elapsedSeconds));


        cn.getTextWindow().setCursorPosition(57, 10);
        cn.getTextWindow().output("--- Player ---");

        cn.getTextWindow().setCursorPosition(57, 11);
        cn.getTextWindow().output("Energy : " + String.format("%4d", playerEnergy));

        cn.getTextWindow().setCursorPosition(57, 12);
        cn.getTextWindow().output("Life   : " + String.format("%4d", playerLife));

        cn.getTextWindow().setCursorPosition(57, 13);
        cn.getTextWindow().output("Trap   : " + String.format("%4d", playerTraps));

        cn.getTextWindow().setCursorPosition(57, 14);
        cn.getTextWindow().output("Score  : " + String.format("%4d", playerScore));


        cn.getTextWindow().setCursorPosition(57, 16);
        cn.getTextWindow().output("---Computer---");

        cn.getTextWindow().setCursorPosition(57, 17);
        cn.getTextWindow().output("S Robot: " + String.format("%4d", 0));

        cn.getTextWindow().setCursorPosition(57, 18);
        cn.getTextWindow().output("Score  : " + String.format("%4d", computerScore));
    }


    private void showGameOver() {
        cn.getTextWindow().setCursorPosition(25, 12);
        cn.getTextWindow().output("GAME OVER", new TextAttributes(Color.RED));


        cn.getTextWindow().setCursorPosition(20, 14);
        cn.getTextWindow().output("Player Score: " + playerScore);

        cn.getTextWindow().setCursorPosition(20, 15);
        cn.getTextWindow().output("Computer Score: " + computerScore);


        cn.getTextWindow().setCursorPosition(25, 17);
        if (playerScore > computerScore) {
            cn.getTextWindow().output("YOU WIN!", new TextAttributes(Color.GREEN));
        } else if (computerScore > playerScore) {
            cn.getTextWindow().output("YOU LOSE!", new TextAttributes(Color.RED));
        } else {
            cn.getTextWindow().output("IT'S A TIE!", new TextAttributes(Color.YELLOW));
        }
    }


    public static void main(String[] args) throws Exception {
        new Main();
    }
}