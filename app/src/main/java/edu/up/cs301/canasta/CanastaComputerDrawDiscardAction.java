package edu.up.cs301.canasta;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * A game-move object that a Canasta computer player sends to the game to make
 * a draw discard move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandeward
 * @version November 2016
 */
public class CanastaComputerDrawDiscardAction extends CanastaMoveAction{
    private static final long serialVersionUID = 1325640856629756038L;

    // instance variable
    ArrayList<Card> meldArray;

    /**
     * Constructor for the CanastaComputerDrawDiscardAction class.
     *
     * @param player  the player making the move
     */
    public CanastaComputerDrawDiscardAction(GamePlayer player, ArrayList<Card> meld)
    {
        // initialize the source with the superclass constructor
        super(player);

        // initialize meldArray to meld
        meldArray = meld;

    }

    /**
     * A getter method to get the meldArray
     * @return meldArray
     */
    public ArrayList<Card> getMeld(){
        return meldArray;
    }
}
