package com.adventuregame.screens;

import org.joml.Vector2d;
import org.joml.Vector2i;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class RaycastTest extends AdventureScreen {

	private ShapeRenderer shapeRenderer;

	private int mapWidth = 24;
	private int mapHeight = 24;

	private int[][] worldMap = new int[mapWidth][mapHeight];

	private Vector2d position = new Vector2d(22, 12);
	private Vector2d direction = new Vector2d(-1, 0);
	private Vector2d plane = new Vector2d(0, 0.66d);

	public RaycastTest(SpriteBatch batch, FitViewport viewport) {
		super(batch, viewport);

		shapeRenderer = new ShapeRenderer();

		// Fill the array with weird shapes
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {

				// Borders
				if (x <= mapWidth && y == mapHeight - 1 || y == 0)
					putTile(x, y, 1);
				else if (x == 0 || x == mapWidth - 1 && y <= mapHeight - 1)
					putTile(x, y, 1);

				// Square
				else if ((x >= 6 && x <= 10) && (y == 4 || y == 8))
					putTile(x, y, 2);
				else if ((x == 6 || x == 10) && (y >= 4 && y <= 8))
					putTile(x, y, 2);

				// Scattered Square
				else if ((x == 15 || x == 17 || x == 19) && (y == 4 || y == 6 || y == 8))
					putTile(x, y, 3);
				else if ((x == 6 || x == 10) && (y >= 12 && y <= 16))
					putTile(x, y, 3);

				// Parallel Lines
				else if ((x == 15 || x == 17 || x == 19) && (y >= 12 && y <= 16))
					putTile(x, y, 4);

			}
		}

	}

	@Override
	public void renderScreen(float delta) {

		// Wall Casting
		for (int x = 0; x < getViewport().getWorldWidth(); x++) {

			// Calculate Ray Position and Direction
			double cameraX = 2 * x / (double) getViewport().getWorldWidth() - 1;
			Vector2d rayDirection = new Vector2d(direction.x + plane.x * cameraX, direction.y + plane.y * cameraX);

			Vector2i mapPosition = new Vector2i((int) position.x, (int) position.y); // Which box of the map we're in

			Vector2d sideDistance = new Vector2d(); // Length of ray from current position to next x or y-side

			// Length of ray from one x or y-side to next x or y-side
			Vector2d deltaDistance = new Vector2d((Math.abs(rayDirection.length() / rayDirection.x)),
					(Math.abs(rayDirection.length() / rayDirection.y)));
			double perpWallDist;

			Vector2i step = new Vector2i(); // What direction to step in x or y-direction (either +1 or -1)

			boolean hit = false; // Was there a wall hit?
			int side = 0; // Was a NS or a EW wall hit?

			// Calculate step and initial sideDistance
			if (rayDirection.x < 0) {
				step.x = -1;
				sideDistance.x = (position.x - mapPosition.x) * deltaDistance.x;
			} else {
				step.x = 1;
				sideDistance.x = (mapPosition.x + 1.0d - position.x) * deltaDistance.x;
			}

			if (rayDirection.y < 0) {
				step.y = -1;
				sideDistance.y = (position.y - mapPosition.y) * deltaDistance.y;
			} else {
				step.y = 1;
				sideDistance.y = (mapPosition.y + 1.0d - position.y) * deltaDistance.y;
			}

			// Perform DDA
			while (!hit) {

				// Jump to next map square, either in x-direction, or in y-direction
				if (sideDistance.x < sideDistance.y) {
					sideDistance.x += deltaDistance.x;
					mapPosition.x += step.x;
					side = 0;
				} else {
					sideDistance.y += deltaDistance.y;
					mapPosition.y += step.y;
					side = 1;
				}

				// Check if ray has hit a wall
				if (worldMap[mapPosition.x][mapPosition.y] > 0)
					hit = true;
			}

			// Calculate distance projected on camera direction (Euclidean distance would
			// give fisheye effect!)
			if (side == 0)
				perpWallDist = (mapPosition.x - position.x + (1 - step.x) / 2) / rayDirection.x;
			else
				perpWallDist = (mapPosition.y - position.y + (1 - step.y) / 2) / rayDirection.y;

			// Calculate height of line to draw on screen
			int lineHeight = (int) (getViewport().getWorldHeight() / perpWallDist);

			// Calculate the lowest and highest pixel to fill in current stripe
			int drawStart = (int) (-lineHeight / 2 + getViewport().getWorldHeight() / 2);
			if (drawStart < 0)
				drawStart = 0;

			int drawEnd = (int) (lineHeight / 2 + getViewport().getWorldHeight() / 2);
			if (drawEnd >= getViewport().getWorldHeight())
				drawEnd = (int) (getViewport().getWorldHeight() - 1);

			// Choose wall color
			Color color = null;
			switch (worldMap[mapPosition.x][mapPosition.y]) {
			case 1:
				color = Color.RED;
				break;
			case 2:
				color = Color.GREEN;
				break;
			case 3:
				color = Color.BLUE;
				break;
			case 4:
				color = Color.WHITE;
				break;
			default:
				color = Color.YELLOW;
				break;
			}

			// Give x and y sides different brightness
			if (side == 1) {
				Color tempColor = new Color(color.r / 2, color.g / 2, color.b / 2, color.a);
				color = tempColor;
			}

			// Draw the pixels of the stripe as a vertical line
			drawLine(x, drawStart, x, drawEnd, color);
		}

		// Input
		double moveSpeed = 20 * (delta / 5);
		double rotationSpeed = 20 * (delta / 10);

		if (Gdx.input.isKeyPressed(Keys.W)) { // Move Forwards
			if (worldMap[(int) (position.x + direction.x * moveSpeed)][(int) (position.y)] == 0)
				position.x += direction.x * moveSpeed;
			if (worldMap[(int) (position.x)][(int) (position.y + direction.y * moveSpeed)] == 0)
				position.y += direction.y * moveSpeed;
		}

		if (Gdx.input.isKeyPressed(Keys.S)) { // Move backwards
			if (worldMap[(int) (position.x - direction.x * moveSpeed)][(int) (position.y)] == 0)
				position.x -= direction.x * moveSpeed;
			if (worldMap[(int) (position.x)][(int) (position.y - direction.y * moveSpeed)] == 0)
				position.y -= direction.y * moveSpeed;
		}

		if (Gdx.input.isKeyPressed(Keys.D)) { // Rotate to the right
			// both camera direction and camera plane must be rotated
			double oldDirX = direction.x;
			direction.x = direction.x * Math.cos(-rotationSpeed) - direction.y * Math.sin(-rotationSpeed);
			direction.y = oldDirX * Math.sin(-rotationSpeed) + direction.y * Math.cos(-rotationSpeed);
			double oldPlaneX = plane.x;
			plane.x = plane.x * Math.cos(-rotationSpeed) - plane.y * Math.sin(-rotationSpeed);
			plane.y = oldPlaneX * Math.sin(-rotationSpeed) + plane.y * Math.cos(-rotationSpeed);
		}

		if (Gdx.input.isKeyPressed(Keys.A)) { // Rotate to the left
			// both camera direction and camera plane must be rotated
			double oldDirX = direction.x;
			direction.x = direction.x * Math.cos(rotationSpeed) - direction.y * Math.sin(rotationSpeed);
			direction.y = oldDirX * Math.sin(rotationSpeed) + direction.y * Math.cos(rotationSpeed);
			double oldPlaneX = plane.x;
			plane.x = plane.x * Math.cos(rotationSpeed) - plane.y * Math.sin(rotationSpeed);
			plane.y = oldPlaneX * Math.sin(rotationSpeed) + plane.y * Math.cos(rotationSpeed);
		}

	}

	public void drawLine(float x, float y, float x2, float y2, Color color) {
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(color);
		shapeRenderer.line(x, y, x2, y2);
		shapeRenderer.end();
	}

	public void putTile(int x, int y, int value) {
		worldMap[x][y] = value;
	}

}
