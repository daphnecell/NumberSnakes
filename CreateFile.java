import java.io.File;
import java.io.IOException;


public class CreateFile {
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
