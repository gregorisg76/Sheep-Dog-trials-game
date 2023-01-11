package game;

import util.GameSettings;
import util.Util;
import interfaces.InterfaceModel;
import animals.Animals;
import animals.Sheep;
import animals.Dog;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class is used to represent the state of the game.
 */

public class Model implements InterfaceModel
{
    private GameSettings settings;
    private byte[][] stateOfBoard;
    private byte stateOfGame;
    private Dog dog;
    private ArrayList<Sheep> sheep;

    // The constructor loads default settings, creates a new dog and initializes the sheep.
    public Model(GameSettings settings)
    {
    	this.settings = settings;
    	setDog(new Dog(0));
        initSheep();
    }

    @Override
    // Called when the state of the board is initialized, where all cells are grass.
    public void initStateOfBoard()
    {
    	int i = getSettings().getNrBoardRows();
    	int j = getSettings().getNrBoardCols();
    	stateOfBoard = new byte[i][j];
    	for (int n = 0; n < i ; n++)
        {
            for (int k = 0; k < j; k++)
            {
                this.stateOfBoard[n][k] = Animals.GRASS_CELL_ID;
            }
        }
    }

    @Override
    // Called when the list of the sheep of the game are initialized.
    public void initSheep()
    {
        sheep = new ArrayList<>();
	}

    @Override
    // Called when a new game is started.
    public void initNewGame(GameSettings settings)
    {
    	setSettings(settings);
        initStateOfBoard();
    }

    @Override
    // Returns if the move passed by the player is valid.
    // It checks if the requested new cell of the dog is in the board's range and if it is grass
    // or if the player wants to concede.
    public boolean isMoveValid(char move)
    {
        if (move == 'w')
        {
            return dog.getRow() - 1 >= 0 &&
                    this.stateOfBoard[dog.getRow() - 1][dog.getCol()] == Animals.GRASS_CELL_ID;
        }
        else if (move == 's')
        {
            return dog.getRow() + 1 < settings.nrBoardRows &&
                    this.stateOfBoard[dog.getRow() + 1][dog.getCol()] == Animals.GRASS_CELL_ID;
        }
        else if (move == 'a')
        {
            return dog.getCol() - 1 >= 0 &&
                    this.stateOfBoard[dog.getRow()][dog.getCol() - 1] == Animals.GRASS_CELL_ID;
        }
        else if (move == 'd')
        {
            return dog.getCol() + 1 < settings.nrBoardCols &&
                    this.stateOfBoard[dog.getRow()][dog.getCol() + 1] == Animals.GRASS_CELL_ID;
        }
        else return move == 'o';
    }

    @Override
    // Implements the move passed by the player if it is valid.
    public void makeMove(char move)
    {
        if (move != 'o')
        {
            this.stateOfBoard[dog.getRow()][dog.getCol()] = 0;
            if (move == 'w')
            {
                dog.setDogRow(move);
                this.stateOfBoard[dog.getRow()][dog.getCol()] = 1;
            }
            else if (move == 's')
            {
                dog.setDogRow(move);
                this.stateOfBoard[dog.getRow()][dog.getCol()] = 1;
            }
            else if (move == 'a')
            {
                dog.setDogColumn(move);
                this.stateOfBoard[dog.getRow()][dog.getCol()] = 1;
            }
            else if (move == 'd')
            {
                dog.setDogColumn(move);
                this.stateOfBoard[dog.getRow()][dog.getCol()] = 1;
            }
        }
        else
        {
            this.stateOfGame = InterfaceModel.CONCEDE_MOVE;
        }
    }

