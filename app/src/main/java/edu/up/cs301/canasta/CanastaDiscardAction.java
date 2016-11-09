package edu.up.cs301.canasta;

import edu.up.cs301.game.GamePlayer;

/**
 * Created by laum18 on 11/8/2016.
 */
public class CanastaDiscardAction extends CanastaMoveAction {

    public CanastaDiscardAction(GamePlayer player)
    {
        // initialize the source with the superclass constructor
        super(player);
    }

    public boolean isDiscard() {
        return true;
    }
}
