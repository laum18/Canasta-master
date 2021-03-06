package edu.up.cs301.card;

import java.io.Serializable;

import edu.up.cs301.game.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * A playing card in the standard 52-card deck. The images, which have been
 * placed in the res/drawable-hdpi folder in the project, are from
 * http://www.pdclipart.org.
 * 
 * In order to display the card-images on the android you need to call the
 *   Card.initImages(currentActivity)
 * method during initialization; the 52 image files need to be placed in the
 * res/drawable-hdpi project area.
 * 
 * @author Steven R. Vegdahl, Aaron Banobi, Nick Edwards, Michelle Lau, David Vandewark
 * @version July 2013
 */
public class Card implements Serializable {

	// to satisfy the Serializable interface
	private static final long serialVersionUID = 893542931190030342L;
	
	// instance variables: the card's rank and the suit
    private Rank rank;
    private Suit suit;
	protected boolean selected;

    /**
     * Constructor for class card
     *
     * @param r the Rank of the card
     * @param s the Suit of the card
     */
    public Card(Rank r, Suit s) {
        rank = r;
        suit = s;
		selected = false;
    }

	public Card(Card c) {
		rank = c.rank;
		suit = c.suit;
		selected = c.getSelected();
	}

    /**
     * Creates a Card from a String.  (Can be used instead of the
     * constructor.)
     *
     * @param str
     * 		a two-character string representing the card, which is
     *		of the form "4C", with the first character representing the rank,
     *		and the second character representing the suit.  Each suit is
     *		denoted by its first letter.  Each single-digit rank is represented
     *		by its digit.  The letters 'T', 'J', 'Q', 'K' and 'A', represent
     *		the ranks Ten, Jack, Queen, King and Ace, respectively.
     * @return
     * 		A Card object that corresponds to the 'str' string. Returns
     *		null if 'str' has improper format.
     */
    public static Card fromString(String str) {
    	// check the string for being null
        if (str == null) return null;
        
        // trim the string; return null if length is not 2
        str = str.trim();
        if (str.length() !=2) return null;
        
        // get the rank and suit corresponding to the two characters
        // in the string
        Rank r = Rank.fromChar(str.charAt(0));
        Suit s = Suit.fromChar(str.charAt(1));
        
        // if both rank and suit are non-null, create the corresponding
        // card; if either is null, return null
        return r==null || s == null ? null : new Card(r, s);
    }

    /**
     * Produces a textual description of a Card.
     *
     * @return
	 *		A string such as "Jack of Spades", which describes the card.
     */
    public String toString() {
        return rank.longName()+" of "+suit.longName()+"s";
    }

    /**
     * Tells whether two Card objects represent the same card.
     *
     * @return
	 *		true if the two card objects represent the same card, false
     *		otherwise.
     */
//	public boolean equals(Card other) {
//		return this.rank == other.rank && this.suit == other.suit;
//	}
	public boolean equals(Object other) {
		if (!(other instanceof Card)) return false;
		Card c = (Card)other;
		return this.rank == c.rank && this.suit == c.suit;
	}

