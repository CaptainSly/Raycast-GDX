package com.adventuregame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AdventureScreen implements Screen {

	private SpriteBatch batch;
	private FitViewport viewport;

	public AdventureScreen(SpriteBatch batch, FitViewport viewport) {
		this.batch = batch;
		this.viewport = viewport;
	}
		
	public abstract void renderScreen(float delta);

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		getSpriteBatch().begin();
		renderScreen(delta);
		getSpriteBatch().end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

	public SpriteBatch getSpriteBatch() {
		return batch;
	}
	
	public FitViewport getViewport() {
		return viewport;
	}

}
