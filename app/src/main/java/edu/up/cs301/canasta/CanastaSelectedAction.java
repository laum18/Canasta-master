package edu.up.cs301.canasta;

import edu.up.cs301.game.GamePlayer;

/**
 * Created by michellelau on 12/7/16.
 */

public class CanastaSelectedAction extends CanastaMoveAction {

    private boolean selected;
    private int position;

    CanastaSelectedAction(GamePlayer player, boolean s, int p) {
        super(player);
        selected = s;
        position = p;
    }

    public boolean getSelected() { return selected; }

    public int getPosition() { return position; }
}
