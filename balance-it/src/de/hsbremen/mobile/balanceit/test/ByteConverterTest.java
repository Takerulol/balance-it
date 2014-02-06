package de.hsbremen.mobile.balanceit.test;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.gameservices.ByteConverter;
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
		
		byte[] update = PackageConverter.getWorldUpdatePackage(sphere, ground);
		List<byte[]> packages = PackageConverter.getPackages(update);
		
		byte[] pkg = packages.get(0);
		assertEquals(Header.SPHERE_MATRIX, Header.fromValue(pkg[0]));
		
		assertEqualMatrices(sphere, ByteConverter.toMatrix4(pkg, 1));
		
	}
	
	@Test
	public void testWorldUpdateGround() {
		Matrix4 sphere = new Matrix4();
		Matrix4 ground = new Matrix4(new Quaternion(1,2,3,4));
		
		byte[] update = PackageConverter.getWorldUpdatePackage(sphere, ground);
		List<byte[]> packages = PackageConverter.getPackages(update);
		
		byte[] pkg = packages.get(1);
		assertEquals(Header.GROUND_ROTATION, Header.fromValue(pkg[0]));
		
		assertEqualMatrices(ground, ByteConverter.toMatrix4(pkg, 1));
		
	}
	
	
	
	private void assertEqualMatrices(Matrix4 matrix1, Matrix4 matrix2) {
		float[] values1 = matrix1.getValues();
		float[] values2 = matrix2.getValues();
		
		float epsilon = 0.001f;
		
		assertTrue(values1.length == values2.length);
		
		for (int i = 0; i < values1.length; i++) {
			float delta = Math.abs(values1[i] - values2[i]);
			assertTrue(delta < epsilon);
			
		}
	}

}
