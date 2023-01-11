package animals;

/**
 * This class is used to extend the Animals class and represents the Dog.
 * It can be found especially useful if the game is expanded, for example, use 2 or more dogs in a game.
 * This class can be expanded to treat each dog using its ID.
 */

public class Dog extends Animals
{
	// Constructor uses Animals class constructor to set the type and the id of the dog,
	// and sets its fixed setup position.
	public Dog(int id)
	{
		super("Dog", id);
		setPosition(0, 0);
	}

	// Setter for dog's row using a single char, to increase or decrease it by 1.
	public void setDogRow(char move)
	{
		if (move == 'w')
		{
			setRow(getRow() - 1);
		}
		else if (move == 's')
		{
			setRow(getRow() + 1);
		}
	}

	// Setter for dog's column using a single char, to increase or decrease it by 1.
	public void setDogColumn(char move)
	{
		if (move == 'a')
		{
			setCol(getCol() - 1);
		}
		else if (move == 'd')
		{
			setCol(getCol() + 1);
		}
	}

	// Setter for dog's row using an int, to set the exact dog row, anywhere in the board.
	public void setDogRow(int dogRow)
	{
		setRow(dogRow);
	}

	// Setter for dog's column using an int, to set the exact dog row, anywhere in the board.
	public void setDogColumn(int dogColumn)
	{
		setCol(dogColumn);
	}
}