    @Override
    // Implements sheep behaviour such as running away from the dog, flocking when the dog is absent
    // and reluctance to enter pen.
    public void sheepBehaviour()
    {
        // Check for each sheep outside the pen, if the euclidean distance from the dog is less than 5.
        double sheepToDogDistance;
        for (int i = 0; i < this.settings.getNrBoardRows(); i++)
        {
            for (int j = 0; j < this.settings.getNrBoardCols(); j++)
            {
                if (this.stateOfBoard[i][j] == Animals.SHEEP_ID)
                {
                    sheepToDogDistance = Util.euclideanDistance(dog.getRow(), dog.getCol(), i, j);
                    if (sheepToDogDistance < 5)
                    {
                        // Makes the sheep move away from the dog and if sheep is close to the pen,
                        // it will be reluctant to enter it.
                        sheepMoveAwayFromDog(i, j);
                    }
                    else
                    {
                        // Makes the sheep start flocking, by approaching the sheep nearest. If the sheep is close
                        // to the pen it might just try to get away from it.
                        sheepFlocking(i,j);
                    }
                }
            }
        }

        // Since the new cells of the sheep are stored with another ID, convert all the updated sheep IDs
        // to the original sheep ID.
        for (int i = 0; i < this.settings.getNrBoardRows(); i++)
        {
            for (int j = 0; j < this.settings.getNrBoardCols(); j++)
            {
                if (this.stateOfBoard[i][j] == Animals.UPDATED_SHEEP_ID)
                {
                    this.stateOfBoard[i][j] = Animals.SHEEP_ID;
                }
            }
        }

        // Checks if all the sheep are now in the pen to set the state of the game accordingly.
        if (areAllSheepInPen())
        {
            setGameState(InterfaceModel.GAME_WON);
        }
    }

