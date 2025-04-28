import enigma.core.Enigma;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static char lastInput = ' ';
    public static void main(String[] args)  {
        enigma.console.Console cn = Enigma.getConsole("snake");
        Maze maze = new Maze("C:\\Users\\ARDA-PC\\IdeaProjects\\pbl2.2\\src\\maze.txt");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            maze.printMaze();
            System.out.println("Hareket için W/A/S/D tuşla:");

            char input = scanner.nextLine().toUpperCase().charAt(0);

            maze.movePlayer(input);
        }
    }
}
