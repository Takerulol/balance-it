package de.hsbremen.mobile.balanceit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "balance-it";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;
		cfg.samples = 4;
		
		new LwjglApplication(new BaseGame(), cfg);
	}
}
