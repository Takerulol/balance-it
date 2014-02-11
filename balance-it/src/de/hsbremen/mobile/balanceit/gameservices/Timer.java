package de.hsbremen.mobile.balanceit.gameservices;

/**
 * This class represents a timer.
 * Is used to calculate the current time and current render time for networking.
 */
public class Timer {

	/**
	 * The time in seconds that the client will be rendered in the past relative to the server.
	 */
	public static final float CLIENT_RENDER_DELAY = 0.1f;
	
	/**
	 * The current time of the local machine.
	 */
	private float localTime = 0.0f;
	
	/**
	 * The offset time to the server. The current server time will be approximately localTime + offset.
	 */
	private float offset = 0.0f;
	
	float currentLatency = 0.0f;
	
	/**
	 * The current time on the server (estimated).
	 * @return
	 */
	public float getEstimatedServerTime() {
		return localTime + offset;
	}
	
	/**
	 * The current render time relative to the server. Used for interpolation.
	 */
	public float getRenderTime() {
		float renderTime = getEstimatedServerTime() - currentLatency * 0.5f - CLIENT_RENDER_DELAY * SendPhysicsProxy.UPDATE_INTERVAL;
		return renderTime;
	}
	
	/**
	 * Resets the timer.
	 */
	public void reset() {
		localTime = 0.0f;
		offset = 0.0f;
		currentLatency = 0.0f;
	}
	
	/**
	 * Updates the timer (updates local time).
	 */
	public void update(float deltaTime) {
		localTime += deltaTime;
	}
	
	/**
	 * Updates the offset time relative to the server time.
	 * Is used, when a sent package got acknowledged by a received package.
	 * @param serverTimestamp Timestamp of the acknowledging package.
	 * @param latency Latency of the acknowledging package.
	 */
	public void updateOffset(float serverTimestamp, float latency) {
		this.currentLatency = latency;

        float newEstimatedServerTime = serverTimestamp + this.currentLatency * 0.5f;

        float newOffset = newEstimatedServerTime - localTime;

        if (offset == 0.0f)
        {
            offset = newOffset;
        }
        else
        {
            offset = offset * 0.95f + newOffset * 0.05f; //only use 5% of new time to prevent huge jumps
        }
	}
	
	
}
