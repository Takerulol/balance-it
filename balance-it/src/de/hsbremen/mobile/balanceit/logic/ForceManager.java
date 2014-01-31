package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.math.Vector3;

/**
 * Interface providing methods to get a force vector for the ball.
 */
public interface ForceManager {

	/**
	 * Returns a vector of the force, that should be applied to the sphere.
	 */
	public abstract Vector3 getForceVector();

}