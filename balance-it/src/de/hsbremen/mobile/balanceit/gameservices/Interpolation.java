package de.hsbremen.mobile.balanceit.gameservices;

import java.util.SortedMap;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;

/**
 * This class is used as a container for matrices that should be interpolated.
 * It provides methods for getting the current interpolated matrix at any time.
 *
 */
public class Interpolation {
	
	private Timer timer;
	
	/**
	 * The map contains the received transformation matrices and their timestamp.
	 */
	private SortedMap<Float, Matrix4> transformationList;
	
	public Interpolation(Timer timer) {
		this.timer = timer;
		this.transformationList = new TreeMap<Float, Matrix4>();
	}
	
	public synchronized void addMatrix(float timestamp, Matrix4 matrix) {
		transformationList.put(timestamp, matrix);
	}
	
	/**
	 * Returns the interpolated matrix for the current point in time.
	 * Should there be no matrices left to interpolate, a null value will be returned.
	 * @return
	 */
	public synchronized Matrix4 getInterpolatedMatrix() {
		Matrix4 result = null;
		log();
		//check which matrix is the starting point
		SortedMap<Float, Matrix4> sublist = transformationList.headMap(timer.getRenderTime());
		if (sublist.size() > 0) {
			
			float timestampA = sublist.lastKey();
			Matrix4 matrixA = transformationList.get(timestampA);
			
			//check which matrix is the endpoint
			sublist = transformationList.tailMap(timer.getRenderTime());
			
			if (sublist.size() > 0) {
				
				float timestampB = sublist.firstKey();
				Matrix4 matrixB = sublist.get(timestampB);
				
				result = interpolateMatrix4(matrixA, timestampA, matrixB, timestampB);
				
			} else {
				result = matrixA;
			}
			
			//delete all matrices before A
			transformationList.headMap(timestampA).clear();
			
		}
		
		
		
		return result;
	}
	
	/**
	 * Interpolates between the two matrices using the current render time provided by the timer.
	 * @param matrixA The starting point matrix.
	 * @param timeAtMatrixA The time at which the starting point matrix should be rendered.
	 * @param matrixB The end point matrix.
	 * @ timeAtMatrixB The time at which the end point matrix should be rendered. (timeAtMatrixA < timeAtMatrixB)
	 * @return
	 */
	public Matrix4 interpolateMatrix4(Matrix4 matrixA, float timeAtMatrixA, Matrix4 matrixB, float timeAtMatrixB) {
		Matrix4 result = matrixA;
		
		if (timeAtMatrixA < timeAtMatrixB) {
			
			float alpha = (timer.getRenderTime() - timeAtMatrixA) / (timeAtMatrixB - timeAtMatrixA);
			result = matrixA.lerp(matrixB, alpha);
			
		}
		
		return result;
	}
	
	private float logTimer = 0;
	
	private void log() {
		
		if (logTimer < timer.getLocalTime()) {
			String tag = "INTERPOLATION";
			Gdx.app.log(tag, "----------------------------------------");
			Gdx.app.log(tag, "Render Time: " + timer.getRenderTime());
			
			int i = 0;
			for (Float timestamp : this.transformationList.keySet()) {
				Gdx.app.log(tag, i + ": " + timestamp);
				i++;
			}
			
			logTimer = timer.getLocalTime() + 5.0f;
		}
		
	}
	
	
}
