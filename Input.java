package calculator;

import java.util.Scanner;


public class Input {

    private static Scanner scanner = new Scanner(System.in);


    String getInput() {



        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals(""))
                return "";
            return line;

        }

        return "";

    }
}
