package edu.up.cs301.canasta;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * This is a smart computer player that can continue melding
 * cards and draw from the discard pile
 *
 * @author Michelle Lau , Nick Edwards, Aaron Banobi, David Vandewark
 * @version December 2016
 */
public class HardComputerPlayer extends GameComputerPlayer {

    // the most recent state of the game
    private CanastaState savedState;

    /**
     * Constructor for the CanastaComputerPlayer class; creates an "average" player.
     *
     * @param name the player's name
     */
    public HardComputerPlayer(String name) {
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

        // create a CanastaDrawDeckAction on this player
        CanastaDrawDeckAction drawDeck = new CanastaDrawDeckAction(this);

        // wait half-second to start the turn
        sleep(500);

        // if we don't have a game-state, ignore
        if (!(info instanceof CanastaState)) {
            return;
        }

        // update our state variable
        savedState = (CanastaState) info;

        // access deck of the player whose turn it is
        Deck myDeck;
                //= savedState.getDeck(savedState.toPlay() + 2);

		/* array list containing the cards from the players hand that will be melded, uses the
        canMeld method to find a possible meld with discard pile in the players hand */
        myDeck = savedState.getDeck(playerNum+2);
        ArrayList<Card> meldDiscard = canMeld(myDeck);

        // check that it is the players turn
        if (savedState.toPlay() == this.playerNum) {

            // check if the meldDiscard is null
            if (meldDiscard != null) {
                // then create a CanastaComputerDrawDiscardAction on this player and meldDiscard
                CanastaComputerDrawDiscardAction computerDrawDiscard = new CanastaComputerDrawDiscardAction(this, meldDiscard);
                // send the computerDrawDiscard action to the game
                game.sendAction(computerDrawDiscard);
            }
            // send the drawDeck action to the game
            game.sendAction(drawDeck);

            // delay half-second
            sleep(500);

            /* array list containing the cards from the players hand that will be melded,
		    uses the findMeld method to find a possible meld in the players hand */
            ArrayList<Card> myMeldArray = findMeld(myDeck);

            do {
                // check if myMeldArray is null
                if (myMeldArray == null) {
                    // then do nothing
                }
                // if myMeldArray is not null
                else {
                    // check that myMeldArray's size is at least 3
                    if (myMeldArray.size() >= 3) {
                        // then create a CanastaComputerMeldAction on this player and myMeldArray
                        CanastaComputerMeldAction computerMeld = new CanastaComputerMeldAction(this, myMeldArray);
                        // send the computerMeld action to the game
                        game.sendAction(computerMeld);
                    }

                    // when myMeldArray is not empty
                    while (myMeldArray.size() > 0) {
                        // remove the first card in the arraylist from the player's hand
                        myDeck.removeCard(myMeldArray.get(0));
                        // remove the first card from the arraylist
                        myMeldArray.remove(0);
                    }

                    //delay half-second
                    sleep(500);

                    // call findMeld to see if we can meld anymore cards
                    myMeldArray = findMeld(myDeck);
                }
            }
            // do this while myMeldArray is not null
            while (myMeldArray != null);

            myDeck = savedState.getDeck(playerNum+2);
            CanastaComputerMeldCardAction meldCard = new CanastaComputerMeldCardAction(this, savedState.getDeck(playerNum+2));
            game.sendAction(meldCard);


            // call the discard method to find a card to discard from the player's deck
            Card discardCard = discard(savedState.getDeck(this.playerNum + 2));

            // check that discardCard is not null
            if (discardCard == null) {
                // if it is, then discard the top card of this player's deck
                discardCard = savedState.getDeck(this.playerNum + 2).peekAtTopCard();
            }

            // create a CanastaDiscardAction on this player and discardCard
            CanastaDiscardAction discard = new CanastaDiscardAction(this, discardCard);

            // send the discard action to the game
            game.sendAction(discard);

        }
    }

    /* method to find a legal meld in a given deck of cards
	* iterates through given deck and compares each card to all the other cards in the deck
	* looking for three cards of the same rank including wild cards*/
    private ArrayList<Card> findMeld(Deck d) {

        // get d and put it into an arraylist, myHand
        ArrayList<Card> myHand = d.getCards();
        // declare an arraylist, meldArray, of card
        ArrayList<Card> meldArray;

        // loop through myHand
        for (int i = 0; i < myHand.size(); i++) {
            // initialize meldArray
            meldArray = new ArrayList<Card>();

            // rank of current card to match with
            Rank rank = myHand.get(i).getRank();

            // search for sets of three of the same rank or wild cards
            for (int j = i; j < myHand.size(); j++) {
                // check that the card we are comparing to is the same rank or a wild card
                if (myHand.get(j).getRank() == rank || myHand.get(j).getRank() == Rank.TWO || myHand.get(j).getRank() == Rank.RJOKER) {
                    // if it is, then add that card to meldArray
                    meldArray.add(myHand.get(j));
                }
            }

            // if set of three or more found
            if (meldArray.size() >= 3) {
                // return meldArray
                return meldArray;
            }
        }

        // if no legal meld found, return null
        return null;

    }

    /*
     * A method to see if we can find at least two of the same ranks in the
     * player's hand
     *
     * @param d The deck being passed in to find meld cards
     */
    private ArrayList<Card> canMeld(Deck d) {

        // put the cards in d into an arraylist, myHand, of Cards
        ArrayList<Card> myHand = d.getCards();
        // declare an arraylist, myMeldArray, of Cards
        ArrayList<Card> meldArray;

        // loop through myHand
        for (int i = 0; i < myHand.size(); i++) {
            // initialize meldArray
            meldArray = new ArrayList<Card>();

            // rank of current card to match with
            Rank rank = myHand.get(i).getRank();

            // loop through myHand starting at the next card of i
            for (int j = i; j < myHand.size(); j++) {
                // if it is the same rank
                if (myHand.get(j).getRank() == rank) {
                    // add that card into meldArray
                    meldArray.add(myHand.get(j));
                }
            }

            // if set of two or more found
            if (meldArray.size() >= 2) {
                // return meldArray
                return meldArray;
            }
        }

        // if no legal meld found, return null
        return null;
    }

    /*
     * A method to find a card to discard in the deck that's passed in
     * Finds a card that does not have a pair or more
     *
     * @param d The deck being passed in to find a discard card
     */
    private Card discard(Deck d) {

        // put the cards in d into an arraylist, myHand, of Cards
        ArrayList<Card> myHand = d.getCards();
        // declare a Card variable discardCard
        Card discardCard;
        // declare a rank R
        Rank r;
        // declare a boolean pair
        boolean pair;

        // loop through myHand
        for (int i = 0; i < myHand.size(); i++) {
            // get the rank of the first car
            r = myHand.get(i).getRank();
            // initialize pair to false
            pair = false;
            // check if r is a wildcard
            if (r.shortName() == '2' || r.shortName() == 'R') {
                // if it is, do nothing
            }
            // if it is not
            else {
                // loop through myHand starting with the card after i
                for (int j = i + 1; j < myHand.size(); j++) {
                    // if this card is the same rank as r
                    if (r == myHand.get(j).getRank()) {
                        // set pair to true
                        pair = true;
                    }
                }
            }

            // if there is no pair
            if (!pair) {
                // initialize discardCard to the current card at i
                discardCard = myHand.get(i);
                // return discardCard
                return discardCard;
            }
        }

        // if all cards have a pair, return null
        return null;
    }
}
