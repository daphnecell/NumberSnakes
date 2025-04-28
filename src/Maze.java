import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Maze {
    private char[][] maze;
    private int playerX;
    private int playerY;

    public Maze(String path) {
        maze = new char[23][55];
        playerX = 11; // Başlangıç X
        playerY = 27; // Başlangıç Y
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
            System.out.println("Dosya okunurken hata oluştu: " + e.getMessage());
        }
    }

    public void printMaze() {
        for (int i = 0; i < 23; i++) {
            for (int j = 0; j < 55; j++) {
                if (i == playerX && j == playerY) {
                    System.out.print('@'); // Oyuncu burada
                } else {
                    System.out.print(maze[i][j]);
                }
            }
            System.out.println();
        }
    }

    public void movePlayer(char direction) {
        int newX = playerX;
        int newY = playerY;

        switch (direction) {
            case 'W': newX--; break;
            case 'S': newX++; break;
            case 'A': newY--; break;
            case 'D': newY++; break;
            default:
                System.out.println("Geçersiz tuş!");
                return;
        }

        // Sınır kontrolü
        if (newX >= 0 && newX < 23 && newY >= 0 && newY < 55 && maze[newX][newY] != '#') {
            playerX = newX;
            playerY = newY;
        } else {
            System.out.println("Gidemezsin oraya!");
        }
    }
}
