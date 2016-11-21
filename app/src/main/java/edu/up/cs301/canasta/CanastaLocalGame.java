package edu.up.cs301.canasta;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * The LocalGame class for a slapjack game.  Defines and enforces
 * the game rules; handles interactions between players.
 * 
 * @author Steven R. Vegdahl 
 * @version July 2013
 */

public class CanastaLocalGame extends LocalGame {

    // the game's state
    CanastaState state;

	/**
	 * checks whether the game is over; if so, returns a string giving the result
	 *
	 * @result
	 * 		the end-of-game message, or null if the game is not over
	 */
	@Override
	protected String checkIfGameOver() {
		// human player and teammate are the winner
		if (state.getTeamOneTotalScore() >= state.getGoal()) {
			return this.playerNames[0] + "," + this.playerNames[3] + " is the winner";
		}
		// computer players are the winner
		else if (state.getTeamTwoTotalScore() >= state.getGoal()){
			return this.playerNames[2] + "," + this.playerNames[4] + " is the winner";
		}
		// the game is not over
		else {
			return null;
		}
	}

	/**
	 * checks whether the round is over.
	 */
	public String checkIfRoundOver() {
		if (state.getDeck(0).size() == 0) {
			state.setTeamOneTotalScore(state.getTeamOneTotalScore() + state.getTeamOneRoundScore());
			state.setTeamTwoTotalScore(state.getTeamTwoTotalScore() + state.getTeamTwoRoundScore());
			return "Round ended";
		}
		else if (state.getDeck(2).size() == 0 || state.getDeck(3).size() == 0
				||state.getDeck(4).size() == 0 || state.getDeck(5).size() == 0) {
			state.setTeamOneTotalScore(state.getTeamOneTotalScore() + state.getTeamOneRoundScore());
			state.setTeamTwoTotalScore(state.getTeamTwoTotalScore() + state.getTeamTwoRoundScore());
			return "Round ended";
		}
		else {
			return null;
		}
	}

    /**
     * Constructor for the SJLocalGame.
     */
    public CanastaLocalGame() {
        Log.i("SJLocalGame", "creating game");
        // create the state for the beginning of the game
        state = new CanastaState();
    }

    /**
     * sends the updated state to the given player. In our case, we need to
     * make a copy of the Deck, and null out all the cards except the top card
     * in the middle deck, since that's the only one they can "see"
     * 
     * @param p
     * 		the player to which the state is to be sent
     */
	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
		// if there is no state to send, ignore
		if (state == null) {
			return;
		}

		// make a copy of the state; null out all cards except for the
		// top card in the middle deck
		CanastaState stateForPlayer = new CanastaState(state); // copy of state
		stateForPlayer.nullAllButTopOf2(); // put nulls except for visible card
		
		// send the modified copy of the state to the player
		p.sendInfo(stateForPlayer);
	}
	
	/**
	 * whether a player is allowed to move
	 * 
	 * @param playerIdx
	 * 		the player-number of the player in question
	 */
	protected boolean canMove(int playerIdx) {
		if (playerIdx < 0 || playerIdx > 4) {
			// if our player-number is out of range, return false
			return false;
		}
		else {
			// player can move if it's their turn, or if the middle deck is non-empty
			// so they can slap
			return state.toPlay() == playerIdx;
		}
	}

	/**
	 * makes a move on behalf of a player
	 * 
	 * @param action
	 * 		the action denoting the move to be made
	 * @return
	 * 		true if the move was legal; false otherwise
	 */
	@Override
	protected boolean makeMove(GameAction action) {
		
		// check that we have slap-jack action; if so cast it
		if (!(action instanceof CanastaMoveAction)) {
			return false;
		} 
		CanastaMoveAction canastaMA = (CanastaMoveAction) action;
		
		// get the index of the player making the move; return false
		int thisPlayerIdx = getPlayerIdx(canastaMA.getPlayer());
		
		if (thisPlayerIdx < 0) { // illegal player
			return false;
		}

		if (canastaMA instanceof CanastaDrawDeckAction) {
			if(state.getDeck(0).getTopCard(state.getDeck(0))== null){
				return false;
			}
			else {
				state.drawCard(state.getDeck(0).getTopCard(state.getDeck(0)));
				state.getDeck(thisPlayerIdx);
				return true;
			}

		}
		else if (canastaMA instanceof CanastaDiscardAction) { // we have a "play" action
			// need to get the player's card
			Card c = CanastaHumanPlayer.getDiscard();
		}
		else if (canastaMA instanceof CanastaDrawDiscardAction) {
			//if (state.canMeld())
			state.drawCard(state.getDeck(1).getTopCard(state.getDeck(1)));
			for (int i = 0; i < state.getDeck(1).size(); i++) {
				state.getDeck(thisPlayerIdx).add(state.getDeck(1).getTopCard(state.getDeck(1)));
			}
		}
		else if (canastaMA instanceof CanastaMeldAction) {
			// need to get cards
			//state.canMeld()
		}

		// return true, because the move was successful if we get here
		return true;
	}
	
//	/**
//	 * helper method that gives all the cards in the middle deck to
//	 * a given player; also shuffles the target deck
//	 *
//	 * @param idx
//	 * 		the index of the player to whom the cards should be given
//	 */
//	private void giveMiddleCardsToPlayer(int idx) {
//		// illegal player: ignore
//		if (idx < 0 || idx > 1) return;
//
//		// move all cards from the middle deck to the target deck
//		state.getDeck(2).moveAllCardsTo(state.getDeck(idx));
//
//		// shuffle the target deck
//		state.getDeck(idx).shuffle();
//	}
}