    @Override
    // Makes the sheep move away from the dog if possible, when the dog is approaching.
    public void sheepMoveAwayFromDog(int sheepRow, int sheepCol)
    {
        // Make the sheep try to get away from the dog if the sheep is not trying to avoid the pen.
        // Takes into consideration the position of the dog relative to the sheep and move the sheep
        // to the best available cell next to it that increases the distance from the dog.
        // The priority of the direction of the sheep's move is clearly indicated, and for the last 4 cases
        // it is mixed to avoid the sheep making circles.
        if(!chasedSheepReluctance(sheepRow, sheepCol))
        {
            // Dog is perpendicularly below the sheep.
            if (dog.getRow() > sheepRow && dog.getCol() == sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow-1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol+1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol-1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
            }
            // Dog is perpendicularly above the sheep.
            else if (dog.getRow() < sheepRow && dog.getCol() == sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow+1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol-1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol+1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
            }
            // Dog is perpendicularly to the right of the sheep.
            else if (dog.getRow() == sheepRow && dog.getCol() > sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol-1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
                else if (isCellFreeOfAnimals(sheepRow-1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
                else if (isCellFreeOfAnimals(sheepRow+1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
            }
            // Dog is perpendicularly to the left of the sheep.
            else if (dog.getRow() == sheepRow && dog.getCol() < sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol+1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
                else if (isCellFreeOfAnimals(sheepRow+1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
                else if (isCellFreeOfAnimals(sheepRow-1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
            }
            // Dog is above and to the left of the sheep.
            else if (dog.getRow() < sheepRow && dog.getCol() < sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol+1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
                else if (isCellFreeOfAnimals(sheepRow+1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
            }
            // Dog is above and to the right of the sheep.
            else if (dog.getRow() < sheepRow && dog.getCol() > sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow+1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol-1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
            }
            // Dog is below and to the left of the sheep.
            else if (dog.getRow() > sheepRow && dog.getCol() < sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow-1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol+1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
            }
            // Dog is below and to the right of the sheep.
            else if (dog.getRow() > sheepRow && dog.getCol() > sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol-1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
                else if (isCellFreeOfAnimals(sheepRow-1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
            }
        }
    }

    @Override
    // Makes the sheep flocking, by moving a sheep towards to its nearest sheep.
    public void sheepFlocking (int sheepRow, int sheepCol)
    {
        // Make the sheep try to get closer to the nearest sheep, if the sheep is not trying to
        // avoid the pen(when it is very close to it).
        // Takes into consideration the position of the nearest sheep relative to the sheep and move the sheep
        // to the best available cell next to it that decreases the distance from the nearest sheep.
        // The priority of the direction of the sheep's move is clearly indicated.
        if(!freeSheepReluctance(sheepRow, sheepCol))
        {
            double sheepToSheepDistance;
            int nearestSheepRow = -1;
            int nearestSheepCol = -1;
            // Defined to a huge number to be easily compared the first instance.
            double smallestSheepToSheepDistance = 1000000;

            // Searches for the nearest sheep on the board and stores its row and column.
            for (int i = 0; i < this.settings.getNrBoardRows(); i++)
            {
                for (int j = 0; j < this.settings.getNrBoardCols(); j++)
                {
                    // Checks if the cell investigated is either a sheep cell or a recently moved sheep cell.
                    if (this.stateOfBoard[i][j] == Animals.SHEEP_ID ||
                            this.stateOfBoard[i][j] == Animals.UPDATED_SHEEP_ID)
                    {
                        sheepToSheepDistance = Util.euclideanDistance(sheepRow, sheepCol, i, j);
                        if (sheepToSheepDistance < smallestSheepToSheepDistance && sheepToSheepDistance != 0)
                        {
                            nearestSheepRow = i;
                            nearestSheepCol = j;
                            smallestSheepToSheepDistance = sheepToSheepDistance;
                        }
                    }
                }
            }
            // In the case the sheep is the last one outside the pen the nearest sheep
            // row will remain -1 as initialized.
            // Set an 'imaginary' sheep randomly placed in the board, so as the sheep move towards it,
            // so as not to remain at the same cell. This will create a random motion of the last sheep.
            if (nearestSheepRow == -1)
            {
                nearestSheepRow = ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardRows());
                nearestSheepCol = ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardRows());
            }
            // The nearest sheep is perpendicularly below the sheep.
            if (nearestSheepRow > sheepRow && nearestSheepCol == sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow + 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol + 1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol - 1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
            }
            // The nearest sheep is perpendicularly above the sheep.
            else if (nearestSheepRow < sheepRow && nearestSheepCol == sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow - 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol - 1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol + 1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
            }
            // The nearest sheep is perpendicularly to the right of the sheep.
            else if (nearestSheepRow == sheepRow && nearestSheepCol > sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol + 1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
                else if (isCellFreeOfAnimals(sheepRow - 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
                else if (isCellFreeOfAnimals(sheepRow + 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
            }
            // The nearest sheep is perpendicularly to the left of the sheep.
            else if (nearestSheepRow == sheepRow && nearestSheepCol < sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol - 1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
                else if (isCellFreeOfAnimals(sheepRow + 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
                else if (isCellFreeOfAnimals(sheepRow - 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
            }
            // The nearest sheep is above and to the left of the sheep.
            else if (nearestSheepRow < sheepRow && nearestSheepCol < sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol - 1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
                else if (isCellFreeOfAnimals(sheepRow - 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
            }
            // The nearest sheep is above and to the right of the sheep.
            else if (nearestSheepRow < sheepRow)
            {
                if (isCellFreeOfAnimals(sheepRow - 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "up");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol + 1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
            }
            // The nearest sheep is below and to the left of the sheep.
            else if (nearestSheepRow > sheepRow && nearestSheepCol < sheepCol)
            {
                if (isCellFreeOfAnimals(sheepRow + 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
                else if (isCellFreeOfAnimals(sheepRow, sheepCol - 1))
                {
                    moveSheep(sheepRow, sheepCol, "left");
                }
            }
            // The nearest sheep is below and to the right the sheep.
            else if (nearestSheepRow > sheepRow)
            {
                if (isCellFreeOfAnimals(sheepRow, sheepCol + 1))
                {
                    moveSheep(sheepRow, sheepCol, "right");
                }
                else if (isCellFreeOfAnimals(sheepRow + 1, sheepCol))
                {
                    moveSheep(sheepRow, sheepCol, "down");
                }
            }
        }
    }

    @Override
    // Makes the sheep reluctant to enter the pen, when it is chased by the dog. At the end
    // it returns true if the sheep didn't approach the pen, or false when it did approach the pen.
    public boolean chasedSheepReluctance(int sheepRow, int sheepCol)
    {
        boolean isReluctant;
        // The sheep is randomly assigned as reluctant or not at each turn, in order to make the game possible to win.
        // Therefore, sometimes the sheep will avoid the pen and some other times it will enter it.
        isReluctant = ThreadLocalRandom.current().nextBoolean();
        int nearestPenCellRow = -1;
        int nearestPenCellCol = -1;
        double sheepToPenDistance;
        // Defined to a huge number to be easily compared the first instance.
        double smallestSheepToPenDistance = 1000000;

        // If sheep is reluctant to enter the pen find the nearest pen cell to it and store its row and column.
        if(isReluctant)
        {
            for (int k = 0; k < this.settings.getNrBoardRows(); k++)
            {
                for (int n = 0; n < this.settings.getNrBoardCols(); n++)
                {
                    if (this.stateOfBoard[k][n] == Animals.PEN_CELL_ID)
                    {
                        sheepToPenDistance = Util.euclideanDistance(sheepRow, sheepCol, k, n);
                        if (sheepToPenDistance<smallestSheepToPenDistance)
                        {
                            nearestPenCellRow = k;
                            nearestPenCellCol = n;
                            smallestSheepToPenDistance = sheepToPenDistance;
                        }
                    }
                }
            }

            // Consider the nearest pen cell position relative to the sheep to make the best available move to
            // avoid the pen. Only consider cases that the nearest pen cell have the same row or column as the sheep.
            if(nearestPenCellCol == sheepCol || nearestPenCellRow == sheepRow)
            {
                // Be reluctant only if the smallest sheep to pen euclidean distance is less than or equal to 2.
                if (smallestSheepToPenDistance <= 2)
                {
                    // The nearest pen cell is to the left or to the right of the sheep.
                    if(nearestPenCellRow == sheepRow)
                    {
                        if (isCellFreeOfAnimals(sheepRow-1, sheepCol))
                        {
                            moveSheep(sheepRow, sheepCol, "up");
                            return true;
                        }
                        else if (isCellFreeOfAnimals(sheepRow+1, sheepCol))
                        {
                            moveSheep(sheepRow, sheepCol, "down");
                            return true;
                        }
                        // The nearest pen cell is to the left of the sheep.
                        else if (isCellFreeOfAnimals(sheepRow, sheepCol+1) &&
                                nearestPenCellCol < sheepCol)
                        {
                            moveSheep(sheepRow, sheepCol, "right");
                            return true;
                        }
                        // The nearest pen cell is to the right of the sheep.
                        else if (isCellFreeOfAnimals(sheepRow, sheepCol-1) &&
                                nearestPenCellCol > sheepCol)
                        {
                            moveSheep(sheepRow, sheepCol, "left");
                            return true;
                        }
                    }
                    // The nearest pen cell is perpendicularly above or below of the sheep.
                    // The sheep only moves parallel to the pen this case, to make the sheep a bit less reluctant,
                    // and the game easier to win.
                    else
                    {
                        if (isCellFreeOfAnimals(sheepRow, sheepCol+1))
                        {
                            moveSheep(sheepRow, sheepCol, "right");
                            return true;
                        }
                        else if (isCellFreeOfAnimals(sheepRow, sheepCol-1))
                        {
                            moveSheep(sheepRow, sheepCol, "left");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    // Makes the sheep reluctant to enter the pen, when it is free and not chased by the dog. At the end,
    // it returns true if the sheep was close to the pen and didn't approach it further,
    // or false when it wasn't close to the pen thus there was no need to be reluctant to enter the pen.
    public boolean freeSheepReluctance(int sheepRow, int sheepCol)
    {
        // In this case, the sheep since it is not chased, it is always trying to avoid the pen.
        int nearestPenCellRow = -1;
        int nearestPenCellCol = -1;
        double sheepToPenDistance;
        // Defined to a huge number to be easily compared the first instance.
        double smallestSheepToPenDistance = 1000000;

        // Find the nearest pen cell to it and store its row and column.
        for (int k = 0; k < this.settings.getNrBoardRows(); k++)
        {
            for (int n = 0; n < this.settings.getNrBoardCols(); n++)
            {
                if (this.stateOfBoard[k][n] == Animals.PEN_CELL_ID)
                {
                    sheepToPenDistance = Util.euclideanDistance(sheepRow, sheepCol, k, n);
                    if (sheepToPenDistance<smallestSheepToPenDistance)
                    {
                        nearestPenCellRow = k;
                        nearestPenCellCol = n;
                        smallestSheepToPenDistance = sheepToPenDistance;
                    }
                }

            }
        }

        // Consider the nearest pen cell position relative to the sheep to make the best available move to
        // avoid the pen. Only consider cases that the nearest pen cell have the same row or column as the sheep.
        if(nearestPenCellCol == sheepCol || nearestPenCellRow == sheepRow)
        {
            // Be reluctant only if the smallest sheep to pen euclidean distance is less than or equal to 2.
            if (smallestSheepToPenDistance <= 2)
            {
                // The nearest pen cell is to the left or to the right of the sheep.
                if(nearestPenCellRow == sheepRow)
                {
                    if (isCellFreeOfAnimals(sheepRow-1, sheepCol))
                    {
                        moveSheep(sheepRow, sheepCol, "up");
                        return true;
                    }
                    else if (isCellFreeOfAnimals(sheepRow+1, sheepCol))
                    {
                        moveSheep(sheepRow, sheepCol, "down");
                        return true;
                    }
                    // The nearest pen cell is to the left of the sheep.
                    else if (isCellFreeOfAnimals(sheepRow, sheepCol+1) &&
                            nearestPenCellCol < sheepCol)
                    {
                        moveSheep(sheepRow, sheepCol, "right");
                        return true;
                    }
                    // The nearest pen cell is to the right of the sheep.
                    else if (isCellFreeOfAnimals(sheepRow, sheepCol-1) &&
                            nearestPenCellCol > sheepCol)
                    {
                        moveSheep(sheepRow, sheepCol, "left");
                        return true;
                    }
                }
                // The nearest pen cell is perpendicularly above or below of the sheep.
                else
                {
                    if (isCellFreeOfAnimals(sheepRow, sheepCol+1))
                    {
                        moveSheep(sheepRow, sheepCol, "right");
                        return true;
                    }
                    else if (isCellFreeOfAnimals(sheepRow, sheepCol-1))
                    {
                        moveSheep(sheepRow, sheepCol, "left");
                        return true;
                    }
                    // The nearest pen cell is perpendicularly below of the sheep.
                    else if (isCellFreeOfAnimals(sheepRow-1, sheepCol) &&
                            nearestPenCellRow > sheepRow)
                    {
                        moveSheep(sheepRow, sheepCol, "up");
                        return true;
                    }
                    // The nearest pen cell is perpendicularly above of the sheep.
                    else if (isCellFreeOfAnimals(sheepRow+1, sheepCol) &&
                            nearestPenCellRow < sheepRow)
                    {
                        moveSheep(sheepRow, sheepCol, "down");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    // Returns true if the specified cell is a grass cell or a pen cell, meaning there is no animal in the cell
    // or the cell is not outside the range of the board. Otherwise, returns false.
    public boolean isCellFreeOfAnimals(int rowToCheck, int colToCheck)
    {
        return getCellInfo(rowToCheck, colToCheck) != Animals.UPDATED_SHEEP_ID &&
                getCellInfo(rowToCheck, colToCheck) != Animals.SHEEP_ID &&
                // If cell information is -1, means that the coordinates specified
                // are outside the board of the game.
                getCellInfo(rowToCheck, colToCheck) != -1 &&
                getCellInfo(rowToCheck, colToCheck) != Animals.DOG_ID;
    }

    @Override
    // It makes the sheep move to the direction specified, if there is no animal, and it is inside the board there.
    // Sets the cell previously occupied by the sheep to a grass cell, and if the new cell of the sheep
    // is not a pen cell, it gives it the updated sheep ID, that will be converted soon to the sheep's ID.
    // The updated sheep ID is given instead of the sheep ID, as after a dog's move, all sheep move, and when iterating
    // over the board to make all sheep move, this prevents a sheep of moving 2 cells in one round.
    public void moveSheep(int sheepRow, int sheepCol, String direction)
    {
        if (Objects.equals(direction, "up"))
        {
            this.stateOfBoard[sheepRow][sheepCol] = 0;
            if (getCellInfo(sheepRow-1, sheepCol) != 3)
            {
                this.stateOfBoard[sheepRow-1][sheepCol] = Animals.UPDATED_SHEEP_ID;
            }
        }
        else if (Objects.equals(direction, "down"))
        {
            this.stateOfBoard[sheepRow][sheepCol] = 0;
            if (getCellInfo(sheepRow+1, sheepCol) != 3)
            {
                this.stateOfBoard[sheepRow+1][sheepCol] = Animals.UPDATED_SHEEP_ID;
            }
        }
        else if (Objects.equals(direction, "left"))
        {
            this.stateOfBoard[sheepRow][sheepCol] = 0;
            if (getCellInfo(sheepRow, sheepCol-1) != 3)
            {
                this.stateOfBoard[sheepRow][sheepCol-1] = Animals.UPDATED_SHEEP_ID;
            }
        }
        else if (Objects.equals(direction, "right"))
        {
            this.stateOfBoard[sheepRow][sheepCol] = 0;
            if (getCellInfo(sheepRow, sheepCol+1) != 3)
            {
                this.stateOfBoard[sheepRow][sheepCol+1] = Animals.UPDATED_SHEEP_ID;
            }
        }
    }

    @Override
    // Returns true if all the sheep are in the pen. Otherwise, returns false.
    public boolean areAllSheepInPen()
    {
        // Iterates between all cells of the board.
        // If a single sheep is found returns false.
        for (int i = 0; i < settings.nrBoardRows; i++)
        {
            for (int j = 0; j < settings.nrBoardCols; j++)
            {
                if (this.stateOfBoard[i][j] == Animals.SHEEP_ID)
                {
                    return false;
                }
            }
        }
        return true;
    }

    // Returns the id of the cell specified.
    // Returns 0 or 1 or 2 or 3 if the cell type is grass, dog, sheep or pen respectively.
    // It also returns 4 if a sheep just moved in the specified cell, which later it will be converted to 2 indicating
    // there is a sheep there.
    // Moreover, returns -1 if the specified coordinated are outside the board, to avoid any out of index errors.
    @Override
    public byte getCellInfo(int row, int column)
    {
        // Checks if the specified coordinates are outside the board to return -1.
        if (row >= getSettings().getNrBoardRows() ||
                column >= getSettings().getNrBoardCols() ||
                row < 0 ||
                column < 0)
        {
            return (byte) -1;
        }
        else
        {
            return this.stateOfBoard[row][column];
        }
    }

    @Override
    // It changes the type of the specific cell to the specified value.
    public void setCellInfo(int i, int j, byte value)
    {
        this.stateOfBoard[i][j] = value;
    }

	@Override
    // Getter of dog.
    public Dog getDog()
    {
		return dog;
	}

	@Override
    // Setter of dog.
    public void setDog(Dog dog)
    {
		this.dog = dog;
	}

    @Override
    // Getter of sheep list.
    public ArrayList<Sheep> getSheep() {
        return sheep;
    }

    @Override
    // Getter of current game settings.
    public GameSettings getSettings()
    {
        return settings;
    }

    @Override
    // Setter of game settings.
    public void setSettings(GameSettings settings)
    {
        this.settings = settings;
    }

    @Override
    // Getter of current game state.
    public byte getGameState()
    {
        return this.stateOfGame;
    }

    @Override
    // Setter of game state.
    public void setGameState(byte stateOfGame)
    {
        this.stateOfGame = stateOfGame;
    }

}
