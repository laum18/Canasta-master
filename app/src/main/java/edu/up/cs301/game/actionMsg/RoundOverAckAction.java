package edu.up.cs301.game.actionMsg;

import edu.up.cs301.game.GamePlayer;

/**
 * Created by michellelau on 12/2/16.
 */

public class RoundOverAckAction extends GameAction {

    // to satisfy the Serializable interface
    private static final long serialVersionUID = 2736839260363438212L;

    /**
     * constructor
     *
     * @param p
     * 		the player to sent the action
     */
    public RoundOverAckAction(GamePlayer p) {
        super(p);
    }
}
