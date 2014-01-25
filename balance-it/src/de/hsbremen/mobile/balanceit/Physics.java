package de.hsbremen.mobile.balanceit;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
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
	
	public static final int SPHERE_MASS = 1;
	
	
	public void setUp(float groundHeight, float sphereHeight) {
		//collision detection
		btBroadphaseInterface broadphase = new btDbvtBroadphase();
		btDefaultCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration();
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		
		//init dynamic world
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, 
				solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3(0, -10, 0));
		
		//set up shapes TODO: Maybe in a separate method and with parameters
		groundShape = new btStaticPlaneShape(new Vector3(0,1,0), groundHeight / 2f);
		fallShape = new btSphereShape(sphereHeight / 2f);
	}
	
	/**
	 * 
	 * @param transform World Transform of the sphere (modelInstance.transform)
	 */
	public void initSphere(Matrix4 transform) {
		//should multiple spheres be build, it is sufficient to pass the same sphere construct info
		//to all rigid bodies
		btDefaultMotionState sphereMotionState = new btDefaultMotionState();
		sphereMotionState.setWorldTransform(transform);
		
		//Trägheit
		Vector3 fallInertia = Vector3.Zero;
		fallShape.calculateLocalInertia(SPHERE_MASS, fallInertia);
		
		btRigidBodyConstructionInfo sphereInfo = new btRigidBodyConstructionInfo
				(SPHERE_MASS, sphereMotionState, fallShape, fallInertia);
		sphere = new btRigidBody(sphereInfo);
		dynamicsWorld.addRigidBody(sphere);
	}
	
	public void initGround(Matrix4 transform) {
		btDefaultMotionState groundMotionState = new btDefaultMotionState();
		groundMotionState.setWorldTransform(transform);
		btRigidBodyConstructionInfo groundInfo = new btRigidBodyConstructionInfo
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
	
	public void setGroundTransform(Matrix4 transform) {
		ground.setWorldTransform(transform);
	}
	
	public void dispose() {
		//TODO: Dispose everything, safe in lists
	}
	
}
