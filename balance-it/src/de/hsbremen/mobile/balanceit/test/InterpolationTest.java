package de.hsbremen.mobile.balanceit.test;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import de.hsbremen.mobile.balanceit.gameservices.Timer;
import de.hsbremen.mobile.balanceit.gameservices.Interpolation;
import de.hsbremen.mobile.balanceit.gameservices.TimerImpl;

public class InterpolationTest {

	Interpolation interpolation;
	Timer timer; //mock
	
	//matrices
	Matrix4 matrix0;
	Matrix4 matrix1;
	Matrix4 matrix2;
	
	@Before
	public void setUp() {
		timer = createNiceMock(Timer.class);
		interpolation = new Interpolation(timer);
		
		//matrices
		Matrix4 matrix = new Matrix4();
		matrix0 = matrix;
		matrix1 = matrix.cpy().setToRotation(Vector3.X, 10.0f);
		matrix2 = matrix.cpy().setToRotation(Vector3.X, 20.0f);
		
		interpolation.addMatrix(0.0f, matrix0);
		interpolation.addMatrix(1.0f, matrix1);
		interpolation.addMatrix(2.0f, matrix2);
	}
	
	private void prepareTimer(float renderTime) {
		timer = createNiceMock(Timer.class);
		expect(timer.getRenderTime()).andReturn(renderTime).anyTimes();
		replay(timer);
		interpolation.setTimer(timer);
	}
	
	@Test
	public void testDefaultValues() {
		prepareTimer(1.0f);
		Matrix4 matrix = interpolation.getInterpolatedMatrix();
		ByteConverterTest.assertEqualMatrices(matrix1, matrix);
		
		prepareTimer(2.0f);
		matrix = interpolation.getInterpolatedMatrix();
		ByteConverterTest.assertEqualMatrices(matrix2, matrix);
	}
	
	//Test at time 0.5
	@Test
	public void testInterpolation1() {
		prepareTimer(0.5f);
		Matrix4 matrix = interpolation.getInterpolatedMatrix();
		Matrix4 expected = new Matrix4().setToRotation(Vector3.X, 5.0f);
		ByteConverterTest.assertEqualMatrices(expected, matrix, 0.1f);
	}
	
	//Test at time 1.5
	@Test
	public void testInterpolation2() {
		prepareTimer(1.5f);
		Matrix4 matrix = interpolation.getInterpolatedMatrix();
		Matrix4 expected = new Matrix4().setToRotation(Vector3.X, 15.0f);
		ByteConverterTest.assertEqualMatrices(expected, matrix, 0.1f);
	}
	
	@Test
	public void testMultipleInterpolations() {
		testInterpolation2();
		testInterpolation2(); 
	}
	
	//Test at time 2.5
	@Test
	public void testInterpolation3() {
		prepareTimer(2.5f);
		Matrix4 matrix = interpolation.getInterpolatedMatrix();
		ByteConverterTest.assertEqualMatrices(matrix2, matrix, 0.01f);
	}

}
