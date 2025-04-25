import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BoardFile {
    public static void createFileIfNotExists() {
        File file = new File("src/maze.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Dosya oluşturulamadı: " + e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        createFileIfNotExists();
    }



}
