package util;

/**
 * This class contains the dimension of the board, the number of dogs,
 * the number of sheep and the dimensions of the pen.
 */

public class GameSettings
{
    public final int nrBoardRows;
    public final int nrBoardCols;
    public final int nrDogs;
    public final int nrSheep;
    public final int nrPenRows;
    public final int nrPenCols;

    // Getter of number of board rows.
    public int getNrBoardRows() {
		return nrBoardRows;
	}

    // Getter of number of board columns.
	public int getNrBoardCols() {
		return nrBoardCols;
	}

    // Getter of number of sheep.
	public int getNrSheep() {
		return nrSheep;
	}

    // Getter of number of pen rows.
	public int getNrPenRows() {
		return nrPenRows;
	}

    // Getter of number of pen columns.
	public int getNrPenCols() {
		return nrPenCols;
	}

	// Creates an instance using the default settings to be used in a
    // fixed setup game if not a random game is requested.
    public GameSettings()
    {
        this(8, 8, 1, 5, 2, 3);
    }

    // Creates an instance using the given settings.
    public GameSettings(int nrBoardRows, int nrBoardCols, int nrDogs, int nrSheep, int nrPenRows, int nrPenCols)
    {
        this.nrBoardRows = nrBoardRows;
        this.nrBoardCols = nrBoardCols;
        this.nrDogs = nrDogs;
        this.nrSheep = nrSheep;
        this.nrPenRows = nrPenRows;
        this.nrPenCols = nrPenCols;
    }

}
