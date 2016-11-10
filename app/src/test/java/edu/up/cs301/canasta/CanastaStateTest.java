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
        CanastaState cs = new CanastaState();
        cs.nullAllButTopOf2();
        //assertEquals();
    }

    @Test
    public void testCanMeld() throws Exception {

    }

    @Test
    public void testDiscardCard() throws Exception {

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
        Card drew = cs.drawCard(two);

        assertEquals(two,drew);
    }

    @Test
    public void testMeld() throws Exception {

    }
}