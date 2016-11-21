package edu.up.cs301.canasta;

import org.junit.Test;

import edu.up.cs301.card.Card;
import edu.up.cs301.card.Rank;
import edu.up.cs301.card.Suit;

import static org.junit.Assert.*;

/**
 * Created by vandewar19 on 11/9/2016.
 */
public class CanastaStateTest {

    @Test
    public void testNullAllButTopOf2() throws Exception {
        CanastaState state = new CanastaState();
        //state.nullAllButTopOf2();

        Deck d = state.getDeck(0);
        d.getTopCard(d);

        assertEquals(d, null);
    }

    @Test
    public void testCanMeld() throws Exception {
        CanastaState state = new CanastaState();
        state.temp = new Card[3];
        state.temp[0] = new Card(Rank.FOUR, Suit.Heart);
        state.temp[1] = new Card(Rank.FOUR, Suit.Diamond);
        state.temp[2] = new Card(Rank.FOUR, Suit.Spade);

        assertEquals(state.canMeld(state.temp), true);
    }

    @Test
    public void testDiscardCard() throws Exception {
        CanastaState state = new CanastaState();

        Card c = new Card(Rank.FOUR, Suit.Spade);

        state.discardCard(c);
        boolean discarded = state.getDeck(state.toPlay()).containsCard(c);

        assertEquals(discarded, false);
    }

    @Test
    public void testCanDiscard() throws Exception {
        CanastaState cs = new CanastaState();
        Card five = new Card(Rank.FIVE,Suit.Heart);
        cs.getDeck(0).add(five);
        cs.getDeck(1).add(five);
        cs.getDeck(2).add(five);
        Boolean discard = cs.canDiscard(five);
        assertEquals(true,discard);
    }

    @Test
    public void testDrawCard() throws Exception {
        CanastaState cs = new CanastaState();
        Card two = new Card(Rank.TWO, Suit.Diamond);
        //Card drew = cs.drawCard(two);

        //assertEquals(two,drew);
    }

    @Test
    public void testMeld() throws Exception {
        CanastaState state = new CanastaState();

        state.temp = new Card[3];
        state.temp[0] = new Card(Rank.FOUR, Suit.Heart);
        state.temp[1] = new Card(Rank.FOUR, Suit.Diamond);
        state.temp[2] = new Card(Rank.FOUR, Suit.Spade);

        state.Meld(state.temp);
        boolean melded0 = state.getDeck(state.toPlay()).containsCard(state.temp[0]);
        boolean melded1 = state.getDeck(state.toPlay()).containsCard(state.temp[1]);
        boolean melded2 = state.getDeck(state.toPlay()).containsCard(state.temp[2]);

        boolean discard0 = state.getDeck(0).containsCard(state.temp[0]);
        boolean discard1 = state.getDeck(1).containsCard(state.temp[1]);
        boolean discard2 = state.getDeck(2).containsCard(state.temp[2]);

        assertEquals(melded0, false);
        assertEquals(melded1, false);
        assertEquals(melded2, false);
    }
}