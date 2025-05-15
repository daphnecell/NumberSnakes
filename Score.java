import enigma.core.Enigma;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Score {

    public static int playerScore = 0;
    public static int computerScore = 0;
    public static int playerEnergy = 500;
    public static int playerLife = 1000;
    public static int playerTraps = 0;


    public static long gameStartTime;
    public static int elapsedSeconds = 0;
    public static int tickCounter = 0;
    public static int robotMoveCounter = 0;
    public static int trapDuration = 100;



    public enigma.console.Console cn = Enigma.getConsole("Score");

    public static void updateGameInfoDisplay(enigma.console.Console cn) {

        cn.getTextWindow().setCursorPosition(57, 7);
        cn.getTextWindow().output("Time    : " + String.format("%4d", elapsedSeconds));


        cn.getTextWindow().setCursorPosition(57, 10);
        cn.getTextWindow().output("--- Player ---");

        cn.getTextWindow().setCursorPosition(57, 11);
        cn.getTextWindow().output("Energy : " + String.format("%4d", playerEnergy));

        cn.getTextWindow().setCursorPosition(57, 12);
        cn.getTextWindow().output("Life   : " + String.format("%4d", playerLife));

        cn.getTextWindow().setCursorPosition(57, 13);
        cn.getTextWindow().output("Trap   : " + String.format("%4d", playerTraps));

        cn.getTextWindow().setCursorPosition(57, 14);
        cn.getTextWindow().output("Score  : " + String.format("%4d", playerScore));


        cn.getTextWindow().setCursorPosition(57, 16);
        cn.getTextWindow().output("---Computer---");

        cn.getTextWindow().setCursorPosition(57, 17);
        cn.getTextWindow().output("S Robot: " + String.format("%4d", 0));

        cn.getTextWindow().setCursorPosition(57, 18);
        cn.getTextWindow().output("Score  : " + String.format("%4d", computerScore));
    }

    public static void updateElapsedTime() {
        long currentTime = System.currentTimeMillis();
        elapsedSeconds = (int) ((currentTime - gameStartTime) / 1000);
    }

    public static void showGameOver(enigma.console.Console cn) {
        cn.getTextWindow().setCursorPosition(25, 12);
        cn.getTextWindow().output("GAME OVER", new TextAttributes(Color.RED));


        cn.getTextWindow().setCursorPosition(20, 14);
        cn.getTextWindow().output("Player Score: " + playerScore);

        cn.getTextWindow().setCursorPosition(20, 15);
        cn.getTextWindow().output("Computer Score: " + computerScore);


        cn.getTextWindow().setCursorPosition(25, 17);
        if (playerScore > computerScore) {
            cn.getTextWindow().output("YOU WIN!", new TextAttributes(Color.GREEN));
        } else if (computerScore > playerScore) {
            cn.getTextWindow().output("YOU LOSE!", new TextAttributes(Color.RED));
        } else {
            cn.getTextWindow().output("IT'S A TIE!", new TextAttributes(Color.YELLOW));
        }
    }

    public static void collectItem(int x, int y) {
        char item = Maze.maze[x][y];

        switch (item) {
            case '1':
                playerScore += 1;
                playerEnergy += 50;
                break;
            case '2':
                playerScore += 4;
                playerEnergy += 150;
                break;
            case '3':
                playerScore += 16;
                playerEnergy += 250;
                break;
            case '@':
                playerTraps++;
                break;
        }
    }




}
