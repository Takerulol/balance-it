package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.math.Vector3;

/**
 * Interface providing methods to get a force vector for the ball.
 */
public interface ForceManager {

	/**
	 * Magnitude of the force vector. 
	 */
	public static final float FORCE_MAGNITUTE = 8.0f;
	
	/**
	 * Returns a vector of the force, that should be applied to the sphere.
	 */
	public abstract Vector3 getForceVector();

}