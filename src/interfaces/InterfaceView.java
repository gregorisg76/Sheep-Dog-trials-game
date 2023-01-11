package interfaces;

/**
 * Methods that must be implemented by a view are defined in this interface.
 */

public interface InterfaceView {

    // Display methods for various information.
    public void displayWelcomeMessage();
    public void displayMoveCount(int moveCount);
    public void displayChosenMove(char move);
    public void displayRejectedMoveMessage(char move);
    public void displayGameState(byte gameState);
    public void displayBoard(InterfaceModel model);


    // Request input methods.
    public char requestMenuSelection();
    public char requestMove();
}
