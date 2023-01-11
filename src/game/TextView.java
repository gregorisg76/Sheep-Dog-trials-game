package game;

import interfaces.InterfaceModel;
import interfaces.InterfaceView;
import util.Util;
import animals.Animals;

/**
 * This class implements InterfaceView and is used to interact with the player.
 */
public class TextView implements InterfaceView
{
    @Override
    // Displays welcome message.
    public void displayWelcomeMessage()
    {
        System.out.println("Welcome to a game of Sheepdog trials!");
    }

    @Override
    // Requests a move by the user and returns a single char containing the move entered.
    public char requestMove()
    {
        System.out.println("Please enter your next move:");
        return Util.readChar();
    }

    @Override
    // Requests selection from menu options, and returns a single char representing the user's decision input.
    public char requestMenuSelection()
    {
        // Print options of menu.
        System.out.println("\n-------- MENU --------");
        System.out.println("(1) Start a new game with a fixed setup.");
        System.out.println("(2) Start a new game with a random setup.");

        // Requests and returns user decision input.
        System.out.print("Select an option and press enter or use any other key to quit:");
        return Util.readChar();
    }

    @Override
    // Displays the state of the game.
    public void displayGameState(byte gameState)
    {
        switch (gameState) {
            case InterfaceModel.GAME_IN_PROGRESS -> System.out.println("The game is in progress.");
            case InterfaceModel.GAME_WON -> System.out.println("Victory! You won!\nSelect an option to " +
                    "play again.");
            case InterfaceModel.CONCEDE_MOVE -> System.out.println("You conceded.\nSelect an option to " +
                    "play again.");
            default -> System.out.println("Error: Unknown state of game");
        }
    }

    @Override
    // Displays the move chosen by the player.
    public void displayChosenMove(char move)
    {
        if (move == 'w')
        {
            System.out.println("The dog moved up.");
        }
        else if (move == 's')
        {
            System.out.println("The dog moved down.");
        }
        else if (move == 'a')
        {
            System.out.println("The dog moved left.");
        }
        else if (move == 'd')
        {
            System.out.println("The dog moved right.");
        }
    }
    @Override
    // Displays an error message if the move of the player is rejected.
    public void displayRejectedMoveMessage(char move)
    {
        System.err.println("The move '" + move + "' was rejected, please try again.");
        System.err.println("To move the dog up, enter 'w' and press enter. Use 's' for down, 'a' " +
                "for left and 'd' for right. ");
        System.err.println("The dog cannot go out of bounds, into the pen or onto a square occupied by a sheep.");
        System.err.println("If you want to concede and get out of the current game, please use 'o'.");
        System.err.println();
    }

    @Override
    // Displays the game's board.
    // "_" represents a grass cell
    // "D" represents a dog cell
    // "S" represents a sheep cell
    // "P" represents a pen cell.
    public void displayBoard(InterfaceModel model)
    {
        System.out.println("\n-GAME STATE-");

        int nrBoardRows = model.getSettings().getNrBoardRows();
        int nrBoardCols = model.getSettings().getNrBoardCols();

        for(int i=0; i<nrBoardRows; i++)
        {

            for (int j = 0; j < nrBoardCols; j++)
            {
                if (model.getCellInfo(i, j) == Animals.GRASS_CELL_ID)
                {
                    System.out.print("_");
                }
                else if (model.getCellInfo(i, j) == Animals.DOG_ID)
                {
                    System.out.print("D");
                }
                else  if (model.getCellInfo(i, j) == Animals.SHEEP_ID)
                {
                    System.out.print("S");
                }
                else  if (model.getCellInfo(i, j) == Animals.PEN_CELL_ID)
                {
                    System.out.print("P");
                }
            }
            System.out.println();
        }
    }

    @Override
    // Displays the number of moves made by the player so far.
    public void displayMoveCount(int moveCount)
    {
        System.out.println();
        System.out.println("Number of moves: " + moveCount);
    }

}