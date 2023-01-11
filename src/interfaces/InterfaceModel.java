package interfaces;

import util.GameSettings;
import animals.Sheep;
import animals.Dog;
import java.util.ArrayList;

/**
 * The methods a model must implement are defined in this interface.
 */
public interface InterfaceModel {

    // This constant is used to represent the game status when the player concedes.
    public static final byte CONCEDE_MOVE = -1;

    // Constants for the status of a game.
    public static final byte GAME_IN_PROGRESS = 0;
    public static final byte GAME_WON = 1;

    // Called when the state of the board is initialized, where all cells are grassed.
    public void initStateOfBoard();

    // Called when the list of the sheep of the game are initialized.
    public void initSheep();

    // Called when a new game is started.
    public void initNewGame(GameSettings settings);

    // Returns if the move passed by the player is valid.
    // It checks if the requested new cell of the dog is in the board's range and if it is grass
    // or if the player wants to concede.
    public boolean isMoveValid(char move);

    // Implements the move passed by the player if it is valid.
    public void makeMove(char move);

    // Implements sheep behaviour such as running away from the dog, flocking when the dog is absent
    // and reluctance to enter pen.
    public void sheepBehaviour();

    // Makes the sheep move away from the dog if possible, when the dog is approaching.
    public void sheepMoveAwayFromDog(int sheepRow, int sheepCol);

    // Makes the sheep flocking, by moving a sheep towards to its nearest sheep.
    public void sheepFlocking (int sheepRow, int sheepCol);

    // Makes the sheep reluctant to enter the pen, when it is chased by the dog. At the end
    // it returns true if the sheep didn't approach the pen, or false when it did approach the pen.
    public boolean chasedSheepReluctance(int sheepRow, int sheepCol);

    // Makes the sheep reluctant to enter the pen, when it is free and not chased by the dog. At the end,
    // it returns true if the sheep was close to the pen and didn't approach it further,
    // or false when it wasn't close to the pen thus there was no need to be reluctant to enter the pen.
    public boolean freeSheepReluctance(int sheepRow, int sheepCol);

    // Returns true if the specified cell is a grass cell or a pen cell, meaning there is no animal in the cell
    // or the cell is not outside the range of the board. Otherwise, returns false.
    public boolean isCellFreeOfAnimals(int rowToCheck, int colToCheck);

    // It makes the sheep move to the direction specified, if there is no animal, and it is inside the board there.
    public void moveSheep(int sheepRow, int sheepCol, String direction);

    // Returns true if all the sheep are in the pen. Otherwise, returns false.
    public boolean areAllSheepInPen();

    // Returns the id of the cell specified.
    // Returns 0 or 1 or 2 or 3 if the cell type is grass, dog, sheep or pen respectively.
    // It also returns 4 if a sheep just moved in the specified cell, which later it will be converted to 2 indicating
    // there is a sheep there.
    // Moreover, returns -1 if the specified coordinated are outside the board, to avoid any out of index errors.
    public byte getCellInfo(int row, int column);

    // It changes the type of the specific cell to the specified value.
    public void setCellInfo(int i, int j, byte value);

    // Getter of dog.
    public Dog getDog();

    // Setter of dog.
    public void setDog(Dog dog);

    // Getter of sheep list.
    public ArrayList<Sheep> getSheep();

    // Getter of current game settings.
    public GameSettings getSettings();

    // Setter of game settings.
    public void setSettings(GameSettings settings);

    // Getter of current game state. Returns a status indicating if this game is ongoing or has ended.
    // See above game status constants.
    public byte getGameState();

    // Setter of game state.
    public void setGameState(byte stateOfGame);
}
