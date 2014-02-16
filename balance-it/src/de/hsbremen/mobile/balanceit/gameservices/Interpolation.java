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
	
	private float renderTime = 0.0f;

	private static final String tag = "INTERPOLATION";
	
	/**
	 * The map contains the received transformation matrices and their timestamp.
	 */
	private SortedMap<Float, Matrix4> transformationList;
	
	private Matrix4 matrixA, matrixB;
	private float timestampA = 0.0f;
	private float timestampB = 0.0f;
	
	//values for debugging
	private int misses = 0;
	private int calls = 0;
	
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
	public Matrix4 getInterpolatedMatrix() {
		Matrix4 result = null;
		renderTime = timer.getRenderTime();
		//log();
		calls++;
		
		//update matrices if required
		if (matrixA == null) {
			updateMatrices();
		} else {
			//only update, if the timestamp from matrixB is not in the future anymore
			if (timestampB < renderTime) {
				updateMatrices();
			}
		}
		
		if (matrixA != null)
		{
			if (matrixB != null) {
				result = interpolateMatrix4(matrixA, timestampA, matrixB, timestampB);
			} else {
				result = matrixA;
				misses++;
			}
		}	
		
		return result;
	}
	
	private synchronized void updateMatrices() {
		//check which matrix is the starting point
		SortedMap<Float, Matrix4> sublist = transformationList.headMap(renderTime);
		if (sublist.size() > 0) {
			
			timestampA = sublist.lastKey();
			matrixA = transformationList.get(timestampA);
			matrixB = null;
			
			//check which matrix is the endpoint
			sublist = transformationList.tailMap(renderTime);
			
			if (sublist.size() > 0) {
				timestampB = sublist.firstKey();
				matrixB = sublist.get(timestampB);
			} 
			
			//delete all matrices before A
			transformationList.headMap(timestampA).clear();
			
		}
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
			
			float alpha = (renderTime - timeAtMatrixA) / (timeAtMatrixB - timeAtMatrixA);
			result = matrixA.lerp(matrixB, alpha);
			log(alpha);
		}
		
		return result;
	}
	
	private float logTimer = 0;
	
	private synchronized void log(float alpha) {
		
		if (logTimer < timer.getLocalTime()) {
			
			float missPercentage = (float) misses / (float) calls * 100.0f;
			misses = 0;
			calls = 0;
			
			Gdx.app.log(tag, "----------------------------------------");
			Gdx.app.log(tag, "Render Time: " + renderTime);
			Gdx.app.log(tag, "Timestamp A: " + timestampA);
			Gdx.app.log(tag, "Timestamp B: " + timestampB);
			Gdx.app.log(tag, "Alpha: " + alpha);
			Gdx.app.log(tag, "Misses: " + missPercentage + "%");
			
			int i = 0;
			for (Float timestamp : this.transformationList.keySet()) {
				Gdx.app.log(tag, i + ": " + timestamp);
				i++;
			}
			
			logTimer = timer.getLocalTime() + 5.0f;
		}
		
	}
	
	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	
}
