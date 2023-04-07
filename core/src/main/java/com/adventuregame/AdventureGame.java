package com.adventuregame;

import java.util.Random;

import com.adventuregame.screens.RaycastTest;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class AdventureGame extends Game {
	private SpriteBatch batch;

	public static final Random rnJESUS = new Random(System.nanoTime());

	private RaycastTest testScreen;

	@Override
	public void create() {
		batch = new SpriteBatch();	
		
		FitViewport viewport = new FitViewport(1280, 720);
		
		testScreen = new RaycastTest(batch, viewport);
		setScreen(testScreen);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
