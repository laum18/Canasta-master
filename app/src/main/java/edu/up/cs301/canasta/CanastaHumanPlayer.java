package edu.up.cs301.canasta;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.animation.Animator;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.IllegalMoveInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;

/**
 * A GUI that allows a human to play Slapjack. Moves are made by clicking
 * regions on a surface. Presently, it is laid out for landscape orientation.
 * If the device is held in portrait mode, the cards will be very long and
 * skinny.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class CanastaHumanPlayer extends GameHumanPlayer implements Animator {

	// sizes and locations of card decks and cards, expressed as percentages
	// of the screen height and width
	private final static float CARD_HEIGHT_PERCENT = 20; // height of a card
	private final static float CARD_WIDTH_PERCENT = 9; // width of a card
	private final static float LEFT_BORDER_PERCENT = 8; // width of left border
	private final static float LEFT_DECK_BORDER_PERCENT = 25; //width of the decks
	private final static float RIGHT_BORDER_PERCENT = 25; // width of right border
	private final static float VERTICAL_BORDER_PERCENT = 40; // width of top/bottom borders
	private final static float OPP_VERTICAL_BORDER_PERCENT = 35; // width of border to first opponents hand
	private final static float PLAYER_HAND_VERTICAL_BORDER_PERCENT = -10; // width of border to player meld pile
	private final static float TEAMMATE_HAND_VERTICAL_BORDER_PERCENT = 10;

	private final static float MELD_LEFT_BORDER_PERCENT = 15;
	private final static float MELD_TOP_BORDER_PERCENT = 15;


	// our game state
	protected CanastaState state;

	// our activity
	private Activity myActivity;

	// the amination surface
	private AnimationSurface surface;

	// the background color
	private int backgroundColor;

	private static Card discard;

	public Card[] selected = new Card[15];

	/**
	 * constructor
	 *
	 * @param name
	 * 		the player's name
	 * @param bkColor
	 * 		the background color
	 */
	public CanastaHumanPlayer(String name, int bkColor) {
		super(name);
		backgroundColor = bkColor;
	}

	/**
	 * callback method: we have received a message from the game
	 *
	 * @param info
	 * 		the message we have received from the game
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		//Log.i("SJComputerPlayer", "receiving updated state ("+info.getClass()+")");
		if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
			// if we had an out-of-turn or illegal move, flash the screen
			surface.flash(Color.RED, 50);
		}
		else if (!(info instanceof CanastaState)) {
			// otherwise, if it's not a game-state message, ignore
			return;
		}
		else {
			// it's a game-state object: update the state. Since we have an animation
			// going, there is no need to explicitly display anything. That will happen
			// at the next animation-tick, which should occur within 1/20 of a second
			this.state = (CanastaState)info;
			//Log.i("human player", "receiving");
		}
	}

	/**
	 * call-back method: called whenever the GUI has changed (e.g., at the beginning
	 * of the game, or when the screen orientation changes).
	 *
	 * @param activity
	 * 		the current activity
	 */
	public void setAsGui(GameMainActivity activity) {

		// remember the activity
		myActivity = activity;

		// Load the layout resource for the new configuration
		activity.setContentView(R.layout.canasta_human_player);

		// link the animator (this object) to the animation surface
		surface = (AnimationSurface) myActivity
				.findViewById(R.id.animation_surface);
		surface.setAnimator(this);

		// read in the card images
		Card.initImages(activity);

		// if the state is not null, simulate having just received the state so that
		// any state-related processing is done
		if (state != null) {
			receiveInfo(state);
		}
	}

	/**
	 * @return the top GUI view
	 */
	@Override
	public View getTopView() {
		return myActivity.findViewById(R.id.top_gui_layout);
	}

	/**
	 * @return
	 * 		the amimation interval, in milliseconds
	 */
	public int interval() {
		// 1/20 of a second
		return 50;
	}

	/**
	 * @return
	 * 		the background color
	 */
	public int backgroundColor() {
		return backgroundColor;
	}

	/**
	 * @return
	 * 		whether the animation should be paused
	 */
	public boolean doPause() {
		return false;
	}

	/**
	 * @return
	 * 		whether the animation should be terminated
	 */
	public boolean doQuit() {
		return false;
	}

	/**
	 * callback-method: we have gotten an animation "tick"; redraw the screen image:
	 * - the middle deck, with the top card face-up, others face-down
	 * - the two players' decks, with all cards face-down
	 * - a red bar to indicate whose turn it is
	 *
	 * @param g
	 * 		the canvas on which we are to draw
	 */
	public void tick(Canvas g) {

		// ignore if we have not yet received the game state
		if (state == null) return;

		Card[][] meldCard = new Card[12][8];

		// get the height and width of the animation surface
		int height = surface.getHeight();
		int width = surface.getWidth();

		// draw the discard card-pile
		Card c = state.getDeck(1).peekAtTopCard(); // top card in pile

		// draw the deck cards, face down
		RectF deckTopLocation = deckCardLocation(); // drawing size/location
		drawCard(g,deckTopLocation,null);

		//draw discard cards, face down
		RectF discardTopLocation = discardTopCardLocation(); // drawing size/location
		drawCard(g, discardTopLocation, c);

		//draw my hand (should be face up)
		RectF playerHandLocation = playerHandFirstCardLocation();
		drawCardFaces(g, playerHandLocation, 0.06f*width, 0, state.getDeck(2).size(), 0);

		//draw left opponent hand, face down
		RectF leftOpponentHand = leftOppHandFirstCardLocation();
		drawCardFaces(g, leftOpponentHand, 0, 0.05f*height, state.getDeck(3).size(), 1);

		//draw teammate hand, face down
		RectF teammateHand = teammateHandFirstCardLocation();
		drawCardFaces(g, teammateHand, 0.06f*width, 0, state.getDeck(4).size(), 2);

		//draw right opponent hand, face down
		RectF rightOpponentHand = rightOppHandFirstCardLocation();
		drawCardFaces(g, rightOpponentHand, 0, 0.05f*height, state.getDeck(5).size(), 3);

		drawOppMeldPiles(g, meldCard);
		drawMyMeldPiles(g, meldCard);


	}

	/**
	 * @return
	 * 		the rectangle that represents the location on the drawing
	 * 		surface where the top card in the opponent's deck is to
	 * 		be drawn
	 */
	private RectF deckCardLocation() {
		// near the left-bottom of the drawing surface, based on the height
		// and width, and the percentages defined above
		int width = surface.getWidth();
		int height = surface.getHeight();
		return new RectF(LEFT_DECK_BORDER_PERCENT*width/100f,
				(100-VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				(LEFT_DECK_BORDER_PERCENT+CARD_WIDTH_PERCENT)*width/100f,
				(100-VERTICAL_BORDER_PERCENT)*height/100f);
	}

	/**
	 * @return
	 * 		the rectangle that represents the location on the drawing
	 * 		surface where the top card in the current player's deck is to
	 * 		be drawn
	 */
	private RectF discardTopCardLocation() {
		// near the right-bottom of the drawing surface, based on the height
		// and width, and the percentages defined above
		int width = surface.getWidth();
		int height = surface.getHeight();
		return new RectF((100-RIGHT_BORDER_PERCENT-CARD_WIDTH_PERCENT)*width/100f,
				(100-VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				(100-RIGHT_BORDER_PERCENT)*width/100f,
				(100-VERTICAL_BORDER_PERCENT)*height/100f);
	}

	/* location of player hand */
	private RectF playerHandFirstCardLocation() {
		int height = surface.getHeight();
		int width = surface.getWidth();
		return new RectF(LEFT_BORDER_PERCENT*width/100f,
				(100-PLAYER_HAND_VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				(LEFT_BORDER_PERCENT+CARD_WIDTH_PERCENT)*width/100f,
				(100-PLAYER_HAND_VERTICAL_BORDER_PERCENT)*height/100f);
	}
	//location of any card in player hand
	private RectF playerHandCardLocation(int cardNum) {
		int height = surface.getHeight();
		int width = surface.getWidth();

		float deltaX = cardNum * (0.06f*width);

		return new RectF((LEFT_BORDER_PERCENT*width/100f) + (deltaX),
				(100-PLAYER_HAND_VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				((LEFT_BORDER_PERCENT+CARD_WIDTH_PERCENT)*width/100f) + (deltaX),
				(100-PLAYER_HAND_VERTICAL_BORDER_PERCENT)*height/100f);
	}

	/* location of teammate hand */
	private RectF teammateHandFirstCardLocation() {
		int height = surface.getHeight();
		int width = surface.getWidth();
		return new RectF(LEFT_BORDER_PERCENT*width/100f,
				(TEAMMATE_HAND_VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				(LEFT_BORDER_PERCENT+CARD_WIDTH_PERCENT)*width/100f,
				(TEAMMATE_HAND_VERTICAL_BORDER_PERCENT)*height/100f);
	}

	/* location of left hand */
	private RectF leftOppHandFirstCardLocation() {
		int height = surface.getHeight();
		int width = surface.getWidth();
		return new RectF(0,(OPP_VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				(CARD_WIDTH_PERCENT)*width/100f,(OPP_VERTICAL_BORDER_PERCENT)*height/100f);
	}


	/*location of right hand*/
	private RectF rightOppHandFirstCardLocation() {
		int height = surface.getHeight();
		int width = surface.getWidth();
		return new RectF((100-CARD_WIDTH_PERCENT)*width/100f,
				(OPP_VERTICAL_BORDER_PERCENT-CARD_HEIGHT_PERCENT)*height/100f,
				(100)*width/100f,
				(OPP_VERTICAL_BORDER_PERCENT)*height/100f);
	}



	/**
	 * draws a sequence of card-backs, each offset a bit from the previous one, so that all can be
	 * seen to some extent
	 *
	 * @param g
	 * 		the canvas to draw on
	 * @param topRect
	 * 		the rectangle that defines the location of the top card (and the size of all
	 * 		the cards
	 * @param deltaX
	 * 		the horizontal change between the drawing position of two consecutive cards
	 * @param deltaY
	 * 		the vertical change between the drawing position of two consecutive cards
	 * @param numCards
	 * 		the number of card-backs to draw
	 */
	private void drawCardBacks(Canvas g, RectF topRect, float deltaX, float deltaY,
							   int numCards) {
		// loop through from back to front, drawing a card-back in each location
		for (int i = 0; i <= numCards-1; i++) {
			// determine theh position of this card's top/left corner
			float left = topRect.left + i*deltaX;
			float top = topRect.top + i*deltaY;
			// draw a card-back (hence null) into the appropriate rectangle
			drawCard(g,
					new RectF(left, top, left + topRect.width(), top + topRect.height()),
					null);
		}
	}

	private void drawCardFaces(Canvas g, RectF topRect, float deltaX, float deltaY,
							   int numCards, int player) {
		// loop through from back to front, drawing a card-back in each location
		for (int i = 0; i <= numCards-1; i++) {
			// determine the position of this card's top/left corner
			float left = topRect.left + i*deltaX;
			float top = topRect.top + i*deltaY;
			// draw a card-back (hence null) into the appropriate rectangle
			drawCard(g,
					new RectF(left, top, left + topRect.width(), top + topRect.height()),
					state.getDeck(player+2).peekAtCards(i));
		}
	}

	private void drawSelectedFaces(Canvas g, RectF topRect, float deltaX, float deltaY,
							   int numCards, int player) {
		// loop through from back to front, drawing a card-back in each location
		for (int i = 0; i <= numCards-1; i++) {
			// determine the position of this card's top/left corner
			float left = topRect.left + i*deltaX;
			float top = topRect.top + i*deltaY;
			// draw a card-back (hence null) into the appropriate rectangle
			drawCard(g,
					new RectF(left, top-100, left + topRect.width(), top + topRect.height()),
					state.getDeck(player+2).peekAtCards(i));
		}
	}

	private void drawOppMeldPiles(Canvas g, Card[][] c){
		int height = surface.getHeight()-250;
		int width = surface.getWidth()-550;
		for(int i =0; i<12;i++){
			for(int j = 0; j<8; j++){
				if(c[i][j] == null){
					RectF rect = new RectF((MELD_LEFT_BORDER_PERCENT + i*CARD_WIDTH_PERCENT)*width/100f,
							MELD_TOP_BORDER_PERCENT*height/100f,
							(MELD_LEFT_BORDER_PERCENT+CARD_WIDTH_PERCENT + i*CARD_WIDTH_PERCENT)*width/100f,
							(MELD_TOP_BORDER_PERCENT+CARD_HEIGHT_PERCENT)*height/100f);
					drawCardFaces(g,rect,0,0,1, state.toPlay());
				}
			}
		}
	}
	private void drawMyMeldPiles(Canvas g, Card[][] c){
		int height = surface.getHeight()-250;
		int width = surface.getWidth()-550;
		for(int i =0; i<12;i++){
			for(int j = 0; j<8; j++){
				if(c[i][j] == null){
					RectF rect2 = new RectF((MELD_LEFT_BORDER_PERCENT + i*CARD_WIDTH_PERCENT)*width/100f,
							(100-MELD_TOP_BORDER_PERCENT)*height/100f,
							(MELD_LEFT_BORDER_PERCENT+CARD_WIDTH_PERCENT + i*CARD_WIDTH_PERCENT)*width/100f,
							(100-MELD_TOP_BORDER_PERCENT+CARD_HEIGHT_PERCENT)*height/100f);
					drawCardFaces(g,rect2,0,0,1, state.toPlay());
				}
			}
		}
	}

	/**
	 * callback method: we have received a touch on the animation surface
	 *
	 * @param event
	 * 		the motion-event
	 */
	public void onTouch(MotionEvent event) {

		// ignore everything except down-touch events
		if (event.getAction() != MotionEvent.ACTION_DOWN) return;

		// get the location of the touch on the surface
		int x = (int) event.getX();
		int y = (int) event.getY();

		// determine whether the touch occurred on the top-card of either
		// the player's pile or the middle pile
		RectF myTopCardLoc = discardTopCardLocation();

		// discard card
		for (int i = 0; i < state.getDeck(2).size(); i++) {
			RectF player = playerHandCardLocation(i);
			if (player.contains(x,y)) {
				surface.flash(Color.GRAY, 100);
				Log.i(state.getDeck(2).peekAtCards(i).toString(),state.getDeck(2).peekAtCards(i).toString());
				Card card = state.getDeck(2).peekAtCards(i);

				game.sendAction(new CanastaDiscardAction(this, card));
			}
		}

		// select card
		for (int i = 0; i < state.getDeck(2).size(); i++) {
			RectF player = playerHandCardLocation(i);
			//drawCardFaces(g, playerHandLocation, 0.06f*width, 0, state.getDeck(2).size());
			//drawSelectedFaces(g, player, 0.06f*width, 0, state.getDeck(2).size());

			if (player.contains(x,y)) {
				surface.flash(Color.GRAY, 100);
				selected[i] = state.getDeck(2).peekAtCards(i);
				Log.i(state.getDeck(2).peekAtCards(i).toString(),state.getDeck(2).peekAtCards(i).toString());
				//Log.i("array", selected[i].toString());
				game.sendAction(new CanastaMeldAction(this));
			}
		}

		// draws a card
		RectF drawDeck = deckCardLocation();
		if (drawDeck.contains(x,y)) {
			//surface.flash(Color.GRAY, 100);
			game.sendAction(new CanastaDrawDeckAction(this));
		}
		if (myTopCardLoc.contains(x, y)) {
			// it's on my pile: we're playing a card: send action to
			// the game
			game.sendAction(new CanastaPlayAction(this));
		}
//		else if (middleTopCardLoc.contains(x, y)) {
//			// it's on the middlel pile: we're slapping a card: send
//			// action to the game
//			game.sendAction(new CanastaMeldAction(this));
//		}
		else {
			// illegal touch-location: flash for 1/20 second
			//surface.flash(Color.RED, 50);
		}
	}

	/**
	 * draws a card on the canvas; if the card is null, draw a card-back
	 *
	 * @param g
	 * 		the canvas object
	 * @param rect
	 * 		a rectangle defining the location to draw the card
	 * @param c
	 * 		the card to draw; if null, a card-back is drawn
	 */
	private static void drawCard(Canvas g, RectF rect, Card c) {
		if (c == null) {
			// null: draw a card-back, consisting of a blue card
			// with a white line near the border. We implement this
			// by drawing 3 concentric rectangles:
			// - blue, full-size
			// - white, slightly smaller
			// - blue, even slightly smaller
			Paint white = new Paint();
			white.setColor(Color.WHITE);
			Paint blue = new Paint();
			blue.setColor(Color.BLUE);
			RectF inner1 = scaledBy(rect, 0.96f); // scaled by 96%
			RectF inner2 = scaledBy(rect, 0.98f); // scaled by 98%
			g.drawRect(rect, blue); // outer rectangle: blue
			g.drawRect(inner2, white); // middle rectangle: white
			g.drawRect(inner1, blue); // inner rectangle: blue
		}
		else {
			// just draw the card
			c.drawOn(g, rect);
		}
	}

	/**
	 * scales a rectangle, moving all edges with respect to its center
	 *
	 * @param rect
	 * 		the original rectangle
	 * @param factor
	 * 		the scaling factor
	 * @return
	 * 		the scaled rectangle
	 */
	private static RectF scaledBy(RectF rect, float factor) {
		// compute the edge locations of the original rectangle, but with
		// the middle of the rectangle moved to the origin
		float midX = (rect.left+rect.right)/2;
		float midY = (rect.top+rect.bottom)/2;
		float left = rect.left-midX;
		float right = rect.right-midX;
		float top = rect.top-midY;
		float bottom = rect.bottom-midY;

		// scale each side; move back so that center is in original location
		left = left*factor + midX;
		right = right*factor + midX;
		top = top*factor + midY;
		bottom = bottom*factor + midY;

		// create/return the new rectangle
		return new RectF(left, top, right, bottom);
	}

}
