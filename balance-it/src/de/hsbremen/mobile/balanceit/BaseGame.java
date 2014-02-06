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
import de.hsbremen.mobile.balanceit.logic.ForceManager;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;
import de.hsbremen.mobile.balanceit.view.GameView;
import de.hsbremen.mobile.balanceit.view.GameViewFactory;
import de.hsbremen.mobile.balanceit.view.MenuView;
import de.hsbremen.mobile.balanceit.view.View;

public class BaseGame implements ApplicationListener, GameView.Listener, MenuView.Listener, GameService.Listener {
	
	View menuView;
	View gameView;
	
	View currentView;
	
	private final GameService gameService;
	private NetworkManager networkManager = null;
	private static final boolean INCREASE_DIFFICULTY = true;
	
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
		
		
		this.menuView = new MenuView(this);
		this.gameView = new GameViewFactory().createGameView(this, role, INCREASE_DIFFICULTY, networkManager);

        
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
		this.currentView.render();
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
		this.gameView = new GameViewFactory().createGameView(this, role, INCREASE_DIFFICULTY, networkManager);
		changeView(this.gameView);
	}
	
	@Override
	public void switchToMenu() {
		//TODO: implement me	
	}

	private void changeView(View view) {
		if(this.currentView != null )
				this.currentView.dispose();
		this.currentView = view;
		this.currentView.create();
	}

	@Override
	public void startMultiplayerGame(PlayerRole role, NetworkManager manager) {
		Gdx.app.log("BaseGame", "Starting multiplayer game as role " + role);
		this.networkManager = manager;
		this.startGame(role);
	}
}
