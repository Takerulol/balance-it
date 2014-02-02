package de.hsbremen.mobile.balanceit.gameservices;

import de.hsbremen.mobile.balanceit.logic.PlayerRole;

public interface GameService {
	
	public interface Listener {
		/**
		 * Tells the listener to start a game using the given player role.
		 * @param role The PlayerRole (i.e. Balancer, ForceApplier or SinglePlayer)
		 * @param manager The NetworkManager used for multiplayer communication during the game.
		 */
		void startMultiplayerGame(PlayerRole role, NetworkManager manager);
	}
	
	void addListener(Listener listener);
	void removeListener(Listener listener);
	
	void login();
	boolean isLoggedIn();
	void logout();
	
	void showAchievements();
	void showLeaderboards();
	
	void submitScore(float score);
	void incrementGamePlayed();
	void addMultiplayerWin();
	void addMultiplayerLoss();
	void invitePlayers();
}
