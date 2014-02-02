package de.hsbremen.mobile.balanceit;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import de.hsbremen.mobile.balanceit.gameservices.GameService;
import de.hsbremen.mobile.balanceit.gameservices.NetworkManager;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;
import de.hsbremen.mobile.googleplay.NetworkManagerImpl;
import de.hsbremen.mobile.googleplay.RoomManager;

public class MainActivity extends AndroidApplication 
	implements GameHelperListener, GameService, RoomManager.Listener {
    
	private GameHelper gameHelper;
	
    private final int RC_RESOLVE = 5000, RC_UNUSED = 5001;
	private final static int RC_INVITATION_INBOX = 10001, RC_SELECT_PLAYERS = 10000;

	private static final String TAG = "MainActivity";
	private List<GameService.Listener> listener;
	private RoomManager roomManager;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new ArrayList<GameService.Listener>();
        
        gameHelper = new GameHelper(this);
        gameHelper.enableDebugLog(true, "BIPS");
        
        roomManager = new RoomManager(this, gameHelper.getGamesClient());
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        
//        initialize(new BaseGame(), cfg);
        
        initialize(new BaseGame(this), cfg);
        this.gameHelper.setup(this);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		this.gameHelper.onStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.gameHelper.onStop();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void login() {
		this.gameHelper.beginUserInitiatedSignIn();
	}

	@Override
	public boolean isLoggedIn() {
		return this.gameHelper.isSignedIn();
	}

	@Override
	public void logout() {
		this.gameHelper.signOut();
	}

	@Override
	public void showAchievements() {
		if(isLoggedIn()) {
			startActivityForResult(this.gameHelper.getGamesClient().getAchievementsIntent(), RC_UNUSED);
		} else {
			this.gameHelper.showAlert("not logged in");
		}
	}

	@Override
	public void showLeaderboards() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitScore(float score) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incrementGamePlayed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMultiplayerWin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMultiplayerLoss() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invitePlayers() {
		if (isLoggedIn()) {
			Intent intent = this.gameHelper.getGamesClient().getSelectPlayersIntent(2, 2);
			startActivityForResult(intent, RC_SELECT_PLAYERS);
        } else {
        	Log.d(TAG, "InvitePlayers Error: Not logged in.");
        }
		
	}

	@Override
	public void addListener(Listener listener) {
		this.listener.add(listener);
	}

	@Override
	public void removeListener(Listener listener) {
		this.listener.remove(listener);
	}

	@Override
	public void onStartMultiplayerGame(boolean firstPlayer) {
		PlayerRole role;
		
		if (firstPlayer) 
			role = PlayerRole.Balancer;
		else 
			role = PlayerRole.ForceApplier;
		
		//initialize network manager and set the NetworkManager in the RoomManager
		NetworkManager manager = new NetworkManagerImpl(this.gameHelper.getGamesClient(),
				this.roomManager.getRoomId(), this.roomManager.getParticipantId());
		this.roomManager.setNetworkManager(manager);
		
		for (GameService.Listener listener : this.listener) {
			listener.startMultiplayerGame(role, manager);
		}
	}
}