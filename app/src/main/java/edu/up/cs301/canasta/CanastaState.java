package edu.up.cs301.canasta;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Contains the state of a Canasta game.  Sent by the game when
 * a player wants to enquire about the state of the game.  (E.g., to display
 * it, or to help figure out its next move.)
 *
 * @author Steven R. Vegdahl, Aaron Banobi, Nick Edwards, Michelle Lau, and David Vandewark
 * @version July 2013
 */
public class CanastaState extends GameState {
	private static final long serialVersionUID = -8269749892027578792L;

	///////////////////////////////////////////////////
	// ************** instance variables ************
	///////////////////////////////////////////////////

	// the three piles of cards:
	//  - 0: pile for draw deck
	//  - 1: pile for discard deck
	//  - 2: pile for player 0's deck
	//  - 3: pile for player 1's deck
	//  - 4: pile for player 2's deck
	//  - 5: pile for player 3's deck
	//  - 6: pile for drawing melded cards
	// Note that when players receive the state, all but the top card in all piles
	// are passed as null.
	private Deck[] piles;
	// whose turn is it to draw turn a card?
	private int toPlay;
	// the goal to reach to end the game
	private int goal;
	// keeps track of each team's round and total score
	private int teamOneRoundScore;
	private int teamTwoRoundScore;
	private int teamOneTotalScore;
	private int teamTwoTotalScore;

	// the substage of the current state
	public int substage;

	// an arrayList to keep track of team one's melded card
	public ArrayList<Card> myTeamMeld = new ArrayList<Card>();
	// an arrayList to keep track of team two's melded card
	public ArrayList<Card> otherTeamMeld = new ArrayList<Card>();

	public Card[] temp;

	// an array of ints to check if a Canasta point was added for that rank for team one
	int[] canastaCheckerOne = new int[12];
	// an array of ints to check if a Canasta point was added for that rank for team two
	int[] canastaCheckerTwo = new int[12];

	// instance variables to keep track of how many melded cards there are for each rank for team one
	public int three = 0;
	public int four = 0;
	public int five = 0;
	public int six = 0;
	public int seven = 0;
	public int eight = 0;
	public int nine = 0;
	public int ten = 0;
	public int jack = 0;
	public int queen = 0;
	public int king = 0;
	public int ace = 0;

	// instance variables to keep track of how many melded cards there are for each rank for team two
	public int oppThree = 0;
	public int oppFour = 0;
	public int oppFive = 0;
	public int oppSix = 0;
	public int oppSeven = 0;
	public int oppEight = 0;
	public int oppNine = 0;
	public int oppTen = 0;
	public int oppJack = 0;
	public int oppQueen = 0;
	public int oppKing = 0;
	public int oppAce = 0;

	public boolean roundStarting;
	public boolean firstRound;

	// a boolean variable to check if the discard pile can be drawn
	public boolean canDrawDiscard;

	/**
	 * Constructor for objects of class CanastaState. Initializes for the beginning of the
	 * game, with player 0 as the first player to make a move
	 */

	public CanastaState() {

		// initialize player 0 to start the game
		toPlay = 0;

		// initialize each team's round and total score to 0
		teamOneRoundScore = 0;
		teamTwoRoundScore = 0;
		teamOneTotalScore = 0;
		teamTwoTotalScore = 0;

		// set the goal score to 2000 points
		goal = 2000;

		roundStarting = false;
		firstRound = true;

		// 0 = drawDeck/drawDiscard stage, 1 = meldCard/discard stage
		substage = 0;

		// initialize the decks as follows:
		// - the draw deck (#0) is empty
		// - the discard deck (#1) is empty
		// - each player deck (#2, #3, #4, and #5) is empty
		piles = new Deck[7];
		piles[0] = new Deck(); // create empty deck (initial deck)
		piles[1] = new Deck(); // create empty deck (discard)
		piles[2] = new Deck(); // player 0 deck (you)
		piles[3] = new Deck(); // player 1 deck (left)
		piles[4] = new Deck(); // player 2 deck (teammate)
		piles[5] = new Deck(); // player 3 deck (right)

		piles[0].add52(); // give all cards to deck

		piles[0].shuffle(); // shuffle the cards

		//add cards to the meld pile
		piles[6] = new Deck();
		piles[6].add(Card.fromString("3" + "D"));
		piles[6].add(Card.fromString("4" + "D"));
		piles[6].add(Card.fromString("5" + "D"));
		piles[6].add(Card.fromString("6" + "D"));
		piles[6].add(Card.fromString("7" + "D"));
		piles[6].add(Card.fromString("8" + "D"));
		piles[6].add(Card.fromString("9" + "D"));
		piles[6].add(Card.fromString("T" + "D"));
		piles[6].add(Card.fromString("J" + "D"));
		piles[6].add(Card.fromString("Q" + "D"));
		piles[6].add(Card.fromString("K" + "D"));
		piles[6].add(Card.fromString("A" + "D"));

		// move the top card of the draw deck to the discard deck
		piles[0].moveTopCardTo(piles[1]);

		// initialize to false
		canDrawDiscard = false;

		/* deals 11 cards to each player */
		for (int i = 0; i < 11; i++) { //card
			for (int j = 0; j < 4; j++) { //player
				piles[0].moveTopCardTo(piles[j + 2]);
			}
		}
	}

	/**
	 * Copy constructor for objects of class CanastaState. Makes a copy of the given state
	 *
	 * @param orig the state to be copied
	 */
	public CanastaState(CanastaState orig) {

		// set index of player whose turn it is
		toPlay = orig.toPlay;

		// set each team's round and total score to the original
		teamOneRoundScore = orig.teamOneRoundScore;
		teamOneTotalScore = orig.teamOneTotalScore;
		teamTwoRoundScore = orig.teamTwoRoundScore;
		teamTwoTotalScore = orig.teamTwoTotalScore;

		// set the goal to orig's goal
		goal = orig.goal;

		// set the substage to the orig's substage
		substage = orig.substage;

		// create new deck array, making copy of each deck
		piles = new Deck[7];
		piles[0] = new Deck(orig.piles[0]); //actual deck
		piles[1] = new Deck(orig.piles[1]); // discard
		piles[2] = new Deck(orig.piles[2]); // human player's deck
		piles[3] = new Deck(orig.piles[3]); // left player
		piles[4] = new Deck(orig.piles[4]); // teammate's deck
		piles[5] = new Deck(orig.piles[5]); // right player
		piles[6] = new Deck(orig.piles[6]); // meld piles

		// set to the orig's same variables
		three = orig.three;
		four = orig.four;
		five = orig.five;
		six = orig.six;
		seven = orig.seven;
		eight = orig.eight;
		nine = orig.nine;
		ten = orig.ten;
		jack = orig.jack;
		queen = orig.queen;
		king = orig.king;
		ace = orig.ace;

		// set to the orig's same variables
		oppThree = orig.oppThree;
		oppFour = orig.oppFour;
		oppFive = orig.oppFive;
		oppSix = orig.oppSix;
		oppSeven = orig.oppSeven;
		oppEight = orig.oppEight;
		oppNine = orig.oppNine;
		oppTen = orig.oppTen;
		oppJack = orig.oppJack;
		oppQueen = orig.oppQueen;
		oppKing = orig.oppKing;
		oppAce = orig.oppAce;

		// set to the orig's canDrawDiscard
		canDrawDiscard = orig.canDrawDiscard;
	}

