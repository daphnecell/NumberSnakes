import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Main {
    public enigma.console.Console cn = Enigma.getConsole("Number Snakes");
    private KeyListener klis;

    private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, NONE = -1;
    private int pendingMove = NONE;

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
    private int snakeMoveCounter = 0;
    private final int trapDuration = 100;

    private boolean spacePressed = false;
    private boolean quickMovement = true;

    private Player player;
    private CRobot cRobot;
    private SnakeManager snakeManager;
    private Random random = new Random();

    private int[][] activeTrapLocations = new int[100][2];
    private int[] trapTimers = new int[100];
    private int activeTrapCount = 0;

    private CircularQueue inputQueue;

    public Main() throws Exception {
        Maze maze = new Maze("src/maze.txt");

        snakeManager = new SnakeManager(cn);

        setupKeyListener();

        SingleLinkedList input = new SingleLinkedList();
        this.inputQueue = new CircularQueue(100);
        InputQueue.createInputQueue(InputQueue.per_treasure1, InputQueue.per_treasure2,
                InputQueue.per_treasure3, InputQueue.per_treasureAD,
                InputQueue.per_treasureS, input, inputQueue);
        InputQueue.printInputQueueToBoard(inputQueue, cn);

        placePlayerAndRobotRandomly();
        placeInitialTreasures();

        Maze.printMaze();
        updateGameInfoDisplay();

        gameStartTime = System.currentTimeMillis();
        long lastInputUpdate = System.currentTimeMillis();

        while (gameRunning) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastInputUpdate >= 100) {
                tickCounter++;
                lastInputUpdate = currentTime;
            }

            processPlayerInput();

            robotMoveCounter++;
            if (robotMoveCounter >= 4) {
                moveCRobot();
                robotMoveCounter = 0;
            }

            snakeMoveCounter++;
            if (snakeMoveCounter >= 4) {
                snakeManager.moveAllSnakes();
                snakeMoveCounter = 0;
            }

            updateTrapTimers();

            snakeManager.checkSnakeTrapCollisions(activeTrapLocations, activeTrapCount);

            if (tickCounter % 20 == 0) {
                addElementFromInputQueue();
                InputQueue.printInputQueueToBoard(inputQueue, cn);
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

                if (code == KeyEvent.VK_UP && pendingMove == NONE) {
                    pendingMove = UP;
                }
                if (code == KeyEvent.VK_DOWN && pendingMove == NONE) {
                    pendingMove = DOWN;
                }
                if (code == KeyEvent.VK_LEFT && pendingMove == NONE) {
                    pendingMove = LEFT;
                }
                if (code == KeyEvent.VK_RIGHT && pendingMove == NONE) {
                    pendingMove = RIGHT;
                }
                if (code == KeyEvent.VK_SPACE) {
                    spacePressed = true;
                }
            }

            public void keyReleased(KeyEvent e) {}
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

        if (tickCounter % moveInterval == 0 && pendingMove != NONE) {
            int newX = player.getX();
            int newY = player.getY();

            if (pendingMove == UP) newX--;
            else if (pendingMove == DOWN) newX++;
            else if (pendingMove == LEFT) newY--;
            else if (pendingMove == RIGHT) newY++;

            if (Maze.isValidPosition(newX, newY) &&
                    Maze.maze[newX][newY] != '#' &&
                    Maze.maze[newX][newY] != 'C' &&
                    !isSnakeBodyAt(newX, newY)) {

                Maze.clearPosition(player.getX(), player.getY());
                collectItem(newX, newY);
                player.setX(newX);
                player.setY(newY);
                Maze.placeElement(newX, newY, 'P');

                lastDirection = pendingMove;

                if (quickMovement && playerEnergy > 0) playerEnergy--;
            }

            pendingMove = NONE;
        }

        checkNeighboringDamage();

        if (spacePressed && playerTraps > 0) {
            placeTrapInLastDirection();
            spacePressed = false;
        }
    }

    private void checkNeighboringDamage() {
        int playerX = player.getX();
        int playerY = player.getY();

        if (cRobot.isAdjacentToPlayer(playerX, playerY)) {
            playerLife -= 30;
        }

        int snakeDamage = calculateSnakeNeighborDamage(playerX, playerY);
        if (snakeDamage > 0) {
            playerLife -= snakeDamage;
        }

        if (playerLife < 0) {
            playerLife = 0;
        }
    }

    private int calculateSnakeNeighborDamage(int playerX, int playerY) {
        int totalDamage = 0;

        int[][] neighborOffsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] offset : neighborOffsets) {
            int checkX = playerX + offset[0];
            int checkY = playerY + offset[1];

            if (Maze.isValidPosition(checkX, checkY)) {
                int snakePartsAtPosition = countSnakePartsAt(checkX, checkY);
                totalDamage += snakePartsAtPosition;
            }
        }

        return totalDamage;
    }

    private int countSnakePartsAt(int x, int y) {
        int count = 0;
        Node current = snakeManager.snakes.head;

        while (current != null) {
            Snake snake = (Snake) current.getData();

            for (int i = 0; i < snake.getBodyLength(); i++) {
                if (snake.bodyPositions != null && i < snake.bodyPositions.length) {
                    int segmentX = snake.bodyPositions[i][0];
                    int segmentY = snake.bodyPositions[i][1];

                    if (segmentX == x && segmentY == y) {
                        count++;
                    }
                }
            }

            current = current.getLink();
        }

        return count;
    }

    private boolean isSnakeBodyAt(int x, int y) {
        Node current = snakeManager.snakes.head;
        while (current != null) {
            Snake snake = (Snake) current.getData();
            if (snake.isBodyPartAt(x, y)) {
                return true;
            }
            current = current.getLink();
        }
        return false;
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

    private int lastDirection = NONE;

    private void placeTrapInLastDirection() {
        if (activeTrapCount >= 100) return;

        int trapX = player.getX();
        int trapY = player.getY();

        if (lastDirection == UP) trapX--;
        else if (lastDirection == DOWN) trapX++;
        else if (lastDirection == LEFT) trapY--;
        else if (lastDirection == RIGHT) trapY++;
        else return;

        if (Maze.isValidPosition(trapX, trapY) && Maze.maze[trapX][trapY] == ' ') {
            Maze.placeElement(trapX, trapY, '=');
            activeTrapLocations[activeTrapCount][0] = trapX;
            activeTrapLocations[activeTrapCount][1] = trapY;
            trapTimers[activeTrapCount] = trapDuration;
            activeTrapCount++;
            playerTraps--;
        }
    }

    private void placeTrap() {
        if (activeTrapCount >= 100) return;

        int trapX = player.getX();
        int trapY = player.getY();

        if (lastDirection == UP) trapX--;
        else if (lastDirection == DOWN) trapX++;
        else if (lastDirection == LEFT) trapY--;
        else if (lastDirection == RIGHT) trapY++;
        else return;

        if (Maze.isValidPosition(trapX, trapY) && Maze.maze[trapX][trapY] == ' ') {
            Maze.placeElement(trapX, trapY, '=');
            activeTrapLocations[activeTrapCount][0] = trapX;
            activeTrapLocations[activeTrapCount][1] = trapY;
            trapTimers[activeTrapCount] = trapDuration;
            activeTrapCount++;
            playerTraps--;
        }
    }

    private void updateTrapTimers() {
        for (int i = 0; i < activeTrapCount; i++) {
            trapTimers[i]--;

            if (trapTimers[i] <= 0) {
                int[] pos = {activeTrapLocations[i][0], activeTrapLocations[i][1]};
                Maze.clearPosition(pos[0], pos[1]);

                activeTrapCount--;
                if (i < activeTrapCount) {
                    activeTrapLocations[i][0] = activeTrapLocations[activeTrapCount][0];
                    activeTrapLocations[i][1] = activeTrapLocations[activeTrapCount][1];
                    trapTimers[i] = trapTimers[activeTrapCount];
                    i--;
                }
            }
        }
    }

    private void moveCRobot() {
        if (!cRobot.hasTarget()) {
            cRobot.findRandomTarget();
        }

        cRobot.move();
    }

    private void addElementFromInputQueue() {
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
        cn.getTextWindow().output("S Robot: " + String.format("%4d", snakeManager.getSnakeCount()));

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