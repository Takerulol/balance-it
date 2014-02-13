package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;

import de.hsbremen.mobile.balanceit.gameservices.DataPackage;
import de.hsbremen.mobile.balanceit.gameservices.GameService;
import de.hsbremen.mobile.balanceit.gameservices.Header;
import de.hsbremen.mobile.balanceit.gameservices.NetworkManager;
import de.hsbremen.mobile.balanceit.gameservices.SendPhysicsProxy;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;

public class GetReadyView extends View implements NetworkManager.Listener {

	private NetworkManager networkManager;
	private PlayerRole role;
	private GetReadyViewListener listener;
	
	/**
	 * Time after which the player is ready. (Only for balancer and single player)
	 * The ForceApplier will be ready, when the READY package is received.
	 */
	private static final float READY_TIME = 3.0f;
	
	public interface GetReadyViewListener {
		/**
		 * Event occurs, when the player is ready.
		 */
		void onReady();
	}
	
	public GetReadyView(PlayerRole role, NetworkManager networkManager,
			GetReadyViewListener listener) {
		this.networkManager = networkManager;
		this.role = role;
		
		if (networkManager != null)
			networkManager.registerListener(this);
		
		this.listener = listener;
	}
	
	@Override
	public void create() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameService(GameService gameService) {
		// TODO Auto-generated method stub

	}
	
	private float readyTimer = 0.0f;
	private boolean started = false;

	private float waitingPackageTimer = 0.0f;
	
	private void sendWaitingPackage() {
		if (!role.equals(PlayerRole.SinglePlayer)) {
			waitingPackageTimer += Gdx.graphics.getDeltaTime();
			if (waitingPackageTimer >= SendPhysicsProxy.UPDATE_INTERVAL) {
				this.networkManager.sendPackage(Header.WAITING, new byte[] { 0 });
				waitingPackageTimer = 0.0f;
			}
		}
	}
	
	@Override
	public void renderObjects() {
		
		sendWaitingPackage();
		
		if (started)
			updateTimer();
		else
			started = true;
		
	}
	
	private void updateTimer() {
		if (!role.equals(PlayerRole.ForceApplier))
		{
			readyTimer += Gdx.graphics.getDeltaTime();
			
			if (readyTimer >= READY_TIME) {
				
				if (role.equals(PlayerRole.Balancer)) {
					//send ready package
					this.networkManager.sendPackage(Header.READY, new byte[] {0});
				}
				
				listener.onReady();
			}
		}
	}

	@Override
	public void onPackageReceived(DataPackage data) {
		if (data.getHeader().equals(Header.READY) && role.equals(PlayerRole.ForceApplier)) {
			listener.onReady();
		}
	}

}
