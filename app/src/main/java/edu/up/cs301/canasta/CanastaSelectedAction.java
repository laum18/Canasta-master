package edu.up.cs301.canasta;

import edu.up.cs301.game.GamePlayer;

/**
 * A game-move action that a Canasta computer player sends to the game to make
 * a selected card move.
 *
 * @author Nick Edwards, Aaron Banobi, Michelle Lau, and David Vandeward
 * @version December 2016
 */
public class CanastaSelectedAction extends CanastaMoveAction {

    // instance variables
    private boolean selected;
    private int position;

    /**
     * A construct for the CanastaSelectedAction class
     *
     * @param player The player making the move
     * @param isSelected If the card is selected
     * @param p The position of the card
     */
    CanastaSelectedAction(GamePlayer player, boolean isSelected, int p) {
        super(player);
        selected = isSelected;
        position = p;
    }

    /**
     * A getter method to get whether the card is selected
     *
     * @return Whether or not the card is selected
     */
    public boolean getSelected() { return selected; }

    /**
     * A getter method to get the position of the card
     *
     * @return The position of the card
     */
    public int getPosition() { return position; }
}
