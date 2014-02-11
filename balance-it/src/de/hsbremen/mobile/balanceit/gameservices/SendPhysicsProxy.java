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
			byte[] updateMessage = getWorldUpdatePackage();
			this.networkManager.sendPackage(updateMessage);
			updateTimer = 0.0f;
		}
	}
	
	/**
	 * Packs the current ground and sphere transformation into a world update.
	 * 
	 * Protocol:
	 * WORLD_UPDATE Header (1 Byte)
	 * SPHERE_MATRIX Header (1 Byte)
	 * Sphere Matrix (12 Byte)
	 * GROUND_ROTATION Header (1 Byte)
	 * Ground Rotation (12 Byte)
	 */
	private byte[] getWorldUpdatePackage() {
		byte[] sphere = ByteConverter.toByte(super.getSphereTransform());
		byte[] ground = ByteConverter.toByte(groundRotation.getRotation());
		
		//3 Header + Payload
		int capacity = 3 + sphere.length + ground.length;
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		
		buffer.put(Header.WORLD_UPDATE.getByteValue());
		buffer.put(Header.SPHERE_MATRIX.getByteValue());
		buffer.put(sphere);
		buffer.put(Header.GROUND_ROTATION.getByteValue());
		buffer.put(ground);
		
		return buffer.array();
	}
	
	
}
