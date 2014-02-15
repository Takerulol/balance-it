package de.hsbremen.mobile.balanceit.view;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.hsbremen.mobile.balanceit.gameservices.GameServiceClient;

public abstract class View implements ApplicationListener, GameServiceClient {
	
	private Skin skin;
	
	@Override
	public final void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		renderObjects();
	}
	
	/**
	 * Autocleared rendering
	 */
	public abstract void renderObjects();

	public Skin getSkin() {
		return skin;
	}

	public void setSkin(Skin skin) {
		//smoothes out all textures from skin atlas
		for(Texture t : skin.getAtlas().getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		this.skin = skin;
	}
}
