package de.hsbremen.mobile.balanceit.gameservices;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.logic.PlayerRole;
import de.hsbremen.mobile.balanceit.view.GameView;

/**
 * This class monitors the current gamestate and changes the player role if necessary (event is fired for that).
 * Depending on the player role, the behaviour of the class changes.
 * SINGLE_PLAYER: The class monitors the sphere position and restarts the game as a single player.
 * BALANCER: The class monitors the sphere position. If the sphere has fallen down, a package is send over the network
 * 			to inform the other player and an event is fired to assume the other player role.
 * FORCE_APPLIER: Listens to a package from the balancer and changes the role accordingly.
 */
public class RoleChanger implements NetworkManager.Listener {

	private PlayerRole currentRole;
	private GameView gameView;
	private RoleChangerListener listener;
	private NetworkManager manager;
	
	public RoleChanger(PlayerRole currentRole, GameView gameView,
			RoleChangerListener listener, NetworkManager manager) {
		this.currentRole = currentRole;
		this.gameView = gameView;
		this.listener = listener;
		setManager(manager);
	}

	

	public interface RoleChangerListener {
		void onChangeRole(PlayerRole role);
		void onEndGame();
	}
	
	private boolean isSphereOut(ModelInstance sphere) {
		boolean out = false; 
		if (sphere != null) {
			Vector3 position = new Vector3();
			sphere.transform.getTranslation(position);
			
			if (position.y < GameView.MIN_BALL_Y_POSITION) {
				out = true;
			}
		}
		return out;
	}
	
	public void update() {
		if (!this.currentRole.equals(PlayerRole.ForceApplier)) {
		//only check the position, if the role is not force applier
		//force applier will be updated via network packages
			if (isSphereOut(this.gameView.getSphere())) {
				
				switch (this.currentRole) {
				case SinglePlayer:
					this.listener.onChangeRole(PlayerRole.SinglePlayer);
					break;
					default:
					break;
				}
				
			}
		}
	}
	
	public PlayerRole getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(PlayerRole currentRole) {
		this.currentRole = currentRole;
	}

	public GameView getGameView() {
		return gameView;
	}

	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}

	public RoleChangerListener getListener() {
		return listener;
	}

	public void setListener(RoleChangerListener listener) {
		this.listener = listener;
	}

	public NetworkManager getManager() {
		return manager;
	}

	public void setManager(NetworkManager manager) {
		if (manager != null) {
			if (this.manager != null) {
				this.manager.unregisterListener(this);
			}
			
			this.manager = manager;
			manager.registerListener(this);
		}
	}

	@Override
	public void onPackageReceived(DataPackage data) {
		// TODO Auto-generated method stub
		
	}
	
}
