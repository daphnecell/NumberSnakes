public class PathNode implements Comparable<PathNode> {
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