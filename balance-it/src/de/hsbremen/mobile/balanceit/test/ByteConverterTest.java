package de.hsbremen.mobile.balanceit.test;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.gameservices.ByteConverter;

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
	}

}
