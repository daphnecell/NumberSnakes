import java.util.Random;

public class CRobot {
    public static int x;
    public static int y;
    public static int targetX;
    public static int targetY;
    public static boolean hasTarget;
    public static Random random;
    public static enigma.console.Console cn;
    private CRobot cRobot;

    public SingleLinkedList pathPositions;
    public int currentPathIndex;

    public CRobot(int startX, int startY, enigma.console.Console console) {
        this.x = startX;
        this.y = startY;
        this.cn = console;
        this.random = new Random();
        this.hasTarget = false;
        this.pathPositions = new SingleLinkedList();
        this.currentPathIndex = 0;
        Maze.placeElement(x, y, 'C', cn);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasTarget() {
        return hasTarget;
    }

    public void move() {
        if (!hasTarget) {
            findRandomTarget();
            return;
        }

        if (currentPathIndex >= pathPositions.size()) {
            if (x == targetX && y == targetY) {
                clearPathMarkers();
                hasTarget = false;
                findRandomTarget();
            } else {
                findRandomTarget();
            }
            return;
        }


        int[] nextPos = getPositionFromList(currentPathIndex);
        int nextX = nextPos[0];
        int nextY = nextPos[1];

        boolean hasTreasure = isTreasureAtPosition(nextX, nextY);
        char treasureType = ' ';
        if (hasTreasure) {
            treasureType = Maze.maze[nextX][nextY];
        }

        Maze.clearPosition(x, y, cn);
        x = nextX;
        y = nextY;

        if (hasTreasure) {
            collectTreasureByType(treasureType);
        }

        if (x == targetX && y == targetY) {
            clearPathMarkers();
            hasTarget = false;
        } else {
            currentPathIndex++;
        }

        Maze.placeElement(x, y, 'C', cn);
    }


    private int[] getPositionFromList(int index) {
        Node current = pathPositions.head;
        for (int i = 0; i < index && current != null; i++) {
            current = current.getLink();
        }

        if (current != null) {
            return (int[]) current.getData();
        }

        return new int[]{x, y};
    }

    private boolean isTreasureAtPosition(int x, int y) {
        char cell = Maze.maze[x][y];
        return cell == '1' || cell == '2' || cell == '3' || cell == '@';
    }

    private void collectTreasureByType(char treasureType) {
        switch (treasureType) {
            case '1':
                Score.computerScore += 1;
                break;
            case '2':
                Score.computerScore += 4;
                break;
            case '3':
                Score.computerScore += 16;
                break;
            case '@':
                Score.computerScore += 50;
                break;
        }
    }

    public static void moveCRobot(CRobot cRobot) {
        if (!cRobot.hasTarget()) {
            cRobot.findRandomTarget();
        }

        /*
        if (cRobot.isAdjacentToPlayer(player.getX(), player.getY())) {
            playerLife -= 30;
        }*/

        cRobot.move();
    }

    public static SingleLinkedList findAllTreasures() {
        SingleLinkedList treasures = new SingleLinkedList();
        for (int i = 0; i < Maze.maze.length; i++) {
            for (int j = 0; j < Maze.maze[0].length; j++) {
                char cell = Maze.maze[i][j];
                if (cell == '1' || cell == '2' || cell == '3') {
                    treasures.addNode(new int[]{i, j});
                }
            }
        }
        return treasures;
    }

    private void clearPathMarkers() {
        for (int i = 0; i < Maze.maze.length; i++) {
            for (int j = 0; j < Maze.maze[0].length; j++) {
                if (Maze.maze[i][j] == '.') {
                    Maze.clearPosition(i, j, cn);
                }
            }
        }
    }

    public int calculateHeuristic(int x, int y, SingleLinkedList pathPositions) {
        return Math.abs(x - targetX) + Math.abs(y - targetY);
    }

    private void reconstructPath(int[][][] parent, int x, int y) {

        this.pathPositions = new SingleLinkedList();

        int currentX = x;
        int currentY = y;


        SingleLinkedList tempPath = new SingleLinkedList();

        while (!(currentX == this.x && currentY == this.y)) {
            tempPath.addNode(new int[]{currentX, currentY});

            int tempX = parent[currentX][currentY][0];
            int tempY = parent[currentX][currentY][1];
            currentX = tempX;
            currentY = tempY;
        }


        Node current = tempPath.head;
        while (current != null) {
            this.pathPositions.addNode(current.getData());
            current = current.getLink();
        }
    }

    private void findPathToTarget() {
        boolean[][] visited = new boolean[Maze.maze.length][Maze.maze[0].length];
        int[][][] parent = new int[Maze.maze.length][Maze.maze[0].length][2];
        PriorityQueue<PathNode> openList = new PriorityQueue<>();

        openList.add(new PathNode(x, y, 0, calculateHeuristic(x, y, pathPositions)));

        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

        while (!openList.isEmpty()) {
            PathNode current = openList.poll();
            int cx = current.getX();
            int cy = current.getY();

            if (visited[cx][cy]) {
                continue;
            }

            visited[cx][cy] = true;

            if (cx == targetX && cy == targetY) {
                reconstructPath(parent, cx, cy);
                return;
            }

            for (int[] dir : directions) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];

                if (Maze.isValidPosition(nx, ny) && !visited[nx][ny]) {
                    int newG = current.getG() + 1;
                    int newH = calculateHeuristic(nx, ny, pathPositions);

                    openList.add(new PathNode(nx, ny, newG, newH));

                    parent[nx][ny][0] = cx;
                    parent[nx][ny][1] = cy;
                }
            }
        }

        hasTarget = false;
    }

    private void markPathOnMaze() {
        Node current = pathPositions.head;
        while (current != null) {
            int[] pos = (int[]) current.getData();
            if (Maze.maze[pos[0]][pos[1]] == ' ') {
                Maze.placeElement(pos[0], pos[1], '.', cn);
            }
            current = current.getLink();
        }
    }

    public void findRandomTarget() {
        if (hasTarget) {
            return;
        }
        clearPathMarkers();
        this.pathPositions = new SingleLinkedList();
        this.currentPathIndex = 0;

        SingleLinkedList treasures = findAllTreasures();
        if (treasures.size() == 0) {
            hasTarget = false;
            return;
        }


        int randomIndex = random.nextInt(treasures.size());
        int[] target = getPositionFromTreasureList(treasures, randomIndex);

        targetX = target[0];
        targetY = target[1];
        hasTarget = true;
        findPathToTarget();
        markPathOnMaze();
    }


    private int[] getPositionFromTreasureList(SingleLinkedList treasures, int index) {
        Node current = treasures.head;
        for (int i = 0; i < index && current != null; i++) {
            current = current.getLink();
        }

        if (current != null) {
            return (int[]) current.getData();
        }


        if (treasures.head != null) {
            return (int[]) treasures.head.getData();
        }


        return new int[]{x, y};
    }

    public static int[] findRandomEmptyPosition() {
        Random random = new Random();
        int x, y;

        do {
            x = random.nextInt(Maze.maze.length);
            y = random.nextInt(Maze.maze[0].length);
        } while (Maze.maze[x][y] != ' ');

        return new int[]{x, y};
    }

    public void placePRobotRandomly() {
        int[] robotPos;
        int[] playerPos;

        playerPos = new int[]{0, 0};

        do {
            robotPos = Maze.findRandomEmptyPosition();
        } while (Math.abs(robotPos[0] - playerPos[0]) + Math.abs(robotPos[1] - playerPos[1]) < 10);

        cRobot = new CRobot(robotPos[0], robotPos[1], cn);
    }
}