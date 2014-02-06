package de.hsbremen.mobile.balanceit.gameservices;

import java.nio.ByteBuffer;

import com.badlogic.gdx.math.Matrix4;
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
		ByteBuffer buffer = ByteBuffer.wrap(values);
		float x = buffer.getFloat(0 + offset);
		float y = buffer.getFloat(4 + offset);
		float z = buffer.getFloat(8 + offset);
		return new Vector3(x, y, z);
	} 
	
	public static byte[] toByte(Matrix4 matrix4) {
		float[] values = matrix4.val;
		ByteBuffer buffer = ByteBuffer.allocate(values.length * 4);
		
		for (int i = 0; i < values.length; i++) {
			buffer.putFloat(i * 4, values[i]);
		}
		
		return buffer.array();
	}
	
	
	public static Matrix4 toMatrix4(byte[] values, int offset) {
		ByteBuffer buffer = ByteBuffer.wrap(values);
		float[] array = new float[(values.length - offset) / 4];
		
		int i = 0;
		int index = 0;
		while ((index + offset) < buffer.limit()) {
			array[i] = buffer.getFloat(index + offset);
			i++;
			index = i * 4;
		}
		
		return new Matrix4(array);
	}
	
}
