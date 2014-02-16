package de.hsbremen.mobile.googleplay;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

import de.hsbremen.mobile.balanceit.gameservices.DataPackage;
import de.hsbremen.mobile.balanceit.gameservices.Header;
import de.hsbremen.mobile.balanceit.gameservices.NetworkManager;

/**
 * Sends and receives packages to/from all participants.
 * To receive packages, the class needs to be set as a listener in the RoomConfig.
 * --> RoomConfig.Builder.setMessageReceivedListener() 
 */
public class NetworkManagerImpl implements NetworkManager {
	
	
	GamesClient client;
	String roomId;
	String participantId;
	private List<NetworkManager.Listener> listener;
	private Queue<byte[]> receivedDataQueue;
	
	public NetworkManagerImpl(GamesClient client, String roomId, String participantId)
	{
		this.client = client;
		this.roomId = roomId;
		this.participantId = participantId;
		this.listener = new ArrayList<NetworkManager.Listener>();
		this.receivedDataQueue = new ConcurrentLinkedQueue<byte[]>();
	}
	
	/* (non-Javadoc)
	 * @see edu.hsbremen.mobile.viergewinnt.googleplay.NetworkManager#registerListener(edu.hsbremen.mobile.viergewinnt.googleplay.NetworkManagerImpl.Listener)
	 */
	@Override
	public void registerListener(Listener listener) {
		Log.d("NETWORK_MANAGER", "listener registered: " + listener);
		this.listener.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see edu.hsbremen.mobile.viergewinnt.googleplay.NetworkManager#unregisterListener()
	 */
	@Override
	public void unregisterListener(Listener listener) {
		this.listener.remove(listener);
	}

	/* (non-Javadoc)
	 * @see edu.hsbremen.mobile.viergewinnt.googleplay.NetworkManager#sendPackage(edu.hsbremen.mobile.viergewinnt.googleplay.Header, byte[])
	 */
	@Override
	public void sendPackage(Header header, byte[] payload) {
		byte[] messageData = new byte[payload.length+1];
		messageData[0] = header.getByteValue();
		for(int i = 1;i < messageData.length;i++) {
			messageData[i] = payload[i-1];
		}
		sendPackage(messageData);
	}
	
	/**
	 * The integer value will be casted to a byte value!
	 * Larger numbers need to be converted to a byte array first.
	 * @param header
	 * @param payload
	 */
	public void sendPackage(Header header, int payload)
	{
		byte[] buffer = intToByte(payload);
		sendPackage(header,buffer);
	}

	/* (non-Javadoc)
	 * @see edu.hsbremen.mobile.viergewinnt.googleplay.NetworkManager#onRealTimeMessageReceived(com.google.android.gms.games.multiplayer.realtime.RealTimeMessage)
	 */
	@Override
	public void onRealTimeMessageReceived(byte[] message) {
		
		this.receivedDataQueue.add(message);
		
	}
	
	private byte[] intToByte(int value) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(value);

		byte[] result = b.array();
		return result;
	}

	@Override
	public void sendPackage(byte[] message) {
		client.sendReliableRealTimeMessage(null, message, roomId, participantId);
	}

	@Override
	public void update() {
		//notify listener
		while (!this.receivedDataQueue.isEmpty()) {
			
			byte[] message = this.receivedDataQueue.poll();
			
			try {
				DataPackage pkg = DataPackage.fromByte(message);
				//notify observers, that a new message has been received. 
				for (NetworkManager.Listener listener : this.listener) {
					listener.onPackageReceived(pkg);
				}
			}
			
			catch (Exception e) {
				Gdx.app.error("NETWORK_MANAGER","Error while parsing package.");
				Gdx.app.error("NETWORK_MANAGER", e.toString());
			}
			
		}
	}

	
	
}
