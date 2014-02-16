package de.hsbremen.mobile.balanceit.logic;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

/**
 * Returns random force values after some time.
 */
public class RandomForceManager implements ForceManager {

	/**
	 * Time in seconds, when the force direction should be changed.
	 */
	private static float CHANGE_FORCE_TIME = 5.0f;
	private float forceTimer = 0.0f;

	/**
	 * The current force vector.
	 */
	private Vector3 forceVector;
	
	private Random random;
	private Random directionRandom;
	
	public RandomForceManager() {
		random = new Random();
		directionRandom = new Random();
		forceVector = Vector3.Zero;
		//forceVector = getRandomForceVector();
	}
	
	@Override
	public Vector3 getForceVector() {
		forceTimer += Gdx.graphics.getDeltaTime();
		
		if (forceTimer >= CHANGE_FORCE_TIME) {
			forceVector = getRandomForceVector();
			forceTimer = 0.0f;
		}
		
		return forceVector; 
		
	}

	/**
	 * Randomly changes the direction of the force.
	 */
	private Vector3 getRandomForceVector() {
		//get a random direction
		float x = randomizeDirection(random.nextFloat());
		float z = randomizeDirection(random.nextFloat());
		
		Vector3 force = new Vector3(x, 0, z).nor().scl(FORCE_MAGNITUTE);
		return force;
	}
	
	private float randomizeDirection(float value) {
		if (directionRandom.nextBoolean()) {
			value = value * -1;
		}
		
		return value;
	}
	
	
	
	
}
