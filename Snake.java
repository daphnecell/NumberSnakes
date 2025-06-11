import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.Random;

public class Snake {
    private SnakeLinkedList body;
    private int headX, headY;
    private int targetX, targetY;
    private boolean hasTarget;
    private boolean isTargetedMode;
    private int randomMoveCounter;
    private String currentDirection;
    private Random random;
    private enigma.console.Console cn;
    private static int snakeIdCounter = 0;
    private int snakeId;

    public int[][] bodyPositions;
    private int bodyLength;
    private int pendingGrowth;
    private final int MAX_BODY_LENGTH = 50;

    private final String[] DIRECTIONS = {"up", "down", "left", "right"};
    private final int MAX_RANDOM_MOVES = 25;

    public Snake(int startX, int startY, enigma.console.Console console) {
        this.cn = console;
        this.headX = startX;
        this.headY = startY;
        this.body = new SnakeLinkedList();
        this.random = new Random();
        this.hasTarget = false;
        this.isTargetedMode = true;
        this.randomMoveCounter = 0;
        this.currentDirection = DIRECTIONS[random.nextInt(4)];
        this.snakeId = ++snakeIdCounter;

        this.bodyPositions = new int[MAX_BODY_LENGTH][2];
        this.bodyLength = 1;
        this.pendingGrowth = 0;
        this.bodyPositions[0][0] = startX;
        this.bodyPositions[0][1] = startY;

        body.add('S');

        Maze.placeElement(startX, startY, 'S');
    }

    public int getHeadX() {
        return headX;
    }

    public int getHeadY() {
        return headY;
    }

    public int getSnakeId() {
        return snakeId;
    }

