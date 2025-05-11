import java.util.List;
import java.util.LinkedList;
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

    public List<int[]> pathPositions;
    public int currentPathIndex;


    public CRobot(int startX, int startY, enigma.console.Console console) {
        this.x = startX;
        this.y = startY;
        this.cn = console;
        this.random = new Random();
        this.hasTarget = false;
        this.pathPositions = new LinkedList<>();
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


        int[] nextPos = pathPositions.get(currentPathIndex);
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

    public static List<int[]> findAllTreasures() {
        List<int[]> treasures = new LinkedList<>();
        for (int i = 0; i < Maze.maze.length; i++) {
            for (int j = 0; j < Maze.maze[0].length; j++) {
                char cell = Maze.maze[i][j];
                if (cell == '1' || cell == '2' || cell == '3') {
                    treasures.add(new int[]{i, j});
                }
            }
        }
        return treasures;
    }

    private void clearPathMarkers() {
        for (int i = 0; i < Maze.maze.length; i++) {
            for (int j = 0; j < Maze.maze[0].length; j++) {
                if (Maze.maze[i][j] == '.') {
                    Maze.clearPosition(i, j,cn);
                }
            }
        }
    }

    public int calculateHeuristic(int x, int y, List<int[]> pathPositions) {
        return Math.abs(x - targetX) + Math.abs(y - targetY);
    }

    private void reconstructPath(int[][][] parent, int x, int y) {

        List<int[]> path = new LinkedList<>();


        int currentX = x;
        int currentY = y;


        while (!(currentX == this.x && currentY == this.y)) {

            path.add(0, new int[]{currentX, currentY});


            int tempX = parent[currentX][currentY][0];
            int tempY = parent[currentX][currentY][1];
            currentX = tempX;
            currentY = tempY;
        }


        this.pathPositions = (List) path;
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
                    int newH = calculateHeuristic(nx, ny,pathPositions);


                    openList.add(new PathNode(nx, ny, newG, newH));


                    parent[nx][ny][0] = cx;
                    parent[nx][ny][1] = cy;
                }
            }
        }


        hasTarget = false;
    }

    private void markPathOnMaze() {
        for (int[] pos : pathPositions) {

            if (Maze.maze[pos[0]][pos[1]] == ' ') {
                Maze.placeElement(pos[0], pos[1], '.', cn);
            }
        }
    }

    public void findRandomTarget() {
        if (hasTarget) {
            return;
        }
        clearPathMarkers();
        this.pathPositions = new  LinkedList<>();
        this.currentPathIndex = 0;
        List<int[]> treasures = findAllTreasures();
        if (treasures.isEmpty()) {
            hasTarget = false;
            return;
        }
        int randomIndex = random.nextInt(treasures.size());
        int[] target = treasures.get(randomIndex);
        targetX = target[0];
        targetY = target[1];
        hasTarget = true;
        findPathToTarget();
        markPathOnMaze();
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
        do {
            robotPos = Maze.findRandomEmptyPosition();
        } while (Math.abs(robotPos[0] - playerPos[0]) + Math.abs(robotPos[1] - playerPos[1]) < 10);

        cRobot = new CRobot(robotPos[0], robotPos[1], cn);
    } //hata var düzeltilmesi gerek

}
