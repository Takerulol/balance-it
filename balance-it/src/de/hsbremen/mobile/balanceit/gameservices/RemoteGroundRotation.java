package de.hsbremen.mobile.balanceit.gameservices;

import com.badlogic.gdx.math.Matrix4;

import de.hsbremen.mobile.balanceit.logic.GroundRotation;

/**
 * This class receives ground rotation over the network.
 */
public class RemoteGroundRotation implements GroundRotation, NetworkManager.Listener {

	private Matrix4 currentRotation;
	private NetworkManager networkManager;
	
	public RemoteGroundRotation(NetworkManager networkManager) {
		this.networkManager = networkManager;
		this.networkManager.registerListener(this);
		this.currentRotation = new Matrix4();
	}
	
	@Override
	public Matrix4 getRotation() {
		return currentRotation;
	}

	@Override
	public void onMessageReceived(byte[] data) {
		Header header = Header.fromValue(data[0]);
		
		if (header.equals(Header.GROUND_ROTATION)) {
			currentRotation = ByteConverter.toMatrix4(data, 1);
		}
	}

}
