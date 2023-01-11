package util;

import java.util.Scanner;

/**
 * This class provides methods to be used by other classes such as single character input method
 * and method calculating the Euclidean distance between 2 points.
 */

public class Util
{
    // Creates a scanner to be used.
    private static final Scanner scanner = new Scanner(System.in);

    // Waits for user to type a single character on input by printing an error message if the input is not
    // of the correct type, and asks for input again. It returns a single char entered by the user
    public static char readChar()
    {
        do
        {
            if(scanner.hasNextLine())
            {
                // Obtain the user input.
                String input = scanner.nextLine();

                // If a single char was entered, return it.
                if(input.length() == 1)
                    return input.charAt(0);
            }

            // If the input length is invalid, print an error and repeat.
            System.err.print("Please enter a single character: ");
        }
        while(true);
    }

    // Calculates the Euclidean distance between 2 specified points. Returns a double containing the distance.
    public static double euclideanDistance(int row1, int col1, int row2, int col2)
    {
        // Returns Euclidean distance formula result.
        return Math.sqrt((row2 - row1) * (row2 - row1) +
                (col2 - col1) * (col2 - col1));
    }
}
