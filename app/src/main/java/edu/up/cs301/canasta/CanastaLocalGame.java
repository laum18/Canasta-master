package edu.up.cs301.canasta;

import android.util.Log;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * The LocalGame class for a Canasta game.  Defines and enforces
 * the game rules; handles interactions between players.
 *
 * @author Steven R. Vegdahl, Nick Edwards, Aaron Banobi, Michele Lau, David Vandewark
 * @version July 2013
 */

public class CanastaLocalGame extends LocalGame {

    // the game's state
    CanastaState state;

    /**
     * checks whether the game is over; if so, returns a string giving the result
     *
     * @result the end-of-game message, or null if the game is not over
     */
    @Override
    protected String checkIfGameOver() {
        // human player and teammate are the winner

        if ((state.getTeamOneTotalScore() > state.getTeamTwoTotalScore()) && (state.getTeamOneRoundScore() >= state.getGoal())) {
            return this.playerNames[0] + "," + this.playerNames[2] + " is the winner";
        } else if (state.getTeamTwoTotalScore() > state.getTeamOneTotalScore() && (state.getTeamTwoRoundScore() >= state.getGoal())) {
            return this.playerNames[1] + "," + this.playerNames[3] + " is the winner";
        } else if (state.getTeamOneTotalScore() >= state.getGoal()) {
            return this.playerNames[0] + "," + this.playerNames[2] + " is the winner";
        }
        // computer players are the winner
        else if (state.getTeamTwoTotalScore() >= state.getGoal()) {
            return this.playerNames[1] + "," + this.playerNames[3] + " is the winner";
        }

        // the game is not over
        else {
            return null;
        }
    }

    /**
     * Constructor for the CanastaLocalGame.
     */
    public CanastaLocalGame() {
        Log.i("CanastaLocalGame", "creating game");
        // create the state for the beginning of the game
        state = new CanastaState();
    }

    /**
     * sends the updated state to the given player. In our case, we need to
     * make a copy of the Deck, and null out all the cards except the top card
     * in the middle deck, since that's the only one they can "see"
     *
     * @param p the player to which the state is to be sent
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        // if there is no state to send, ignore
        if (state == null) {
            return;
        }

        // make a copy of the state
        CanastaState stateForPlayer = new CanastaState(state); // copy of state
        //stateForPlayer.nullAllButTopOf2(); // put nulls except for visible card

        // send the modified copy of the state to the player
        p.sendInfo(stateForPlayer);
    }

    /**
     * whether a player is allowed to move
     *
     * @param playerIdx the player-number of the player in question
     */
    protected boolean canMove(int playerIdx) {
        if (playerIdx < 0 || playerIdx > 4) {
            // if our player-number is out of range, return false
            return false;
        } else {
            // player can move if it's their turn
            return state.toPlay() == playerIdx;
        }
    }

    /**
     * makes a move on behalf of a player
     *
     * @param action the action denoting the move to be made
     * @return true if the move was legal; false otherwise
     */
    @Override
    protected boolean makeMove(GameAction action) {
        // check that we have CanastaMoveAction; if so cast it
        if (!(action instanceof CanastaMoveAction)) {
            return false;
        }
        CanastaMoveAction canastaMA = (CanastaMoveAction) action;

        // get the index of the player making the move
        int thisPlayerIdx = getPlayerIdx(canastaMA.getPlayer());

        // check if it is a legal player
        if (thisPlayerIdx < 0) {
            return false;
        }

        // check if canastaMa is a CanastaDrawDeckAction
        if (canastaMA instanceof CanastaDrawDeckAction) {
            // check that the state's substage is 0
            if (state.substage == 0) {
                // check that the draw deck is not null
                if (state.getDeck(0) == null) {
                    return false;
                } else if (state.substage == 0) {
                    // call the draw card method on the top card of draw deck
                    state.drawCard(state.getDeck(0).getTopCard(state.getDeck(0)));
                    state.getDeck(thisPlayerIdx);
                    // set the state's substage to 1
                    state.substage = 1;

                    return true;
                } else {
                    return false;
                }
            }
        }
        // check if we can discard a card
        else if (canastaMA instanceof CanastaDiscardAction) { // we have a "play" action
            // check that the state's substage is 1
            if (state.substage == 1) {
                // get the discard card
                Card c = ((CanastaDiscardAction) canastaMA).getCard();
                // call the discard method passing in c
                state.discardCard(c);
                // print the card being discard
                System.out.println(c);
                // print whose turn it is
                System.out.println(state.toPlay());

                return true;
            }
            // if the state's substage is not 1
            else {
                return false;
            }
        }
        // if we can draw from the discard pile
        else if (canastaMA instanceof CanastaDrawDiscardAction) {
            // check that the state's substage is 0 and the discard pile is not null
            if (state.substage == 0 && state.getDeck(1) != null) {
                // call the drawDiscard method passing in the top card of the discard pile, and the deck of the player
                state.drawDiscard(state.getDeck(1).peekAtTopCard(), state.getDeck(thisPlayerIdx + 2).getCards());

                return true;
            }

            // if the state's substage is not 0 or the discard pile is null
            return false;
        }
        // check if we can meld cards
        else if (canastaMA instanceof CanastaMeldAction) {
            // check that the state's substage is 1
            if (state.substage == 1) {
                // call the Meld method passing in the player's deck
                state.Meld(state.getDeck(thisPlayerIdx + 2).getCards());

                return true;
            }
            // if the substage is not 1
            else {
                return false;
            }
        }
        // check if the computer player can meld
        else if (canastaMA instanceof CanastaComputerMeldAction) {
            // check that the state's substage is 1
            if (state.substage == 1) {
                // call computerMeld method passing in the computer's meld pile and it's player number
                state.computerMeld(((CanastaComputerMeldAction) canastaMA).getMeld(), thisPlayerIdx);

                return true;
            }
            // if the state's substage is not 1
            else {
                return false;
            }
        }
        // check if the computer player can draw from the discard pile
        else if (canastaMA instanceof CanastaComputerDrawDiscardAction) {
            // check that the state's substage is 0 and the discard pile is not null
            if (state.substage == 0 && state.getDeck(1) != null) {
                // call computerMeldDiscard method passing in the computer's meld pile, the top card
                // of the discard pile, and the player's number
                state.computerMeldDiscard(((CanastaComputerDrawDiscardAction) canastaMA).getMeld(),
                        state.getDeck(1).peekAtTopCard(), thisPlayerIdx);

                return true;
            }
            // if the state's substage is not 0 and the discard pile is not null
            else {
                return false;
            }
        }
        // if the computer selected cards
        else if (canastaMA instanceof CanastaSelectedAction) {
            // get the action and cast it to a CanastaSelectedAction
            CanastaSelectedAction a = (CanastaSelectedAction) canastaMA;
            // get the player's deck and get the card at the position the computer player selected
            // and set that the card's selected
            state.getDeck(thisPlayerIdx + 2).peekAtCards(a.getPosition()).setSelected(a.getSelected());

            return true;
        }


        // return true, because the move was successful if we get here
        return true;
    }
}
