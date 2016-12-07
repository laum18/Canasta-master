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

	public ArrayList<Card> myTeamMeld = new ArrayList<Card>();
	public ArrayList<Card> otherTeamMeld = new ArrayList<Card>();
	public Card[] temp;

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

	public boolean canDrawDiscard;

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
		goal = 1000;

		// 0 = drawDeck/drawDiscard stage, 1 = meldCard/discard stage
		substage = 0;

		// initialize the decks as follows:
		// - each player deck (#0 and #1) gets half the cards, randomly
		//   selected
		// - the middle deck (#2) is empty
		piles = new Deck[7];
		piles[0] = new Deck(); // create empty deck (initial deck)
		piles[1] = new Deck(); // create empty deck (discard)
		piles[2] = new Deck(); // player 0 deck (you)
		piles[3] = new Deck(); // player 1 deck (left)
		piles[4] = new Deck(); // player 2 deck (teammate)
		piles[5] = new Deck(); // player 3 deck (right)
		piles[0].add52(); // give all cards to deck
		Log.i("","NUMBER OF CARDS "+piles[0].size());
		piles[0].shuffle(); // shuffle the cards
		// move cards to opponent, until to piles have ~same size
    	/*while (piles[toPlay].size() >=
    			piles[1-toPlay].size()+1) {
    		piles[toPlay].moveTopCardTo(piles[1-toPlay]);
    	}*/

		piles[6] = new Deck();
		piles[6].add(Card.fromString("3"+"D"));
		piles[6].add(Card.fromString("4"+"D"));
		piles[6].add(Card.fromString("5"+"D"));
		piles[6].add(Card.fromString("6"+"D"));
		piles[6].add(Card.fromString("7"+"D"));
		piles[6].add(Card.fromString("8"+"D"));
		piles[6].add(Card.fromString("9"+"D"));
		piles[6].add(Card.fromString("T"+"D"));
		piles[6].add(Card.fromString("J"+"D"));
		piles[6].add(Card.fromString("Q"+"D"));
		piles[6].add(Card.fromString("K"+"D"));
		piles[6].add(Card.fromString("A"+"D"));

		piles[0].moveTopCardTo(piles[1]);
		canDrawDiscard = false;

		/* deals 11 cards to each player */
		for(int i=0; i<11; i++){ //card
			for(int j=0; j<4; j++){ //player
				//Log.i("" +j, piles[0].getTopCard(piles[j+2]) +"");
				piles[0].moveTopCardTo(piles[j+2]);
			}
		}

		for (int i = 0; i < 35; i++) {
			piles[2] = sortHand(piles[2]); //must be called many times, not sure why
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
		piles = new Deck[7];
		piles[0] = new Deck(orig.piles[0]); //actual deck
		piles[1] = new Deck(orig.piles[1]); // discard
		piles[2] = new Deck(orig.piles[2]); // human player's deck
		piles[3] = new Deck(orig.piles[3]); // left player
		piles[4] = new Deck(orig.piles[4]); // teammate's deck
		piles[5] = new Deck(orig.piles[5]); // right player
		piles[6] = new Deck(orig.piles[6]); // meld piles

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

		canDrawDiscard = orig.canDrawDiscard;
	}

	/**
	 * Gives the given deck.
	 *
	 * @return  the deck for the given player, or the middle deck if the
	 *   index is 2
	 */
	public Deck getDeck(int num) {
		if (num < 0 || num > 6) return null;
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
			if(d != toPlay+2) {
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

	public boolean getCanDiscard() { return canDrawDiscard; }

	public boolean canMeld(Card[] cards, boolean hasWildCard) {
		if (cards.length == 0) {
			return false;
		}
		Rank rank = cards[0].getRank();
		int check = 0;
		boolean r = false;
		int wildCardCheck = 0;
		int numWild = 0;

		// checks if the player selected at least three cards
		if (cards != null && cards.length > 0) {

			// trying to meld all wildcards
			for (int i = 0; i < cards.length; i++) {
				if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2') {
					numWild++;
				}
			}

			if (numWild == cards.length) {
				return false;
			}

			for (int i = 0; i < cards.length - 1; i++) {
				if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2') {
					wildCardCheck++;
				}
				for (int j = i; j < cards.length - 1; j++) {
					if (cards[i].getRank().equals(cards[j + 1].getRank())) {
						if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2'
								|| cards[j + 1].getRank().shortName() == 'R' || cards[j + 1].getRank().shortName() == '2') {

						}
						else {
							check++;
						}
					} else if (cards[i].getRank().shortName() == 'R' || cards[i].getRank().shortName() == '2'
							|| cards[j + 1].getRank().shortName() == 'R' || cards[j + 1].getRank().shortName() == '2') {
//
					} else {
						r = false;
						return r;
					}
				}
			}
		}

		if (cards[cards.length-1].getRank().shortName() == 'R' || cards[cards.length-1].getRank().shortName() == '2') {
			wildCardCheck++;
		}
		// if there is at least one wildcard
		if (check >= 1 && hasWildCard == true) {
			if (wildCardCheck > check) {
				return false;
			}
			r = true;
			return r;

		}
		// if all selected cards are the same rank with no wildcards
		else if (check > 2) {

			r = true;
			return r;

		}
		// if they are trying to meld a card that has an existing melded pile
		else {
			r = false;
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
			}
			else if (rank.shortName() == 'J' && jack > 0) {
				r = true;
			}
			else if (rank.shortName() == 'Q' && queen > 0) {
				r = true;
			}
			else if (rank.shortName() == 'K' && king > 0) {
				r = true;
			}
			else if (rank.shortName() == 'A' && ace > 0) {
				r = true;
			}
		}
		return r;
	}

	public void discardCard(Card c) {
		Deck player = getDeck(toPlay+2);
		Deck discard = getDeck(1);
		player.removeCard(c);
		//c.setSelected(true);
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
		//Log.i(c.toString(), "skdgfkad");
		count++;
		//Log.i("" +count, "" +count);
		Deck player = getDeck(toPlay()+2);
		Deck deck = getDeck(0);
		ArrayList<Card> ca = deck.getCards();
		ca.remove(c);
		Log.i("deck size", ""+getDeck(0).size());
		player.add(c);
		//return c;
	}

	//TODO: need to increment meld plie count
	public void drawDiscard(Card c, ArrayList<Card> list) {
		Deck player = getDeck(toPlay + 2);
		Deck discardDeck = getDeck(1);
		int count = 0;
		boolean wildcard = false;

		// find how many cards are selected
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getSelected() == true){
				count++;
			}
		}

		// create a new array of the selected cards
		Card[] selected = new Card[count+1];

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
		// add to the new array
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

			// update the gui according to the melded card's rank
			if (r.shortName() == '3') {
				three += selected.length;
				System.out.println("Three: "+three);
			} else if (r.shortName() == '4') {
				four += selected.length;
				System.out.println("Four: "+four);
			} else if (r.shortName() == '5') {
				five += selected.length;
				System.out.println("Five: "+five);
			} else if (r.shortName() == '6') {
				six += selected.length;
				System.out.println("Six: "+six);
			} else if (r.shortName() == '7') {
				seven += selected.length;
				System.out.println("Seven: "+seven);
			} else if (r.shortName() == '8') {
				eight += selected.length;
				System.out.println("Eight: "+eight);
			} else if (r.shortName() == '9') {
				nine += selected.length;
				System.out.println("Nine: "+nine);
			} else if (r.shortName() == 'T') {
				ten += selected.length;
				System.out.println("Ten: "+ten);
			} else if (r.shortName() == 'J') {
				jack += selected.length;
				System.out.println("Jack: "+jack);
			} else if (r.shortName() == 'Q') {
				queen += selected.length;
				System.out.println("Queen: "+queen);
			} else if (r.shortName() == 'K') {
				king += selected.length;
				System.out.println("King: "+king);
			} else if (r.shortName() == 'A') {
				ace += selected.length;
				System.out.println("Ace: "+ace);
			}

			// removes selected cards from player's hand and updates score
			for (int i = 0; i < selected.length-1; i++) {
				if (toPlay() == 0 || toPlay() == 2) {
					myTeamMeld.add(selected[i]);
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						teamOneRoundScore +=5;
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
				}
				else {
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
					else {
						teamTwoRoundScore += 50;
						player.removeCard(selected[i]);
					}
				}
			}

			// removes top card from the discard pile & update score
			if (toPlay() == 0 || toPlay() == 2) {
				myTeamMeld.add(selected[selected.length-1]);

				if (selected[selected.length-1].getRank().shortName() == '3' || selected[selected.length-1].getRank().shortName() == '4'
						|| selected[selected.length-1].getRank().shortName() == '5' || selected[selected.length-1].getRank().shortName() == '6'
						|| selected[selected.length-1].getRank().shortName() == '7') {
					teamOneRoundScore += 5;
				} else if (selected[selected.length-1].getRank().shortName() == '8' || selected[selected.length-1].getRank().shortName() == '9'
						|| selected[selected.length-1].getRank().shortName() == 'T' || selected[selected.length-1].getRank().shortName() == 'J'
						|| selected[selected.length-1].getRank().shortName() == 'Q' || selected[selected.length-1].getRank().shortName() == 'K') {
					teamOneRoundScore += 10;
				} else if (selected[selected.length-1].getRank().shortName() == 'R' || selected[selected.length-1].getRank().shortName() == '2') {
					teamOneRoundScore += 20;
				} else {
					teamOneRoundScore += 50;
				}

				getDeck(1).removeCard(selected[selected.length-1]);
			}
			else {
				otherTeamMeld.add(selected[selected.length-1]);

				if (selected[selected.length-1].getRank().shortName() == '3' || selected[selected.length-1].getRank().shortName() == '4'
						|| selected[selected.length-1].getRank().shortName() == '5' || selected[selected.length-1].getRank().shortName() == '6'
						|| selected[selected.length-1].getRank().shortName() == '7') {
					teamTwoRoundScore += 5;
				} else if (selected[selected.length-1].getRank().shortName() == '8' || selected[selected.length-1].getRank().shortName() == '9'
						|| selected[selected.length-1].getRank().shortName() == 'T' || selected[selected.length-1].getRank().shortName() == 'J'
						|| selected[selected.length-1].getRank().shortName() == 'Q' || selected[selected.length-1].getRank().shortName() == 'K') {
					teamTwoRoundScore += 10;
				} else if (selected[selected.length-1].getRank().shortName() == 'R' || selected[selected.length-1].getRank().shortName() == '2') {
					teamTwoRoundScore += 20;
				} else {
					teamTwoRoundScore += 50;
				}

				getDeck(1).removeCard(selected[selected.length-1]);
			}

			// add meld pile to player's hand
			int discardSize = discardDeck.size();
			for (int i = 0; i < discardSize; i++) {
				discardDeck.peekAtTopCard().setSelected(false);
				player.add(discardDeck.removeTopCard());

			}
			substage = 1;
		}

	}

	/* method to represent cards being melded in the game state */
	public void Meld(ArrayList<Card> c) {
		Deck player = getDeck(toPlay + 2);
		int count = 0;
		boolean wildCard = false;

		for (int i = 0; i < c.size(); i++) {
			if (c.get(i).getSelected() == true) {
				count++;
			}
		}

		System.out.println(count+ " cards selected");

		Card[] selected = new Card[count];

		int temp = 0;
		for (int i =0; i < c.size(); i++) { //changed
			if (c.get(i).getSelected() == true) {
				selected[temp] = c.get(i);
				temp++;
			}
		}

		for (int i = 0; i < selected.length; i++) {
			if (selected[i].getRank().shortName() == 'R' || selected[i].getRank().shortName() == '2') {
				wildCard = true;
			}
		}

		if (canMeld(selected, wildCard)) {
			Rank r = selected[0].getRank();;

			if (selected[0].getRank().shortName() == 'R' || selected[0].getRank().shortName() == '2') {
				r = selected[1].getRank();
			}

			if (r.shortName() == '3') {
				three += count;
				System.out.println(""+three);
			} else if (r.shortName() == '4') {
				four += count;
				System.out.println(""+four);
			} else if (r.shortName() == '5') {
				five += count;
				System.out.println(""+five);
			} else if (r.shortName() == '6') {
				six += count;
				System.out.println(""+six);
			} else if (r.shortName() == '7') {
				seven += count;
				System.out.println(""+seven);
			} else if (r.shortName() == '8') {
				eight += count;
				System.out.println(""+eight);
			} else if (r.shortName() == '9') {
				nine += count;
				System.out.println(""+nine);
			} else if (r.shortName() == 'T') {
				ten += count;
				System.out.println(""+ten);
			}
			else if (r.shortName() == 'J') {
				jack += count;
				System.out.println(""+jack);
			}
			else if (r.shortName() == 'Q') {
				queen += count;
				System.out.println(""+queen);
			}
			else if (r.shortName() == 'K') {
				king += count;
				System.out.println(""+king);
			}
			else if (r.shortName() == 'A') {
				ace += count;
				System.out.println(""+ace);
			}



			for (int i = 0; i < selected.length; i++) {
				//player.removeCard(selected[i]);
				if (toPlay() == 0 || toPlay() == 2) {
					myTeamMeld.add(selected[i]);
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						teamOneTotalScore +=5;
						player.removeCard(selected[i]);
					}
					else if (selected[i].getRank().shortName() == '8' || selected[i].getRank().shortName() == '9'
							|| selected[i].getRank().shortName() == 'T' || selected[i].getRank().shortName() == 'J'
							|| selected[i].getRank().shortName() == 'Q' || selected[i].getRank().shortName() == 'K') {
						teamOneTotalScore +=10;
						player.removeCard(selected[i]);
					}
					else if (selected[i].getRank().shortName() == '2' || selected[i].getRank().shortName() == 'A'){
						teamOneTotalScore +=20;
						player.removeCard(selected[i]);
					}
					else {
						teamOneTotalScore += 50;
						player.removeCard(selected[i]);
					}

				}
				else {
					otherTeamMeld.add(selected[i]);
					teamTwoTotalScore += 5;
				}

			}
		}
	}

	public void computerMeld(ArrayList<Card> c){
		Deck player = getDeck(toPlay + 2);
		int count = 0;
		boolean wildCard = false;

		for (int i = 0; i < c.size(); i++) {
			count++;
		}


		Card[] selected = new Card[count];

		int temp = 0;
		for (int i =0; i < c.size(); i++) {
			selected[temp] = c.get(i);
			temp++;
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
				if (r.shortName() == '2') {
					rank = 2;
				} else if (r.shortName() == '3') {
					rank = 3;
					three += count;
					System.out.println(""+three);
				} else if (r.shortName() == '4') {
					rank = 4;
					four += count;
					System.out.println(""+four);
				} else if (r.shortName() == '5') {
					rank = 5;
					five += count;
					System.out.println(""+five);
				} else if (r.shortName() == '6') {
					rank = 6;
					six += count;
					System.out.println(""+six);
				} else if (r.shortName() == '7') {
					rank = 7;
					seven += count;
					System.out.println(""+seven);
				} else if (r.shortName() == '8') {
					rank = 8;
					eight += count;
					System.out.println(""+eight);
				} else if (r.shortName() == '9') {
					rank = 9;
					nine += count;
					System.out.println(""+nine);
				} else if (r.shortName() == 'T') {
					rank = 10;
					ten += count;
					System.out.println(""+ten);
				}
				else if (r.shortName() == 'J') {
					rank = 11;
					jack += count;
					System.out.println(""+jack);
				}
				else if (r.shortName() == 'Q') {
					rank = 12;
					queen += count;
					System.out.println(""+queen);
				}
				else if (r.shortName() == 'K') {
					rank = 13;
					king += count;
					System.out.println(""+king);
				}
				else if (r.shortName() == 'A') {
					rank = 14;
					ace += count;
					System.out.println(""+ace);
				}
			}
			else if(toPlay() == 1 || toPlay() == 3){
				if (r.shortName() == '2') {
					rank = 2;
				} else if (r.shortName() == '3') {
					rank = 3;
					oppThree += count;
					System.out.println(""+three);
				} else if (r.shortName() == '4') {
					rank = 4;
					oppFour += count;
					System.out.println(""+four);
				} else if (r.shortName() == '5') {
					rank = 5;
					oppFive += count;
					System.out.println(""+five);
				} else if (r.shortName() == '6') {
					rank = 6;
					oppSix += count;
					System.out.println(""+six);
				} else if (r.shortName() == '7') {
					rank = 7;
					oppSeven += count;
					System.out.println(""+seven);
				} else if (r.shortName() == '8') {
					rank = 8;
					oppEight += count;
					System.out.println(""+eight);
				} else if (r.shortName() == '9') {
					rank = 9;
					oppNine += count;
					System.out.println(""+nine);
				} else if (r.shortName() == 'T') {
					rank = 10;
					oppTen += count;
					System.out.println(""+ten);
				}
				else if (r.shortName() == 'J') {
					rank = 11;
					oppJack += count;
					System.out.println(""+jack);
				}
				else if (r.shortName() == 'Q') {
					rank = 12;
					oppQueen += count;
					System.out.println(""+queen);
				}
				else if (r.shortName() == 'K') {
					rank = 13;
					oppKing += count;
					System.out.println(""+king);
				}
				else if (r.shortName() == 'A') {
					rank = 14;
					oppAce += count;
					System.out.println(""+ace);
				}
			}



			for (int i = 0; i < selected.length; i++) {
				System.out.println("I'm looking at deck number " +player);
				//player.removeCard(selected[i]);
				if (toPlay() == 0 || toPlay() == 2) {
					myTeamMeld.add(selected[i]);
					if (selected[i].getRank().shortName() == '3' || selected[i].getRank().shortName() == '4' ||
							selected[i].getRank().shortName() == '5' || selected[i].getRank().shortName() == '6' ||
							selected[i].getRank().shortName() == '7') {
						teamOneRoundScore +=5;
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
				}

			}
		}
	}


	public CanastaState getState() {
		return this;
	}

	public Deck sortHand(Deck d) {
		Card temp;
		int index2 = 0;
		int c1 = 0;
		ArrayList<Card> cs1 = d.getCards();
		int c2 = 0;
		//ArrayList<Card> cs2 = d.getCards();
		ifNull:
		for (int i = 0; i < d.size()-1; i++) {
			Rank r1 = null;
			Rank r2 = null;
			if (cs1.get(i) != null) {
				r1 = cs1.get(i).getRank();
			} else {
				break ifNull;
			}
			if (r1.shortName() == '2') {
				c1 = 2;
			} else if (r1.shortName() == '3') {
				c1 = 3;
			} else if (r1.shortName() == '4') {
				c1 = 4;
			} else if (r1.shortName() == '5') {
				c1 = 5;
			} else if (r1.shortName() == '6') {
				c1 = 6;
			} else if (r1.shortName() == '7') {
				c1 = 7;
			} else if (r1.shortName() == '8') {
				c1 = 8;
			} else if (r1.shortName() == '9') {
				c1 = 9;
			}else if (r1.shortName() == 'T') {
				c1 = 10;
			} else if (r1.shortName() == 'J') {
				c1 = 11;
			} else if (r1.shortName() == 'Q') {
				c1 = 12;
			} else if (r1.shortName() == 'K') {
				c1 = 13;
			} else if (r1.shortName() == 'A') {
				c1 = 14;
			} else if (r1.shortName() == 'R') {
				c1 = 1;
			}
			int smallest = c1;
			ifEqual:
			for (int j = i+1; j < d.size(); j++) {
				if (cs1.get(j) != null) {
					r2 = cs1.get(j).getRank();
				} else {
					//break ifNull;
				}
				if (r2.shortName() == '2') {
					c2 = 2;
				} else if (r2.shortName() == '3') {
					c2 = 3;
				} else if (r2.shortName() == '4') {
					c2 = 4;
				} else if (r2.shortName() == '5') {
					c2 = 5;
				} else if (r2.shortName() == '6') {
					c2 = 6;
				} else if (r2.shortName() == '7') {
					c2 = 7;
				} else if (r2.shortName() == '8') {
					c2 = 8;
				} else if (r2.shortName() == '9') {
					c2 = 9;
				}else if (r2.shortName() == 'T') {
					c2 = 10;
				} else if (r2.shortName() == 'J') {
					c2 = 11;
				} else if (r2.shortName() == 'Q') {
					c2 = 12;
				} else if (r2.shortName() == 'K') {
					c2 = 13;
				} else if (r2.shortName() == 'A') {
					c2 = 14;
				} else if (r2.shortName() == 'R') {
					c2 = 1;
				}
				if (c2 < smallest) {
					smallest = c2;
					index2 = j;
				} else if (c2 > smallest) {
					//
				} else if (c2 == smallest) {
					//smallest = c2;
					//index2 = j;
				}
			}
			piles[toPlay+2] = piles[toPlay+2].swapCards(d, cs1.get(i), cs1.get(index2), i, index2);
		}
		return piles[toPlay+2];
	}

	public void setPlayerDeck(Deck d) {
		piles[2] = d;
	}
}
