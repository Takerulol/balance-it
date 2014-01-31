package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class PhysicsBaseDecorator implements Physics {

	private Physics instance;
	
	public PhysicsBaseDecorator(Physics instance) {
		this.instance = instance;
	}
	
	@Override
	public void setUp(float groundHeight, float groundWidth, float sphereHeight) {
		instance.setUp(groundHeight, groundWidth, sphereHeight);

	}

	@Override
	public void initSphere(Matrix4 transform) {
		instance.initSphere(transform);

	}

	@Override
	public void initGround(Matrix4 transform) {
		instance.initGround(transform);

	}

	@Override
	public void update(float deltaTime) {
		instance.update(deltaTime);

	}

	@Override
	public Matrix4 getSphereTransform() {
		return instance.getSphereTransform();
	}

	@Override
	public void resetSphere(Matrix4 transform) {
		instance.resetSphere(transform);

	}

	@Override
	public void setGroundTransform(Matrix4 transform) {
		instance.setGroundTransform(transform);

	}

	@Override
	public void applyForceToSphere(Vector3 force) {
		instance.applyForceToSphere(force);

	}

	@Override
	public void dispose() {
		instance.dispose();

	}

}
