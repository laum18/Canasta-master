package edu.up.cs301.canasta;

import edu.up.cs301.game.GamePlayer;

/**
 * A game-move action that a Canasta player sends to the game to make
 * a meld move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandeward
 * @version November 8, 2016
 */
public class CanastaMeldAction extends CanastaMoveAction
{
	private static final long serialVersionUID = 2134321631283669359L;

	/**
     * Constructor for the CanastaMeldAction class.
     * 
     * @param player The player making the move
     */
    public CanastaMeldAction(GamePlayer player)
    {
        // initialize the source with the superclass constructor
        super(player);
    }

}
