package edu.up.cs301.canasta;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by laum18 on 12/6/2016.
 */
public class HardComputerPlayer extends GameComputerPlayer {

    // the most recent state of the game
    private CanastaState savedState;

    /**
     * Constructor for the CanastaComputerPlayer class; creates an "average" player.
     *
     * @param name
     * 		the player's name
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

        Card topOfDiscardPile = savedState.getDeck(1).peekAtTopCard();

		/* array list containing the cards from the players hand that will be melded,
		uses the findMeld method to find a possible meld in the players hand */


        ArrayList<Card> meldDiscard = canMeld(myDeck);

        // check that it is the players turn
        if (savedState.toPlay() == this.playerNum) {

           if (meldDiscard != null) {
               CanastaComputerDrawDiscardAction computerDrawDiscard = new CanastaComputerDrawDiscardAction(this,meldDiscard);
               game.sendAction(computerDrawDiscard);
            }
               // draw a card
               game.sendAction(drawDeck);


            // delay half-second
            sleep(500);

            ArrayList<Card> myMeldArray = findMeld(myDeck);
            //findMeld(myDeck);
            do {
                if (myMeldArray == null) {

                } else {
                    if (myMeldArray.size() >= 3) {
                        CanastaComputerMeldAction computerMeld = new CanastaComputerMeldAction(this, myMeldArray);
                        game.sendAction(computerMeld);
                    }

                    while (myMeldArray.size() > 0) {
                        myDeck.removeCard(myMeldArray.get(0));
                        myMeldArray.remove(0);
                    }
                    //delay half-second
                    sleep(500);
                    myMeldArray = findMeld(myDeck);
                }

            } while (myMeldArray != null);

//            // check that meld was found and contains at least three cards; if so, create and send meld action to the game
//            if(myMeldArray != null) {
//
//            }

            Card discardCard = discard(savedState.getDeck(this.playerNum + 2));
            // create and send discard action to the game
            if (discardCard == null) {
                discardCard = savedState.getDeck(this.playerNum + 2).peekAtTopCard();
            }
            CanastaDiscardAction discard = new CanastaDiscardAction(this, discardCard);
            game.sendAction(discard);


        }
    }

    /* method to find a legal meld in a given deck of cards
	* iterates through given deck and compares each card to all the other cards in the deck
	* looking for three cards of the same rank including wild cards*/
    private ArrayList<Card> findMeld(Deck d){
//    private void findMeld(Deck d){
        ArrayList<Card> myHand = d.getCards();
        ArrayList<Card> meldArray;

        for(int i=0; i<myHand.size(); i++){
            meldArray = new ArrayList<Card>();

            // rank of current card to match with
            Rank rank = myHand.get(i).getRank();

            // search for sets of three of the same rank or wild cards
            for(int j=i; j<myHand.size(); j++){
                if(myHand.get(j).getRank() == rank || myHand.get(j).getRank() == Rank.TWO || myHand.get(j).getRank() == Rank.RJOKER){
                    meldArray.add(myHand.get(j));
                    //myHand.get(j).setSelected(true);
                }
            }
            // if set of three or more found, return meld
            if(meldArray.size()>=3){
                return meldArray;
            }
        }

        // if no legal meld found, return null
        return null;

//        int selected = 0;
//        for (int i = 0; i < d.size(); i++) {
//            if (myHand.get(i).getSelected()) {
//                selected++;
//            }
//        }
//
//        if (selected < 3) {
//            for (int i = 0; i < d.size(); i++) {
//                myHand.get(i).setSelected(false);
//            }
//        }
    }

    private ArrayList<Card> canMeld(Deck d){
        ArrayList<Card> myHand = d.getCards();
        ArrayList<Card> meldArray;

        for(int i=0; i<myHand.size(); i++){
            meldArray = new ArrayList<Card>();

            // rank of current card to match with
            Rank rank = myHand.get(i).getRank();

            // search for sets of three of the same rank or wild cards
            for(int j=i; j<myHand.size(); j++){
                if(myHand.get(j).getRank() == rank){
                    meldArray.add(myHand.get(j));
                }
            }
            // if set of three or more found, return meld
            if(meldArray.size()>=2){
                return meldArray;
            }
        }

        // if no legal meld found, return null
        return null;
    }

    private Card discard(Deck d) {
        ArrayList<Card> myHand = d.getCards();
        Card discardCard;
        Rank r;
        boolean pair;

        for (int i = 0; i < myHand.size(); i++) {
            r = myHand.get(i).getRank();
            pair = false;
            if (r.shortName() == '2' || r.shortName() == 'R') {

            }
            else {
                for (int j = i+1; j < myHand.size(); j++) {
                    if (r == myHand.get(j).getRank()) {
                        pair = true;
                    }
                }
            }

            if (!pair) {
                discardCard = myHand.get(i);
                return discardCard;
            }
        }
        return null;
    }
}
