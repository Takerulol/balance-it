package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuView extends View {
	
	public static interface Listener {
		void startGame();
	}
	
	private Listener listener;

	Skin skin;
	Stage stage;
	
	/**
	 * private to enforce listener
	 */
	@SuppressWarnings("unused")
	private MenuView() {}
	
	public MenuView(Listener listener) {
		this.listener = listener;
	}
	
	
	
	@Override
	public void create() {
		//default libgdx menu skin
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),false);
		Gdx.input.setInputProcessor(stage);
		
		//start game button
		TextButton startGameButton = new TextButton("Test Button", skin);
		startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.startGame();				
			}
		});
		
		
		//create container for objects
		Table table = new Table(skin);
		table.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
		
		
		//Add buttons
		table.add(startGameButton);
		
		this.stage.addActor(table);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void renderObjects() {
		this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		this.stage.draw();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
