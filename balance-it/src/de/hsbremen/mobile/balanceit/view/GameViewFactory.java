package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.input.GestureDetector;

import de.hsbremen.mobile.balanceit.logic.BulletPhysics;
import de.hsbremen.mobile.balanceit.logic.ForceDifficultyManager;
import de.hsbremen.mobile.balanceit.logic.ForceManager;
import de.hsbremen.mobile.balanceit.logic.GestureForceManager;
import de.hsbremen.mobile.balanceit.logic.Physics;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;
import de.hsbremen.mobile.balanceit.logic.RandomForceManager;

/**
 * This class creates a GameView class based on given options.
 */
public class GameViewFactory {

	/**
	 * Creates a GameView based on the parameters.
	 * @param listener The GameView.Listener that should be used.
	 * @param role The PlayerRole which the GameView will be used for.
	 * @param increaseFoce Determines whether or not the GameView should increase the force after some time (does not apply to role Balancer).
	 * @return The created GameView.
	 */
	public GameView createGameView(GameView.Listener listener, PlayerRole role, boolean increaseForce) {
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
				ForceManager randomManager = createForceManager(role, increaseForce);
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
	
	/**
	 * Creates the ForceManager based on the player role.
	 * @param increase Adds a ForceDifficultyManager proxy to the ForceManager if true.
	 * @return
	 */
	private ForceManager createForceManager(PlayerRole role, boolean increase) {
		ForceManager manager = null;
		
		switch (role) {
		
		case Balancer:
			manager = new GestureForceManager(); //TODO: Networked
			break;
		
		case ForceApplier:
			manager = new GestureForceManager();
			break;
			
		case SinglePlayer:
			manager = new RandomForceManager(); 
			break;
			
		}
		
		if (increase) {
			manager = new ForceDifficultyManager(manager);
		}
		
		return manager;
	}
	
}
