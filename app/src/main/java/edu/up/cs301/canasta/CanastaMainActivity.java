package edu.up.cs301.canasta;

import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;
import android.graphics.Color;

/**
 * this is the primary activity for Canasta game
 * 
 * @author Steven R. Vegdahl, Nick Edwards, Aaron Banobi, Michele Lau, David Vanderwark
 * @version July 2013
 */
public class CanastaMainActivity extends GameMainActivity {
	
	public static final int PORT_NUMBER = 4752;

	/** a slapjack game for two players. The default is human vs. computer */
	@Override
	public GameConfig createDefaultConfig() {

		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
		
		playerTypes.add(new GamePlayerType("human player (green)") {
			public GamePlayer createPlayer(String name) {
				return new CanastaHumanPlayer(name, Color.GREEN);
			}});
		playerTypes.add(new GamePlayerType("human player (yellow)") {
			public GamePlayer createPlayer(String name) {
				return new CanastaHumanPlayer(name, Color.YELLOW);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (normal)") {
			public GamePlayer createPlayer(String name) {
				return new CanastaComputerPlayer(name);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (hard)") {
			public GamePlayer createPlayer(String name) {
				return new HardComputerPlayer(name);
			}
		});




		// Create a game configuration class for SlapJack
		GameConfig defaultConfig = new GameConfig(playerTypes, 4, 4, "Canasta", PORT_NUMBER);


		// Add the default players
		defaultConfig.addPlayer("Human", 0);
		defaultConfig.addPlayer("Computer1", 2);
		defaultConfig.addPlayer("Computer2", 3);
		defaultConfig.addPlayer("Computer3", 4);
		
		// Set the initial information for the remote player
		defaultConfig.setRemoteData("Guest", "", 1);
		
		//done!
		return defaultConfig;
	}//createDefaultConfig

	@Override
	public LocalGame createLocalGame() {
		return new CanastaLocalGame();
	}
}