	/**
	 * Gives the given deck.
	 *
	 * @return the deck for the given player or the draw/discard deck
	 */
	public Deck getDeck(int num) {
		// make sure num is valid
		if (num < 0 || num > 6) return null;
		// return the deck for num
		return piles[num];
	}

	/**
	 * Tells which player's turn it is.
	 *
	 * @return the index (0, 1, 2, or 3) of the player whose turn it is.
	 */
	public int toPlay() {
		return toPlay;
	}

	/**
	 * change whose move it is
	 *
	 * @param idx the index of the player whose move it now is
	 */
	public void setToPlay(int idx) {
		// set toPlay to idx
		toPlay = idx;
		// when it's the next player's turn, reset substage to 0
		substage = 0;
	}

	/**
	 * Replaces all cards with null, except for the top card of deck 2
	 */
	public void nullAllButTopOf2() { //TODO: fix this
		// see if the middle deck is empty; remove top card from middle deck
		boolean empty2 = piles[1].size() == 0;
		Card c = piles[1].removeTopCard();

		// set all cards in deck to null
		/*for (Deck d : piles) {
			d.nullifyDeck();
		}*/

		for (int d = 0; d < 6; d++) {
			if (d != toPlay + 2) {
				getDeck(d).nullifyDeck();
			}
		}

		// if middle deck had not been empty, add back the top (non-null) card
		if (!empty2) {
			piles[1].add(c);
		}
	}

	/**
	 * Get team one's total score
	 *
	 * @return teamOneTotalScore
	 */
	public int getTeamOneTotalScore() {
		return teamOneTotalScore;
	}

	/**
	 * Get team two's total score
	 *
	 * @return teamTwoTotalScore
	 */
	public int getTeamTwoTotalScore() {
		return teamTwoTotalScore;
	}

	/**
	 * Get team two's round score
	 *
	 * @return teamTwoRoundScore
	 */
	public int getTeamTwoRoundScore() {
		return teamTwoRoundScore;
	}

	/**
	 * Get team one's round score
	 *
	 * @return teamOneRoundScore
	 */
	public int getTeamOneRoundScore() {
		return teamOneRoundScore;
	}

	/**
	 * Get the goal to reach
	 *
	 * @return goal
	 */
	public int getGoal() {
		return goal;
	}

	public void setTeamOneTotalScore(int totalScore) {
		teamOneTotalScore += totalScore;
	}

	public void setTeamTwoTotalScore(int totalScore) {
		teamTwoTotalScore += totalScore;
	}

