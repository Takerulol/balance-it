package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ForceManager extends GestureAdapter {

	/**
	 * Vector of the force, that should be applied to the sphere.
	 */
	private Vector3 forceVector = Vector3.Zero;
	
	
	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		forceVector = new Vector3(velocityX, 0, velocityY).nor();
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Returns a vector of the force, that should be applied to the sphere.
	 */
	public Vector3 getForceVector() {
		return forceVector;
	}

}