package edu.up.cs301.canasta;

import edu.up.cs301.game.GamePlayer;

/**
 * A game-move object that a Canasta player sends to the game to make
 * a draw discard move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandeward
 * @version November 8, 2016
 */
public class CanastaDrawDiscardAction extends CanastaMoveAction {

    /**
     * A construct for the CanastaDrawDiscardAction
     *
     * @param player The player making the mvoe
     */
    public CanastaDrawDiscardAction(GamePlayer player)
    {
        // initialize the source with the superclass constructor
        super(player);
    }

}
