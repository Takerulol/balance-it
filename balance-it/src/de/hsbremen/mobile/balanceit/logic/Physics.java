package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public interface Physics {

	public abstract void setUp(float groundHeight, float groundWidth,
			float sphereHeight);

	/**
	 * 
	 * @param transform World Transform of the sphere (modelInstance.transform)
	 */
	public abstract void initSphere(Matrix4 transform);

	public abstract void initGround(Matrix4 transform);

	public abstract void update(float deltaTime);

	public abstract Matrix4 getSphereTransform();

	public abstract void resetSphere(Matrix4 transform);

	public abstract void setGroundTransform(Matrix4 transform);

	/**
	 * Applies the given force to the center of the sphere.
	 */
	public abstract void applyForceToSphere(Vector3 force);

	public abstract void dispose();

}