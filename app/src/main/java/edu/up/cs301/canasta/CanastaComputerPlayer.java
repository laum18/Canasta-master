package edu.up.cs301.canasta;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * This is a computer player that slaps at an average rate given
 * by the constructor parameter.
 * 
 * @author Steven R. Vegdahl
 * @version July 2013 
 */
public class CanastaComputerPlayer extends GameComputerPlayer
{
	// the minimum reaction time for this player, in milliseconds
	private double minReactionTimeInMillis;
	
	// the most recent state of the game
	private CanastaState savedState;
	
    /**
     * Constructor for the SJComputerPlayer class; creates an "average"
     * player.
     * 
     * @param name
     * 		the player's name
     */
    public CanastaComputerPlayer(String name) {
        // invoke general constructor to create player whose average reaction
    	// time is half a second.
        this(name, 0.5);
    }	
    
    /*
     * Constructor for the SJComputerPlayer class
     */
    public CanastaComputerPlayer(String name, double avgReactionTime) {
        // invoke superclass constructor
        super(name);
        
        // set the minimim reaction time, which is half the average reaction
        // time, converted to milliseconds (0.5 * 1000 = 500)
        minReactionTimeInMillis = 500*avgReactionTime;
    }

	/**
	 * Invoked whenever the player's timer has ticked. It is expected
	 * that this will be overridden in many players.
	 */
    @Override
    protected void timerTicked() {
    	// we had seen a Jack, now we have waited the requisite time to slap
    	
    	// look at the top card now. If it's still a Jack, slap it
    	Card topCard = savedState.getDeck(2).peekAtTopCard();
    	if (topCard != null && topCard.getRank() == Rank.JACK) {
    		// the Jack is still there, so submit our move to the game object
    		game.sendAction(new CanastaMeldAction(this));
    	}
    	
    	// stop the timer, since we don't want another timer-tick until it
    	// again is explicitly started
    	getTimer().stop();
    }

    /**
     * callback method, called when we receive a message, typically from
     * the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {

		CanastaDrawDeckAction drawDeck = new CanastaDrawDeckAction(this);
		CanastaMeldAction meld = new CanastaMeldAction(this);

		sleep(500);

    	// if we don't have a game-state, ignore
    	if (!(info instanceof CanastaState)) {
    		return;
    	}
    	
    	// update our state variable
    	savedState = (CanastaState)info;
    	
    	// access the state's middle deck
    	Deck middleDeck = savedState.getDeck(2);
    	
    	// look at the top card
    	Card topCard = middleDeck.peekAtTopCard();
    	
    	// if it's a Jack, slap it; otherwise, if it's our turn to
    	// play, play a card
    	if (topCard != null && topCard.getRank() == Rank.JACK) {
    		// we have a Jack to slap: set up a timer, depending on reaction time.
    		// The slap will occur when the timer "ticks". Our reaction time will be
    		// between the minimum reaction time and 3 times the minimum reaction time
        	int time = (int)(minReactionTimeInMillis*(1+2*Math.random()));
    		this.getTimer().setInterval(time);
    		this.getTimer().start();
    	}
    	else if (savedState.toPlay() == this.playerNum) {
    		// not a Jack but it's my turn to play a card
    		
    		// delay for up to two seconds; then play
        	sleep((int)(2000*Math.random()));

			game.sendAction(drawDeck);

			//while (savedState.canMeld( )) {
			//	game.sendAction(meld);
			//}

//			while (savedState.canMeld( )) {
//				game.sendAction(meld);
//			}

			CanastaDiscardAction discard = new CanastaDiscardAction(this, savedState.getDeck(this.playerNum).peekAtTopCard());
			game.sendAction(discard);

        	// submit our move to the game object
        	game.sendAction(new CanastaPlayAction(this));
    	}
    }
}
