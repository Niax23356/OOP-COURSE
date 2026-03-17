import java.util.ArrayList;
import java.util.Scanner;

public class GameHelper {
    private static final String alphabet = "abcdefg";
    private int[] comArray = new int[49];
    private int gridLength = 7;
    private int gridSize = 49;
    private int comCount = 0;
    private Scanner scanner = new Scanner(System.in);

    public String getUserInput(String prompt) {
        System.out.print(prompt + " ");
        String input = scanner.nextLine();
        return input.toLowerCase();
    }

    public ArrayList<String> placeDotCom(int comSize) {
        ArrayList<String> alphaCells = new ArrayList<String>();
        String[] coords = new String[comSize];
        String temp = null;
        int[] coord = new int[comSize];
        int attempts = 0;
        boolean success = false;
        int location = 0;

        comCount++;
        int incr = 1;

        if ((comCount % 2) == 1) {
            incr = gridLength; // vertical
        }

        while (!success & attempts++ < 200) {
            location = (int) (Math.random() * gridSize);
            int x = 0;
            success = true;

            while (success && x < comSize) {
                coord[x] = location;

                if (location >= gridSize) {
                    success = false;
                    break;
                }

                if (x > 0 && (coord[x] % gridLength == 0) && incr == 1) {
                    success = false;
                    break;
                }

                if (comArray[coord[x]] == 1) {
                    success = false;
                    break;
                }

                location += incr;
                x++;
            }
        }

        int x = 0;
        int row;
        int column;

        while (x < comSize) {
            comArray[coord[x]] = 1;
            row = coord[x] / gridLength;
            column = coord[x] % gridLength;
            temp = String.valueOf(alphabet.charAt(row)) + column;
            alphaCells.add(temp);
            x++;
        }

        return alphaCells;
    }
}