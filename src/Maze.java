import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import enigma.core.Enigma;

public class Maze {
    public static char[][] maze;
    private static int playerX;
    private static int playerY;
    private enigma.console.Console cn = Enigma.getConsole("Maze Game");

    public Maze(String path) {
        maze = new char[23][55];
        playerX = 11;
        playerY = 27;
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

    public static void printMaze(enigma.console.Console cn) {
        for (int i = 0; i < 23; i++) {
            for (int j = 0; j < 55; j++) {
                cn.getTextWindow().setCursorPosition(j, i);
                if (i == playerX && j == playerY) {
                    cn.getTextWindow().output('@');
                } else {
                    cn.getTextWindow().output(maze[i][j]);
                }
            }
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
                System.out.println("Geçersiz tuş");
                return;
        }


        if (newX >= 0 && newX < 23 && newY >= 0 && newY < 55 && maze[newX][newY] != '#') {


            cn.getTextWindow().output(playerY, playerX, ' ');


            playerX = newX;
            playerY = newY;


            cn.getTextWindow().output(playerY, playerX, '@');
        }
    }
}