package edu.up.cs301.canasta;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * This is a computer player that slaps at an average rate given
 * by the constructor parameter.
 * 
 * @author Steven R. Vegdahl
 * @version December 2016
 */
public class CanastaComputerPlayer extends GameComputerPlayer {

	// the most recent state of the game
	private CanastaState savedState;
	
    /**
     * Constructor for the CanastaComputerPlayer class; creates an "average" player.
     * 
     * @param name
     * 		the player's name
     */
    public CanastaComputerPlayer(String name) {
        // invoke general constructor to create player whose average reaction
    	// time is half a second.
        super(name);
    }	


    /**
     * callback method, called when we receive a message, typically from
     * the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {

		CanastaDrawDeckAction drawDeck = new CanastaDrawDeckAction(this);

		// wait half-second to start the turn
		sleep(500);

    	// if we don't have a game-state, ignore
    	if (!(info instanceof CanastaState)) {
    		return;
    	}
    	
    	// update our state variable
    	savedState = (CanastaState)info;

		// access deck of the player whose turn it is
		Deck myDeck = savedState.getDeck(savedState.toPlay() + 2);

		/* array list containing the cards from the players hand that will be melded,
		uses the findMeld method to find a possible meld in the players hand */
		ArrayList<Card> myMeldArray = findMeld(myDeck);

		// check that it is the players turn
    	if (savedState.toPlay() == this.playerNum) {

			// draw a card
			game.sendAction(drawDeck);
			// delay half-second
			sleep(500);

			// check that meld was found and contains at least three cards; if so, create and send meld action to the game
			if(myMeldArray != null) {
				if (myMeldArray.size() >= 3) {
					CanastaComputerMeldAction computerMeld = new CanastaComputerMeldAction(this, myMeldArray);
					game.sendAction(computerMeld);
				}
				//delay half-second
				sleep(500);
			}

			// create and send discard action to the game
			CanastaDiscardAction discard = new CanastaDiscardAction(this, savedState.getDeck(this.playerNum+2).peekAtTopCard());
			game.sendAction(discard);
			sleep(500);


    	}
    }

	/* method to find a legal meld in a given deck of cards
	* iterates through given deck and compares each card to all the other cards in the deck
	* looking for three cards of the same rank including wild cards*/
	private ArrayList<Card> findMeld(Deck d){
		ArrayList<Card> myHand = d.getCards();
		ArrayList<Card> meldArray;

		for(int i=0; i<myHand.size(); i++){
			meldArray = new ArrayList<Card>();

			// rank of current card to match with
			Rank rank = myHand.get(i).getRank();

			// search for sets of three of the same rank or wild cards
			for(int j=i; j<myHand.size()-i; j++){
				if(myHand.get(j).getRank() == rank || myHand.get(j).getRank() == Rank.TWO || myHand.get(j).getRank() == Rank.RJOKER){
					meldArray.add(myHand.get(j));
				}
			}
			// if set of three or more found, return meld
			if(meldArray.size()>=3){
				return meldArray;
			}
		}

		// if no legal meld found, return null
		return null;
	}
}
