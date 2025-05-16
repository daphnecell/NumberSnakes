import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

public class CRobot {
    private int x;
    private int y;
    private int targetX;
    private int targetY;
    private boolean hasTarget;
    private Random random;
    private enigma.console.Console cn;


    private List<int[]> pathPositions;
    private int currentPathIndex;


    public CRobot(int startX, int startY, enigma.console.Console console) {
        this.x = startX;
        this.y = startY;
        this.cn = console;
        this.random = new Random();
        this.hasTarget = false;
        this.pathPositions = new LinkedList<>();
        this.currentPathIndex = 0;


        Maze.placeElement(x, y, 'C');
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


    public void findRandomTarget() {

        if (hasTarget) {
            return;
        }


        clearPathMarkers();


        this.pathPositions = new LinkedList<>();
        this.currentPathIndex = 0;


        List<int[]> treasures = Maze.findAllTreasures();


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


    private void findPathToTarget() {

        boolean[][] visited = new boolean[Maze.maze.length][Maze.maze[0].length];


        int[][][] parent = new int[Maze.maze.length][Maze.maze[0].length][2];


        PriorityQueue<PathNode> openList = new PriorityQueue<>();


        openList.add(new PathNode(x, y, 0, calculateHeuristic(x, y)));


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


                if (isValidPosition(nx, ny) && !visited[nx][ny]) {

                    int newG = current.getG() + 1;
                    int newH = calculateHeuristic(nx, ny);


                    openList.add(new PathNode(nx, ny, newG, newH));


                    parent[nx][ny][0] = cx;
                    parent[nx][ny][1] = cy;
                }
            }
        }


        hasTarget = false;
    }


    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < Maze.maze.length && y >= 0 && y < Maze.maze[0].length
                && Maze.maze[x][y] != '#';
    }


    private int calculateHeuristic(int x, int y) {
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


        this.pathPositions = path;
    }


    private void markPathOnMaze() {
        for (int[] pos : pathPositions) {

            if (Maze.maze[pos[0]][pos[1]] == ' ') {
                Maze.placeElement(pos[0], pos[1], '.');
            }
        }
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


        Maze.clearPosition(x, y);


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


        Maze.placeElement(x, y, 'C');
    }


    private boolean isTreasureAtPosition(int x, int y) {
        char cell = Maze.maze[x][y];
        return cell == '1' || cell == '2' || cell == '3' || cell == '@';
    }


    private void collectTreasureByType(char treasureType) {
        switch (treasureType) {
            case '1':
                Main.computerScore += 1;
                break;
            case '2':
                Main.computerScore += 4;
                break;
            case '3':
                Main.computerScore += 16;
                break;
            case '@':
                Main.computerScore += 50;
                break;
        }
    }


    private void collectTreasure() {
        if (isTreasureAtPosition(x, y)) {
            char treasureType = Maze.maze[x][y];
            collectTreasureByType(treasureType);
        }
    }


    private void clearPathMarkers() {
        for (int i = 0; i < Maze.maze.length; i++) {
            for (int j = 0; j < Maze.maze[0].length; j++) {
                if (Maze.maze[i][j] == '.') {
                    Maze.clearPosition(i, j);
                }
            }
        }
    }


    public boolean isAdjacentToPlayer(int playerX, int playerY) {
        return (Math.abs(x - playerX) + Math.abs(y - playerY)) == 1;
    }


    private class PathNode implements Comparable<PathNode> {
        private int x;
        private int y;
        private int g;
        private int h;

        public PathNode(int x, int y, int g, int h) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getG() {
            return g;
        }

        public int getH() {
            return h;
        }

        public int getF() {
            return g + h;
        }

        @Override
        public int compareTo(PathNode other) {
            return Integer.compare(this.getF(), other.getF());
        }
    }
}