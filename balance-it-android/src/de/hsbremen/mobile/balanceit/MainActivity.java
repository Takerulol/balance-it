package de.hsbremen.mobile.balanceit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import de.hsbremen.mobile.balanceit.gameservices.GameService;

public class MainActivity extends AndroidApplication implements GameHelperListener, GameService {
    
	private GameHelper gameHelper;
	
    private final int RC_RESOLVE = 5000, RC_UNUSED = 5001;
	private final static int RC_INVITATION_INBOX = 10001, RC_SELECT_PLAYERS = 10000;

	private static final String TAG = "MainActivity";
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        gameHelper = new GameHelper(this);
        gameHelper.enableDebugLog(true, "BIPS");
        
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
		Log.d(TAG, "Inviting players..");
		if (isLoggedIn()) {
			Intent intent = this.gameHelper.getGamesClient().getSelectPlayersIntent(1, 3);
			startActivityForResult(intent, RC_SELECT_PLAYERS);
        } else {
        	Log.d(TAG, "InvitePlayers Error: Not logged in.");
        }
		
	}
}