package de.hsbremen.mobile.googleplay;

import android.content.Context;

import com.google.android.gms.games.GamesClient;
import com.google.example.games.basegameutils.GameHelper;

import de.hsbremen.mobile.balanceit.gameservices.Achievements;

public class AchievementsImpl implements Achievements {

	private GameHelper gameHelper;
	private Context context;
	
	public AchievementsImpl(GameHelper gameHelper, Context context) {
		super();
		this.gameHelper = gameHelper;
		this.context = context;
	}

	@Override
	public void unlockSingleplayerTime(float time) {
		GamesClient client = gameHelper.getGamesClient();
		if (client != null) {
			if (client.isConnected()) {
			
				if (time > 60)
					client.unlockAchievement(
							context.getString(de.hsbremen.mobile.balanceit.R.string.achievement_steady__));
				
				if (time > 90)
					client.unlockAchievement(
							context.getString(de.hsbremen.mobile.balanceit.R.string.achievement_its_almost_over_9000_));
					
				if (time > 120)
					client.unlockAchievement(
							context.getString(de.hsbremen.mobile.balanceit.R.string.achievement_steady_steady__));
				
				if (time > 180)
					client.unlockAchievement(
							context.getString(de.hsbremen.mobile.balanceit.R.string.achievement_balancement_king));
				
			}
			
		}
	}

	@Override
	public void unlockMultiplayerWin() {
		GamesClient client = gameHelper.getGamesClient();
		if (client != null) {
			if (client.isConnected()) {
				
				client.unlockAchievement(
						context.getString(de.hsbremen.mobile.balanceit.R.string.achievement_junior_balancer));
				
				client.incrementAchievement(
						context.getString(de.hsbremen.mobile.balanceit.R.string.achievement_senior_balancer), 1);
				
				client.incrementAchievement(
						context.getString(de.hsbremen.mobile.balanceit.R.string.achievement_master_balancer), 1);
			}
			
		}

	}

}