	/**
	 * A method to check if the given cards can be melded
	 *
	 * @param cards The given cards to check if it can be melded
	 * @param hasWildCard A boolean to check if the cards passed in has a wildcard
     * @return if we can meld the given cards or not
     */
	public boolean canMeld(Card[] cards, boolean hasWildCard) {
		// check that the array of the given cards is not null
		if (cards.length == 0) {
			return false;
		}

		// get the rank of the first card
		Rank rank = cards[0].getRank();

		// a counter to check how many cards are selected
		int check = 0;

		// a boolean to see what the return value is and initialized to false
		boolean r = false;

		// a counter to keep track of the number of wildcards
		int numWild = 0;

		// checks if the player selected at least three cards
		if (cards != null && cards.length > 0) {

			// loop through cards
			for (int i = 0; i < cards.length; i++) {
				// if the card is a wildcard
				if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2') {
					// increment numWild
					numWild++;
				}
			}

			// if the number of wildcards is equal to the size of cards
			if (numWild == cards.length) {
				// return false; cannot meld only wildcards
				return false;
			}

			// reset numWild back to 0
			numWild = 0;

			// loop through to the length of cards-1
			for (int i = 0; i < cards.length - 1; i++) {
				// check if the card is a wildcard
				if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2') {
					// increment numWild
					numWild++;
				}

				// loop through the length of cards-1 starting at the card after i
				for (int j = i; j < cards.length - 1; j++) {
					// if the two cards are equal
					if (cards[i].getRank().equals(cards[j+1].getRank())) {
						// check if either card is a wildcard
						if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2'
								|| cards[j + 1].getRank().shortName() == 'R' || cards[j + 1].getRank().shortName() == '2') {

							// if it is, do nothing
						}
						// if neither cards is a wildcard
						else {
							// increment check
							check++;
						}
					}
					// if either cards is a wildcard
					else if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2'
							|| cards[j+1].getRank().shortName() == 'R' || cards[j+1].getRank().shortName() == '2') {
						// do nothing
					}
					else {
						// set r to false
						r = false;
						// return r
						return r;
					}
				}
			}
		}

		// check if the last card in cards is a wildcard
		if (cards[cards.length-1].getRank().shortName() == 'R' || cards[cards.length-1].getRank().shortName() == '2') {
			// increment numWild
			numWild++;
		}

		// if there is at least one wildcard and at least one non-wildcard
		if (check >= 1 && hasWildCard) {
			// check that there is not more wildcards than non-wildcards
			if (numWild > check) {
				return false;
			}
			// set r to true
			r = true;
			// return r
			return r;

		}
		// if all selected cards are the same rank with no wildcards
		else if (check > 2) {
			// set r to true
			r = true;
			// return r
			return r;

		}
		// if they are trying to meld a card that has an existing melded pile
		else {
			// set r to false
			r = false;

			// check what rank is and the melded pile for that rank exists
			// if it does, set r to true
			if (rank.shortName() == '3' && three > 0) {
				r = true;
			} else if (rank.shortName() == '4' && four > 0) {
				r = true;
			} else if (rank.shortName() == '5' && five > 0) {
				r = true;
			} else if (rank.shortName() == '6' && six > 0) {
				r = true;
			} else if (rank.shortName() == '7' && seven > 0) {
				r = true;
			} else if (rank.shortName() == '8' && eight > 0) {
				r = true;
			} else if (rank.shortName() == '9' && nine > 0) {
				r = true;
			} else if (rank.shortName() == 'T' && ten > 0) {
				r = true;
			} else if (rank.shortName() == 'J' && jack > 0) {
				r = true;
			} else if (rank.shortName() == 'Q' && queen > 0) {
				r = true;
			} else if (rank.shortName() == 'K' && king > 0) {
				r = true;
			} else if (rank.shortName() == 'A' && ace > 0) {
				r = true;
			}
		}

		// return r
		return r;
	}

	//removes the card from the players hand and adds it to the dicard pile
	//also increments whose turn it is
	public void discardCard(Card c) {
		Deck player = getDeck(toPlay + 2);
		Deck discard = getDeck(1);
		System.out.println(c +" is selected");
		player.removeCard(c);
		System.out.println(c +" is removed");
		c.setSelected(true);

		if(getDeck(0).size() == 0 || getDeck(toPlay+2).size() == 0){
			System.out.println("Round Over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			newRound();
		}

		if (toPlay == 3) {
			setToPlay(0);
		} else {
			setToPlay(toPlay + 1);
		}
		discard.add(c);
	}

	//check to make sure the card to be discarded is in fact in the players hand
	public boolean canDiscard(Card c) {
		Deck player = getDeck(toPlay+2);
		if (player.containsCard(c)) {
			return true;
		} else {
			return false;
		}
	}

	int count = 0;

	public void drawCard(Card c) {
		count++;
		//Log.i("" +count, "" +count);
		Deck player = getDeck(toPlay() + 2);
		Deck deck = getDeck(0);
		ArrayList<Card> ca = deck.getCards();
		ca.remove(c);
		Log.i("deck size", "" + getDeck(0).size());
		player.add(c);
		//return c;
	}

	public void drawDiscard(Card c, ArrayList<Card> list) {
		Deck player = getDeck(toPlay + 2);
		Deck discardDeck = getDeck(1);
		int count = 0;
		boolean wildcard = false;

		// find how many cards are selected
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getSelected() == true) {
				count++;
			}
		}

		// create a new array of the selected cards
		Card[] selected = new Card[count + 1];

		int temp = 0;
		// move the selected cards into the new array
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getSelected()) {
				selected[temp] = list.get(i);
				temp++;
			}
		}

		// set the discarded card to true
		c.setSelected(true);

		// add to the selected array
		selected[count] = c;

		// check if there are any wildcards selected
		for (int i = 0; i < selected.length; i++) {
			if (selected[i].getRank().shortName() == 'R' || selected[i].getRank().shortName() == '2') {
				wildcard = true;
			}
		}

		// check if it can be melded
		if (canMeld(selected, wildcard)) {
			canDrawDiscard = true;
			Rank r = selected[0].getRank();

			// check if the first selected card is a wildcard
			if (selected[0].getRank().shortName() == 'R' || selected[0].getRank().shortName() == '2') {
				r = selected[1].getRank();
			}
			if(r.shortName()== 'R' || r.shortName() == '2'){
				r = selected[2].getRank();
			}
			if(r.shortName()== 'R' || r.shortName() == '2'){
				r = selected[3].getRank();
			}

			// update the gui according to the melded card's rank
			if (r.shortName() == '3') {
				three += selected.length;
				System.out.println("Three: " + three);
			} else if (r.shortName() == '4') {
				four += selected.length;
				System.out.println("Four: " + four);
			} else if (r.shortName() == '5') {
				five += selected.length;
				System.out.println("Five: " + five);
			} else if (r.shortName() == '6') {
				six += selected.length;
				System.out.println("Six: " + six);
			} else if (r.shortName() == '7') {
				seven += selected.length;
				System.out.println("Seven: " + seven);
			} else if (r.shortName() == '8') {
				eight += selected.length;
				System.out.println("Eight: " + eight);
			} else if (r.shortName() == '9') {
				nine += selected.length;
				System.out.println("Nine: " + nine);
			} else if (r.shortName() == 'T') {
				ten += selected.length;
				System.out.println("Ten: " + ten);
			} else if (r.shortName() == 'J') {
				jack += selected.length;
				System.out.println("Jack: " + jack);
			} else if (r.shortName() == 'Q') {
				queen += selected.length;
				System.out.println("Queen: " + queen);
			} else if (r.shortName() == 'K') {
				king += selected.length;
				System.out.println("King: " + king);
			} else if (r.shortName() == 'A') {
				ace += selected.length;
				System.out.println("Ace: " + ace);
			}

			// removes selected cards from player's hand and updates score
			for (int i = 0; i < selected.length; i++) {
				// check what player it is and add score to the appropriate team
				if (toPlay() == 0 || toPlay() == 2) {
					// add current card to team one's meld pile
					myTeamMeld.add(selected[i]);
					// if the current card's rank is 3,4,5,6, or 7
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						// add five points to the round score
						teamOneRoundScore += 5;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is 8,9,10,J,Q, or K
					else if (selected[i].getRank().shortName() == '8' || selected[i].getRank().shortName() == '9'
							|| selected[i].getRank().shortName() == 'T' || selected[i].getRank().shortName() == 'J'
							|| selected[i].getRank().shortName() == 'Q' || selected[i].getRank().shortName() == 'K') {
						// add 10 points to the round score
						teamOneRoundScore += 10;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is a 2 or Ace
					else if (selected[i].getRank().shortName() == '2' || selected[i].getRank().shortName() == 'A') {
						// add 20 points to the round score
						teamOneRoundScore += 20;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card is a joker
					else {
						// add 50 points to the round score
						teamOneRoundScore += 50;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}

					// check each rank's melded piles to see if a Canasta is made and
					// if a Canasta score was already added to the round score
					// depending on whether CanastaCheckerOne array is a 0 or 1
					if (three >= 7 && canastaCheckerOne[0] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[0] = 1;
					}
					else if (four >= 7 && canastaCheckerOne[1] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[1] = 1;
					}
					else if (five >= 7 && canastaCheckerOne[2] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[2] = 1;
					}
					else if (six >= 7 && canastaCheckerOne[3] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[3] = 1;
					}
					else if (seven >= 7 && canastaCheckerOne[4] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[4] = 1;
					}
					else if (eight >= 7 && canastaCheckerOne[5] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[5] = 1;
					}
					else if (nine >= 7 && canastaCheckerOne[6] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[6] = 1;
					}
					else if (ten >= 7 && canastaCheckerOne[7] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[7] = 1;
					}
					else if (jack>= 7 && canastaCheckerOne[8] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[8] = 1;
					}
					else if (queen >= 7 && canastaCheckerOne[9] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[0] = 1;
					}
					else if (king >= 7 && canastaCheckerOne[10] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[10] = 1;
					}
					else if (ace >= 7 && canastaCheckerOne[11] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[11] = 1;
					}
				}
				// check what player it is and add score to the appropriate team
				else  if (toPlay() == 1 || toPlay() == 3){
					// add current card to team two's meld pile
					otherTeamMeld.add(selected[i]);
					// if the current card's rank is 3,4,5,6, or 7
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						// add five points to the round score
						teamTwoRoundScore += 5;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is 8,9,10,J,Q, or K
					else if (selected[i].getRank().shortName() == '8' || selected[i].getRank().shortName() == '9'
							|| selected[i].getRank().shortName() == 'T' || selected[i].getRank().shortName() == 'J'
							|| selected[i].getRank().shortName() == 'Q' || selected[i].getRank().shortName() == 'K') {
						// add 10 points to the round score
						teamTwoRoundScore += 10;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is a 2 or Ace
					else if (selected[i].getRank().shortName() == '2' || selected[i].getRank().shortName() == 'A') {
						// add 20 points to the round score
						teamTwoRoundScore += 20;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is a joker
					else {
						// add 50 points to the round score
						teamTwoRoundScore += 50;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}

					// check each rank's melded piles to see if a Canasta is made and
					// if a Canasta score was already added to the round score
					// depending on whether CanastaCheckerTwo array is a 0 or 1
					if (oppThree >= 7 && canastaCheckerTwo[0] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[0] = 1;
					}
					else if (oppFour >= 7 && canastaCheckerTwo[1] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[1] = 1;
					}
					else if (oppFive >= 7 && canastaCheckerTwo[2] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[2] = 1;
					}
					else if (oppSix >= 7 && canastaCheckerTwo[3] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[3] = 1;
					}
					else if (oppSeven >= 7 && canastaCheckerTwo[4] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[4] = 1;
					}
					else if (oppEight >= 7 && canastaCheckerTwo[5] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[5] = 1;
					}
					else if (oppNine >= 7 && canastaCheckerTwo[6] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[6] = 1;
					}
					else if (oppTen >= 7 && canastaCheckerTwo[7] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[7] = 1;
					}
					else if (oppJack>= 7 && canastaCheckerTwo[8] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[8] = 1;
					}
					else if (oppQueen >= 7 && canastaCheckerTwo[9] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[0] = 1;
					}
					else if (oppKing >= 7 && canastaCheckerTwo[10] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[10] = 1;
					}
					else if (oppAce >= 7 && canastaCheckerTwo[11] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[11] = 1;
					}
				}
			}

			// add meld pile to player's hand
			// get tje size of the discard deck
			int discardSize = discardDeck.size();
			// loop through the discard deck
			for (int i = 0; i < discardSize; i++) {
				// set the top card of the discard deck to false
				discardDeck.peekAtTopCard().setSelected(false);
				// add the top card of the discard deck to player's deck
				player.add(discardDeck.removeTopCard());

			}

			// set the substage to 1
			substage = 1;
		}

	}

	/**
	 * A method to meld the given cards c
	 * @param c The list of cards to meld
     */
	public void Meld(ArrayList<Card> c) {
		// get the deck of the player
		Deck player = getDeck(toPlay + 2);

		// declare and initialize count to see how many cards are selected
		int count = 0;

		// declare and initialize wildCard to false
		boolean wildCard = false;

		// loop through c
		for (int i = 0; i < c.size(); i++) {
			// check if the current card is selected
			if (c.get(i).getSelected() == true) {
				// increment count
				count++;
			}
		}

		// create an array of Card, selected, of size count
		Card[] selected = new Card[count];

		// an int, temp, variable
		int temp = 0;

		// loop through c
		for (int i = 0; i < c.size(); i++) {
			// check if the card is selected
			if (c.get(i).getSelected() == true) {
				// put the card into selected at temp
				selected[temp] = c.get(i);
				// increment temp
				temp++;
			}
		}

		// loop through selected
		for (int i = 0; i < selected.length; i++) {
			// check if the card is a wildcard
			if (selected[i].getRank().shortName() == 'R' || selected[i].getRank().shortName() == '2') {
				// set wildCard to true
				wildCard = true;
			}
		}

		// check if selected can be meld
		if (canMeld(selected, wildCard)) {
			// get the rank of the first card in selected
			Rank r = selected[0].getRank();
			// if r is a wildcard
			if (r.shortName() == 'R' || r.shortName() == '2') {
				// get the rank of the next card
				r = selected[1].getRank();
			}

			// check the rank we are melding and increment the counter for that rank according to count
			// Print out that counter in order for us to know the program is working correctly
			if (r.shortName() == '3') {
				three += count;
				System.out.println("" + three);
			} else if (r.shortName() == '4') {
				four += count;
				System.out.println("" + four);
			} else if (r.shortName() == '5') {
				five += count;
				System.out.println("" + five);
			} else if (r.shortName() == '6') {
				six += count;
				System.out.println("" + six);
			} else if (r.shortName() == '7') {
				seven += count;
				System.out.println("" + seven);
			} else if (r.shortName() == '8') {
				eight += count;
				System.out.println("" + eight);
			} else if (r.shortName() == '9') {
				nine += count;
				System.out.println("" + nine);
			} else if (r.shortName() == 'T') {
				ten += count;
				System.out.println("" + ten);
			} else if (r.shortName() == 'J') {
				jack += count;
				System.out.println("" + jack);
			} else if (r.shortName() == 'Q') {
				queen += count;
				System.out.println("" + queen);
			} else if (r.shortName() == 'K') {
				king += count;
				System.out.println("" + king);
			} else if (r.shortName() == 'A') {
				ace += count;
				System.out.println("" + ace);
			}

			// loop through selected
			for (int i = 0; i < selected.length; i++) {
				// check what player it is and add score to the appropriate team
				if (toPlay() == 0 || toPlay() == 2) {
					// add current card to team one's meld pile
					myTeamMeld.add(selected[i]);
					// if the current card's rank is 3,4,5,6, or 7
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						// add five points to the round score
						teamOneRoundScore +=5;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is 8,9,10,J,Q, or K
					else if (selected[i].getRank().shortName() == '8' || selected[i].getRank().shortName() == '9'
							|| selected[i].getRank().shortName() == 'T' || selected[i].getRank().shortName() == 'J'
							|| selected[i].getRank().shortName() == 'Q' || selected[i].getRank().shortName() == 'K') {
						// add 10 points to the round score
						teamOneRoundScore +=10;
						// remove the card from player's deck
						player.removeCard(selected[i]);

					}
					// if the current card's rank is a 2 or Ace
					else if (selected[i].getRank().shortName() == '2' || selected[i].getRank().shortName() == 'A') {
						// add 20 points to the round score
						teamOneRoundScore += 20;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is a joker
					else {
						// add 50 points to the round score
						teamOneRoundScore += 50;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}

					// check each rank's melded piles to see if a Canasta is made and
					// if a Canasta score was already added to the round score
					// depending on whether CanastaCheckerOne array is a 0 or 1
					if (three >= 7 && canastaCheckerOne[0] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[0] = 1;
					}
					else if (four >= 7 && canastaCheckerOne[1] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[1] = 1;
					}
					else if (five >= 7 && canastaCheckerOne[2] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[2] = 1;
					}
					else if (six >= 7 && canastaCheckerOne[3] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[3] = 1;
					}
					else if (seven >= 7 && canastaCheckerOne[4] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[4] = 1;
					}
					else if (eight >= 7 && canastaCheckerOne[5] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[5] = 1;
					}
					else if (nine >= 7 && canastaCheckerOne[6] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[6] = 1;
					}
					else if (ten >= 7 && canastaCheckerOne[7] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[7] = 1;
					}
					else if (jack>= 7 && canastaCheckerOne[8] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[8] = 1;
					}
					else if (queen >= 7 && canastaCheckerOne[9] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[0] = 1;
					}
					else if (king >= 7 && canastaCheckerOne[10] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[10] = 1;
					}
					else if (ace >= 7 && canastaCheckerOne[11] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[11] = 1;
					}
				}
				// if the player is 1 or 3, add score to the team two's round score
				else {
					// add current card to team two's meld pile
					otherTeamMeld.add(selected[i]);
					// if the current card's rank is 3,4,5,6, or 7
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						// add five points to the round score
						teamTwoRoundScore +=5;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is 8,9,10,J,Q, or K
					else if (selected[i].getRank().shortName() == '8' || selected[i].getRank().shortName() == '9'
							|| selected[i].getRank().shortName() == 'T' || selected[i].getRank().shortName() == 'J'
							|| selected[i].getRank().shortName() == 'Q' || selected[i].getRank().shortName() == 'K') {
						// add 10 points to the round score
						teamTwoRoundScore += 10;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is a 2 or Ace
					else if (selected[i].getRank().shortName() == '2' || selected[i].getRank().shortName() == 'A') {
						// add 20 points to the round score
						teamTwoRoundScore += 20;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}
					// if the current card's rank is a joker
					else {
						// add 50 points to the round score
						teamTwoRoundScore += 50;
						// remove the card from player's deck
						player.removeCard(selected[i]);
					}

					// check each rank's melded piles to see if a Canasta is made and
					// if a Canasta score was already added to the round score
					// depending on whether CanastaCheckerTwo array is a 0 or 1
					if (oppThree >= 7 && canastaCheckerTwo[0] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[0] = 1;
					}
					else if (oppFour >= 7 && canastaCheckerTwo[1] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[1] = 1;
					}
					else if (oppFive >= 7 && canastaCheckerTwo[2] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[2] = 1;
					}
					else if (oppSix >= 7 && canastaCheckerTwo[3] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[3] = 1;
					}
					else if (oppSeven >= 7 && canastaCheckerTwo[4] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[4] = 1;
					}
					else if (oppEight >= 7 && canastaCheckerTwo[5] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[5] = 1;
					}
					else if (oppNine >= 7 && canastaCheckerTwo[6] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[6] = 1;
					}
					else if (oppTen >= 7 && canastaCheckerTwo[7] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[7] = 1;
					}
					else if (oppJack>= 7 && canastaCheckerTwo[8] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[8] = 1;
					}
					else if (oppQueen >= 7 && canastaCheckerTwo[9] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[0] = 1;
					}
					else if (oppKing >= 7 && canastaCheckerTwo[10] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[10] = 1;
					}
					else if (oppAce >= 7 && canastaCheckerTwo[11] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[11] = 1;
					}
				}

			}
		}

		if(getDeck(toPlay+2).size() == 0){
			System.out.println("Round Over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			newRound();
		}
	}

	public void computerMeld(ArrayList<Card> c, int playerNumber){

		Deck player = getDeck(playerNumber + 2);

		boolean wildCard = false;

		Card[] selected = new Card[c.size()];
		for (int i =0; i < c.size(); i++) {
			selected[i] = c.get(i);
		}

		for (int i = 0; i < selected.length; i++) {
			if (selected[i].getRank().shortName() == 'R' || selected[i].getRank().shortName() == '2') {
				wildCard = true;
			}
		}

		if (canMeld(selected, wildCard) == true) {
			Rank r = selected[0].getRank();
			int rank = 0;

			if(toPlay() == 0 || toPlay() == 2){
				if (r.shortName() == '3') {
					rank = 3;
					three += selected.length;
					System.out.println(""+three);
				} else if (r.shortName() == '4') {
					rank = 4;
					four += selected.length;
					System.out.println(""+four);
				} else if (r.shortName() == '5') {
					rank = 5;
					five += selected.length;
					System.out.println(""+five);
				} else if (r.shortName() == '6') {
					rank = 6;
					six += selected.length;
					System.out.println(""+six);
				} else if (r.shortName() == '7') {
					rank = 7;
					seven += selected.length;
					System.out.println(""+seven);
				} else if (r.shortName() == '8') {
					rank = 8;
					eight += selected.length;
					System.out.println(""+eight);
				} else if (r.shortName() == '9') {
					rank = 9;
					nine += selected.length;
					System.out.println(""+nine);
				} else if (r.shortName() == 'T') {
					rank = 10;
					ten += selected.length;
					System.out.println(""+ten);
				}
				else if (r.shortName() == 'J') {
					rank = 11;
					jack += selected.length;
					System.out.println(""+jack);
				}
				else if (r.shortName() == 'Q') {
					rank = 12;
					queen += selected.length;
					System.out.println(""+queen);
				}
				else if (r.shortName() == 'K') {
					rank = 13;
					king += selected.length;
					System.out.println(""+king);
				}
				else if (r.shortName() == 'A') {
					rank = 14;
					ace += selected.length;
					System.out.println(""+ace);
				}
			}
			else if(toPlay() == 1 || toPlay() == 3){
				if (r.shortName() == '2') {
					rank = 2;
				} else if (r.shortName() == '3') {
					rank = 3;
					oppThree += selected.length;
					System.out.println(""+three);
				} else if (r.shortName() == '4') {
					rank = 4;
					oppFour += selected.length;
					System.out.println(""+four);
				} else if (r.shortName() == '5') {
					rank = 5;
					oppFive += selected.length;
					System.out.println(""+five);
				} else if (r.shortName() == '6') {
					rank = 6;
					oppSix += selected.length;
					System.out.println(""+six);
				} else if (r.shortName() == '7') {
					rank = 7;
					oppSeven += selected.length;
					System.out.println(""+seven);
				} else if (r.shortName() == '8') {
					rank = 8;
					oppEight += selected.length;
					System.out.println(""+eight);
				} else if (r.shortName() == '9') {
					rank = 9;
					oppNine += selected.length;
					System.out.println(""+nine);
				} else if (r.shortName() == 'T') {
					rank = 10;
					oppTen += selected.length;
					System.out.println(""+ten);
				}
				else if (r.shortName() == 'J') {
					rank = 11;
					oppJack += selected.length;
					System.out.println(""+jack);
				}
				else if (r.shortName() == 'Q') {
					rank = 12;
					oppQueen += selected.length;
					System.out.println(""+queen);
				}
				else if (r.shortName() == 'K') {
					rank = 13;
					oppKing += selected.length;
					System.out.println(""+king);
				}
				else if (r.shortName() == 'A') {
					rank = 14;
					oppAce += selected.length;
					System.out.println(""+ace);
				}
			}


			for (int i = 0; i < selected.length; i++) {
				//System.out.println("I'm looking at deck number " +player);
				//player.removeCard(selected[i]);
				if (toPlay() == 0 || toPlay() == 2) {
					myTeamMeld.add(selected[i]);
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						teamOneRoundScore += 5;
						player.removeCard(selected[i]);

					}
					else if (selected[i].getRank().shortName() == '8' || selected[i].getRank().shortName() == '9'
							|| selected[i].getRank().shortName() == 'T' || selected[i].getRank().shortName() == 'J'
							|| selected[i].getRank().shortName() == 'Q' || selected[i].getRank().shortName() == 'K') {
						teamOneRoundScore +=10;
						player.removeCard(selected[i]);

					}
					else if (selected[i].getRank().shortName() == '2' || selected[i].getRank().shortName() == 'A'){
						teamOneRoundScore +=20;
						player.removeCard(selected[i]);

					}
					else {
						teamOneRoundScore += 50;
						player.removeCard(selected[i]);

					}

					if (three >= 7 && canastaCheckerOne[0] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[0] = 1;
					}
					else if (four >= 7 && canastaCheckerOne[1] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[1] = 1;
					}
					else if (five >= 7 && canastaCheckerOne[2] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[2] = 1;
					}
					else if (six >= 7 && canastaCheckerOne[3] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[3] = 1;
					}
					else if (seven >= 7 && canastaCheckerOne[4] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[4] = 1;
					}
					else if (eight >= 7 && canastaCheckerOne[5] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[5] = 1;
					}
					else if (nine >= 7 && canastaCheckerOne[6] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[6] = 1;
					}
					else if (ten >= 7 && canastaCheckerOne[7] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[7] = 1;
					}
					else if (jack>= 7 && canastaCheckerOne[8] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[8] = 1;
					}
					else if (queen >= 7 && canastaCheckerOne[9] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[0] = 1;
					}
					else if (king >= 7 && canastaCheckerOne[10] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[10] = 1;
					}
					else if (ace >= 7 && canastaCheckerOne[11] == 0) {
						teamOneRoundScore += 300;
						canastaCheckerOne[11] = 1;
					}
				}

				// else team two made the meld
				else if (toPlay() == 1 || toPlay() == 3){
					otherTeamMeld.add(selected[i]);
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						teamTwoRoundScore +=5;
						player.removeCard(selected[i]);

					}
					else if (selected[i].getRank().shortName() == '8' || selected[i].getRank().shortName() == '9'
							|| selected[i].getRank().shortName() == 'T' || selected[i].getRank().shortName() == 'J'
							|| selected[i].getRank().shortName() == 'Q' || selected[i].getRank().shortName() == 'K') {
						teamTwoRoundScore +=10;
						player.removeCard(selected[i]);

					}
					else if (selected[i].getRank().shortName() == '2' || selected[i].getRank().shortName() == 'A'){
						teamTwoRoundScore +=20;
						player.removeCard(selected[i]);

					}
					else if(selected[i].getRank().shortName() == 'R'){
						teamTwoRoundScore += 50;
						player.removeCard(selected[i]);

					}

					if (oppThree >= 7 && canastaCheckerTwo[0] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[0] = 1;
					}
					else if (oppFour >= 7 && canastaCheckerTwo[1] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[1] = 1;
					}
					else if (oppFive >= 7 && canastaCheckerTwo[2] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[2] = 1;
					}
					else if (oppSix >= 7 && canastaCheckerTwo[3] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[3] = 1;
					}
					else if (oppSeven >= 7 && canastaCheckerTwo[4] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[4] = 1;
					}
					else if (oppEight >= 7 && canastaCheckerTwo[5] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[5] = 1;
					}
					else if (oppNine >= 7 && canastaCheckerTwo[6] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[6] = 1;
					}
					else if (oppTen >= 7 && canastaCheckerTwo[7] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[7] = 1;
					}
					else if (oppJack>= 7 && canastaCheckerTwo[8] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[8] = 1;
					}
					else if (oppQueen >= 7 && canastaCheckerTwo[9] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[0] = 1;
					}
					else if (oppKing >= 7 && canastaCheckerTwo[10] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[10] = 1;
					}
					else if (oppAce >= 7 && canastaCheckerTwo[11] == 0) {
						teamTwoRoundScore += 300;
						canastaCheckerTwo[11] = 1;
					}
				}

			}
		}
		if(getDeck(0).size() == 0 || getDeck(toPlay+2).size() == 0){
			toPlay = -1;
			System.out.println("Round Over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			newRound();
		}
	}

	/**
	 * A method for the computer player to draw from the discard pile
	 *
	 * @param hand The arraylist being checked to see if it can be melded
	 * @param c The top card of the discard pile
	 * @param playerNumber The player trying to draw from the discard pile
     */
	public void computerMeldDiscard(ArrayList<Card> hand, Card c, int playerNumber){
		// get the rank of the top card of the discard pile
		Rank discardRank = c.getRank();
		// get the rank of the first card in the hand arraylist
		Rank handRank = hand.get(0).getRank();
		// declare meldDiscard and set it to false
		boolean meldDiscard = false;

		// if the discardRank is a wildcard rank
		if (discardRank == Rank.TWO || discardRank == Rank.RJOKER) {
			// if the player is on team one
			if(toPlay() == 0 || toPlay() == 2){
				// check the rank we are melding and increment the counter for that rank according to
				// the size of the hand + 1. Print out that counter in order for us to know the program
				// is working correctly
				if (handRank.shortName() == '3') {
					three += hand.size()+1;
					System.out.println(""+three);
				} else if (handRank.shortName() == '4') {
					four += hand.size()+1;
					System.out.println(""+four);
				} else if (handRank.shortName() == '5') {
					five += hand.size()+1;
					System.out.println(""+five);
				} else if (handRank.shortName() == '6') {
					six += hand.size()+1;
					System.out.println(""+six);
				} else if (handRank.shortName() == '7') {
					seven += hand.size()+1;
					System.out.println(""+seven);
				} else if (handRank.shortName() == '8') {
					eight += hand.size()+1;
					System.out.println(""+eight);
				} else if (handRank.shortName() == '9') {
					nine += hand.size()+1;
					System.out.println(""+nine);
				} else if (handRank.shortName() == 'T') {
					ten += hand.size()+1;
					System.out.println(""+ten);
				} else if (handRank.shortName() == 'J') {
					jack += hand.size()+1;
					System.out.println(""+jack);
				} else if (handRank.shortName() == 'Q') {
					queen += hand.size()+1;
					System.out.println(""+queen);
				} else if (handRank.shortName() == 'K') {
					king += hand.size()+1;
					System.out.println(""+king);
				} else if (handRank.shortName() == 'A') {
					ace += hand.size()+1;
					System.out.println(""+ace);
				}

				// check each rank's melded piles to see if a Canasta is made and
				// if a Canasta score was already added to the round score
				// depending on whether CanastaCheckerOne array is a 0 or 1
				if (three >= 7 && canastaCheckerOne[0] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[0] = 1;
				}
				else if (four >= 7 && canastaCheckerOne[1] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[1] = 1;
				}
				else if (five >= 7 && canastaCheckerOne[2] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[2] = 1;
				}
				else if (six >= 7 && canastaCheckerOne[3] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[3] = 1;
				}
				else if (seven >= 7 && canastaCheckerOne[4] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[4] = 1;
				}
				else if (eight >= 7 && canastaCheckerOne[5] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[5] = 1;
				}
				else if (nine >= 7 && canastaCheckerOne[6] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[6] = 1;
				}
				else if (ten >= 7 && canastaCheckerOne[7] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[7] = 1;
				}
				else if (jack>= 7 && canastaCheckerOne[8] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[8] = 1;
				}
				else if (queen >= 7 && canastaCheckerOne[9] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[0] = 1;
				}
				else if (king >= 7 && canastaCheckerOne[10] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[10] = 1;
				}
				else if (ace >= 7 && canastaCheckerOne[11] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[11] = 1;
				}
			}
			// if the player is on team two
			else if(toPlay() == 1 || toPlay() == 3){
				// check the rank we are melding and increment the counter for that rank according to
				// the size of the hand + 1. Print out that counter in order for us to know the program
				// is working correctly
				if (handRank.shortName() == '3') {
					oppThree += hand.size()+1;
					System.out.println(""+three);
				} else if (handRank.shortName() == '4') {
					oppFour += hand.size()+1;
					System.out.println(""+four);
				} else if (handRank.shortName() == '5') {
					oppFive += hand.size()+1;
					System.out.println(""+five);
				} else if (handRank.shortName() == '6') {
					oppSix += hand.size()+1;
					System.out.println(""+six);
				} else if (handRank.shortName() == '7') {
					oppSeven += hand.size()+1;
					System.out.println(""+seven);
				} else if (handRank.shortName() == '8') {
					oppEight += hand.size()+1;
					System.out.println(""+eight);
				} else if (handRank.shortName() == '9') {
					oppNine += hand.size()+1;
					System.out.println(""+nine);
				} else if (handRank.shortName() == 'T') {
					oppTen += hand.size()+1;
					System.out.println(""+ten);
				} else if (handRank.shortName() == 'J') {
					oppJack += hand.size()+1;
					System.out.println(""+jack);
				} else if (handRank.shortName() == 'Q') {
					oppQueen += hand.size()+1;
					System.out.println(""+queen);
				} else if (handRank.shortName() == 'K') {
					oppKing += count;
					System.out.println(""+king);
				} else if (handRank.shortName() == 'A') {
					oppAce += count;
					System.out.println(""+ace);
				}

				// check each rank's melded piles to see if a Canasta is made and
				// if a Canasta score was already added to the round score
				// depending on whether CanastaCheckerTwo array is a 0 or 1
				if (oppThree >= 7 && canastaCheckerTwo[0] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[0] = 1;
				}
				else if (oppFour >= 7 && canastaCheckerTwo[1] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[1] = 1;
				}
				else if (oppFive >= 7 && canastaCheckerTwo[2] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[2] = 1;
				}
				else if (oppSix >= 7 && canastaCheckerTwo[3] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[3] = 1;
				}
				else if (oppSeven >= 7 && canastaCheckerTwo[4] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[4] = 1;
				}
				else if (oppEight >= 7 && canastaCheckerTwo[5] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[5] = 1;
				}
				else if (oppNine >= 7 && canastaCheckerTwo[6] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[6] = 1;
				}
				else if (oppTen >= 7 && canastaCheckerTwo[7] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[7] = 1;
				}
				else if (oppJack>= 7 && canastaCheckerTwo[8] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[8] = 1;
				}
				else if (oppQueen >= 7 && canastaCheckerTwo[9] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[0] = 1;
				}
				else if (oppKing >= 7 && canastaCheckerTwo[10] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[10] = 1;
				}
				else if (oppAce >= 7 && canastaCheckerTwo[11] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[11] = 1;
				}
			}

			// set meldDiscard to true
			meldDiscard = true;
		}
		// check if handRank is the same rank as discardRank
		else if (handRank == discardRank){
			// if the player is on team one
			if(toPlay() == 0 || toPlay() == 2){
				// check the rank we are melding and increment the counter for that rank according to
				// the size of the hand + 1. Print out that counter in order for us to know the program
				// is working correctly
				if (handRank.shortName() == '3') {
					three += hand.size()+1;
					System.out.println(""+three);
				} else if (handRank.shortName() == '4') {
					four += hand.size()+1;
					System.out.println(""+four);
				} else if (handRank.shortName() == '5') {
					five += hand.size()+1;
					System.out.println(""+five);
				} else if (handRank.shortName() == '6') {
					six += hand.size()+1;
					System.out.println(""+six);
				} else if (handRank.shortName() == '7') {
					seven += hand.size()+1;
					System.out.println(""+seven);
				} else if (handRank.shortName() == '8') {
					eight += hand.size()+1;
					System.out.println(""+eight);
				} else if (handRank.shortName() == '9') {
					nine += hand.size()+1;
					System.out.println(""+nine);
				} else if (handRank.shortName() == 'T') {
					ten += hand.size()+1;
					System.out.println(""+ten);
				} else if (handRank.shortName() == 'J') {
					jack += hand.size()+1;
					System.out.println(""+jack);
				} else if (handRank.shortName() == 'Q') {
					queen += hand.size()+1;
					System.out.println(""+queen);
				} else if (handRank.shortName() == 'K') {
					king += hand.size()+1;
					System.out.println(""+king);
				} else if (handRank.shortName() == 'A') {
					ace += hand.size()+1;
					System.out.println(""+ace);
				}

				// check each rank's melded piles to see if a Canasta is made and
				// if a Canasta score was already added to the round score
				// depending on whether CanastaCheckerOne array is a 0 or 1
				if (three >= 7 && canastaCheckerOne[0] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[0] = 1;
				}
				else if (four >= 7 && canastaCheckerOne[1] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[1] = 1;
				}
				else if (five >= 7 && canastaCheckerOne[2] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[2] = 1;
				}
				else if (six >= 7 && canastaCheckerOne[3] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[3] = 1;
				}
				else if (seven >= 7 && canastaCheckerOne[4] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[4] = 1;
				}
				else if (eight >= 7 && canastaCheckerOne[5] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[5] = 1;
				}
				else if (nine >= 7 && canastaCheckerOne[6] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[6] = 1;
				}
				else if (ten >= 7 && canastaCheckerOne[7] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[7] = 1;
				}
				else if (jack>= 7 && canastaCheckerOne[8] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[8] = 1;
				}
				else if (queen >= 7 && canastaCheckerOne[9] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[0] = 1;
				}
				else if (king >= 7 && canastaCheckerOne[10] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[10] = 1;
				}
				else if (ace >= 7 && canastaCheckerOne[11] == 0) {
					teamOneRoundScore += 300;
					canastaCheckerOne[11] = 1;
				}
			}
			// if the player is on team two
			else if(toPlay() == 1 || toPlay() == 3){
				// check the rank we are melding and increment the counter for that rank according to
				// the size of the hand + 1. Print out that counter in order for us to know the program
				// is working correctly
				if (handRank.shortName() == '3') {
					oppThree += hand.size()+1;
					System.out.println(""+three);
				} else if (handRank.shortName() == '4') {
					oppFour += hand.size()+1;
					System.out.println(""+four);
				} else if (handRank.shortName() == '5') {
					oppFive += hand.size()+1;
					System.out.println(""+five);
				} else if (handRank.shortName() == '6') {
					oppSix += hand.size()+1;
					System.out.println(""+six);
				} else if (handRank.shortName() == '7') {
					oppSeven += hand.size()+1;
					System.out.println(""+seven);
				} else if (handRank.shortName() == '8') {
					oppEight += hand.size()+1;
					System.out.println(""+eight);
				} else if (handRank.shortName() == '9') {
					oppNine += hand.size()+1;
					System.out.println(""+nine);
				} else if (handRank.shortName() == 'T') {
					oppTen += hand.size()+1;
					System.out.println(""+ten);
				} else if (handRank.shortName() == 'J') {
					oppJack += hand.size()+1;
					System.out.println(""+jack);
				} else if (handRank.shortName() == 'Q') {
					oppQueen += hand.size()+1;
					System.out.println(""+queen);
				} else if (handRank.shortName() == 'K') {
					oppKing += count;
					System.out.println(""+king);
				} else if (handRank.shortName() == 'A') {
					oppAce += count;
					System.out.println(""+ace);
				}

				// check each rank's melded piles to see if a Canasta is made and
				// if a Canasta score was already added to the round score
				// depending on whether CanastaCheckerTwo array is a 0 or 1
				if (oppThree >= 7 && canastaCheckerTwo[0] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[0] = 1;
				}
				else if (oppFour >= 7 && canastaCheckerTwo[1] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[1] = 1;
				}
				else if (oppFive >= 7 && canastaCheckerTwo[2] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[2] = 1;
				}
				else if (oppSix >= 7 && canastaCheckerTwo[3] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[3] = 1;
				}
				else if (oppSeven >= 7 && canastaCheckerTwo[4] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[4] = 1;
				}
				else if (oppEight >= 7 && canastaCheckerTwo[5] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[5] = 1;
				}
				else if (oppNine >= 7 && canastaCheckerTwo[6] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[6] = 1;
				}
				else if (oppTen >= 7 && canastaCheckerTwo[7] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[7] = 1;
				}
				else if (oppJack>= 7 && canastaCheckerTwo[8] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[8] = 1;
				}
				else if (oppQueen >= 7 && canastaCheckerTwo[9] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[0] = 1;
				}
				else if (oppKing >= 7 && canastaCheckerTwo[10] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[10] = 1;
				}
				else if (oppAce >= 7 && canastaCheckerTwo[11] == 0) {
					teamTwoRoundScore += 300;
					canastaCheckerTwo[11] = 1;
				}
			}

			// set meldDiscard to true
			meldDiscard = true;
		}

		// if we can draw from the discard pile
		if (meldDiscard) {
			// add c to hand
			hand.add(c);
			// loop through hand
			for (int i = 0; i < hand.size(); i++) {
				// if player is on team one
				if (toPlay() == 0 || toPlay() == 2) {
					// add the card in hand to myTeamMeld
					myTeamMeld.add(hand.get(i));
					// if the current card's rank is 3,4,5,6, or 7
					if (hand.get(i).getRank().shortName() == '3' || hand.get(i).getRank().shortName() == '4' ||
							hand.get(i).getRank().shortName() == '5' || hand.get(i).getRank().shortName() == '6' ||
							hand.get(i).getRank().shortName() == '7') {
						// add five points to the round score
						teamOneRoundScore +=5;
						// remove the card from player's deck
						hand.remove(i);
					}
					// if the current card's rank is 8,9,10,J,Q, or K
					else if (hand.get(i).getRank().shortName() == '8' || hand.get(i).getRank().shortName() == '9'
							|| hand.get(i).getRank().shortName() == 'T' || hand.get(i).getRank().shortName() == 'J'
							|| hand.get(i).getRank().shortName() == 'Q' || hand.get(i).getRank().shortName() == 'K') {
						// add 10 points to the round score
						teamOneRoundScore +=10;
						// remove the card from player's deck
						hand.remove(i);
					}
					// if the current card's rank is a 2 or Ace
					else if (hand.get(i).getRank().shortName() == '2' || hand.get(i).getRank().shortName() == 'A'){
						// add 20 points to the round score
						teamOneRoundScore +=20;
						// remove the card from player's deck
						hand.remove(i);
					}
					// if the current card's rank is a joker
					else {
						// add 50 points to the round score
						teamOneRoundScore += 50;
						// remove the card from player's deck
						hand.remove(i);
					}

				}
				// else team two made the meld
				else if (toPlay() == 1 || toPlay() == 3){
					// add the card in hand to team two's meld pile
					otherTeamMeld.add(hand.get(i));
					// if the current card's rank is 3,4,5,6, or 7
					if (hand.get(i).getRank().shortName() == '3' || hand.get(i).getRank().shortName() == '4' ||
							hand.get(i).getRank().shortName() == '5' || hand.get(i).getRank().shortName() == '6' ||
							hand.get(i).getRank().shortName() == '7') {
						// add five points to the round score
						teamTwoRoundScore +=5;
						// remove the card from player's deck
						hand.remove(i);
					}
					// if the current card's rank is 8,9,10,J,Q, or K
					else if (hand.get(i).getRank().shortName() == '8' || hand.get(i).getRank().shortName() == '9'
							|| hand.get(i).getRank().shortName() == 'T' || hand.get(i).getRank().shortName() == 'J'
							|| hand.get(i).getRank().shortName() == 'Q' || hand.get(i).getRank().shortName() == 'K') {
						// add 10 points to the round score
						teamTwoRoundScore +=10;
						// remove the card from player's deck
						hand.remove(i);
					}
					// if the current card's rank is a 2 or Ace
					else if (hand.get(i).getRank().shortName() == '2' || hand.get(i).getRank().shortName() == 'A'){
						// add 20 points to the round score
						teamTwoRoundScore +=20;
						// remove the card from player's deck
						hand.remove(i);
					}
					// if the current card's rank is a joker
					else if(hand.get(i).getRank().shortName() == 'R'){
						// add 50 points to the round score
						teamTwoRoundScore += 50;
						// remove the card from player's deck
						hand.remove(i);
					}
				}
			}

			// add card to player's hand
			// get the size of the discard deck
			int discardSize = getDeck(1).size();
			// loop through the discard deck
			for (int i = 0; i < discardSize; i++) {
				// get each card in the discard deck to false
				getDeck(1).peekAtTopCard().setSelected(false);
				// add that card to player's deck
				getDeck(playerNumber + 2).add(getDeck(1).removeTopCard());

			}

			// set the substage to 1
			substage = 1;
		}
	}


	public CanastaState getState() {
		return this;
	}

	//sorts the cards in a given deck
	public Deck sortHand(Deck d) {
		int c1 = 0; //first card being compared
		int smallest = 0; //the smallest card that got compared to c1
		int c2 = 0; //second card being compared
		ArrayList<Card> cs1 = d.getCards(); //cards in the given deck

		ifNull:
		for (int i = 0; i < d.size() - 1; i++) {
			int index2 = i + 1;
			Rank r1 = null;
			Rank r2 = null;
			if (cs1.get(i) != null) {
				r1 = cs1.get(i).getRank();
			} else {
				break ifNull;
			}

			c1 = cs1.get(i).getCardValue(r1);
			smallest = c1;

			ifEqual:
			for (int j = (i + 1); j < d.size(); j++) {
				if (cs1.get(j) != null) {
					r2 = cs1.get(j).getRank();
				} else {

				}

				c2 = cs1.get(j).getCardValue(r2);
				if (c2 < smallest) {
					smallest = c2;
					index2 = j;
				} else if (c2 > smallest) {
					//do nothing
				}
			}
			//helper method that does the actual swapping
			piles[toPlay + 2] = piles[toPlay + 2].swapCards(d, cs1.get(i), cs1.get(index2), i, index2);
		}

		//returns a deck
		return piles[toPlay + 2];
	}

	//method to set the contents of a deck equal to a different deck
	public void setPlayerDeck(Deck d) {
		piles[toPlay+2] = d;
	}

	public void newRound(){

		System.out.println("Hello?");
		toPlay=0;
		substage=0;

		piles[0] = new Deck(); // create empty deck (initial deck)
		piles[1] = new Deck(); // create empty deck (discard)
		piles[2] = new Deck(); // player 0 deck (you)
		piles[3] = new Deck(); // player 1 deck (left)
		piles[4] = new Deck(); // player 2 deck (teammate)
		piles[5] = new Deck(); // player 3 deck (right)

		three = 0;
		four = 0;
		five = 0;
		six = 0;
		seven = 0;
		eight = 0;
		nine = 0;
		ten = 0;
		jack = 0;
		queen = 0;
		king = 0;
		ace = 0;

		oppThree = 0;
		oppFour = 0;
		oppFive = 0;
		oppSix = 0;
		oppSeven = 0;
		oppEight = 0;
		oppNine = 0;
		oppTen = 0;
		oppJack = 0;
		oppQueen = 0;
		oppKing = 0;
		oppAce = 0;

		teamOneTotalScore = teamOneRoundScore+teamOneTotalScore;
		teamTwoTotalScore = teamTwoRoundScore+teamTwoTotalScore;

		teamOneRoundScore = 0;
		teamTwoRoundScore = 0;

		for (int d = 0; d < 7; d++) {
			getDeck(d).nullifyDeck();
		}

		piles[0].add52(); // give all cards to deck

		piles[0].shuffle(); // shuffle the cards

		piles[0].moveTopCardTo(piles[1]);
		canDrawDiscard = false;

			/* deals 11 cards to each player */
		for(int i=0; i<11; i++){ //card
			for(int j=0; j<4; j++){ //player
				//Log.i("" +j, piles[0].getTopCard(piles[j+2]) +"");
				piles[0].moveTopCardTo(piles[j+2]);
			}
		}

		piles[6] = new Deck();
		piles[6].add(Card.fromString("3" + "D"));
		piles[6].add(Card.fromString("4" + "D"));
		piles[6].add(Card.fromString("5" + "D"));
		piles[6].add(Card.fromString("6" + "D"));
		piles[6].add(Card.fromString("7" + "D"));
		piles[6].add(Card.fromString("8" + "D"));
		piles[6].add(Card.fromString("9" + "D"));
		piles[6].add(Card.fromString("T" + "D"));
		piles[6].add(Card.fromString("J" + "D"));
		piles[6].add(Card.fromString("Q" + "D"));
		piles[6].add(Card.fromString("K" + "D"));
		piles[6].add(Card.fromString("A" + "D"));

		roundStarting = true;
	}
}
