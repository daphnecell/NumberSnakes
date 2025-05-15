public class Player {
    private int x;
    private int y;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveUp() {
        x--;  // Yukarı hareket
    }

    public void moveDown() {
        x++;  // Aşağı hareket
    }

    public void moveLeft() {
        y--;  // Sol hareket
    }

    public void moveRight() {
        y++;  // Sağ hareket
    }

}
