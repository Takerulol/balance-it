package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

/**
 * Proxy class that enhances the returned force vector based on difficulty settings and elapsed time.
 */
public class ForceDifficultyManager implements ForceManager {

	/**
	 * Time in seconds, after which the force will be increased.
	 */
	private static float INCREASE_DIFFICULTY_TIME = 3.0f;
	
	/**
	 * Value in percent determining by how much the force will be increased after each increasing time step.
	 */
	private static float INCREASING_VALUE = 0.05f;
	
	private float currentPercentage = 1.0f;
	
	private float increaseTimer = 0.0f;
	
	private ForceManager manager;
	
	
	public ForceDifficultyManager(ForceManager baseInstance) {
		this.manager = baseInstance;
	}
	
	@Override
	public Vector3 getForceVector() {
		increaseTimer += Gdx.graphics.getDeltaTime();
		
		boolean log = false;
		
		//increase percentage if necessary
		if (increaseTimer >= INCREASE_DIFFICULTY_TIME) {
			currentPercentage += INCREASING_VALUE;
			increaseTimer = 0.0f;
			log = true;
		}
		
		Vector3 baseVector = this.manager.getForceVector();
		Vector3 increasedVector = new Vector3(baseVector).scl(currentPercentage);
		
		if (log) {
			String percentage = "Percentage: " + currentPercentage;
			Gdx.app.log("balance-it", percentage);
			Gdx.app.log("balance-it", baseVector.toString());
			Gdx.app.log("balance-it", increasedVector.toString());
		}
		
		return increasedVector;
	}

}
