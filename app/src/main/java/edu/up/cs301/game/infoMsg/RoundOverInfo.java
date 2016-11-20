package edu.up.cs301.game.infoMsg;

/**
 * Created by laum18 on 11/20/2016.
 */
public class RoundOverInfo extends GameInfo {

    // to satisfy the Serializable interface
    private static final long serialVersionUID = -865530446658858732L;

    // the message that gives the game's result
    private String message;

    /**
     * constructor
     *
     * @param msg
     * 		a message that tells the result of the game
     */
    public RoundOverInfo(String msg) {
        this.message = msg;
    }

    /**
     * getter method for the message
     *
     * @return
     * 		the message, telling the result of the round
     */
    public String getMessage() {
        return message;
    }
}
