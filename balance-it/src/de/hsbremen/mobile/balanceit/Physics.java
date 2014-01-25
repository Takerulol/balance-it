package de.hsbremen.mobile.balanceit;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

public class Physics {

	btCollisionShape groundShape;
	btCollisionShape fallShape; 
	btDiscreteDynamicsWorld dynamicsWorld;
	btRigidBody sphere;
	btRigidBody ground;
	
	
	public static final int SPHERE_MASS = 2;
	
	//store objects for disposal
	btBroadphaseInterface broadphase;
	btDefaultCollisionConfiguration collisionConfiguration;
	btCollisionDispatcher dispatcher;
	btSequentialImpulseConstraintSolver solver; 
	btRigidBodyConstructionInfo sphereInfo;
	btDefaultMotionState sphereMotionState;
	btDefaultMotionState groundMotionState;
	btRigidBodyConstructionInfo groundInfo;
	
	
	public void setUp(float groundHeight, float groundWidth, float sphereHeight) {
		//collision detection
		broadphase = new btDbvtBroadphase();
		collisionConfiguration = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfiguration);
		solver = new btSequentialImpulseConstraintSolver();
		
		//init dynamic world
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, 
				solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3(0, -10, 0));
		
		//groundShape = new btStaticPlaneShape(new Vector3(0,1,0), groundHeight / 2f);
		Vector3 groundHalfExtends = new 
				Vector3(groundWidth /2f, groundHeight / 2f, groundWidth /2f); //half x, y, z extend
		groundShape = new btCylinderShape(groundHalfExtends);
		fallShape = new btSphereShape(sphereHeight / 2f);
	}
	
	/**
	 * 
	 * @param transform World Transform of the sphere (modelInstance.transform)
	 */
	public void initSphere(Matrix4 transform) {
		//should multiple spheres be build, it is sufficient to pass the same sphere construct info
		//to all rigid bodies
		sphereMotionState = new btDefaultMotionState();
		sphereMotionState.setWorldTransform(transform);
		
		//Trägheit
		Vector3 fallInertia = Vector3.Zero;
		fallShape.calculateLocalInertia(SPHERE_MASS, fallInertia);
		
		sphereInfo = new btRigidBodyConstructionInfo
				(SPHERE_MASS, sphereMotionState, fallShape, fallInertia);
		sphere = new btRigidBody(sphereInfo);
		sphere.setDamping(0.2f, 0.2f);
		dynamicsWorld.addRigidBody(sphere);
	}
	
	public void initGround(Matrix4 transform) {
		groundMotionState = new btDefaultMotionState();
		groundMotionState.setWorldTransform(transform);
		groundInfo = new btRigidBodyConstructionInfo
				(0f, groundMotionState, groundShape, Vector3.Zero);
		ground = new btRigidBody(groundInfo);
		dynamicsWorld.addRigidBody(ground);
	}
	
	public void update(float deltaTime) {
		dynamicsWorld.stepSimulation(deltaTime);
	}
	
	public Matrix4 getSphereTransform() {
		return sphere.getWorldTransform();
	}
	
	public void resetSphere(Matrix4 transform) {
		dynamicsWorld.removeRigidBody(sphere);
		sphere.dispose();
		initSphere(transform);
	}
	
	public void setGroundTransform(Matrix4 transform) {
		ground.setWorldTransform(transform);
	}
	
	public void dispose() {
		if (groundShape != null)
			groundShape.dispose();
		
		if (fallShape != null)
			fallShape.dispose();
		
		if (dynamicsWorld != null)
			dynamicsWorld.dispose();
		
		if (sphere != null)
			sphere.dispose();
		
		if (sphere != null)
			sphere.dispose();
		
		if (ground != null)
			ground.dispose();
		
		if (broadphase != null)
			broadphase.dispose();
		
		if (collisionConfiguration != null)
			collisionConfiguration.dispose();
		
		if (dispatcher != null)
			dispatcher.dispose();
		
		if (solver != null)
			solver.dispose();
		
		if (sphereInfo != null)
			sphereInfo.dispose();
		
		if (sphereMotionState != null)
			sphereMotionState.dispose();
		
		if (groundMotionState != null)
			groundMotionState.dispose();

		if (groundInfo != null)
			groundInfo.dispose();
	}
	
}