    public SnakeLinkedList getBody() {
        return body;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void move() {
        if (isTargetedMode) {
            if (!hasTarget) {
                findRandomTarget();
            }

            if (hasTarget) {
                moveTowardsTarget();
            } else {
                switchToRandomMode();
            }
        } else {
            performRandomMove();
        }
    }

    private void findRandomTarget() {
        SingleLinkedList treasures = Maze.findAllTreasures();

        if (treasures.size() == 0) {
            hasTarget = false;
            return;
        }

        int randomIndex = random.nextInt(treasures.size());
        int[] target = getTreasureAtIndex(treasures, randomIndex);
        targetX = target[0];
        targetY = target[1];
        hasTarget = true;
    }

    private int[] getTreasureAtIndex(SingleLinkedList treasures, int index) {
        Node current = treasures.head;
        for (int i = 0; i < index && current != null; i++) {
            current = current.getLink();
        }
        return (int[]) current.getData();
    }

    private void moveTowardsTarget() {
        int nextX = headX;
        int nextY = headY;

        if (headX < targetX) {
            nextX++;
            currentDirection = "down";
        } else if (headX > targetX) {
            nextX--;
            currentDirection = "up";
        } else if (headY < targetY) {
            nextY++;
            currentDirection = "right";
        } else if (headY > targetY) {
            nextY--;
            currentDirection = "left";
        }

        if (canMoveTo(nextX, nextY)) {
            performMove(nextX, nextY);

            if (headX == targetX && headY == targetY) {
                hasTarget = false;
            }
        } else {
            switchToRandomMode();
        }
    }

    private void switchToRandomMode() {
        isTargetedMode = false;
        randomMoveCounter = 0;
    }

    private void performRandomMove() {
        randomMoveCounter++;

        if (random.nextInt(5) == 0) {
            currentDirection = DIRECTIONS[random.nextInt(4)];
        }

        int nextX = headX;
        int nextY = headY;

        switch (currentDirection) {
            case "up":
                nextX--;
                break;
            case "down":
                nextX++;
                break;
            case "left":
                nextY--;
                break;
            case "right":
                nextY++;
                break;
        }

        if (canMoveTo(nextX, nextY)) {
            performMove(nextX, nextY);
        } else {
            boolean moved = false;
            for (String dir : DIRECTIONS) {
                if (!dir.equals(currentDirection)) {
                    int testX = headX, testY = headY;
                    switch (dir) {
                        case "up": testX--; break;
                        case "down": testX++; break;
                        case "left": testY--; break;
                        case "right": testY++; break;
                    }

                    if (canMoveTo(testX, testY)) {
                        currentDirection = dir;
                        performMove(testX, testY);
                        moved = true;
                        break;
                    }
                }
            }

            if (!moved) {
                reverseSnake();
            }
        }

        if (randomMoveCounter >= MAX_RANDOM_MOVES) {
            isTargetedMode = true;
            randomMoveCounter = 0;
        }
    }

    private boolean canMoveTo(int x, int y) {
        if (!Maze.isValidPosition(x, y)) {
            return false;
        }

        for (int i = 0; i < bodyLength; i++) {
            if (bodyPositions[i][0] == x && bodyPositions[i][1] == y) {
                return false;
            }
        }

        char cell = Maze.maze[x][y];

        if (cell == ' ' || cell == '1' || cell == '2' || cell == '3') {
            return true;
        }

        return false;
    }

    private void performMove(int newX, int newY) {
        char treasureAtPosition = Maze.maze[newX][newY];
        boolean eatTreasure = (treasureAtPosition == '1' || treasureAtPosition == '2' || treasureAtPosition == '3');

        clearBodyFromMaze();

        if (eatTreasure) {
            int treasureValue = Character.getNumericValue(treasureAtPosition);
            body.add(treasureValue);

            pendingGrowth++;

            switch (treasureAtPosition) {
                case '1':
                    Main.computerScore += 1;
                    break;
                case '2':
                    Main.computerScore += 4;
                    break;
                case '3':
                    Main.computerScore += 16;
                    break;
            }
        }

        if (pendingGrowth > 0 && bodyLength < MAX_BODY_LENGTH) {
            bodyLength++;
            pendingGrowth--;
        } else {
            for (int i = bodyLength - 1; i > 0; i--) {
                bodyPositions[i][0] = bodyPositions[i - 1][0];
                bodyPositions[i][1] = bodyPositions[i - 1][1];
            }
        }

        bodyPositions[0][0] = newX;
        bodyPositions[0][1] = newY;
        headX = newX;
        headY = newY;

        displaySnakeOnMaze();
    }

    private void clearBodyFromMaze() {
        for (int i = 0; i < bodyLength; i++) {
            int x = bodyPositions[i][0];
            int y = bodyPositions[i][1];
            if (Maze.isValidPosition(x, y)) {
                Maze.clearPosition(x, y);
            }
        }
    }

    private void displaySnakeOnMaze() {
        for (int i = 0; i < bodyLength; i++) {
            int x = bodyPositions[i][0];
            int y = bodyPositions[i][1];

            if (Maze.isValidPosition(x, y)) {
                if (i == 0) {
                    Maze.placeElement(x, y, 'S');
                } else {
                    Object bodyData = body.getDataAtIndex(i);
                    char displayChar = '?';

                    if (bodyData instanceof Integer) {
                        int value = (Integer) bodyData;
                        displayChar = Character.forDigit(value, 10);

                        Maze.placeElement(x, y, displayChar);
                    } else if (bodyData instanceof Character) {
                        displayChar = (Character) bodyData;
                        Maze.placeElement(x, y, displayChar);
                    } else {
                        cn.getTextWindow().setCursorPosition(y, x);
                        cn.getTextWindow().output('*', new TextAttributes(Color.MAGENTA));
                        if (Maze.isValidPosition(x, y)) {
                            Maze.maze[x][y] = '*';
                        }
                    }
                }
            }
        }
    }

    private void reverseSnake() {
        clearBodyFromMaze();

        switch (currentDirection) {
            case "up":
                currentDirection = "down";
                break;
            case "down":
                currentDirection = "up";
                break;
            case "left":
                currentDirection = "right";
                break;
            case "right":
                currentDirection = "left";
                break;
        }

        int[][] tempPositions = new int[bodyLength][2];
        for (int i = 0; i < bodyLength; i++) {
            tempPositions[i][0] = bodyPositions[bodyLength - 1 - i][0];
            tempPositions[i][1] = bodyPositions[bodyLength - 1 - i][1];
        }

        for (int i = 0; i < bodyLength; i++) {
            bodyPositions[i][0] = tempPositions[i][0];
            bodyPositions[i][1] = tempPositions[i][1];
        }

        headX = bodyPositions[0][0];
        headY = bodyPositions[0][1];

        if (body.head != null && body.head.getLink() != null) {
            body.reverse();
        }

        displaySnakeOnMaze();
    }

    public boolean isAt(int x, int y) {
        return headX == x && headY == y;
    }

    public boolean isAdjacentToPlayer(int playerX, int playerY) {
        for (int i = 0; i < bodyLength; i++) {
            int segmentX = bodyPositions[i][0];
            int segmentY = bodyPositions[i][1];
            if (Math.abs(segmentX - playerX) + Math.abs(segmentY - playerY) == 1) {
                return true;
            }
        }
        return false;
    }

    public void removeFromMaze() {
        clearBodyFromMaze();
    }

    public boolean collidesWith(Snake other) {
        return this.headX == other.headX && this.headY == other.headY;
    }

    public int checkBodyCollision(Snake other) {
        for (int i = 1; i < other.bodyLength; i++) {
            if (this.headX == other.bodyPositions[i][0] && this.headY == other.bodyPositions[i][1]) {
                Object bodyData = other.body.getDataAtIndex(i);
                if (bodyData instanceof Integer) {
                    return (Integer) bodyData;
                }
                return 0;
            }
        }
        return -1;
    }

    public static void handleHeadToHeadCollision(Snake snake1, Snake snake2) {
        snake1.removeFromMaze();
        snake2.removeFromMaze();
    }

    public void handleBodyCollision(Snake otherSnake, int hitValue) {
        if (hitValue == 1) {
            joinSnakes(this, otherSnake);
            this.removeFromMaze();
        } else if (hitValue == 2 || hitValue == 3) {
            splitSnakeAtHit(otherSnake, hitValue);
            this.reverseSnake();
        }
    }

    private static void joinSnakes(Snake hitterSnake, Snake targetSnake) {
        SnakeNode current = hitterSnake.body.head;
        while (current != null) {
            if (current.getData() instanceof Integer) {
                targetSnake.body.add(current.getData());
                targetSnake.pendingGrowth++;
            }
            current = current.getLink();
        }
    }

    private void splitSnakeAtHit(Snake targetSnake, int hitValue) {
        int hitIndex = -1;
        for (int i = 1; i < targetSnake.bodyLength; i++) {
            if (this.headX == targetSnake.bodyPositions[i][0] && this.headY == targetSnake.bodyPositions[i][1]) {
                Object bodyData = targetSnake.body.getDataAtIndex(i);
                if (bodyData instanceof Integer && (Integer) bodyData == hitValue) {
                    hitIndex = i;
                    break;
                }
            }
        }

        if (hitIndex != -1) {
            SnakeLinkedList secondPart = targetSnake.body.splitAt(hitIndex);
            if (secondPart != null) {
                secondPart.reverse();
            }

            targetSnake.bodyLength = hitIndex;

            for (int i = hitIndex; i < MAX_BODY_LENGTH; i++) {
                if (i < targetSnake.bodyPositions.length) {
                    targetSnake.bodyPositions[i][0] = -1;
                    targetSnake.bodyPositions[i][1] = -1;
                }
            }
        }
    }

    public boolean isTrapped(int[][] activeTrapLocations, int activeTrapCount) {
        for (int i = 0; i < activeTrapCount; i++) {
            int trapX = activeTrapLocations[i][0];
            int trapY = activeTrapLocations[i][1];

            if (Math.abs(headX - trapX) <= 1 && Math.abs(headY - trapY) <= 1) {
                return true;
            }
        }
        return false;
    }

    public void destroy() {
        removeFromMaze();
        Main.playerScore += 200;
    }

    public int getBodySegmentAt(int x, int y) {
        for (int i = 0; i < bodyLength; i++) {
            if (bodyPositions[i][0] == x && bodyPositions[i][1] == y) {
                return i;
            }
        }
        return -1;
    }

    public boolean isBodyPartAt(int x, int y) {
        return getBodySegmentAt(x, y) != -1;
    }
}