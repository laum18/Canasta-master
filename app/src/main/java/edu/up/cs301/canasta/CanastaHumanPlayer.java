package edu.up.cs301.canasta;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.animation.Animator;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.IllegalMoveInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;

/**
 * A GUI that allows a human to play Canasta. Moves are made by clicking
 * regions on a surface. Presently, it is laid out for landscape orientation.
 * If the device is held in portrait mode, the cards will be very long and
 * skinny.
 *
 * @author Steven R. Vegdahl, Nick Edwards, Aaron Banobi, Michele Lau, David Vandewark
 * @version December 2016
 */
public class CanastaHumanPlayer extends GameHumanPlayer implements Animator, View.OnClickListener {

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
    private final static float TEAMMATE_HAND_VERTICAL_BORDER_PERCENT = 10; // width of border to teammate hand

    private final static float MELD_LEFT_BORDER_PERCENT = 15; // width of left border for the meld piles
    private final static float MELD_TOP_BORDER_PERCENT = 15; //width of vertical border to meld pile


    // our game state
    protected CanastaState state;

    // our activity
    private Activity myActivity;

    // the amination surface
    private AnimationSurface surface;

    // the background color
    private int backgroundColor;

    private static Card discard;

    //button instance variables
    private Button meldButton;
    private Button discardButton;
    private Button drawDiscardButton;
    private Button sortButton;

    //Initialize TextViews for the different GUI texts
    TextView turn;
    TextView teamOneRound;
    TextView teamTwoRound;
    TextView teamOneTotal;
    TextView teamTwoTotal;
    TextView deckSize;
    TextView discardSize;
    TextView playerNumber;
    TextView newRound;

    // text view for number of cards in team melds of each rank
    TextView my3;
    TextView my4;
    TextView my5;
    TextView my6;
    TextView my7;
    TextView my8;
    TextView my9;
    TextView my10;
    TextView myj;
    TextView myq;
    TextView myk;
    TextView mya;

    // text view for number of cards in opponent melds of each rank
    TextView opp3;
    TextView opp4;
    TextView opp5;
    TextView opp6;
    TextView opp7;
    TextView opp8;
    TextView opp9;
    TextView opp10;
    TextView oppj;
    TextView oppq;
    TextView oppk;
    TextView oppa;


    /**
     * constructor
     *
     * @param name    the player's name
     * @param bkColor the background color
     */
    public CanastaHumanPlayer(String name, int bkColor) {
        super(name);
        backgroundColor = bkColor;
    }

