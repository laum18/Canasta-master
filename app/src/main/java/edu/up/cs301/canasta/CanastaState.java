package edu.up.cs301.canasta;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Contains the state of a Slapjack game.  Sent by the game when
 * a player wants to enquire about the state of the game.  (E.g., to display
 * it, or to help figure out its next move.)
 *
 * @author Steven R. Vegdahl 
 * @version July 2013
 */
public class CanastaState extends GameState
{
	private static final long serialVersionUID = -8269749892027578792L;

	///////////////////////////////////////////////////
	// ************** instance variables ************
	///////////////////////////////////////////////////

	// the three piles of cards:
	//  - 0: pile for player 0
	//  - 1: pile for player 1
	//  - 2: the "up" pile, where the top card
	// Note that when players receive the state, all but the top card in all piles
	// are passed as null.
	private Deck[] piles;
	// whose turn is it to draw turn a card?
	private int toPlay;
	private int goal;
	private int teamOneRoundScore;
	private int teamTwoRoundScore;
	private int teamOneTotalScore;
	private int teamTwoTotalScore;

	public int substage;

	public Card[][] myTeamMeld;
	public Card[][] otherTeamMeld;
	public Card[] temp;


	/**
	 * Constructor for objects of class SJState. Initializes for the beginning of the
	 * game, with a random player as the first to turn card
	 *
	 */

	public CanastaState() {
		// randomly pick the player who starts

		toPlay = (int)(2*Math.random());
		toPlay = 0;//(int)(2*Math.random());

		teamOneRoundScore = 0;
		teamTwoRoundScore = 0;
		teamOneTotalScore = 0;
		teamTwoTotalScore = 0;
		goal = 5000;

		// 0 = drawDeck/drawDiscard stage, 1 = meldCard/discard stage
		substage = 0;

		// initialize the decks as follows:
		// - each player deck (#0 and #1) gets half the cards, randomly
		//   selected
		// - the middle deck (#2) is empty
		piles = new Deck[6];
		piles[0] = new Deck(); // create empty deck (initial deck)
		piles[1] = new Deck(); // create empty deck (discard)
		piles[2] = new Deck(); // player 0 deck (you)
		piles[3] = new Deck(); // player 1 deck (left)
		piles[4] = new Deck(); // player 2 deck (teammate)
		piles[5] = new Deck(); // player 3 deck (right)
		piles[0].add52(); // give all cards to deck
		piles[0].shuffle(); // shuffle the cards
		// move cards to opponent, until to piles have ~same size
    	/*while (piles[toPlay].size() >=
    			piles[1-toPlay].size()+1) {
    		piles[toPlay].moveTopCardTo(piles[1-toPlay]);
    	}*/

		/* deals 11 cards to each player */
		for(int i=0; i<11; i++){ //card
			for(int j=0; j<4; j++){ //player
				piles[0].moveTopCardTo(piles[j+2]);
				//Log.i("card",getTopCard(piles[j+2]));
			}
		}
	}

	/**
	 * Copy constructor for objects of class SJState. Makes a copy of the given state
	 *
	 * @param orig  the state to be copied
	 */
	public CanastaState(CanastaState orig) {
		// set index of player whose turn it is
		toPlay = orig.toPlay;
		teamOneRoundScore = orig.teamOneRoundScore;
		teamOneTotalScore = orig.teamOneTotalScore;
		teamTwoRoundScore = orig.teamTwoRoundScore;
		teamTwoTotalScore = orig.teamTwoTotalScore;
		goal = orig.goal;
		substage = orig.substage;

		// create new deck array, making copy of each deck
		piles = new Deck[6];
		piles[0] = new Deck(orig.piles[0]); //actual deck
		piles[1] = new Deck(orig.piles[1]); // discard
		piles[2] = new Deck(orig.piles[2]); // human player's deck
		piles[3] = new Deck(orig.piles[3]); // left player
		piles[4] = new Deck(orig.piles[4]); // teammate's deck
		piles[5] = new Deck(orig.piles[5]); // right player
	}

	/**
	 * Gives the given deck.
	 *
	 * @return  the deck for the given player, or the middle deck if the
	 *   index is 2
	 */
	public Deck getDeck(int num) {
		if (num < 0 || num > 5) return null;
		return piles[num];
	}

	/**
	 * Tells which player's turn it is.
	 *
	 * @return the index (0 or 1) of the player whose turn it is.
	 */
	public int toPlay() {
		return toPlay;
	}

	/**
	 * change whose move it is
	 *
	 * @param idx
	 * 		the index of the player whose move it now is
	 */
	public void setToPlay(int idx) {
		toPlay = idx;
	}

	/**
	 * Replaces all cards with null, except for the top card of deck 2
	 */
	public void nullAllButTopOf2() {
		// see if the middle deck is empty; remove top card from middle deck
		boolean empty2 = piles[0].size() == 0;
		Card c = piles[0].removeTopCard();

		// set all cards in deck to null
		/*for (Deck d : piles) {
			d.nullifyDeck();
		}*/

		for (int d = 3; d < 6; d++) {
			getDeck(d).nullifyDeck();
		}

		// if middle deck had not been empty, add back the top (non-null) card
		if (!empty2) {
			piles[0].add(c);
		}
	}

	/**
	 * Get team one's total score
	 * @return teamOneTotalScore
     */
	public int getTeamOneTotalScore() {
		return teamOneTotalScore;
	}

	/**
	 * Get team two's total score
	 * @return teamTwoTotalScore
	 */
	public int getTeamTwoTotalScore() {
		return teamTwoTotalScore;
	}

	/**
	 * Get team two's round score
	 * @return teamTwoRoundScore
	 */
	public int getTeamTwoRoundScore() {
		return teamTwoRoundScore;
	}

	/**
	 * Get team one's round score
	 * @return teamOneRoundScore
	 */
	public int getTeamOneRoundScore() {
		return teamOneRoundScore;
	}

	public int getGoal() {
		return goal;
	}

	public void setTeamOneRoundScore(int scoreOne) {
		teamOneRoundScore += scoreOne;
	}

	public void setTeamTwoRoundScore(int scoreTwo) {
		teamTwoRoundScore += scoreTwo;
	}

	public void setTeamOneTotalScore(int totalScore) {
		teamOneTotalScore += totalScore;
	}

	public void setTeamTwoTotalScore(int totalScore) {
		teamTwoTotalScore += totalScore;
	}

	public boolean canMeld(Card[] cards) {
		Rank rank = cards[0].getRank();
		boolean r = false;
		for (int i = 1; i < cards.length - 1; i++) {
			if (rank.equals(cards[i].getRank())) {
				r = true;
			} else {
				r = false;
			}
		}
		return r;
	}

	public void discardCard(Card c) {
		Deck player = getDeck(toPlay);
		Deck discard = getDeck(2);
		player.removeCard(c);
		discard.add(c);
	}

	public boolean canDiscard(Card c) {
		Deck player = getDeck(toPlay);
		if (player.containsCard(c)) {
			return true;
		} else {
			return false;
		}
	}

	int count = 0;

	public void drawCard(Card c) {
		Log.i(c.toString(), "skdgfkad");
		count++;
		Log.i("" +count, "" +count);
		Deck player = getDeck(2);
		Deck deck = getDeck(0);
		ArrayList<Card> ca = deck.getCards();
		ca.remove(c);
		player.add(c);
		//return c;
	}

	public void Meld(Card[] c) {
		Deck player = getDeck(toPlay);
		Deck meld = getDeck(2);
		for (int i = 0; i <c.length; i++) {
			player.removeCard(c[i]);
			meld.add(c[i]);
		}
	}




}
