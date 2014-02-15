package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;

import de.hsbremen.mobile.balanceit.gameservices.GameService;

public class HelpView extends View {

	public static interface Listener {
		void switchBack();
	}

	private Listener listener;
	
	private Stage stage;
	private BitmapFont font;
	private SpriteBatch batch;

	private Sprite backgroundSprite;
	
	public HelpView(Listener listener, Skin skin) {
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
				listener.switchBack();				
			}
		});
		this.stage.addActor(backButton);
		
		Texture background = new Texture(Gdx.files.internal("images/textures/background_help.png"));
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		backgroundSprite = new Sprite(background);
		backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) {
//		Vector2 size = Scaling.fit.apply(800, 480, width, height);
//        int viewportX = (int)(width - size.x) / 2;
//        int viewportY = (int)(height - size.y) / 2;
//        int viewportWidth = (int)size.x;
//        int viewportHeight = (int)size.y;
//        Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
//        stage.setViewport(800, 480, true, viewportX, viewportY, viewportWidth, viewportHeight);
//        this.menuTable.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f);
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

	@Override
	public void renderObjects() {
		batch.begin();
		backgroundSprite.draw(batch);
		float ratio = (float)Gdx.graphics.getWidth() / 800f;
		font.setScale(ratio * 2f);
		renderCenteredText("How to Play", -0.3f);
		font.setScale(ratio);
		renderCenteredText("1. Hold the phone flat", 0f);
		renderCenteredText("2. Start the Game", 0.05f);
		renderCenteredText("3. Balance the Ball on the Plate!", 0.1f);
		font.setScale(1f);
		batch.end();
		
		this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		this.stage.draw();
	}
	
	private void renderCenteredText(String text) {
		renderCenteredText(text, 0f);
	}
	
	private void renderCenteredText(String text, float yDisplace) {
		TextBounds bounds = font.getBounds(text);
		font.draw(batch, text, 0.5f * Gdx.graphics.getWidth() - bounds.width / 2f, 0.5f * Gdx.graphics.getHeight() + bounds.height / 2f - yDisplace * Gdx.graphics.getHeight());
	}

}
