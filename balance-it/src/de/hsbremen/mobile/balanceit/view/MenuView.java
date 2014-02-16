package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;

import de.hsbremen.mobile.balanceit.gameservices.GameService;
import de.hsbremen.mobile.balanceit.gameservices.GameServiceClient;
import de.hsbremen.mobile.balanceit.logic.PlayerRole;

public class MenuView extends View implements GameServiceClient {
	
	private GameService gameService;
	
	public static interface Listener {
		void startGame(PlayerRole role);
		void viewHelp();
	}
	
	private Listener listener;

	private Stage stage;

	private Table menuTable;
	
	private Table gameServiceMenu = null;
	private Table loggedInMenu;
	private Table loggedOutMenu;

	private Sprite logoSprite;
	private SpriteBatch batch;
	
	public MenuView(Listener listener, Skin skin) {
		this.listener = listener;
		setSkin(skin);
	}
	
	@Override
	public void create() {
		//default libgdx menu skin
//		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),false);
		stage = new Stage();
		stage.setViewport(800, 480, false, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(stage);
		
		//start game button
		TextButton startGameButton = new TextButton("Start Game", getSkin());
		startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.startGame(PlayerRole.SinglePlayer);				
			}
		});
		
		//help button
		TextButton helpButton = new TextButton("Help", getSkin());
		helpButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.viewHelp();				
			}
		});
		
		this.menuTable = new Table(getSkin());
		this.menuTable.setTransform(true);
		this.menuTable.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f - stage.getHeight() / 10f);
		this.menuTable.scale(.3f);
		
		//Add buttons
		this.menuTable.add(startGameButton);
		this.menuTable.row();
		this.menuTable.add(helpButton);
		this.menuTable.row();
		
		if(this.gameService != null) {
			initializeGameServiceMenu();
		}
		
		this.stage.addActor(this.menuTable);
		
		Texture logo = new Texture(Gdx.files.internal("images/textures/logo.png"));
		logo.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		logoSprite = new Sprite(logo);
		logoSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        this.menuTable.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f - stage.getHeight() / 10f);
	}

	@Override
	public void pause() {
	}
	

	@Override
	public void renderObjects() {
		batch.begin();
		logoSprite.draw(batch);
		batch.end();
		
		this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		checkMenuStatus();
		this.stage.draw();
	}

	private void checkMenuStatus() {
		if(this.gameService != null) {
			if(this.gameService.isLoggedIn()) {
				switchLoggedInMenuState(true);
			} else {
				switchLoggedInMenuState(false);
			}
		}
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
		this.gameServiceMenu = new Table(getSkin());
		this.menuTable.add(this.gameServiceMenu); //add to main menu
		
		//logged in
		this.loggedInMenu = new Table(getSkin());
		
		TextButton logoutButton = new TextButton("Logout", getSkin());
		logoutButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.logout();				
			}
		});
		
		TextButton achievementsButton = new TextButton("Achievements", getSkin());
		achievementsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.showAchievements();				
			}
		});
		
		TextButton invitePlayersButton = new TextButton("Invite Players", getSkin());
		invitePlayersButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.invitePlayers();				
			}
		});
		
		TextButton showInvitationsButton = new TextButton("Show Invitations", getSkin());
		showInvitationsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.showInvitations();				
			}
		});
		
		TextButton quickGameButton = new TextButton("Quick Game", getSkin());
		quickGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameService.quickGame();				
			}
		});
		

		this.loggedInMenu.add(invitePlayersButton);
		this.loggedInMenu.row();
		this.loggedInMenu.add(showInvitationsButton);
		this.loggedInMenu.row();
		this.loggedInMenu.add(quickGameButton);
		this.loggedInMenu.row();
		this.loggedInMenu.add(achievementsButton);
		this.loggedInMenu.row();
		this.loggedInMenu.add(logoutButton);
		
		
		//not logged in
		this.loggedOutMenu = new Table(getSkin());
		
		TextButton loginButton = new TextButton("Login", getSkin());
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

	public void showDisconnectedDialog() {
		
		Gdx.app.postRunnable(new Runnable() {
	         @Override
	         public void run() {
	        	 new Dialog("Game canceled.", getSkin()) {
	     			{
	     				text("The oppenent disconnected.");
	     				button("OK");
	     			}
	     		}.show(stage);
	         }
		});
	}
	
}
