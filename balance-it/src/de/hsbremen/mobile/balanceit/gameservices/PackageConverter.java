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
	private static final int MATRIX4_BYTE_LENGTH = 64;
	
	/**
	 * Packs the current ground and sphere transformation into a world update.
	 * Only contains the payload, i.e. not the WORLD_UPDATE Header.
	 * Protocol:
	 * WORLD_UPDATE Header (1 Byte) (excluded)
	 * SPHERE_MATRIX Header (1 Byte)
	 * Sphere Matrix (64 Byte)
	 * GROUND_ROTATION Header (1 Byte)
	 * Ground Rotation (64 Byte)
	 */
	public static byte[] getWorldUpdatePackagePayload(Matrix4 sphereTransform, Matrix4 groundRotation) {
		byte[] sphere = ByteConverter.toByte(sphereTransform);
		byte[] ground = ByteConverter.toByte(groundRotation);
		
		//2 Header + Payload
		int capacity = 2 + sphere.length + ground.length;
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		
		//buffer.put(Header.WORLD_UPDATE.getByteValue());
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
	public static List<DataPackage> getPackages(DataPackage worldUpdate) {
		List<DataPackage> packages = new ArrayList<DataPackage>();
		
		Header header = worldUpdate.getHeader();
		
		if (header.equals(Header.WORLD_UPDATE)) {
			int firstPkgLimit = MATRIX4_BYTE_LENGTH + 1; //Matrix4 + 1 Header (zero based)
			byte[] payload = Arrays.copyOfRange(worldUpdate.getPayload(), 1, firstPkgLimit); //exclude the header
			
			//Sphere package
			DataPackage pkg = new DataPackage(Header.SPHERE_MATRIX, worldUpdate.getTimestamp(), 0, 0, payload);
			packages.add(pkg); 
			
			//buffer = ByteBuffer.wrap(worldUpdate, 2 + matrix4Length, matrix4Length + 1); 
			
			int secondPkgLimit = firstPkgLimit + 1 + MATRIX4_BYTE_LENGTH; //first limit + 1 Header + 1 Matrix
			
			payload = Arrays.copyOfRange(worldUpdate.getPayload(),  firstPkgLimit + 1, secondPkgLimit); //exclude the header
			pkg = new DataPackage(Header.GROUND_ROTATION, worldUpdate.getTimestamp(), 0, 0, payload);
			packages.add(pkg);
		}
		
		else {
			Gdx.app.error(TAG, "No WORLD_UPDATE Header found. Header was: " + header.toString());
		}
		
		return packages; 
	}
	
}
