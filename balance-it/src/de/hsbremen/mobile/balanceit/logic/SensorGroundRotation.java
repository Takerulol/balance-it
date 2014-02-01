package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class SensorGroundRotation implements GroundRotation {

	private float roll = 0.0f;
	private float pitch = 0.0f;
	
	/**
	 * The maximum rotation rate per frame in degrees.
	 */
	private static final float MAX_ROTATION_RATE = 5.0f;
	
	/**
	 * The minimum rotation rate per frame in degrees.
	 */
	private static final float MIN_ROTATION_RATE = 0.5f;
	
	@Override
	public Matrix4 getRotation() {
		roll = calculateNewValue(Gdx.input.getRoll(), roll);
		pitch = calculateNewValue(Gdx.input.getPitch(), pitch);
		
		Matrix4 rotation = new Matrix4().setToRotation(Vector3.Z, pitch)
				.mul(new Matrix4().setToRotation(Vector3.X, -roll));
		
		return rotation;
	}
	
	/**
	 * Calculates the new value in degrees using the max and min rotation rate.
	 */
	private float calculateNewValue(float value, float oldValue) {
		float newValue = value;
		float delta = value - oldValue;
		
		if (Math.abs(delta) > MAX_ROTATION_RATE)
			newValue = MAX_ROTATION_RATE * Math.signum(delta) + oldValue;
		
		else if (Math.abs(delta) < MIN_ROTATION_RATE) 
			newValue = oldValue;
		
		String message = "old: " + oldValue + " current: " + value + " new: " + newValue;
		Gdx.app.log("balance-it", message);
		
		return newValue;
	}

}
