package edu.up.cs301.canasta;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * A game-move object that a Canasta player sends to the game to make
 * a discard card move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandeward
 * @version November 2016
 */
public class CanastaDiscardAction extends CanastaMoveAction {

    // instance variable
    private Card card;

    /**
     * A CanastaDiscardAction constructor
     * @param player The player making the move
     * @param c The card being discarded
     */
    public CanastaDiscardAction(GamePlayer player, Card c)
    {
        // initialize the source with the superclass constructor
        super(player);

        // initialize card to c
        card = c;
    }

    /**
     * A public getter method to get the card being discarded
     *
     * @return The card being discarded
     */
    public Card getCard(){
      return card;
    }

}
