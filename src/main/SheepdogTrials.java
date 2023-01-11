package main;

import game.Controller;

/**
 * This class, is the main class of the project and creates a controller to start the game.
 */

public class SheepdogTrials
{
    // Controller is created to start the game.
	public static void main(String[] args)
    {
        Controller controller = new Controller();
        controller.startSession();
    }
}
