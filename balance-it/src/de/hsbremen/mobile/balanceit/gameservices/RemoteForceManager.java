package de.hsbremen.mobile.balanceit.gameservices;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.logic.ForceManager;

/**
 * This class processes force vector packages received over the network and provides the current force vector.
 *
 */
public class RemoteForceManager implements ForceManager, NetworkManager.Listener {

	private Vector3 currentForce;
	
	public RemoteForceManager(NetworkManager networkManager) {
		networkManager.registerListener(this);
		currentForce = new Vector3();
	}
	
	@Override
	public Vector3 getForceVector() {
		return currentForce;
	}

	@Override
	public void onMessageReceived(byte[] data) {
		Header header = Header.fromValue(data[0]);
		
		//Gdx.app.log("RemoteForceManager", "Package received with header " + header.toString());
		
		if (header.equals(Header.FORCE_VECTOR)) {
			//the package contains a force vector
			//get force vector and set the current force accordingly
			currentForce = ByteConverter.toVector3(data, 1);
			Gdx.app.log("RemoteForceManager", "Current received force: " + currentForce.toString());
		}
	}

}
