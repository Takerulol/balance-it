package de.hsbremen.mobile.balanceit.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class SensorGroundRotation implements GroundRotation {

	@Override
	public Matrix4 getRotation() {
		float roll = Gdx.input.getRoll();
		float pitch = Gdx.input.getPitch();
		
		Matrix4 rotation = new Matrix4().setToRotation(Vector3.Z, pitch)
				.mul(new Matrix4().setToRotation(Vector3.X, -roll));
		
		return rotation;
	}

}
