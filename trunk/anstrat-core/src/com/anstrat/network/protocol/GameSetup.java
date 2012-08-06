package com.anstrat.network.protocol;

import java.io.Serializable;

import com.anstrat.geography.Map;

/**
 * Describes information requires to setup a network game.
 * @author Erik
 *
 */
public class GameSetup implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public transient static final int gametype_small_random = 1;
	public transient static final int gametype_medium_random = 2;
	public transient static final int gametype_large_random = 3;
	public transient static final int gametype_small_premade = 4;
	public transient static final int gametype_medium_premade = 5;
	public transient static final int gametype_large_premade = 6;
	
	public final Map map;
	public final long randomSeed;
	public final Player[] players;
	
	public GameSetup(Map map, long randomSeed, Player[] players) {
		this.map = map;
		this.randomSeed = randomSeed;
		this.players = players;
	}

	public static class Player implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public final long userID;
		public final int team;
		public final int god;
		public final String displayName;
		
		public Player(long userID, int team, int god, String displayName) {
			this.userID = userID;
			this.team = team;
			this.god = god;
			this.displayName = displayName;
		}
	}
}
