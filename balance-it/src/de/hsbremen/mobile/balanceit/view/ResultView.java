package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsbremen.mobile.balanceit.gameservices.GameService;

public class ResultView extends View {
	
	public static interface Listener {
		void backFromResult();
	}
	
	private Listener listener;
	
	private Stage stage;
	private BitmapFont font;
	private SpriteBatch batch;

	private Sprite backgroundSprite;

	private float myTime;
	private float enemyTime;
	private boolean wasMultiplayer;


	public ResultView(Listener listener, Skin skin) {
		this.listener = listener;
		setSkin(skin);
	}
	
	@Override
	public void create() {
		stage = new Stage();
		stage.setViewport(800, 480, false, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);
		
		batch = new SpriteBatch();
		font = getSkin().getFont("default-font");
		
		//back button
		TextButton backButton = new TextButton("Back", getSkin());
		backButton.setPosition(stage.getWidth() / 2f - backButton.getWidth() / 2f, stage.getHeight() / 4f);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.backFromResult();				
			}
		});
		this.stage.addActor(backButton);
		
		Texture background = new Texture(Gdx.files.internal("images/textures/background_result.png"));
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		backgroundSprite = new Sprite(background);
		backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
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
	public void dispose() {
		stage.dispose();
		batch.dispose();
	}

	@Override
	public void setGameService(GameService gameService) {
	}

	@Override
	public void renderObjects() {
		batch.begin();
		backgroundSprite.draw(batch);
		drawEndingText();
		batch.end();
		
		this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		this.stage.draw();
	}

	private void drawEndingText() {
		float ratio = (float)Gdx.graphics.getWidth() / 800f;
		float rMyTime = Math.round(myTime * 100f) / 100f;
		float rEnemyTime = Math.round(enemyTime * 100f) / 100f;
		
		if(wasMultiplayer) { //multiplayer
			font.setScale(ratio*3f);
			if(myTime > enemyTime) { //win
				renderCenteredText("You won!", -0.2f);
			} else { //lose
				renderCenteredText("You lost!", -0.2f);
			}
			font.setScale(ratio);
			renderCenteredText("You achieved "+rMyTime+" seconds.", 0.0f);
			renderCenteredText("Your enemy achieved "+rEnemyTime+" seconds.", 0.1f);
		} else { //singleplayer
			font.setScale(ratio*1.5f);
			renderCenteredText("You achieved "+ rMyTime +" seconds of balancing! Great!");
		}
		font.setScale(1f);
	}

	public void showSingleplayerEnd(float myTime) {
		this.myTime = myTime;
		this.wasMultiplayer = false;
	}

	public void showMultiplayerEnd(float myTime, float enemyTime) {
		this.myTime = myTime;
		this.enemyTime = enemyTime;
		this.wasMultiplayer = true;
	}
	
	private void renderCenteredText(String text) {
		renderCenteredText(text, 0f);
	}
	
	private void renderCenteredText(String text, float yDisplace) {
		TextBounds bounds = font.getBounds(text);
		font.draw(batch, text, 0.5f * Gdx.graphics.getWidth() - bounds.width / 2f, 0.5f * Gdx.graphics.getHeight() + bounds.height / 2f - yDisplace * Gdx.graphics.getHeight());
	}

}
