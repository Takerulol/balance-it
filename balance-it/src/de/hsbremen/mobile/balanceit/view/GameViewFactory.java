package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.input.GestureDetector;

import de.hsbremen.mobile.balanceit.logic.BulletPhysics;
import de.hsbremen.mobile.balanceit.logic.ForceManager;
import de.hsbremen.mobile.balanceit.logic.GestureForceManager;
import de.hsbremen.mobile.balanceit.logic.Physics;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;
import de.hsbremen.mobile.balanceit.logic.RandomForceManager;

/**
 * This class creates a GameView class based on given options.
 */
public class GameViewFactory {

	public GameView createGameView(GameView.Listener listener, PlayerRole role) {
		GameView view = null;
		GestureForceManager manager = null;
		GestureDetector gestureDetector = null;
		
		//create GameView based on player role
		switch (role) {
			case Balancer: //TODO: Networked ForceManager
				manager = new GestureForceManager(); 
				gestureDetector = new GestureDetector(manager);
				Physics balancerPhysics = createBulletPhysics();
				view = new GameView(listener, manager, gestureDetector, balancerPhysics);
				break;
				
				
			case ForceApplier:
				manager = new GestureForceManager(); 
				gestureDetector = new GestureDetector(manager);
				Physics applierPhysics = createBulletPhysics(); // TODO: Networked physics
				view = new GameView(listener, manager, gestureDetector, applierPhysics);
				break;
				
			case SinglePlayer:
				ForceManager randomManager = new RandomForceManager();
				Physics singlePlayerPhysics = createBulletPhysics();
				view = new GameView(listener, randomManager, null, singlePlayerPhysics);
				break;
				
		}
		
		
		return view;
	}
	
	private Physics createBulletPhysics() {
		Physics physics = new BulletPhysics();
		physics.setUp(GameView.GROUND_HEIGHT, GameView.GROUND_WIDTH, GameView.SPHERE_HEIGHT);
		return physics;
	}
	
}
