package edu.up.cs301.canasta;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * A game-move object that a Canasta computer player sends to the game to make
 * a meld move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandewark
 * @version November 2016
 */
public class CanastaComputerMeldAction extends CanastaMoveAction{
    private static final long serialVersionUID = 1846103856620662038L;

    // instance variable
    ArrayList<Card> meldArray;

    /**
     * Constructor for the CanastaComputerMeldAction class.
     *
     * @param player The player making the move
     * @param meld The cards being melded
     */
    public CanastaComputerMeldAction(GamePlayer player, ArrayList<Card> meld)
    {
        // initialize the source with the superclass constructor
        super(player);

        // set meldArray to meld
        meldArray = meld;

    }

    /**
     * A getter method to get the melded cards
     *
     * @return The melded cards
     */
    public ArrayList<Card> getMeld(){
        return meldArray;
    }
}
