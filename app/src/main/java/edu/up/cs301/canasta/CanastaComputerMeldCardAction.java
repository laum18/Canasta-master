package edu.up.cs301.canasta;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * A game-move object that a Canasta computer player sends to the game to make
 * a meld move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandewark
 * @version November 4, 2016
 */
public class CanastaComputerMeldCardAction extends CanastaMoveAction{

    private static final long serialVersionUID = 2916103930210684208L;

    // instance variable
    Deck myHand;

    /**
     * Constructor for the CanastaComputerMeldAction class.
     *
     * @param player The player making the move
     * @param initHand The cards being melded
     */
    public CanastaComputerMeldCardAction(GamePlayer player, Deck initHand)
    {
        // initialize the source with the superclass constructor
        super(player);

        // set meldArray to meld
        myHand = initHand;

    }

    /**
     * A getter method to get the melded cards
     *
     * @return The melded cards
     */
    public Deck getMeld(){
        return myHand;
    }
}
