package de.hsbremen.mobile.balanceit.gameservices;

public interface NetworkManager {

	public interface Listener {
		void onMessageReceived(byte[] data);
	}
	
	public abstract void registerListener(Listener listener); //TODO: This has to accept multiple listeners

	public abstract void unregisterListener(Listener listener);

	/**
	 * Sends a package with the given header to the participant.
	 * @param header
	 * @param payload
	 */
	public abstract void sendPackage(Header header, byte[] payload);

	/**
	 * Message received handler.
	 */
	public abstract void onRealTimeMessageReceived(byte[] message);

}