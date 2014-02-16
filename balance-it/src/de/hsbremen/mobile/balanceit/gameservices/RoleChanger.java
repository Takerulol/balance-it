package de.hsbremen.mobile.balanceit.gameservices;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.logic.PlayerRole;
import de.hsbremen.mobile.balanceit.view.GameView;

/**
 * This class monitors the current gamestate and changes the player role if necessary (event is fired for that).
 * Depending on the player role, the behavior of the class changes.
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
	private static final String TAG = "RoleChanger";
	
	//Variables for winner detection
	private Timer timer;
	private float enemyTime = 0.0f;
	private float myTime = 0.0f;
	
	public RoleChanger(PlayerRole currentRole, GameView gameView,
			RoleChangerListener listener, NetworkManager manager, Timer timer) {
		this.currentRole = currentRole;
		this.gameView = gameView;
		this.listener = listener;
		this.timer = timer;
		setManager(manager);
	}

	

	public interface RoleChangerListener {
		void onChangeRole(PlayerRole role);
		void onEndGame(float myTime);
		void onEndGame(float myTime, float enemyTime);
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
				
				myTime = timer.getLocalTime();
				
				if (myTime != 0.0f) {
				
					switch (this.currentRole) {
					case SinglePlayer:
						Gdx.app.log(TAG, "Changing role to SinglePlayer");
						changeRole(PlayerRole.SinglePlayer);
						break;
					
					case Balancer:
					//send a package to the other player to change the role then change role
						Gdx.app.log(TAG, "Changing role to ForceApplier. Sending Package.");
						ByteBuffer payload = ByteBuffer.allocate(5); //PlayerRole + Time
						payload.put(PlayerRole.Balancer.getByteValue());
						payload.putFloat(myTime);
						//byte[] payload = { PlayerRole.Balancer.getByteValue() };
						this.manager.sendPackage(Header.CHANGE_ROLE, payload.array());
						changeRole(PlayerRole.ForceApplier);
						break;
						
					case ForceApplier:
						//do nothing
						break;
					}
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
		
		if (data.getHeader().equals(Header.CHANGE_ROLE)) {
			//change role to the corresponding role
			
			Gdx.app.log(TAG, "Changing role according to Package.");
			
			ByteBuffer payload = ByteBuffer.wrap(data.getPayload());
			PlayerRole role = PlayerRole.fromValue(payload.get());
			enemyTime = payload.getFloat();
			
			
			Gdx.app.log(TAG, "Package role: " + role.toString() + " EnemyTime: " + enemyTime);
			
			changeRole(role);
		}
	}
	
	/**
	 * Notifies all listeners to change the role, or notifies them to show the result screen, if
	 * the game is over.
	 */
	private void changeRole(PlayerRole role) {
		
		if (myTime != 0.0f && role.equals(PlayerRole.SinglePlayer)) {
			//end the game single player
			this.listener.onEndGame(myTime);
		}
		else if (myTime != 0.0f && enemyTime != 0.0f) {
			//end game multiplayer
			this.listener.onEndGame(myTime, enemyTime);
		}
		else {
			//change the role multiplayer
			if (!role.equals(PlayerRole.SinglePlayer))
				this.listener.onChangeRole(role);
		}
	
	}
	
	public void reset() {
		enemyTime = 0.0f;
		myTime = 0.0f;
	}
	
}
