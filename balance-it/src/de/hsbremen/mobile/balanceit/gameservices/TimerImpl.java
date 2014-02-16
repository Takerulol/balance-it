package de.hsbremen.mobile.balanceit.gameservices;

/**
 * This class represents a timer.
 * Is used to calculate the current time and current render time for networking.
 */
public class TimerImpl implements Timer {

	/**
	 * The amount of update intervals the client render time is shifted back for interpolation.
	 */
	public static final float CLIENT_RENDER_DELAY = 2.5f;
	
	/**
	 * The current time of the local machine.
	 */
	private float localTime = 0.0f;
	
	/**
	 * The offset time to the server. The current server time will be approximately localTime + offset.
	 * This value will only be updated in intervals specified in OFFSET_UPDATE_INTERVAL.
	 */
	private float offset = 0.0f;
	
	/**
	 * Time in seconds in which the offset will be set to the currentOffset.
	 * This is to prevent the render time to change every time a package is received. 
	 */
	private static final float OFFSET_UPDATE_INTERVAL = 2.0f;
	
	/**
	 * Percentage of the newly calculated offset that will be taken into account for the new offset.
	 */
	private static final float OFFSET_UPDATE_PERCENTAGE = 0.1f;
	
	/**
	 * This offset will be calculated every time a package is received.
	 * It therefore represents the current offset.
	 */
	private float currentOffset = 0.0f;
	
	/* (non-Javadoc)
	 * @see de.hsbremen.mobile.balanceit.gameservices.ITimer#getEstimatedServerTime()
	 */
	@Override
	public float getEstimatedServerTime() {
		return localTime + offset;
	}
	
	/* (non-Javadoc)
	 * @see de.hsbremen.mobile.balanceit.gameservices.ITimer#getRenderTime()
	 */
	@Override
	public float getRenderTime() {
		float renderTime = getEstimatedServerTime() - CLIENT_RENDER_DELAY * SendPhysicsProxy.UPDATE_INTERVAL;
		return renderTime;
	}
	
	/* (non-Javadoc)
	 * @see de.hsbremen.mobile.balanceit.gameservices.ITimer#reset()
	 */
	@Override
	public void reset() {
		localTime = 0.0f;
		offset = 0.0f;
		currentOffset = 0.0f;
	}
	
	private float offsetUpdateTimer = 0.0f;
	
	/* (non-Javadoc)
	 * @see de.hsbremen.mobile.balanceit.gameservices.ITimer#update(float)
	 */
	@Override
	public void update(float deltaTime) {
		localTime += deltaTime;
		offsetUpdateTimer += deltaTime;
		
		//update the offset used for render time calculation.
		if (offsetUpdateTimer >= OFFSET_UPDATE_INTERVAL) {
			offset = currentOffset;
			offsetUpdateTimer = 0.0f;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.hsbremen.mobile.balanceit.gameservices.ITimer#updateOffset(float, float)
	 */
	@Override
	public void updateOffset(float serverTimestamp) {


        float newOffset = serverTimestamp - localTime;

        if (offset == 0.0f)
        {
            offset = newOffset;
            currentOffset = offset;
        }
        else
        {
        	currentOffset = currentOffset * (1 - OFFSET_UPDATE_PERCENTAGE) + newOffset * OFFSET_UPDATE_PERCENTAGE; //use just a percentage to prevent huge jumps
        }
	}
	
	/* (non-Javadoc)
	 * @see de.hsbremen.mobile.balanceit.gameservices.ITimer#getLocalTime()
	 */
	@Override
	public float getLocalTime() {
		return localTime;
	}
	
	
}
