package de.hsbremen.mobile.balanceit.gameservices;

public interface Timer {

	/**
	 * The current time on the server (estimated).
	 * @return
	 */
	public abstract float getEstimatedServerTime();

	/**
	 * The current render time relative to the server. Used for interpolation.
	 */
	public abstract float getRenderTime();

	/**
	 * Resets the timer.
	 */
	public abstract void reset();

	/**
	 * Updates the timer (updates local time).
	 */
	public abstract void update(float deltaTime);

	/**
	 * Updates the offset time relative to the server time.
	 * Is used, when a sent package got acknowledged by a received package.
	 * @param serverTimestamp Timestamp of the acknowledging package.
	 * @param latency Latency of the acknowledging package.
	 */
	public abstract void updateOffset(float serverTimestamp, float latency);

	/**
	 * Returns the local time.
	 */
	public abstract float getLocalTime();

}