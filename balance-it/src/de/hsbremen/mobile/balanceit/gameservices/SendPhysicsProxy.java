package de.hsbremen.mobile.balanceit.gameservices;

import java.nio.ByteBuffer;

import de.hsbremen.mobile.balanceit.logic.GroundRotation;
import de.hsbremen.mobile.balanceit.logic.Physics;
import de.hsbremen.mobile.balanceit.logic.PhysicsBaseDecorator;

/**
 * Proxy class for sending physic updates over the network.
 * Will send a data package containing the sphere and ground transform data.
 */
public class SendPhysicsProxy extends PhysicsBaseDecorator {

	private NetworkManager networkManager;
	private GroundRotation groundRotation;
	
	/**
	 * Update interval in seconds.
	 * The class will send a world update to the client in these intervals.
	 */
	public static final float UPDATE_INTERVAL = 0.05f;
	private float updateTimer = 0.0f;
	
	public SendPhysicsProxy(Physics instance, GroundRotation rotation, NetworkManager networkManager) {
		super(instance);
		this.networkManager = networkManager;
		this.groundRotation = rotation;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		updateTimer += deltaTime;
		
		if (updateTimer >= UPDATE_INTERVAL) {
			//send a world update
			byte[] updateMessage = PackageConverter.getWorldUpdatePackagePayload(
					super.getSphereTransform(), groundRotation.getRotation());
			this.networkManager.sendPackage(Header.WORLD_UPDATE, updateMessage);
			updateTimer = 0.0f;
		}
	}
}
