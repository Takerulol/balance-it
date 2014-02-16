package de.hsbremen.mobile.balanceit.gameservices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
	
	private long sequenceNumber = 1;
	private long lastReceivedSequenceNumber = 0;
	
	
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
		DataPackage pkg = createPackage(header, payload);
		this.instance.sendPackage(pkg.toByte());
	}

	@Override
	public void sendPackage(byte[] message) {
		byte[] payload = Arrays.copyOfRange(message, 1, message.length);
		sendPackage(Header.fromValue(message[0]), payload);
	}
	
	private DataPackage createPackage(Header header, byte[] payload) {
		DataPackage pkg = new DataPackage(header, timer.getLocalTime(), 
				sequenceNumber, lastReceivedSequenceNumber, payload);
		
		sequenceNumber++;
		
		return pkg;
	}

	@Override
	public void onRealTimeMessageReceived(byte[] message) {
		this.instance.onRealTimeMessageReceived(message);
	}
	
	private void notifyListener(DataPackage pkg) {
		for (Listener listener : getListener()) {
			listener.onPackageReceived(pkg);
		}
	}

	private List<Listener> getListener() {
		return this.listener;
	}

	
	@Override
	public void onPackageReceived(DataPackage message) {

		this.timer.updateOffset(message.getTimestamp());
		
		if (message.getSequenceNumber() > lastReceivedSequenceNumber) {
			lastReceivedSequenceNumber = message.getSequenceNumber();
		}
		
		//split the message, if its a world update
		Header header = message.getHeader();
		
		//Gdx.app.log("NetworkManagerProxy", "Received message with header " + header);
		
		if (header.equals(Header.WORLD_UPDATE)) {
			List<DataPackage> packages = PackageConverter.getPackages(message);
			
			for (DataPackage pkg : packages) {
				notifyListener(pkg);
			}
		}
		else {
			notifyListener(message);
		}
		
		printLatency(); //TODO: Delete
	}
	
	//TODO: remove below here
	
	private float latencyTimer = 0.0f;
	
	private void printLatency() {
		String TAG = "LATENCY";
		
		if (timer.getLocalTime() > latencyTimer) {
			Gdx.app.log(TAG, "Sequence: " + sequenceNumber);
			Gdx.app.log(TAG, "Last Received: " + lastReceivedSequenceNumber);
			Gdx.app.log(TAG, "Local Time: " + timer.getLocalTime());
			Gdx.app.log(TAG, "Estimated Server Time: " + timer.getEstimatedServerTime());
			Gdx.app.log(TAG, "Render Time: " + timer.getRenderTime());
			
			latencyTimer = timer.getLocalTime() + 3.0f;
		}
	}

	@Override
	public void update() {
		this.instance.update();
		
	}

}
