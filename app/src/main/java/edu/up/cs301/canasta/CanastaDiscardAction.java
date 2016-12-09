package edu.up.cs301.canasta;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * Created by laum18 on 11/8/2016.
 */
public class CanastaDiscardAction extends CanastaMoveAction {

    private Card card;

    public CanastaDiscardAction(GamePlayer player, Card c)
    {
        // initialize the source with the superclass constructor
        super(player);

        card = c;
    }

    public Card getCard(){
      return card;
    }

}
