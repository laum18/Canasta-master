package edu.up.cs301.canasta;

import edu.up.cs301.game.GamePlayer;

/**
 * A game-move object that a Canasta player sends to the game to make
 * a draw card move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandeward
 * @version November 8, 2016
 */
public class CanastaDrawDeckAction extends CanastaMoveAction {

    /**
     * Construct for the CanastaDrawDeckAction class
     *
     * @param player The player making the move
     */
    public CanastaDrawDeckAction(GamePlayer player)
    {
        // initialize the source with the superclass constructor
        super(player);
    }

}
