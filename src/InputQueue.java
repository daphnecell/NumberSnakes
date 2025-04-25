import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InputQueue {
    static int per_treasure1 = 50;
    static int per_treasure2 = 25;
    static int per_treasure3 = 13;
    static int per_treasureAD = 9;
    static int per_treasureS = 3;

    int playerscore_1 = 1;
    int playerscore_2 = 4;
    int playerscore_3 = 16;
    int playerscore_S = 200;

    int player_energy_1 = 50;
    int player_energy_2 = 150;
    int player_energy_3 = 250;
    int player_energy_S = 500;

    int AIscore_1 = 50;
    int AIscore_2 = 150;
    int AIscore_3 = 250;
    int AIscore_AD = 500;

    List<String> input = new ArrayList<>();
    CircularQueue inputQueue = new CircularQueue(100);

    static void createInputQueue(int per_treasure1,int per_treasure2,int per_treasure3,int per_treasureAD,int per_treasureS, LinkedList<String> input, CircularQueue inputQueue) {
        for (int i = 0; i < per_treasure1; i++) {
            input.add("1");
        }
        for (int i = 0; i < per_treasure2; i++) {
            input.add("2");
        }
        for (int i = 0; i < per_treasure3; i++) {
            input.add("3");
        }
        for (int i = 0; i < per_treasureAD; i++) {
            input.add("@");
        }
        for (int i = 0; i < per_treasureS; i++) {
            input.add("S");
        }

        Collections.shuffle(input);

        int size = input.size();
        while (!input.isEmpty()){
            inputQueue.enqueue(input.remove());
        }

    }

    public static void printInputQueueToBoard(){

    }

    public static void printTreasuresToBoard(){

    }



    public static void main(String[] args) {
        LinkedList<String> input = new LinkedList<>();
        CircularQueue inputQueue = new CircularQueue(100);
        createInputQueue(per_treasure1,per_treasure2,per_treasure3, per_treasureAD, per_treasureS, input, inputQueue);
    }

}
