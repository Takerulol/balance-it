package de.hsbremen.mobile.balanceit.test;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.gameservices.ByteConverter;
import de.hsbremen.mobile.balanceit.gameservices.DataPackage;
import de.hsbremen.mobile.balanceit.gameservices.Header;
import de.hsbremen.mobile.balanceit.gameservices.PackageConverter;

public class ByteConverterTest {

	@Test
	public void testVector3() {
		Vector3 original = new Vector3(2,3,4);
		byte[] array = ByteConverter.toByte(original);
		Vector3 converted = ByteConverter.toVector3(array, 0);
		assertEquals(original, converted);
	}
	
	@Test
	public void testMatrix4() {
		Matrix4 original = new Matrix4(new Quaternion(1,2,3,4));
		byte[] array = ByteConverter.toByte(original);
		Matrix4 converted = ByteConverter.toMatrix4(array, 0);
		//assertEquals(original, converted); //fails, because equals is not implemented correctly for Matrix4
		assertEqualMatrices(original, converted);
	}
	
	@Test
	public void testWorldUpdateSphere() {
		Matrix4 sphere = new Matrix4(new Quaternion(1,2,3,4));
		Matrix4 ground = new Matrix4();
		
		byte[] payload = PackageConverter.getWorldUpdatePackagePayload(sphere, ground);
		float timestamp = 2.5f; 
		DataPackage dataPackage = new DataPackage(Header.WORLD_UPDATE, timestamp, 0, 0, payload);
		List<DataPackage> packages = PackageConverter.getPackages(dataPackage);
		
		DataPackage pkg = packages.get(0);
		assertEquals(Header.SPHERE_MATRIX, pkg.getHeader());
		assertEquals(timestamp, pkg.getTimestamp(), 0.001f);
		assertEqualMatrices(sphere, ByteConverter.toMatrix4(pkg.getPayload(), 0));
		
	}
	
	@Test
	public void testWorldUpdateGround() {
		Matrix4 sphere = new Matrix4();
		Matrix4 ground = new Matrix4(new Quaternion(1,2,3,4));
		
		byte[] payload = PackageConverter.getWorldUpdatePackagePayload(sphere, ground);
		float timestamp = 2.5f; 
		DataPackage dataPackage = new DataPackage(Header.WORLD_UPDATE, timestamp, 0, 0, payload);
		List<DataPackage> packages = PackageConverter.getPackages(dataPackage);
		
		DataPackage pkg = packages.get(1);
		assertEquals(Header.GROUND_ROTATION, pkg.getHeader());
		assertEquals(timestamp, pkg.getTimestamp(), 0.001f);
		assertEqualMatrices(ground, ByteConverter.toMatrix4(pkg.getPayload(), 0));
		
	}
	
	@Test
	public void testPackage() {
		byte[] payload = new byte[] { (byte) 5, (byte) 6};
		DataPackage pkg = new DataPackage(Header.GROUND_ROTATION, 4.3f, 2, 3, payload);
		
		DataPackage reconstructedPackage = DataPackage.fromByte(pkg.toByte());
		
		assertEqualsPackages(pkg, reconstructedPackage);
	}
	
	private void assertEqualsPackages(DataPackage expected, DataPackage actual) {
		assertEquals(expected.getHeader(), actual.getHeader());
		assertEquals(expected.getTimestamp(), actual.getTimestamp(), 0.001f);
		assertEquals(expected.getSequenceNumber(), actual.getSequenceNumber());
		assertEquals(expected.getLastReceivedSequenceNumber(), actual.getLastReceivedSequenceNumber());
		
		assertArrayEquals(expected.getPayload(), actual.getPayload());
	}
	
	
	
	public static void assertEqualMatrices(Matrix4 matrix1, Matrix4 matrix2) {
		assertEqualMatrices(matrix1, matrix2, 0.001f);
	}
	
	public static void assertEqualMatrices(Matrix4 matrix1, Matrix4 matrix2, float epsilon) {
		float[] values1 = matrix1.getValues();
		float[] values2 = matrix2.getValues();
		
		assertTrue(values1.length == values2.length);
		
		for (int i = 0; i < values1.length; i++) {
			float delta = Math.abs(values1[i] - values2[i]);
			assertTrue(delta < epsilon);
			
		}
	}

}
