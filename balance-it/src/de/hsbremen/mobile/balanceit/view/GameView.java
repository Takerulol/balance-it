package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;

import de.hsbremen.mobile.balanceit.logic.ForceManager;
import de.hsbremen.mobile.balanceit.logic.GestureForceManager;
import de.hsbremen.mobile.balanceit.logic.BulletPhysics;
import de.hsbremen.mobile.balanceit.logic.GroundRotation;
import de.hsbremen.mobile.balanceit.logic.Physics;

public class GameView extends View {
	
	public static interface Listener {
		void switchToMenu();
	}
	
	private Listener listener;

	public static final float SPHERE_HEIGHT = 1.5f;
	public static final float GROUND_HEIGHT = 1.0f;
	public static final float GROUND_WIDTH = 20f;
	private static final Vector3 SPHERE_INITIAL_POSITION = new Vector3(0,10,0);
	private static final float MIN_BALL_Y_POSITION = -GROUND_WIDTH * 1.5f; //balls will get reseted when falling below this value
	
	private PerspectiveCamera cam;
	private Model model;
	private Model ball;
	private ModelInstance instance;
	private ModelInstance instance2;
	private ModelBatch modelBatch;
	private Environment environment;
	private CameraInputController camController; //TODO: delete
	private ForceManager forceManager;
	private InputProcessor inputProcessor; 
	
	Physics physics;

	private GroundRotation groundRotation;
	
	
	/**
	 * private to enforce listener
	 */
	private GameView() {}
	
	public GameView(Listener listener, ForceManager forceManager, InputProcessor input,
			Physics physics, GroundRotation rotation) {
		this.listener = listener;
		this.forceManager = forceManager;
		this.inputProcessor = input;
		this.physics = physics;
		this.groundRotation = rotation;
	}
	
	
	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.4f,0.4f,0.4f,1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		modelBatch = new ModelBatch();
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//cam.position.set(10f, 10f, 10f);
		cam.position.set(0.0f, 20f, 0.01f);
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();
			
		camController = new CameraInputController(cam);
		//Gdx.input.setInputProcessor(camController);
		
		Gdx.input.setInputProcessor(inputProcessor);
		
		
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createCylinder(20f, GROUND_HEIGHT, GROUND_WIDTH, 32, 
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), 
				Usage.Position | Usage.Normal);
//		model = modelBuilder.createBox(5f, 5f, 5f, 
//				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), 
//				Usage.Position | Usage.Normal);
		instance = new ModelInstance(model);
		
		physics.initGround(instance.transform);
		
		ball = modelBuilder.createSphere(SPHERE_HEIGHT, SPHERE_HEIGHT, SPHERE_HEIGHT, 32, 32, 
				new Material(ColorAttribute.createDiffuse(Color.RED)),
				Usage.Position | Usage.Normal);
		
		instance2 = new ModelInstance(ball, SPHERE_INITIAL_POSITION);
		
		physics.initSphere(instance2.transform);
		
//		camera = new OrthographicCamera(1, h/w);
//		batch = new SpriteBatch();
//		
//		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
//		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//		
//		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
//		
//		sprite = new Sprite(region);
//		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
//		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
//		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
		reset();
	}

	private void reset() {
		// TODO IMPLEMENT ME
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
//		batch.dispose();
//		texture.dispose();
		model.dispose();
		modelBatch.dispose();
		physics.dispose();
	}
	
	/**
	 * Rotates the given model based on the accelerometer data.
	 */
	private void rotateModel(ModelInstance model) {
		Matrix4 rotation = groundRotation.getRotation();
		model.transform.set(rotation); 
	}
	
	private void checkSpherePosition(ModelInstance sphere) {
		Vector3 position = Vector3.Zero;
		sphere.transform.getTranslation(position);
		if (position.y < MIN_BALL_Y_POSITION) {
			//reset ball
			sphere.transform.setTranslation(SPHERE_INITIAL_POSITION);
			sphere.calculateTransforms();
			physics.resetSphere(sphere.transform);
		}
	}
	
	private void applyForce() {
		Vector3 force = forceManager.getForceVector();
		physics.applyForceToSphere(force);
	}

	@Override
	public void renderObjects() {
		
		camController.update();
		//Gdx.app.log("balance-it", cam.position.toString());
		rotateModel(instance);
		
		//update physics
		physics.setGroundTransform(instance.transform);
		applyForce();
		physics.update(Gdx.graphics.getDeltaTime());
		instance2.transform = physics.getSphereTransform();
		checkSpherePosition(instance2);
		
		modelBatch.begin(cam);
		modelBatch.render(instance,environment);
		modelBatch.render(instance2,environment);
		modelBatch.end();
		
//		batch.setProjectionMatrix(camera.combined);
//		batch.begin();
//		sprite.draw(batch);
//		batch.end();
	}

}
