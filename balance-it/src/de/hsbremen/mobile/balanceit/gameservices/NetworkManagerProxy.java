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
	
	//current latency in seconds
	private float latency = 0.0f;
	
	/**
	 * List of packages that have been send. Used for latency calculations.
	 */
	private SortedMap<Long, DataPackage> packages;
	
	public NetworkManagerProxy(NetworkManager instance, Timer timer) {
		this.instance = instance;
		this.listener = new ArrayList<Listener>();
		this.instance.registerListener(this);
		this.timer = timer;
		packages = new TreeMap<Long, DataPackage>();
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
		addPackage(pkg);
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
		//update latency and offset
		updateLatency(message);
		this.timer.updateOffset(message.getTimestamp(), latency);
		
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
	
	private synchronized void addPackage(DataPackage sentPackage) {
		packages.put(sentPackage.getSequenceNumber(), sentPackage);
	}
	
	/**
	 * Updates the latency.
	 * Synchronized, because the method will get called on a received package. Because of that, 
	 * multiply threads may enter the method at the same time. Synchronization is needed, because
	 * the method makes heavy modifications to the package map.
	 */
	private synchronized void updateLatency(DataPackage receivedPackage) {
		//calculate the latency for the current received package
		long sequenceNumber = receivedPackage.getLastReceivedSequenceNumber();
		if (packages.containsKey(sequenceNumber)) {
			DataPackage acknowledgedPackage = packages.get(sequenceNumber);
			
			float packageLatency = timer.getLocalTime() - acknowledgedPackage.getTimestamp();
			if (Math.abs(latency) < 0.00001f) {
				//first latency
				latency = packageLatency;
			}
			else {
				//update latency, but take only 10% of the package latency into account
				latency = latency * 0.9f + packageLatency * 0.1f;
			}
			
			//remove all packages that have a smaller sequence number than the last received number
			packages.headMap(sequenceNumber).clear();
			
			
		}
	}
	
	
	//TODO: remove below here
	
	private float latencyTimer = 0.0f;
	
	private void printLatency() {
		String TAG = "LATENCY";
		
		if (timer.getLocalTime() > latencyTimer) {
			//Gdx.app.log(TAG, "Latency: " + new DecimalFormat("#.##").format(latency / 1000));
			Gdx.app.log(TAG, "Latency: " + latency);
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
