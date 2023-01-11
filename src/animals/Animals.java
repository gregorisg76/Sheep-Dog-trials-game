package animals;

/**
 * This class is used to represent the objects that are found on the board,
 * and focuses on moving objects, animals like the sheep and the dog.
 */

public class Animals
{
	// IDs of all objects that can be found in the board of the game.
    public static byte GRASS_CELL_ID = 0;
	public static byte DOG_ID = 1;
	public static byte SHEEP_ID = 2;
	public static byte PEN_CELL_ID = 3;
	// This ID is used only from recently moved sheep, and changes to sheep ID almost immediately.
	// It never appears when the board is displayed.
	public static byte UPDATED_SHEEP_ID = 4;

	// Initialize variables.
    public byte type;
    public int id;
    public int row;
    public int col;

	// Constructor sets the specified type of animal (1 for dog, 2 for sheep).
    public Animals(String type, int id)
	{
    	if(type.equals("Dog"))
		{
    		setType((byte)1);
    	}
    	else
		{
    		setType((byte)2);
    	}
    	setId(id);
    }

	// Getter for animal's row.
    public int getRow()
	{
		return row;
	}

	// Setter for animal's row.
	public void setRow(int row)
	{
		this.row = row;
	}

	// Getter for animal's column.
	public int getCol()
	{
		return col;
	}

	// Setter for animal's column.
	public void setCol(int col)
	{
		this.col = col;
	}

	// Setter for animal's position.
	public void setPosition(int row, int col)
	{
		this.row = row;
		this.col = col;
	}

	// Getter for animal's type.
    public byte getType()
	{
		return type;
	}

	// Setter for animal's type.
	public void setType(byte type)
	{
		this.type = type;
	}
	// Setter for animal's id.
	public void setId(int id)
	{
		this.id = id;
	}
}
