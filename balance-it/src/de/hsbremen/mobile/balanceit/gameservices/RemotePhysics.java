package de.hsbremen.mobile.balanceit.gameservices;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.logic.Physics;

/**
 * Receives physic updates.
 * Ignores all input, except applying force. The class will send the force vector over the network.
 */
public class RemotePhysics implements Physics, NetworkManager.Listener {

	private NetworkManager networkManager;
	private Matrix4 sphereTransform;

	public RemotePhysics(NetworkManager manager) {
		this.networkManager = manager;
		this.networkManager.registerListener(this);
		
		sphereTransform = new Matrix4();
	}
	
	
	@Override
	public Matrix4 getSphereTransform() {
		return sphereTransform;
	}

	@Override
	public void applyForceToSphere(Vector3 force) {
		byte[] payload = ByteConverter.toByte(force);
		this.networkManager.sendPackage(Header.FORCE_VECTOR, payload);
	}
	

	@Override
	public void onMessageReceived(byte[] data) {
		//set the sphere transform according to the package
		
		Header header = Header.fromValue(data[0]);
		if (header.equals(Header.SPHERE_MATRIX)) {
			sphereTransform = ByteConverter.toMatrix4(data, 1);
		}
	}

	@Override
	public void dispose() {
		//ignore
		
	}
	
	@Override
	public void setUp(float groundHeight, float groundWidth, float sphereHeight) {
		//ignore
		
	}

	@Override
	public void initSphere(Matrix4 transform) {
		//ignore
		
	}

	@Override
	public void initGround(Matrix4 transform) {
		//ignore
		
	}

	@Override
	public void update(float deltaTime) {
		//ignore
	}
	
	@Override
	public void resetSphere(Matrix4 transform) {
		//ignore
	}

	@Override
	public void setGroundTransform(Matrix4 transform) {
		//ignore
	}



}
