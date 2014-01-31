package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;

import de.hsbremen.mobile.balanceit.logic.ForceManager;
import de.hsbremen.mobile.balanceit.logic.LocalForceManager;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;

/**
 * This class creates a GameView class based on given options.
 */
public class GameViewFactory {

	public GameView createGameView(GameView.Listener listener, PlayerRole role) {
		GameView view = null;
		LocalForceManager manager = null;
		GestureDetector gestureDetector = null;
		
		//create GameView based on player role
		switch (role) {
			case Balancer: //TODO: Networked
				manager = new LocalForceManager(); 
				gestureDetector = new GestureDetector(manager);
				view = new GameView(listener, manager, gestureDetector);
				break;
			case ForceApplier:
				manager = new LocalForceManager(); 
				gestureDetector = new GestureDetector(manager);
				view = new GameView(listener, manager, gestureDetector);
				break;
		}
		
		
		return view;
	}
	
}
