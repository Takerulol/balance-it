package de.hsbremen.mobile.balanceit.gameservices;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;

/**
 * Represents a single data package that can be send over the network.
 *
 */
public class DataPackage {

	//Header and timestamp of the package.
	private Header header;
	private float timestamp;
	
	//Sequence Number of this package and of the last received package by the sender
	private long sequenceNumber;
	private long lastReceivedSequenceNumber;
	
	//payload of the package
	private byte[] payload;

	public DataPackage(Header header, float timestamp, long sequenceNumber,
			long lastReceivedSequenceNumber, byte[] payload) {
		this.header = header;
		this.timestamp = timestamp;
		this.sequenceNumber = sequenceNumber;
		this.lastReceivedSequenceNumber = lastReceivedSequenceNumber;
		this.payload = payload;
	}

	//byte conversion methods
	
	/**
	 * Converts the package into a byte array.
	 * @return The package as a byte array.
	 */
	public byte[] toByte() {
		/* Header: 1 Byte
		 * Timestamp: 4 Byte
		 * SequenceNumber: 8 Byte
		 * LastSequenceNumber: 8 Byte
		 * Payload: Variable
		*/
		int size = 1 + 4 + 8 + 8 + payload.length;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		
		buffer.put(header.getByteValue());
		buffer.putFloat(timestamp);
		buffer.putLong(sequenceNumber);
		buffer.putLong(lastReceivedSequenceNumber);
		buffer.put(payload);
		
		return buffer.array();
	}
	
	public static DataPackage fromByte(byte[] bytePackage) {
		ByteBuffer buffer = ByteBuffer.wrap(bytePackage);
		
		Header header = Header.fromValue(buffer.get());
		float timestamp = buffer.getFloat();
		long sequenceNumber = buffer.getLong();
		long receivedSequenceNumber = buffer.getLong();
		
		byte[] payload = Arrays.copyOfRange(bytePackage, buffer.position(), buffer.limit());
		
		DataPackage pkg = new DataPackage(header,timestamp, sequenceNumber, receivedSequenceNumber, payload);
		
		return pkg;
	}
	
	
	//getter and setter
	
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public float getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(float timestamp) {
		this.timestamp = timestamp;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public long getLastReceivedSequenceNumber() {
		return lastReceivedSequenceNumber;
	}

	public void setLastReceivedSequenceNumber(long lastReceivedSequenceNumber) {
		this.lastReceivedSequenceNumber = lastReceivedSequenceNumber;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	
	
}
