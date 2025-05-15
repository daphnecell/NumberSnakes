import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import enigma.core.Enigma;

public class PrinMazeConsole {

    public static void main(String[] args) {
        SingleLinkedList lines = new SingleLinkedList();
        enigma.console.Console cn = Enigma.getConsole("maze");

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/maze.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                lines.addNode(line);
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Dosya okunamadı: " + e.getMessage());
        }


        Node current = lines.head;
        while (current != null) {
            System.out.println(current.getData());
            current = current.getLink();
        }
    }
}