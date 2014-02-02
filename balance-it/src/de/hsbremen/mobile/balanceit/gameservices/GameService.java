package de.hsbremen.mobile.balanceit.gameservices;

public interface GameService {
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