    /**
     * callback method: we have received a message from the game
     *
     * @param info the message we have received from the game
     */
    @Override
    public void receiveInfo(GameInfo info) {
        //Log.i("SJComputerPlayer", "receiving updated state ("+info.getClass()+")");
        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if we had an out-of-turn or illegal move, flash the screen
            surface.flash(Color.RED, 50);
        } else if (!(info instanceof CanastaState)) {
            // otherwise, if it's not a game-state message, ignore
            return;
        } else {
            // it's a game-state object: update the state. Since we have an animation
            // going, there is no need to explicitly display anything. That will happen
            // at the next animation-tick, which should occur within 1/20 of a second
            this.state = (CanastaState) info;
            updateGUI();
            //Log.i("human player", "receiving");
        }

    }

    /**
     * call-back method: called whenever the GUI has changed (e.g., at the beginning
     * of the game, or when the screen orientation changes).
     *
     * @param activity the current activity
     */
    public void setAsGui(GameMainActivity activity) {

        // remember the activity
        myActivity = activity;

        //Lock the orientation of the screen to landscape
        myActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Load the layout resource for the new configuration
        activity.setContentView(R.layout.canasta_human_player);

        // link the animator (this object) to the animation surface
        surface = (AnimationSurface) myActivity.findViewById(R.id.animation_surface);
        surface.setAnimator(this);

        //link the various buttons to each id
        meldButton = (Button) activity.findViewById(R.id.meldButton);
        discardButton = (Button) activity.findViewById(R.id.discardButton);
        drawDiscardButton = (Button) activity.findViewById(R.id.drawDiscardButton);
        sortButton = (Button) activity.findViewById(R.id.sortButton);

        //make the buttons listeners
        meldButton.setOnClickListener(this);
        discardButton.setOnClickListener(this);
        drawDiscardButton.setOnClickListener(this);
        sortButton.setOnClickListener(this);

        //link the textViews
        turn = (TextView) activity.findViewById(R.id.turnIndicator);
        teamOneRound = (TextView) activity.findViewById(R.id.oneRoundText);
        teamTwoRound = (TextView) activity.findViewById(R.id.twoRoundText);
        teamOneTotal = (TextView) activity.findViewById(R.id.oneTotalText);
        teamTwoTotal = (TextView) activity.findViewById(R.id.twoTotalText);
        deckSize = (TextView) activity.findViewById(R.id.deckSize);
        discardSize = (TextView) activity.findViewById(R.id.discardSize);
        playerNumber = (TextView) activity.findViewById(R.id.playerNum);
        newRound = (TextView) activity.findViewById(R.id.newRound);

        //link team 1 meld pile numbers
        my3 = (TextView) activity.findViewById(R.id.myThree);
        my4 = (TextView) activity.findViewById(R.id.myFour);
        my5 = (TextView) activity.findViewById(R.id.myFive);
        my6 = (TextView) activity.findViewById(R.id.mySix);
        my7 = (TextView) activity.findViewById(R.id.mySeven);
        my8 = (TextView) activity.findViewById(R.id.myEight);
        my9 = (TextView) activity.findViewById(R.id.myNine);
        my10 = (TextView) activity.findViewById(R.id.myTen);
        myj = (TextView) activity.findViewById(R.id.myEleven);
        myq = (TextView) activity.findViewById(R.id.myTwelve);
        myk = (TextView) activity.findViewById(R.id.myThirteen);
        mya = (TextView) activity.findViewById(R.id.myAce);

        //link team 2 meld pile numbers
        opp3 = (TextView) activity.findViewById(R.id.oppThree);
        opp4 = (TextView) activity.findViewById(R.id.oppFour);
        opp5 = (TextView) activity.findViewById(R.id.oppFive);
        opp6 = (TextView) activity.findViewById(R.id.oppSix);
        opp7 = (TextView) activity.findViewById(R.id.oppSeven);
        opp8 = (TextView) activity.findViewById(R.id.oppEight);
        opp9 = (TextView) activity.findViewById(R.id.oppNine);
        opp10 = (TextView) activity.findViewById(R.id.oppTen);
        oppj = (TextView) activity.findViewById(R.id.oppEleven);
        oppq = (TextView) activity.findViewById(R.id.oppTwelve);
        oppk = (TextView) activity.findViewById(R.id.oppThirteen);
        oppa = (TextView) activity.findViewById(R.id.oppAce);

        //We set the view of the sort button to gone for now so that it is removed for now.
        //Once the functionality works, the button will be visible and operational
        sortButton.setVisibility(View.GONE);

        //Have to fix our round indicator as well
        newRound.setVisibility(View.GONE);
        // read in the card images
        Card.initImages(activity);

        // if the state is not null, simulate having just received the state so that
        // any state-related processing is done
        if (state != null) {
            receiveInfo(state);
        }
    }

    /**
     * callback method: we have received a touch on the animation surface
     *
     * @param v the button clicked
     */
    public void onClick(View v) {

        if (state.toPlay() != playerNum) {
            surface.flash(Color.RED, 1);
            return;
        }

        //if meld button is clicked, send a CanastaMeldAction
        if (v == meldButton) {
            game.sendAction((new CanastaMeldAction(this)));
        }

        //if discard button is clicked, discard a card
        else if (v == discardButton) {
            ArrayList<Card> selected = new ArrayList<Card>();
            ArrayList<Card> hand = state.getDeck(playerNum + 2).getCards();
            for (Card c : hand) {
                if (c == null) {
                    break;
                }
                if (c.getSelected()) {
                    selected.add(c);
                }
                //discard a selected card and send the game action
                if (selected.size() == 1) {
                    CanastaDiscardAction discard = new CanastaDiscardAction(this, selected.get(0));
                    game.sendAction(discard);
                    break;
                }

            }
            // redraw animation surface to display completion of action to the user
            surface.invalidate();
        }

        // if drawDiscard button is clicked
        else if (v == drawDiscardButton) {
            game.sendAction((new CanastaDrawDiscardAction(this))); //send game action
        }

        /* TODO fix sort button
            //sort button
            else if (v == sortButton) {
            state.setPlayerDeck(state.sortHand(state.getDeck(playerNum+2)));

        }*/
        updateGUI();

    }

    //method that updates all the text views on the gui
    private void updateGUI() {
        playerNumber.setText("You are player " + playerNum);

        my3.setText(Integer.toString(state.three));
        my4.setText(Integer.toString(state.four));
        my5.setText(Integer.toString(state.five));
        my6.setText(Integer.toString(state.six));
        my7.setText(Integer.toString(state.seven));
        my8.setText(Integer.toString(state.eight));
        my9.setText(Integer.toString(state.nine));
        my10.setText(Integer.toString(state.ten));
        myj.setText(Integer.toString(state.jack));
        myq.setText(Integer.toString(state.queen));
        myk.setText(Integer.toString(state.king));
        mya.setText(Integer.toString(state.ace));

        opp3.setText(Integer.toString(state.oppThree));
        opp4.setText(Integer.toString(state.oppFour));
        opp5.setText(Integer.toString(state.oppFive));
        opp6.setText(Integer.toString(state.oppSix));
        opp7.setText(Integer.toString(state.oppSeven));
        opp8.setText(Integer.toString(state.oppEight));
        opp9.setText(Integer.toString(state.oppNine));
        opp10.setText(Integer.toString(state.oppTen));
        oppj.setText(Integer.toString(state.oppJack));
        oppq.setText(Integer.toString(state.oppQueen));
        oppk.setText(Integer.toString(state.oppKing));
        oppa.setText(Integer.toString(state.oppAce));

        //TODO: needs to be fixed
        /*new round indicator
        if(state.roundStarting == true){
           newRound.setVisibility(View.VISIBLE);
            newRound.setText("!!!!!!!!!!!!!!");
        }*/

        //update scoring on the GUI
        teamOneRound.setText(" " + state.getTeamOneRoundScore());
        teamOneTotal.setText(" " + state.getTeamOneTotalScore());
        teamTwoRound.setText(" " + state.getTeamTwoRoundScore());
        teamTwoTotal.setText(" " + state.getTeamTwoTotalScore());
        deckSize.setText("" + state.getDeck(0).size());
        discardSize.setText("" + state.getDeck(1).size());
        turn.setText("Player " + state.toPlay() + "'s turn");
        teamTwoTotal.setText(" " + state.getTeamTwoTotalScore());

        surface.invalidate();
    }

    /**
     * @return the top GUI view
     */
    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * @return the amimation interval, in milliseconds
     */
    public int interval() {
        // 1/20 of a second
        return 50;
    }

    /**
     * @return the background color
     */
    public int backgroundColor() {
        return backgroundColor;
    }

    /**
     * @return whether the animation should be paused
     */
    public boolean doPause() {
        return false;
    }

    /**
     * @return whether the animation should be terminated
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
     * @param g the canvas on which we are to draw
     */
    public void tick(Canvas g) {

        // ignore if we have not yet received the game state
        if (state == null) return;

        //This ensures that there are enough spaces in the meld piles
        Card[][] meldCard = new Card[12][8];

        // get the height and width of the animation surface
        int height = surface.getHeight();
        int width = surface.getWidth();

        // draw the discard card-pile
        Card c = state.getDeck(1).peekAtTopCard(); // top card in pile

        // draw the deck cards, face down
        RectF deckTopLocation = deckCardLocation(); // drawing size/location
        drawCard(g, deckTopLocation, null);

        //draw discard cards, face down
        RectF discardTopLocation = discardTopCardLocation(); // drawing size/location
        drawCard(g, discardTopLocation, c);

        int draw = 0;
        //draw my hand (should be face up)
        RectF playerHandLocation = playerHandFirstCardLocation();
        if (!state.canDrawDiscard) {
            drawCardFaces(g, playerHandLocation, 0.075f * width, 0, state.getDeck(playerNum + 2).size(), 0);
        } else {
            drawDiscardFaces(g, playerHandLocation, 0.055f * width, 0, state.getDeck(playerNum + 2).size(), 0);
            draw = 1;
        }
        if (draw == 0) {
            drawSelected(g, playerHandLocation, 0.075f * width, 0, state.getDeck(playerNum + 2).size(), 0);

        } else {
            drawDiscardSelected(g, playerHandLocation, 0.055f * width, 0, state.getDeck(playerNum + 2).size(), 0);

        }
        //draw left opponent hand, face down
        RectF leftOpponentHand = leftOppHandFirstCardLocation();
        drawCardBacks(g, leftOpponentHand, 0, 0.05f * height, state.getDeck((1 + playerNum)% 4 + 2).size());

        //draw teammate hand, face down
        RectF teammateHand = teammateHandFirstCardLocation();
        drawCardBacks(g, teammateHand, 0.06f * width, 0, state.getDeck((2 + playerNum)% 4 + 2).size());

        //draw right opponent hand, face down
        RectF rightOpponentHand = rightOppHandFirstCardLocation();
        drawCardBacks(g, rightOpponentHand, 0, 0.05f * height, state.getDeck((3 + playerNum)% 4 + 2).size());


        drawOppMeldPiles(g, meldCard);
        drawMyMeldPiles(g, meldCard);


    }

    /**
     * @return the rectangle that represents the location on the drawing
     * surface where the top card in the opponent's deck is to
     * be drawn
     */
    private RectF deckCardLocation() {
        // near the left-bottom of the drawing surface, based on the height
        // and width, and the percentages defined above
        int width = surface.getWidth();
        int height = surface.getHeight();
        return new RectF(LEFT_DECK_BORDER_PERCENT * width / 100f,
                (100 - VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                (LEFT_DECK_BORDER_PERCENT + CARD_WIDTH_PERCENT) * width / 100f,
                (100 - VERTICAL_BORDER_PERCENT) * height / 100f);
    }

    /**
     * @return the rectangle that represents the location on the drawing
     * surface where the top card in the current player's deck is to
     * be drawn
     */
    private RectF discardTopCardLocation() {
        // near the right-bottom of the drawing surface, based on the height
        // and width, and the percentages defined above
        int width = surface.getWidth();
        int height = surface.getHeight();
        return new RectF((100 - RIGHT_BORDER_PERCENT - CARD_WIDTH_PERCENT) * width / 100f,
                (100 - VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                (100 - RIGHT_BORDER_PERCENT) * width / 100f,
                (100 - VERTICAL_BORDER_PERCENT) * height / 100f);
    }

    /* location of player hand */
    private RectF playerHandFirstCardLocation() {
        int height = surface.getHeight();
        int width = surface.getWidth();
        return new RectF(LEFT_BORDER_PERCENT * width / 100f,
                (100 - PLAYER_HAND_VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                (LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT) * width / 100f,
                (100 - PLAYER_HAND_VERTICAL_BORDER_PERCENT) * height / 100f);
    }

    //location of any card in player hand
    private RectF playerHandCardLocation(int cardNum) {
        int height = surface.getHeight();
        int width = surface.getWidth();

        float deltaX = cardNum * (0.075f * width);

        return new RectF((LEFT_BORDER_PERCENT * width / 100f) + (deltaX),
                (100 - PLAYER_HAND_VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                ((LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT) * width / 100f) + (deltaX),
                (100 - PLAYER_HAND_VERTICAL_BORDER_PERCENT) * height / 100f);
    }

    //location of any card in player hand
    private RectF playerDiscardHandCardLocation(int cardNum) {
        int height = surface.getHeight();
        int width = surface.getWidth();

        float deltaX = cardNum * (0.055f * width);

        return new RectF((LEFT_BORDER_PERCENT * width / 100f) + (deltaX),
                (100 - PLAYER_HAND_VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                ((LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT) * width / 100f) + (deltaX),
                (100 - PLAYER_HAND_VERTICAL_BORDER_PERCENT) * height / 100f);
    }

    /* location of teammate hand */
    private RectF teammateHandFirstCardLocation() {
        int height = surface.getHeight();
        int width = surface.getWidth();
        return new RectF(LEFT_BORDER_PERCENT * width / 100f,
                (TEAMMATE_HAND_VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                (LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT) * width / 100f,
                (TEAMMATE_HAND_VERTICAL_BORDER_PERCENT) * height / 100f);
    }

    /* location of left hand */
    private RectF leftOppHandFirstCardLocation() {
        int height = surface.getHeight();
        int width = surface.getWidth();
        return new RectF(0, (OPP_VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                (CARD_WIDTH_PERCENT) * width / 100f, (OPP_VERTICAL_BORDER_PERCENT) * height / 100f);
    }


    /*location of right hand*/
    private RectF rightOppHandFirstCardLocation() {
        int height = surface.getHeight();
        int width = surface.getWidth();
        return new RectF((100 - CARD_WIDTH_PERCENT) * width / 100f,
                (OPP_VERTICAL_BORDER_PERCENT - CARD_HEIGHT_PERCENT) * height / 100f,
                (100) * width / 100f,
                (OPP_VERTICAL_BORDER_PERCENT) * height / 100f);
    }


    /**
     * draws a sequence of card-backs, each offset a bit from the previous one, so that all can be
     * seen to some extent
     *
     * @param g        the canvas to draw on
     * @param topRect  the rectangle that defines the location of the top card (and the size of all
     *                 the cards
     * @param deltaX   the horizontal change between the drawing position of two consecutive cards
     * @param deltaY   the vertical change between the drawing position of two consecutive cards
     * @param numCards the number of card-backs to draw
     */
    private void drawCardBacks(Canvas g, RectF topRect, float deltaX, float deltaY,
                               int numCards) {
        // loop through from back to front, drawing a card-back in each location
        for (int i = 0; i <= numCards - 1; i++) {
            // determine theh position of this card's top/left corner
            float left = topRect.left + i * deltaX;
            float top = topRect.top + i * deltaY;
            // draw a card-back (hence null) into the appropriate rectangle
            drawCard(g,
                    new RectF(left, top, left + topRect.width(), top + topRect.height()),
                    null);
        }
    }

    //same as drawCardBacks, except it draws the faces of the cards
    private void drawCardFaces(Canvas g, RectF topRect, float deltaX, float deltaY,
                               int numCards, int player) {
        // loop through from back to front, drawing a card-face in each location
        for (int i = 0; i <= numCards - 1; i++) {
            // determine the position of this card's top/left corner
            float left = topRect.left + i * deltaX;
            float top = topRect.top + i * deltaY;
            // draw a card-face (hence null) into the appropriate rectangle
            drawCard(g,
                    new RectF(left, top, left + topRect.width(), top + topRect.height()),
                    state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i));
        }
    }

    /* method to draw selected cards bigger than non-selected cards */
    private void drawSelected(Canvas g, RectF topRect, float deltaX, float deltaY, int numCards, int player) {
        // loop through from back to front, drawing a card-face in each location
        for (int i = 0; i <= numCards - 1; i++) {
            if (state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i) == null) {
                //if (state.getDeck(player + 2).peekAtCards(i).getSelected() == true) {

                // determine the position of this card's top/left corner
                float left = topRect.left + i * deltaX;
                float top = topRect.top + i * deltaY;
                // draw a card-face (hence null) into the appropriate rectangle

                drawCard(g,
                        new RectF(left - 20, top - 20, left + topRect.width(), top + topRect.height()),
                        state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i));
                break;
            } else {
                if (state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i).getSelected()) {

                    // determine the position of this card's top/left corner
                    float left = topRect.left + i * deltaX;
                    float top = topRect.top + i * deltaY;
                    // draw a card-face (hence null) into the appropriate rectangle

                    drawCard(g,
                            new RectF(left - 20, top - 20, left + topRect.width(), top + topRect.height()),
                            state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i));
                }
            }

        }
    }

    private void drawDiscardFaces(Canvas g, RectF topRect, float deltaX, float deltaY,
                                  int numCards, int player) {
        // loop through from back to front, drawing a card-face in each location
        for (int i = 0; i <= numCards - 1; i++) {
            // determine the position of this card's top/left corner
            float left = topRect.left + i * deltaX;
            float top = topRect.top + i * deltaY;
            // draw a card-face (hence null) into the appropriate rectangle
            drawCard(g,
                    new RectF(left, top, left + topRect.width() - 40, top + topRect.height()),
                    state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i));
        }
    }

    private void drawDiscardSelected(Canvas g, RectF topRect, float deltaX, float deltaY, int numCards, int player) {
        // loop through from back to front, drawing a card-face in each location
        for (int i = 0; i <= numCards - 1; i++) {
            if (state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i) == null) {
                //if (state.getDeck(player + 2).peekAtCards(i).getSelected() == true) {

                // determine the position of this card's top/left corner
                float left = topRect.left + i * deltaX;
                float top = topRect.top + i * deltaY;
                // draw a card-face (hence null) into the appropriate rectangle

                drawCard(g,
                        new RectF(left, top - 20, left + topRect.width(), top + topRect.height()),
                        state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i));
                break;
            } else {
                if (state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i).getSelected()) {

                    // determine the position of this card's top/left corner
                    float left = topRect.left + i * deltaX;
                    float top = topRect.top + i * deltaY;
                    // draw a card-face (hence null) into the appropriate rectangle

                    drawCard(g,
                            new RectF(left, top - 20, left + topRect.width() - 40, top + topRect.height()),
                            state.getDeck((player + playerNum) % 4 + 2).peekAtCards(i));
                }
            }
        }
    }

    //draw the opponents meld piles from the first card, and offsetting the rest accordingly
    private void drawOppMeldPiles(Canvas g, Card[][] c) {
        int height = surface.getHeight() - 250;
        int width = surface.getWidth() - 550;
        for (int i = 0; i < 12; i++) {
            RectF rect = new RectF((MELD_LEFT_BORDER_PERCENT + i * CARD_WIDTH_PERCENT) * width / 100f,
                    MELD_TOP_BORDER_PERCENT * height / 100f,
                    (MELD_LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT + i * CARD_WIDTH_PERCENT) * width / 100f,
                    (MELD_TOP_BORDER_PERCENT + CARD_HEIGHT_PERCENT) * height / 100f);

            drawCard(g, rect, state.getDeck(6).peekAtCards(i));
        }
    }
    //draw your meld piles from the first card, and offsetting the rest accordingly
    private void drawMyMeldPiles(Canvas g, Card[][] c) {
        int height = surface.getHeight() - 250;
        int width = surface.getWidth() - 550;
        for (int i = 0; i < 12; i++) {
            RectF rect2 = new RectF((MELD_LEFT_BORDER_PERCENT + i * CARD_WIDTH_PERCENT) * width / 100f,
                    (100 - MELD_TOP_BORDER_PERCENT) * height / 100f,
                    (MELD_LEFT_BORDER_PERCENT + CARD_WIDTH_PERCENT + i * CARD_WIDTH_PERCENT) * width / 100f,
                    (100 - MELD_TOP_BORDER_PERCENT + CARD_HEIGHT_PERCENT) * height / 100f);
            drawCard(g, rect2, state.getDeck(6).peekAtCards(i));

        }
    }

    /**
     * callback method: we have received a touch on the animation surface
     *
     * @param event the motion-event
     */
    public void onTouch(MotionEvent event) {
        if (state.toPlay() != playerNum) {
            return;
        }


        CanastaDrawDeckAction drawCard = new CanastaDrawDeckAction(this);

        // ignore everything except down-touch events
        if (event.getAction() != MotionEvent.ACTION_DOWN) return;

        // get the location of the touch on the surface
        int x = (int) event.getX();
        int y = (int) event.getY();

        // determine whether the touch occurred on the top-card of either
        // the player's pile or the middle pile
        RectF myTopCardLoc = discardTopCardLocation();


        // select card
        for (int i = state.getDeck(playerNum + 2).size()-1; i >=0 ; i--) {
            if (state.canDrawDiscard) {
                RectF player = playerDiscardHandCardLocation(i);
                if (player.contains(x, y)) {
                    //surface.flash(Color.GRAY, 100);
                    if (state.getDeck(playerNum + 2).peekAtCards(i).getSelected() == false) {
                        //state.getDeck(playerNum + 2).peekAtCards(i).setSelected(true);
                        CanastaSelectedAction selected = new CanastaSelectedAction(this, true, i);
                        game.sendAction(selected);
                        break; //If two cards contain the touch point, only the right most card will be selected
                    } else {
                        //state.getDeck(playerNum + 2).peekAtCards(i).setSelected(false);
                        CanastaSelectedAction selected = new CanastaSelectedAction(this, false, i);
                        game.sendAction(selected);
                        break; //If two cards contain the touch point, only the right most card will be selected
                    }
                }
            }
            else {
                RectF player = playerHandCardLocation(i);
                if (player.contains(x, y)) {
                    //surface.flash(Color.GRAY, 100);
                    if (state.getDeck(playerNum + 2).peekAtCards(i).getSelected() == false) {
                        //state.getDeck(playerNum + 2).peekAtCards(i).setSelected(true);
                        CanastaSelectedAction selected = new CanastaSelectedAction(this, true, i);
                        game.sendAction(selected);
                        break; //If two cards contain the touch point, only the right most card will be selected
                    } else {
                        //state.getDeck(playerNum + 2).peekAtCards(i).setSelected(false);
                        CanastaSelectedAction selected = new CanastaSelectedAction(this, false, i);
                        game.sendAction(selected);
                        break; //If two cards contain the touch point, only the right most card will be selected
                    }
                }
            }

        }

        // draws a card
        RectF drawDeck = deckCardLocation();
        if (drawDeck.contains(x, y)) {
            //surface.flash(Color.GRAY, 100);
            game.sendAction(drawCard);
        }
        if (myTopCardLoc.contains(x, y)) {
            // it's on my pile: we're playing a card: send action to
            // the game
            game.sendAction(new CanastaPlayAction(this));
        }
        else {
            // illegal touch-location: flash for 1/20 second
            //surface.flash(Color.RED, 50);
        }
    }

    /**
     * draws a card on the canvas; if the card is null, draw a card-back
     *
     * @param g    the canvas object
     * @param rect a rectangle defining the location to draw the card
     * @param c    the card to draw; if null, a card-back is drawn
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
        } else {
            // just draw the card
            c.drawOn(g, rect);
        }

    }

    /**
     * scales a rectangle, moving all edges with respect to its center
     *
     * @param rect   the original rectangle
     * @param factor the scaling factor
     * @return the scaled rectangle
     */
    private static RectF scaledBy(RectF rect, float factor) {
        // compute the edge locations of the original rectangle, but with
        // the middle of the rectangle moved to the origin
        float midX = (rect.left + rect.right) / 2;
        float midY = (rect.top + rect.bottom) / 2;
        float left = rect.left - midX;
        float right = rect.right - midX;
        float top = rect.top - midY;
        float bottom = rect.bottom - midY;

        // scale each side; move back so that center is in original location
        left = left * factor + midX;
        right = right * factor + midX;
        top = top * factor + midY;
        bottom = bottom * factor + midY;

        // create/return the new rectangle
        return new RectF(left, top, right, bottom);
    }


}
