package game;

import interfaces.InterfaceView;
import interfaces.InterfaceModel;
import util.GameSettings;

/**
 * This class controls how the game flows.
 */
public class Controller {

    // References to communicate with view
    private final InterfaceView view;

    // The current game settings.
    private GameSettings settings;

    // The constructor creates view and loads default settings.
    public Controller()
    {
        this.view = new TextView();
        settings = new GameSettings();
    }

    // Starts a session, which can consist of multiple games and starts a new game, as required by the user.
    public void startSession()
    {
        view.displayWelcomeMessage();
        do
        {
            char c = view.requestMenuSelection();
            if (c == '1')
            {
                startNewGame();
            }
            else if (c == '2')
            {
                startRandomNewGame();
            }
            else
            {
                return;
            }
        }
        while(true);
    }

    // Starts a new game with the fixed setup.
    private void startNewGame()
    {
    	FixedModel model = new FixedModel(settings);
        model.initNewGame(settings);
        startMatchLoop(model);
    }

    // Starts a new game with a random setup.
    private void startRandomNewGame()
    {
    	RandomModel model = new RandomModel(settings);
        model.initNewGame(settings);
        startMatchLoop(model);
    }

    // Starts a match loop.
    // The user is asked to make moves until there is a victory or until the player concedes.
    // The state of the board of the game is displayed when the dog moves.
    private void startMatchLoop(Model model)
    {
        byte gameState;
        gameState = model.getGameState();

        // Display the message corresponding to the initial game state.
        view.displayGameState(gameState);

        // Display the initial state of the board.
        view.displayBoard(model);

        // Initialize a counter of the moves made so far.
        int moveCount = 0;

        while((gameState = model.getGameState()) == InterfaceModel.GAME_IN_PROGRESS)
        {
            // Ask the player to make a valid move.
            char move = askForValidMove(model);

            // Display the move chosen by the player.
            view.displayChosenMove(move);

            // Implement the move chosen.
            model.makeMove(move);

            // If the player does not concede, implement sheep behaviour according to the dog's move and
            // display the updated state of the board. Also add 1 to the counter of moves made so far.
            if (move != 'o')
            {
                model.sheepBehaviour();
                view.displayBoard(model);
                moveCount += 1;
            }

            // Display the number of moves done so far.
            view.displayMoveCount(moveCount);

        }

        // As the game has ended, display the outcome.
        view.displayGameState(gameState);
    }

    // Ask the player to submit a move command.
    // Ask for a valid move until a move accepted by the model is entered.
    private char askForValidMove(Model model)
    {
        char move;
        boolean moveRejected;

        // Ask for a move until a valid move is submitted by the given player.
        do
        {
            // Request a move from the player.
            move = view.requestMove();

            // Check the move's validity.
            moveRejected = !model.isMoveValid(move);

            // Display error message if move is rejected and ask again for a valid move.
            if(moveRejected)
                view.displayRejectedMoveMessage(move);
        }
        while(moveRejected);
        return move;
    }
}
