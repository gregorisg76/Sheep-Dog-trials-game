package game;

import util.GameSettings;
import interfaces.InterfaceModel;
import animals.Animals;
import animals.Sheep;

/**
 * This class inherits from the Model class, and is used to represent the state of a fixed setup game.
 */
public class FixedModel extends Model
{
	// The constructor loads the settings from the Model class.
	public FixedModel(GameSettings settings)
	{
		super(settings);
	}

	 @Override
	 // Used to initialize the fixed setup game.
	 public void initNewGame(GameSettings settings)
	 {
		// Set the settings and the game state and initialize the board.
		setSettings(settings);
		initStateOfBoard();
		setGameState(InterfaceModel.GAME_IN_PROGRESS);

		// Insert dog on specified cell.
		setCellInfo(getDog().getRow(),getDog().getCol(), getDog().getType());

		// Initialize and insert 5 sheep on specified cells.
		initFixedSheep();
		for (Sheep sheep : getSheep())
		{
		   setCellInfo(sheep.getRow(),sheep.getCol(),sheep.type);
		}

		// Insert pen on specified cells.
		for (int i = 3; i < 3 + getSettings().getNrPenRows(); i++)
		{
			for (int j = 3; j < 3 + getSettings().getNrPenCols(); j++)
			{
			   setCellInfo(i,j, Animals.PEN_CELL_ID);
			}
		}
	}

	// Initializes sheep on fixed setup, and inserts them in the sheep list while setting their locations.
	private void initFixedSheep()
	{
		// Create 5 new sheep.
		for(int i = 0; i < 5 ; i++)
		{
	        	Sheep sheep = new Sheep(i);
	        	getSheep().add(sheep);
		}

		// Insert them in sheep list and set their positions.
		getSheep().get(0).setPosition(0,4);
		getSheep().get(1).setPosition(7,5);
		getSheep().get(2).setPosition(7,3);
		getSheep().get(3).setPosition(4,7);
		getSheep().get(4).setPosition(4,0);
	}
}
