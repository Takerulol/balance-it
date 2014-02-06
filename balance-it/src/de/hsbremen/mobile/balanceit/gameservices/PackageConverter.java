package de.hsbremen.mobile.balanceit.gameservices;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public abstract class PackageConverter {

	private static final String TAG = "PackageConverter";
	
	/**
	 * Packs the current ground and sphere transformation into a world update.
	 * 
	 * Protocol:
	 * WORLD_UPDATE Header (1 Byte)
	 * SPHERE_MATRIX Header (1 Byte)
	 * Sphere Matrix (12 Byte)
	 * GROUND_ROTATION Header (1 Byte)
	 * Ground Rotation (12 Byte)
	 */
	public static byte[] getWorldUpdatePackage(Matrix4 sphereTransform, Matrix4 groundRotation) {
		byte[] sphere = ByteConverter.toByte(sphereTransform);
		byte[] ground = ByteConverter.toByte(groundRotation);
		
		//3 Header + Payload
		int capacity = 3 + sphere.length + ground.length;
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		
		buffer.put(Header.WORLD_UPDATE.getByteValue());
		buffer.put(Header.SPHERE_MATRIX.getByteValue());
		buffer.put(sphere);
		buffer.put(Header.GROUND_ROTATION.getByteValue());
		buffer.put(ground);
		
		return buffer.array();
	}
	
	/**
	 * Splits the world update in smaller packages and returns them in a list.
	 * @param worldUpdate The complete world update.
	 * @return List of packages, i.e. 1. SPHERE_MATRIX - Matrix4 2. GROUND_ROTATION - Matrix4
	 */
	public static List<byte[]> getPackages(byte[] worldUpdate) {
		List<byte[]> packages = new ArrayList<byte[]>();
		
		Header header = Header.fromValue(worldUpdate[0]);
		
		if (Header.fromValue(worldUpdate[0]).equals(Header.WORLD_UPDATE)) {
			int matrix4Length = new Matrix4().getValues().length * 4; //TODO: Constant
			//ByteBuffer buffer = ByteBuffer.wrap(worldUpdate, 1, matrix4Length + 1); //Header + Matrix4
			int firstPkgLimit = matrix4Length + 2; //Matrix4 + 2 Headers (zero based)
			byte[] pkg = Arrays.copyOfRange(worldUpdate, 1, firstPkgLimit);
			
			packages.add(pkg); //Sphere package
			
			//buffer = ByteBuffer.wrap(worldUpdate, 2 + matrix4Length, matrix4Length + 1); 
			
			int secondPkgLimit = firstPkgLimit + 1 + matrix4Length; //first limit + 1 Header + 1 Matrix
			
			pkg = Arrays.copyOfRange(worldUpdate,  firstPkgLimit, secondPkgLimit);
			packages.add(pkg);
		}
		
		else {
			Gdx.app.error(TAG, "No WORLD_UPDATE Header found. Header was: " + header.toString());
		}
		
		return packages; 
	}
	
}
