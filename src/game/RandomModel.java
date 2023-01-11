package game;

import util.GameSettings;
import java.util.concurrent.ThreadLocalRandom;
import animals.Animals;
import animals.Sheep;
import interfaces.InterfaceModel;

/**
 * This class inherits from the Model class, and is used to represent the state of a random setup game.
 */
public class RandomModel extends Model
{
    // The constructor loads the settings from the model class.
	public RandomModel(GameSettings settings)
    {
		super(settings);
	}
	
	@Override
    // Used to initialize the random setup game.
    public void initNewGame(GameSettings settings)
    {
        // Find random numbers for each required setting in given ranges.
        // Ranges are set up in a way that ensures the game is playable.
        int nrBoardRows = ThreadLocalRandom.current().nextInt(6, 51);
        int nrBoardCols = ThreadLocalRandom.current().nextInt(6, 51);
        int nrDogs = 1;
        int nrPenCols = ThreadLocalRandom.current().nextInt(2, Math.round(nrBoardCols/2)+1);
        int nrPenRows = ThreadLocalRandom.current().nextInt(2, Math.round(nrBoardRows/2)+1);
        int penSpace = nrPenRows*nrPenCols;
        int availableSpace = (nrBoardRows*nrBoardCols) - penSpace;
        // Number of sheep are never more than pen size.
        int nrSheep = ThreadLocalRandom.current().nextInt(1, Math.min(Math.round(availableSpace/3) + 1,
                penSpace + 1));

        // Create new game settings and set the game settings using the random settings generated above.
        GameSettings newSetting = new GameSettings(nrBoardRows, nrBoardCols, nrDogs, nrSheep, nrPenRows, nrPenCols);
        setSettings(newSetting);

        // Initialize the board, and set the game state as in progress.
        initStateOfBoard();
        setGameState(InterfaceModel.GAME_IN_PROGRESS);

        // Randomly generate a starting position of the pen and insert pen on specified cells.
        int firstPenRow = ThreadLocalRandom.current().nextInt(0,
                getSettings().getNrBoardRows()- getSettings().getNrPenRows());
        int firstPenCol = ThreadLocalRandom.current().nextInt(0,
        		getSettings().getNrBoardCols() - getSettings().getNrPenCols());
        for (int i = firstPenRow; i < firstPenRow + getSettings().getNrPenRows(); i++)
        {
            for (int j = firstPenCol; j < firstPenCol + getSettings().getNrPenCols(); j++)
            {
                setCellInfo(i,j, Animals.PEN_CELL_ID);
            }
        }

        // Initialize and insert randomly in grass cells the sheep.
        initRandomSheep();

        // Insert dog on a random grass cell.
        getDog().setDogRow(ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardRows()));
        getDog().setDogColumn(ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardCols()));
        int dogRow = getDog().getRow();
        int dogColumn = getDog().getCol();
        // This ensures that the dog is not placed in a cell, that does not offer any valid move and traps him.
        // Randomly generate dog coordinates until a suitable grass cell is found.
        while(getCellInfo(dogRow,dogColumn) != 0 ||
                (getCellInfo(dogRow-1,dogColumn) != 0 &&
                        getCellInfo(dogRow+1,dogColumn) != 0 &&
                        getCellInfo(dogRow,dogColumn-1) != 0 &&
                        getCellInfo(dogRow,dogColumn+1) != 0 &&
                        getCellInfo(dogRow-1,dogColumn) != -1 &&
                        getCellInfo(dogRow+1,dogColumn) != -1 &&
                        getCellInfo(dogRow,dogColumn-1) != -1 &&
                        getCellInfo(dogRow,dogColumn+1) != -1))
        {
        	getDog().setDogRow(ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardRows()));
			getDog().setDogColumn(ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardCols()));
            dogRow = getDog().getRow();
            dogColumn = getDog().getCol();
        }
        setCellInfo(dogRow,dogColumn, getDog().getType());
    }

    // Initializes sheep on random setup, and inserts them in the sheep list while setting their locations.
	private void initRandomSheep()
    {
        // Place the required number of sheep in grass cells.
        int sheepRow;
        int sheepColumn;
        for (int i = 0; i < getSettings().getNrSheep(); i++)
        {
            sheepRow = ThreadLocalRandom.current().nextInt(0,getSettings().getNrBoardRows());
            sheepColumn = ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardCols());
            // Randomly generate sheep coordinates until a suitable grass cell is found.
            while(getCellInfo(sheepRow,sheepColumn) != 0)
            {
                sheepRow = ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardRows());
                sheepColumn = ThreadLocalRandom.current().nextInt(0, getSettings().getNrBoardCols());
            }
            // Create new sheep, add it in the sheep list and insert it on the board.
            Sheep sheep = new Sheep(i, sheepRow, sheepColumn);
            getSheep().add(sheep);
            setCellInfo(sheepRow, sheepColumn, sheep.type);
        }
	}
}
