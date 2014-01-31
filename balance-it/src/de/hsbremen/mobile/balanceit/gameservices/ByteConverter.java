package de.hsbremen.mobile.balanceit.gameservices;

import java.nio.ByteBuffer;
import com.badlogic.gdx.math.Vector3;

/**
 * Class offers some convert methods for byte arrays.
 */
public abstract class ByteConverter {

	public static byte[] toByte(Vector3 vector3) {
		//float is 32 bit, i.e. 4 Byte. Buffer needs to be 12 Bytes long to hold a vector
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.putFloat(vector3.x);
		buffer.putFloat(vector3.y);
		buffer.putFloat(vector3.z);
		
		return buffer.array();
		
	}
	
	public static Vector3 toVector3(byte[] values, int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(values, offset, values.length - 1);
		float x = buffer.getFloat(0);
		float y = buffer.getFloat(4);
		float z = buffer.getFloat(8);
		return new Vector3(x, y, z);
	}
	
}
