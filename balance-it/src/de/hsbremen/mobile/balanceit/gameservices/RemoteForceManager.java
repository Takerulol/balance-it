package de.hsbremen.mobile.balanceit.gameservices;

import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.logic.ForceManager;

/**
 * This class processes force vector packages received over the network and provides the current force vector.
 *
 */
public class RemoteForceManager implements ForceManager, NetworkManager.Listener {

	private Vector3 currentForce = Vector3.Zero;
	
	public RemoteForceManager(NetworkManager networkManager) {
		networkManager.registerListener(this);
	}
	
	@Override
	public Vector3 getForceVector() {
		return currentForce;
	}

	@Override
	public void onMessageReceived(byte[] data) {
		Header header = Header.fromValue(data[0]);
		
		if (header.equals(Header.FORCE_VECTOR)) {
			//the package contains a force vector
			//get force vector and set the current force accordingly
			currentForce = ByteConverter.toVector3(data, 1);
		}
	}

}
