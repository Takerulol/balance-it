package de.hsbremen.mobile.balanceit.gameservices;

import com.badlogic.gdx.Gdx;
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
	private Vector3 lastForce = Vector3.Zero;
	
	private static final String TAG = "RemotePhysics";

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
		//only send packages, if the force has been altered.
		if (!lastForce.equals(force)) {
			
			Gdx.app.log(TAG, "Sending force: " + force.toString());
			
			byte[] payload = ByteConverter.toByte(force);
			this.networkManager.sendPackage(Header.FORCE_VECTOR, payload);
			lastForce = force;
		}
	}
	

	@Override
	public void onMessageReceived(byte[] data) {
		//set the sphere transform according to the package
		Header header = Header.fromValue(data[0]);
		
		//Gdx.app.log(TAG, "Message received with Header " + header.toString());
		
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
