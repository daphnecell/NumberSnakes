import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import enigma.core.Enigma;

public class PrinMazeConsole {

    public static void main(String[] args) {
        LinkedList<String> lines = new LinkedList<>();
        enigma.console.Console cn = Enigma.getConsole("maze");

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/maze.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Dosya okunamadı: " + e.getMessage());
        }

        for (String satir : lines) {
            System.out.println(satir);
        }
    }
}
