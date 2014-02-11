package de.hsbremen.mobile.balanceit.gameservices;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

/**
 * This class splits the messages received in smaller packages.
 * Important for world updates, that contain more than one package.
 * In addition the class keeps track of sequence numbers, calculates latency, and adds timestamps to the packages.
 *
 */
public class NetworkManagerProxy implements NetworkManager, NetworkManager.Listener {

	private NetworkManager instance;
	private List<Listener> listener;
	private Timer timer;
	
	public NetworkManagerProxy(NetworkManager instance, Timer timer) {
		this.instance = instance;
		this.listener = new ArrayList<Listener>();
		this.instance.registerListener(this);
		this.timer = timer;
	}
	
	@Override
	public void registerListener(Listener listener) {
		this.listener.add(listener);

	}

	@Override
	public void unregisterListener(Listener listener) {
		this.listener.remove(listener);

	}

	@Override
	public void sendPackage(Header header, byte[] payload) {
		this.instance.sendPackage(header, payload);

	}

	@Override
	public void sendPackage(byte[] message) {
		this.instance.sendPackage(message);

	}

	@Override
	public void onRealTimeMessageReceived(byte[] message) {
		this.instance.onRealTimeMessageReceived(message);
	}
	
	private void notifyListener(byte[] pkg) {
		for (Listener listener : getListener()) {
			listener.onMessageReceived(pkg);
		}
	}

	private List<Listener> getListener() {
		return this.listener;
	}

	@Override
	public void onMessageReceived(byte[] message) {
		//split the message, if its a world update
		Header header = Header.fromValue(message[0]);
		
		Gdx.app.log("NetworkManagerProxy", "Received message with header " + header);
		
		if (header.equals(Header.WORLD_UPDATE)) {
			List<byte[]> packages = PackageConverter.getPackages(message);
			
			for (byte[] pkg : packages) {
				notifyListener(pkg);
			}
		}
		else {
			notifyListener(message);
		}
		
	}

}
