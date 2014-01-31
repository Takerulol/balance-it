package de.hsbremen.mobile.balanceit.gameservices;

import de.hsbremen.mobile.balanceit.logic.Physics;
import de.hsbremen.mobile.balanceit.logic.PhysicsBaseDecorator;

/**
 * Proxy class for sending physic updates over the network.
 * Will send a data package on every update call containing the current sphere transform.
 */
public class SendPhysicsProxy extends PhysicsBaseDecorator {

	private NetworkManager networkManager;
	
	public SendPhysicsProxy(Physics instance, NetworkManager networkManager) {
		super(instance);
		this.networkManager = networkManager;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		//send sphere transform
		byte[] payload = ByteConverter.toByte(super.getSphereTransform());
		this.networkManager.sendPackage(Header.SPHERE_MATRIX, payload);
	}
	
	
}
