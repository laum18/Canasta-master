package edu.up.cs301.canasta;

import edu.up.cs301.game.GamePlayer;

/**
 * A CanastaPlayAction is an action that represents playing a card on the meld
 * pile.
 * 
 * @author Steven R. Vegdahl
 * @version 31 July 2002
 */
public class CanastaPlayAction extends CanastaMoveAction
{
	private static final long serialVersionUID = 3250639793499599047L;

	/**
     * Constructor for the CanastaPlayAction class.
     * 
     * @param player  the player making the move
     */
    public CanastaPlayAction(GamePlayer player)
    {
        // initialize the source with the superclass constructor
        super(player);
    }

    
}
