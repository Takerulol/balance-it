package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsbremen.mobile.balanceit.gameservices.GameService;
import de.hsbremen.mobile.balanceit.gameservices.GameServiceClient;

public class MenuView extends View implements GameServiceClient {
	
	private GameService gameService;
	
	public static interface Listener {
		void startGame();
	}
	
	private Listener listener;

	private Skin skin;
	private Stage stage;

	private Table menuTable;
	
	private boolean tempLoggedInState = false;
	private Table gameServiceMenu = null;
	private Table loggedInMenu;
	private Table loggedOutMenu;
	
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
		TextButton startGameButton = new TextButton("Start Game", skin);
		startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.startGame();				
			}
		});
		
		
		this.menuTable = new Table(skin);
		this.menuTable.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
		 
		if(this.gameService != null) {
			initializeGameServiceMenu();
		}

		//Add buttons
		this.menuTable.add(startGameButton);
		
		this.stage.addActor(this.menuTable);
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
		checkMenuStatus();
		this.stage.draw();
	}

	private void checkMenuStatus() {
		if(this.gameService != null) {
			if(this.gameService.isLoggedIn()) {
//				if(this.tempLoggedInState == false) {
					switchLoggedInMenuState(true);
//				}
			} else {
//				if(this.tempLoggedInState == true) {
					switchLoggedInMenuState(false);
//				}
			}
		}
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
		this.gameService = gameService;
		
		//if main menu already exists
		if (this.menuTable != null) 
			initializeGameServiceMenu();
	}

	private void initializeGameServiceMenu() {
		buildGameServiceMenus();
		
		if(this.gameService.isLoggedIn()) {
			this.gameServiceMenu.add(this.loggedInMenu);
		} else {
			this.gameServiceMenu.add(this.loggedOutMenu);
		}
	}
	
	private void buildGameServiceMenus() {
		this.gameServiceMenu = new Table(skin);
		this.menuTable.add(this.gameServiceMenu); //add to main menu
		
		//logged in
		this.loggedInMenu = new Table(skin);
		
		TextButton logoutButton = new TextButton("Logout", skin);
		logoutButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.logout();				
			}
		});
		
		TextButton achievementsButton = new TextButton("Achievements", skin);
		achievementsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.showAchievements();				
			}
		});
		
		this.loggedInMenu.add(logoutButton);
		this.loggedInMenu.add(achievementsButton);
		
		
		//not logged in
		this.loggedOutMenu = new Table(skin);
		
		TextButton loginButton = new TextButton("Login", skin);
		loginButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.login();				
			}
		});
		
		this.loggedOutMenu.add(loginButton);
	}

	private void switchLoggedInMenuState(boolean loggedIn) {
		if (loggedIn) {
			this.gameServiceMenu.removeActor(this.loggedOutMenu);
			this.gameServiceMenu.add(this.loggedInMenu);
		} else {
			this.gameServiceMenu.removeActor(this.loggedInMenu);
			this.gameServiceMenu.add(this.loggedOutMenu);
		}
		
	}
	
}
