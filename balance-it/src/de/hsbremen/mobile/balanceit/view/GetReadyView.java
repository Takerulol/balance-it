package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Scaling;

import de.hsbremen.mobile.balanceit.gameservices.DataPackage;
import de.hsbremen.mobile.balanceit.gameservices.GameService;
import de.hsbremen.mobile.balanceit.gameservices.Header;
import de.hsbremen.mobile.balanceit.gameservices.NetworkManager;
import de.hsbremen.mobile.balanceit.gameservices.SendPhysicsProxy;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;

public class GetReadyView extends View implements NetworkManager.Listener {

	private NetworkManager networkManager;
	private PlayerRole role;
	private GetReadyViewListener listener;
	
	private Stage stage;
	private Table outputTable;
	private SpriteBatch batch;
	private BitmapFont font;
	private Sprite backgroundSprite;
	
	/**
	 * Time after which the player is ready. (Only for balancer and single player)
	 * The ForceApplier will be ready, when the READY package is received.
	 */
	private static final float READY_TIME = 3.0f;
	
	public interface GetReadyViewListener {
		/**
		 * Event occurs, when the player is ready.
		 */
		void onReady();
	}
	
	public GetReadyView(PlayerRole role, NetworkManager networkManager,
			GetReadyViewListener listener, Skin skin) {
		this.networkManager = networkManager;
		this.role = role;
		
		if (networkManager != null)
			networkManager.registerListener(this);
		
		this.listener = listener;
		
		setSkin(skin);
	}
	
	@Override
	public void create() {
		stage = new Stage();
		stage.setViewport(800, 480, false, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		this.outputTable = new Table(getSkin());
		this.outputTable.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f);
		
		batch = new SpriteBatch();
		font = getSkin().getFont("default-font");
		
//		Label label = new Label("Sie sind dran!", getSkin());
//		this.outputTable.add(label);
		
//		TextField text = new TextField("Jing gong in da hood ya!", getSkin());
//		text.setWidth(600);
//		text.setHeight(100);
//		this.outputTable.add(text);
		
//		Dialog d = new Dialog("Das Match wird gestartet", getSkin(),"dialog").text("Balancieren sie die Kugel!");
//		this.outputTable.add(d);
		
		String playerString;
		switch (role) {
		case SinglePlayer:
			playerString = "Balance the Ball!";
			break;
		case Balancer:
			playerString = "It's your turn to balance!";
			break;
		case ForceApplier:
			playerString = "Disturb the enemy ball!";
			break;
		default:
			playerString = "Balance the Ball!";
			break;
		}
		Window window = new Window(playerString, getSkin());
		window.add("Get ready! The game is starting soon!");
		this.outputTable.add(window);
		
		stage.addActor(outputTable);
		
		Texture background = new Texture(Gdx.files.internal("images/textures/background_start.png"));
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		backgroundSprite = new Sprite(background);
		backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) {
		Vector2 size = Scaling.fit.apply(800, 480, width, height);
        int viewportX = (int)(width - size.x) / 2;
        int viewportY = (int)(height - size.y) / 2;
        int viewportWidth = (int)size.x;
        int viewportHeight = (int)size.y;
        Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        stage.setViewport(800, 480, true, viewportX, viewportY, viewportWidth, viewportHeight);
        this.outputTable.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameService(GameService gameService) {
		// TODO Auto-generated method stub

	}
	
	private float readyTimer = 0.0f;
	private boolean started = false;

	private float waitingPackageTimer = 0.0f;
	
	private void sendWaitingPackage() {
		if (!role.equals(PlayerRole.SinglePlayer)) {
			waitingPackageTimer += Gdx.graphics.getDeltaTime();
			if (waitingPackageTimer >= SendPhysicsProxy.UPDATE_INTERVAL) {
				this.networkManager.sendPackage(Header.WAITING, new byte[] { 0 });
				waitingPackageTimer = 0.0f;
			}
		}
	}
	
	
	@Override
	public void renderObjects() {
		
		sendWaitingPackage();
		
		if (started)
			updateTimer();
		else
			started = true;
		
		batch.begin();
		backgroundSprite.draw(batch);
		batch.end();
		
		this.stage.draw();
		
//		batch.begin();
//		font.setScale((float)Gdx.graphics.getWidth() / 800f * 2f);
//		font.draw(batch, "Wurst", Gdx.graphics.getWidth() / 800 * 100, Gdx.graphics.getHeight() / 480 * 100);
//		font.setScale(2f);
//		font.draw(batch, "Wurst", Gdx.graphics.getWidth() / 800 * 150, Gdx.graphics.getHeight() / 480 * 150);
//		font.setScale(1f);
//		batch.end();
		
	}
	
	private void updateTimer() {
		if (!role.equals(PlayerRole.ForceApplier))
		{
			readyTimer += Gdx.graphics.getDeltaTime();
			
			if (readyTimer >= READY_TIME) {
				
				if (role.equals(PlayerRole.Balancer)) {
					//send ready package
					this.networkManager.sendPackage(Header.READY, new byte[] {0});
				}
				
				listener.onReady();
			}
		}
	}

	@Override
	public void onPackageReceived(DataPackage data) {
		if (data.getHeader().equals(Header.READY) && role.equals(PlayerRole.ForceApplier)) {
			listener.onReady();
		}
	}

}
