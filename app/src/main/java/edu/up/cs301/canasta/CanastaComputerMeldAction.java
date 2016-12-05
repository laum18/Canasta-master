package edu.up.cs301.canasta;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

/**
 * Created by banobi19 on 12/4/2016.
 */
public class CanastaComputerMeldAction extends CanastaMoveAction{
    private static final long serialVersionUID = 1846103856620662038L;

    /**
     * Constructor for the SJSlapMoveAction class.
     *
     * @param player  the player making the move
     */

    ArrayList<Card> meldArray;
    public CanastaComputerMeldAction(GamePlayer player, ArrayList<Card> meld)
    {
        // initialize the source with the superclass constructor
        super(player);

        meldArray = meld;

    }

    public ArrayList<Card> getMeld(){
        return meldArray;
    }
}
