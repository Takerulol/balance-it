package de.hsbremen.mobile.balanceit.gameservices;

import com.badlogic.gdx.math.Matrix4;

import de.hsbremen.mobile.balanceit.logic.GroundRotation;

/**
 * This class receives ground rotation over the network.
 */
public class RemoteGroundRotation implements GroundRotation, NetworkManager.Listener {

	private Matrix4 currentRotation;
	private NetworkManager networkManager;
	private Interpolation interpolation;
	
	public RemoteGroundRotation(NetworkManager networkManager, Timer timer) {
		this.networkManager = networkManager;
		this.networkManager.registerListener(this);
		this.currentRotation = new Matrix4();
		this.interpolation = new Interpolation(timer);
	}
	
	@Override
	public Matrix4 getRotation() {
		Matrix4 interpolated = this.interpolation.getInterpolatedMatrix();
		
		if (interpolated != null) {
			currentRotation = interpolated;
		}
		
		return currentRotation;
	}

	@Override
	public void onPackageReceived(DataPackage data) {
		Header header = data.getHeader();
		
		if (header.equals(Header.GROUND_ROTATION)) {
			currentRotation = ByteConverter.toMatrix4(data.getPayload(), 0);
			this.interpolation.addMatrix(data.getTimestamp(), currentRotation);
		}
	}

}
