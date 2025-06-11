import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.Random;

public class Maze {
    public static char[][] maze;
    private static enigma.console.Console cn = Enigma.getConsole("Number Snakes");

    public Maze(String path) {
        maze = new char[23][55];
        loadMaze(path);
    }

    private void loadMaze(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < 23) {
                for (int col = 0; col < 55 && col < line.length(); col++) {
                    maze[row][col] = line.charAt(col);
                }
                row++;
            }
        } catch (IOException e) {
            System.out.println("Error reading maze file: " + e.getMessage());
            initializeDefaultMaze();
        }
    }

    private void initializeDefaultMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (i == 0 || i == maze.length - 1 || j == 0 || j == maze[0].length - 1) {
                    maze[i][j] = '#';
                } else {
                    maze[i][j] = ' ';
                }
            }
        }

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(maze.length - 2) + 1;
            int y = random.nextInt(maze[0].length - 2) + 1;
            maze[x][y] = '#';
        }
    }

    public static void printMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                char ch = maze[i][j];

                if (ch == 'P') {
                    cn.getTextWindow().output(j, i, ch, new TextAttributes(Color.GREEN));
                } else if (ch == 'C') {
                    cn.getTextWindow().output(j, i, ch, new TextAttributes(Color.RED));
                } else if (ch == 'S') {
                    cn.getTextWindow().output(j, i, ch, new TextAttributes(Color.MAGENTA));
                } else if (ch == '1' || ch == '2' || ch == '3') {
                    cn.getTextWindow().output(j, i, ch, new TextAttributes(Color.YELLOW));
                } else if (ch == '@') {
                    cn.getTextWindow().output(j, i, ch, new TextAttributes(Color.CYAN));
                } else if (ch == '=') {
                    cn.getTextWindow().output(j, i, ch, new TextAttributes(Color.ORANGE));
                } else if (ch == '.') {
                    cn.getTextWindow().output(j, i, ch, new TextAttributes(Color.LIGHT_GRAY));
                } else {
                    cn.getTextWindow().output(j, i, ch);
                }
            }
        }
    }

    public static boolean isValidPosition(int x, int y) {
        return x >= 0 && x < maze.length && y >= 0 && y < maze[0].length && maze[x][y] != '#';
    }

    public static boolean isTreasure(int x, int y) {
        char item = maze[x][y];
        return item == '1' || item == '2' || item == '3' || item == '@';
    }

    public static int[] findRandomEmptyPosition() {
        Random random = new Random();
        int x, y;

        do {
            x = random.nextInt(maze.length);
            y = random.nextInt(maze[0].length);
        } while (maze[x][y] != ' ');

        return new int[]{x, y};
    }

    public static SingleLinkedList findAllTreasures() {
        SingleLinkedList treasures = new SingleLinkedList();

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                char cell = maze[i][j];
                if (cell == '1' || cell == '2' || cell == '3') {
                    treasures.addNode(new int[]{i, j});
                }
            }
        }

        return treasures;
    }

    public static boolean isTrap(int x, int y) {
        return maze[x][y] == '=';
    }

    public static void placeElement(int x, int y, char element) {
        if (isValidPosition(x, y)) {
            maze[x][y] = element;

            TextAttributes attr = new TextAttributes(Color.WHITE);
            switch (element) {
                case 'P': attr = new TextAttributes(Color.GREEN); break;
                case 'C': attr = new TextAttributes(Color.RED); break;
                case 'S': attr = new TextAttributes(Color.MAGENTA); break;
                case '1': case '2': case '3': attr = new TextAttributes(Color.YELLOW); break;
                case '@': attr = new TextAttributes(Color.CYAN); break;
                case '=': attr = new TextAttributes(Color.ORANGE); break;
                case '.': attr = new TextAttributes(Color.LIGHT_GRAY); break;
            }

            cn.getTextWindow().output(y, x, element, attr);
        }
    }

    public static void clearPosition(int x, int y) {
        if (x >= 0 && x < maze.length && y >= 0 && y < maze[0].length) {
            maze[x][y] = ' ';
            cn.getTextWindow().output(y, x, ' ');
        }
    }

    public static Stack findPath(int startX, int startY, int targetX, int targetY) {
        Stack pathStack = new Stack(maze.length * maze[0].length);

        if (startX == targetX && startY == targetY) {
            return pathStack;
        }

        boolean[][] visited = new boolean[maze.length][maze[0].length];
        int[][][] parent = new int[maze.length][maze[0].length][2];

        // Basit PriorityQueue implementasyonu - SingleLinkedList tabanlı
        SimplePriorityQueue openList = new SimplePriorityQueue();

        openList.add(new PathNode(startX, startY, 0, calculateHeuristic(startX, startY, targetX, targetY)));

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
                reconstructPath(parent, cx, cy, startX, startY, pathStack);
                return pathStack;
            }

            for (int[] dir : directions) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];

                if (isValidPosition(nx, ny) && !visited[nx][ny]) {
                    int newG = current.getG() + 1;
                    int newH = calculateHeuristic(nx, ny, targetX, targetY);

                    openList.add(new PathNode(nx, ny, newG, newH));

                    parent[nx][ny][0] = cx;
                    parent[nx][ny][1] = cy;
                }
            }
        }

        return pathStack;
    }

    private static int calculateHeuristic(int x, int y, int targetX, int targetY) {
        return Math.abs(x - targetX) + Math.abs(y - targetY);
    }

    private static void reconstructPath(int[][][] parent, int x, int y, int startX, int startY, Stack pathStack) {
        Stack tempStack = new Stack(maze.length * maze[0].length);

        int currentX = x;
        int currentY = y;

        while (!(currentX == startX && currentY == startY)) {
            tempStack.push(new int[]{currentX, currentY});

            int tempX = parent[currentX][currentY][0];
            int tempY = parent[currentX][currentY][1];
            currentX = tempX;
            currentY = tempY;
        }

        while (!tempStack.isEmpty()) {
            pathStack.push(tempStack.pop());
        }
    }

    // SingleLinkedList tabanlı basit PriorityQueue
    private static class SimplePriorityQueue {
        private SingleLinkedList list = new SingleLinkedList();

        public void add(PathNode item) {
            if (list.head == null) {
                list.addNode(item);
                return;
            }

            // En küçük F değerine sahip elemanı bul ve önüne ekle
            Node current = list.head;
            Node previous = null;

            while (current != null &&
                    item.getF() >= ((PathNode) current.getData()).getF()) {
                previous = current;
                current = current.getLink();
            }

            Node newNode = new Node(item);
            if (previous == null) {
                // Başa ekle
                newNode.setLink(list.head);
                list.head = newNode;
            } else {
                // Ortaya ekle
                newNode.setLink(current);
                previous.setLink(newNode);
            }
        }

        public PathNode poll() {
            if (isEmpty()) return null;
            PathNode result = (PathNode) list.head.getData();
            list.head = list.head.getLink();
            return result;
        }

        public boolean isEmpty() {
            return list.head == null;
        }
    }

    public static class PathNode implements Comparable<PathNode> {
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