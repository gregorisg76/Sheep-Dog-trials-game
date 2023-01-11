package animals;

/**
 * This class is used to extend the Animals class and represents the Sheep.
 * It can be found especially useful if the game is expanded.
 * This class can be expanded to treat each sheep using its ID.
 */

public class Sheep extends Animals
{
	// Constructor uses Animals class constructor to set the type and the id of the sheep.
	public Sheep (int id)
	{
		super("Sheep", id);
	}

	// Constructor uses Animals class constructor to set the type and the id of the sheep, and sets its position.
	public Sheep (int id, int sheepRow, int sheepColumn)
	{
		super("Sheep", id);
		setPosition(sheepRow, sheepColumn);
	}
}