    /**
     * Draws the card on a Graphics object.  The card is drawn as a
     * white card with a black border.  If the card's rank is numerih, the
     * appropriate number of spots is drawn.  Otherwise the appropriate
     * picture (e.g., of a queen) is included in the card's drawing.
     *
     * @param g  the graphics object on which to draw
     * @param where  a rectangle that tells where the card should be drawn
     */
    public void drawOn(Canvas g, RectF where) {
    	// create the paint object
    	Paint p = new Paint();
    	p.setColor(Color.BLACK);
    	
    	// get the bitmap for the card
    	Bitmap bitmap = cardImages[this.getSuit().ordinal()][this.getRank().ordinal()];

    	// create the source rectangle
    	Rect r = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());

    	
    	// draw the bitmap into the target rectangle
    	g.drawBitmap(bitmap, r, where, p);
    }

    
    /**
     * Gives a two-character version of the card (e.g., "TS" for ten of
     * spades).
     */
    public String shortName() {
        return "" + getRank().shortName() + getSuit().shortName();
    }

    /**
     * Tells the card's rank.
     *
     * @return
	 *		a Rank object (actually of a subclass) that tells the card's
     *		rank (e.g., Jack, three).
     */
    public Rank getRank() {
    	return rank;
    }

    /**
     * Tells the card's suit.
     *
     * @return
	 *		a Suit object (actually of a subclass) that tells the card's
     *		rank (e.g., heart, club).
     */
    public Suit getSuit() {
    	return suit;
    }
 
    // array that contains the android resource indices for the 52 card
    // images
    private static int[][] resIdx = {
    	{
    		R.drawable.card_ac, R.drawable.card_2c, R.drawable.card_3c,
    		R.drawable.card_4c, R.drawable.card_5c, R.drawable.card_6c,
    		R.drawable.card_7c, R.drawable.card_8c, R.drawable.card_9c,
    		R.drawable.card_tc, R.drawable.card_jc, R.drawable.card_qc,
    		R.drawable.card_kc, R.drawable.card_rc,
    	},
    	{
    		R.drawable.card_ad, R.drawable.card_2d, R.drawable.card_3d,
    		R.drawable.card_4d, R.drawable.card_5d, R.drawable.card_6d,
    		R.drawable.card_7d, R.drawable.card_8d, R.drawable.card_9d,
    		R.drawable.card_td, R.drawable.card_jd, R.drawable.card_qd,
    		R.drawable.card_kd, R.drawable.card_rd,
    	},
    	{
    		R.drawable.card_ah, R.drawable.card_2h, R.drawable.card_3h,
    		R.drawable.card_4h, R.drawable.card_5h, R.drawable.card_6h,
    		R.drawable.card_7h, R.drawable.card_8h, R.drawable.card_9h,
    		R.drawable.card_th, R.drawable.card_jh, R.drawable.card_qh,
    		R.drawable.card_kh, R.drawable.card_rh,
    	},
    	{
    		R.drawable.card_as, R.drawable.card_2s, R.drawable.card_3s,
    		R.drawable.card_4s, R.drawable.card_5s, R.drawable.card_6s,
    		R.drawable.card_7s, R.drawable.card_8s, R.drawable.card_9s,
    		R.drawable.card_ts, R.drawable.card_js, R.drawable.card_qs,
    		R.drawable.card_ks, R.drawable.card_rs,
    	},
    };
    
    // the array of card images
    private static Bitmap[][] cardImages = null;
    
    /**
     * initializes the card images
     * 
     * @param activity
     * 		the current activity
     */
    public static void initImages(Activity activity) {
    	// if it's already initialized, then ignore
    	if (cardImages != null) return;
    	
    	// create the outer array
    	cardImages = new Bitmap[resIdx.length][];
    	
    	// loop through the resource-index array, creating a
    	// "parallel" array with the images themselves
    	for (int i = 0; i < resIdx.length; i++) {
    		// create an inner array
    		cardImages[i] = new Bitmap[resIdx[i].length];
    		for (int j = 0; j < resIdx[i].length; j++) {
    			// create the bitmap from the corresponding image
    			// resource, and set the corresponding array element
    			cardImages[i][j] =
    					BitmapFactory.decodeResource(
    							activity.getResources(),
    							resIdx[i][j]);
    		}
    	}
    }

	/**
	 * A getter method to see if the card is selected
	 * @return whether the card was selected
     */
	public Boolean getSelected() {
		return selected;
	}

	/**
	 * A setter method to set if the card was selected or not
	 * @param b Set the selected instance variable to b
     */
	public void setSelected(Boolean b) {
		selected = b;
	}


	/**
	 * A getter method to get the value of a given rank
	 * @param r The rank we are trying to get the value of
	 * @return The value of the rank
     */
	public int getCardValue(Rank r) {
		int cardValue = 0;
		if (r.shortName() == '2') {
			cardValue = 2;
		} else if (r.shortName() == '3') {
			cardValue = 3;
		} else if (r.shortName() == '4') {
			cardValue = 4;
		} else if (r.shortName() == '5') {
			cardValue = 5;
		} else if (r.shortName() == '6') {
			cardValue = 6;
		} else if (r.shortName() == '7') {
			cardValue = 7;
		} else if (r.shortName() == '8') {
			cardValue = 8;
		} else if (r.shortName() == '9') {
			cardValue = 9;
		} else if (r.shortName() == 'T') {
			cardValue = 10;
		} else if (r.shortName() == 'J') {
			cardValue = 11;
		} else if (r.shortName() == 'Q') {
			cardValue = 12;
		} else if (r.shortName() == 'K') {
			cardValue = 13;
		} else if (r.shortName() == 'A') {
			cardValue = 14;
		} else if (r.shortName() == 'R') {
			cardValue = 1;
		} else {
			cardValue = -1;
		}
		return cardValue;
	}

}
