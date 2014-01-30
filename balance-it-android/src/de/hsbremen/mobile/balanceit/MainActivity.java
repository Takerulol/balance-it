package de.hsbremen.mobile.balanceit;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import de.hsbremen.mobile.balanceit.gameservices.GameService;

public class MainActivity extends AndroidApplication implements GameHelperListener, GameService {
    private GameHelper gameHelper;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        gameHelper = new GameHelper(this);
        gameHelper.enableDebugLog(true, "BIPS");
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        
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
		// TODO Auto-generated method stub
		
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
}