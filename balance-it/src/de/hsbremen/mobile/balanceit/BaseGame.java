package de.hsbremen.mobile.balanceit;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.Input.Peripheral;

import de.hsbremen.mobile.balanceit.gameservices.GameService;
import de.hsbremen.mobile.balanceit.gameservices.NetworkManager;
import de.hsbremen.mobile.balanceit.gameservices.NetworkManagerProxy;
import de.hsbremen.mobile.balanceit.gameservices.RoleChanger;
import de.hsbremen.mobile.balanceit.gameservices.RoleChanger.RoleChangerListener;
import de.hsbremen.mobile.balanceit.gameservices.Timer;
import de.hsbremen.mobile.balanceit.logic.ForceManager;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;
import de.hsbremen.mobile.balanceit.view.GameView;
import de.hsbremen.mobile.balanceit.view.GameViewFactory;
import de.hsbremen.mobile.balanceit.view.GetReadyView;
import de.hsbremen.mobile.balanceit.view.GetReadyView.GetReadyViewListener;
import de.hsbremen.mobile.balanceit.view.MenuView;
import de.hsbremen.mobile.balanceit.view.View;

public class BaseGame implements ApplicationListener, GameView.Listener, MenuView.Listener, 
GameService.Listener, RoleChangerListener, GetReadyViewListener {
	
	View menuView;
	GameView gameView;
	
	View currentView;
	View nextView;
	
	private final GameService gameService;
	private NetworkManager networkManager = null;
	private static final boolean INCREASE_DIFFICULTY = true;
	private RoleChanger roleChanger;
	
	private boolean changeView = false;
	
	private Timer timer; 
	
	public BaseGame() {
		this.gameService = null;
	}
	
	public BaseGame(GameService gameService) {
		this.gameService = gameService;
		this.gameService.addListener(this);
	}

	@Override
	public void create() {
		
		
		//check Sensor availability
		if (!Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)) {
			//TODO: Throw error and terminate game
		}
		
		
		Bullet.init();
		PlayerRole role = PlayerRole.SinglePlayer;
		this.timer = new Timer();
		
		
		this.menuView = new MenuView(this);
		this.gameView = new GameViewFactory().createGameView(this, role, INCREASE_DIFFICULTY, networkManager, timer);

		this.roleChanger = new RoleChanger(role, this.gameView, this, this.networkManager);
        
        if(this.gameService != null) {
        	this.menuView.setGameService(this.gameService);
        }
		
		changeView(this.menuView);
	}

	@Override
	public void dispose() {
		if (this.gameService != null) {
			this.gameService.removeListener(this);
		}
	}

	@Override
	public void render() {
		if (this.changeView) {
			
			if (this.currentView != null) {
				this.currentView.dispose();
			}
			
			this.currentView = this.nextView;
			this.currentView.create();
			this.changeView = false;
		}
		this.currentView.render();
		this.roleChanger.update();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void startGame(PlayerRole role) {
		this.gameView = new GameViewFactory().createGameView(this, role, INCREASE_DIFFICULTY, networkManager, timer);
		this.roleChanger.setCurrentRole(role);
		this.roleChanger.setGameView(this.gameView);
		this.roleChanger.setManager(networkManager);
		
		//display ready screen
		GetReadyView readyView = new GetReadyView(role, networkManager, this);
		changeView(readyView);
	}
	
	@Override
	public void switchToMenu() {
		//TODO: implement me	
	}

	private void changeView(View view) {
		this.nextView = view;
		this.changeView = true;
	}

	@Override
	public void startMultiplayerGame(PlayerRole role, NetworkManager manager) {
		Gdx.app.log("BaseGame", "Starting multiplayer game as role " + role);
		setNetworkManager(manager);
		this.startGame(role);
	}
	
	public void setNetworkManager(NetworkManager manager) {
		NetworkManager proxy = new NetworkManagerProxy(manager, timer);
		this.networkManager = proxy;
	}

	@Override
	public void onChangeRole(PlayerRole role) {
		startGame(role);
	}

	@Override
	public void onEndGame() {
		// TODO Implement score screen
		
	}

	@Override
	public void onReady() {
		changeView(this.gameView);
	}
}
