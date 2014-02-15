package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.hsbremen.mobile.balanceit.gameservices.GameService;
import de.hsbremen.mobile.balanceit.gameservices.Timer;
import de.hsbremen.mobile.balanceit.logic.ForceManager;
import de.hsbremen.mobile.balanceit.logic.GestureForceManager;
import de.hsbremen.mobile.balanceit.logic.BulletPhysics;
import de.hsbremen.mobile.balanceit.logic.GroundRotation;
import de.hsbremen.mobile.balanceit.logic.Physics;
import de.hsbremen.mobile.balanceit.view.shader.SkyboxShader;

public class GameView extends View {
	
	public static interface Listener {
		void switchToMenu();
	}
	
	private Listener listener;

	public static final float SPHERE_HEIGHT = 1.5f;
	public static final float GROUND_HEIGHT = 1.0f;
	public static final float GROUND_WIDTH = 20f;
	public static final Vector3 SPHERE_INITIAL_POSITION = new Vector3(0,10,0);
	public static final float MIN_BALL_Y_POSITION = -GROUND_WIDTH * 1.5f; //balls will get reseted when falling below this value
	
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
	
	private Skybox skybox;
	private ShaderProgram skyboxShader;
	
	private DirectionalShadowLight shadowLight;
	private ModelBatch shadowBatch;
	
	private SpriteBatch interfaceBatch;
	private BitmapFont font;
	
	Physics physics;

	private GroundRotation groundRotation;
	
	private Timer timer;

	
	public GameView(Listener listener, ForceManager forceManager, InputProcessor input,
			Physics physics, GroundRotation rotation, Timer timer, Skin skin) {
		this.listener = listener;
		this.forceManager = forceManager;
		this.inputProcessor = input;
		this.physics = physics;
		this.groundRotation = rotation;
		this.timer = timer;
		setSkin(skin);
	}
	
	
	@Override
	public void create() {
		modelBatch = new ModelBatch();
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.4f,0.4f,0.4f,1f));
//		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		shadowLight = new DirectionalShadowLight(1024, 1024, 30f, 30f, 1f, 100f);
		shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -.8f, -.2f);
		environment.add(shadowLight);
		environment.shadowMap = shadowLight;
		
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		
		
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
		
		Texture texture1 = new Texture(Gdx.files.internal("images/textures/wood.png"),true);
		texture1.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createCylinder(20f, GROUND_HEIGHT, GROUND_WIDTH, 32, 
				new Material(), 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		model.manageDisposable(texture1);
		instance = new ModelInstance(model);
		instance.materials.first().set(new TextureAttribute(TextureAttribute.Diffuse, texture1));
		
		physics.initGround(instance.transform);
		
		Texture texture2 = new Texture(Gdx.files.internal("images/textures/metal.png"),true);
		texture2.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		ball = modelBuilder.createSphere(SPHERE_HEIGHT, SPHERE_HEIGHT, SPHERE_HEIGHT, 32, 32, 
				new Material(),
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		ball.manageDisposable(texture2);
		
		instance2 = new ModelInstance(ball, SPHERE_INITIAL_POSITION);
		instance2.materials.first().set(new TextureAttribute(TextureAttribute.Diffuse, texture2));
		
		physics.initSphere(instance2.transform);
		
		
		skyboxShader = new ShaderProgram(SkyboxShader.vertexShader, SkyboxShader.fragmentShader);
		if(!skyboxShader.isCompiled()) {
			throw new RuntimeException("skybox shader couldn't compile: " + skyboxShader.getLog());
		}
		skybox = new Skybox("sky");
		
		font = getSkin().getFont("default-font");
		interfaceBatch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) {
		//stage.setViewport(800, 480, false);
	}

	@Override
	public void pause() {
		reset();
	}

	private void reset() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
//		batch.dispose();
//		texture.dispose();
		skybox.dispose();
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
	
	
	
	private void applyForce() {
		Vector3 force = forceManager.getForceVector();
		physics.applyForceToSphere(force);
	}

	@Override
	public void renderObjects() {
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		timer.update(Gdx.graphics.getDeltaTime());
		camController.update();
		
		//Gdx.app.log("balance-it", cam.position.toString());
		rotateModel(instance);
		
		//update physics
		physics.setGroundTransform(instance.transform);
		applyForce();
		
		logForce(Gdx.graphics.getDeltaTime());
		
		physics.update(Gdx.graphics.getDeltaTime());
		
		Matrix4 physicsSphereTransform = physics.getSphereTransform();
		
		if (physicsSphereTransform != null) //shows the sphere at the default position at the beginning
			instance2.transform = physicsSphereTransform;
		
		cam.update();
		
		//Rendering
		skyboxShader.begin();
		skybox.render(skyboxShader, cam);
		skyboxShader.end();
		
		shadowLight.begin(Vector3.Zero,cam.direction);
		shadowBatch.begin(shadowLight.getCamera());
		shadowBatch.render(instance2);
		shadowBatch.render(instance);
		shadowBatch.end();
		shadowLight.end();
		
		modelBatch.begin(cam);
		modelBatch.render(instance,environment);
		modelBatch.render(instance2,environment);
		modelBatch.end();
		
		interfaceBatch.begin();
		float ratio = (float)Gdx.graphics.getWidth() / 800f;
		font.setScale(ratio * 1f);
		//TODO: enter time
		renderCenteredText("Time :     "+ Math.round(timer.getRenderTime() * 100f) / 100f , -0.45f, font.getBounds("Time :     0.00"));
		font.setScale(1f);
		interfaceBatch.end();
	}

	@Override
	public void setGameService(GameService gameService) {
		//TODO: implement me
	}
	
	//TODO: Remove
	private float logTimer = 0.0f;
	
	/**
	 * TODO: Remove
	 * @param deltaTime
	 */
	private void logForce(float deltaTime) {
		logTimer += deltaTime;
		
		if (logTimer > 3) {
			Gdx.app.log("GameView", "Current force: " + forceManager.getForceVector().toString());
			logTimer = 0;
		}
	}
	
	public ModelInstance getSphere() {
		return this.instance2;
	}
	
	private void renderCenteredText(String text) {
		renderCenteredText(text, 0f);
	}
	
	private void renderCenteredText(String text, float yDisplace) {
		TextBounds bounds = font.getBounds(text);
		font.draw(interfaceBatch, text, 0.5f * Gdx.graphics.getWidth() - bounds.width / 2f, 0.5f * Gdx.graphics.getHeight() + bounds.height / 2f - yDisplace * Gdx.graphics.getHeight());
	}
	
	private void renderCenteredText(String text, float yDisplace, TextBounds bounds) {
		font.draw(interfaceBatch, text, 0.5f * Gdx.graphics.getWidth() - bounds.width / 2f, 0.5f * Gdx.graphics.getHeight() + bounds.height / 2f - yDisplace * Gdx.graphics.getHeight());
	}

}
