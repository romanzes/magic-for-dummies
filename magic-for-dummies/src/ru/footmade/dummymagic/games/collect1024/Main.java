package ru.footmade.dummymagic.games.collect1024;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ru.footmade.dummymagic.CommonResources;
import ru.footmade.dummymagic.games.MiniGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Main extends MiniGame {
	private int scrW, scrH;
	private BitmapFont font;
	
	public static final int BOARD_WIDTH = 5;
	public static final int BOARD_HEIGHT = 5;
	public static final float DRAG_LENGTH = 0.1f;
	
	public static final int GOAL = 1024;
	public static final int INITIAL_TILES = 2;
	
	public static final Color COLOR_BACKGROUND = Color.valueOf("FAF8EF");
	public static final Color COLOR_EMPTY_TILE = Color.valueOf("CCC0B3");
	public static final Color COLOR_CELL = Color.valueOf("EEE4DA");
	public static final Color COLOR_FONT = Color.valueOf("776E65");
	
	public static final int MOVE_PERIOD = 100;
	
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	
	private int cellSizePx;
	private int cellVisibleSizePx;
	
	private int dragLength;
	
	private final Random random = new Random();
	
	private Cell[][] field;
	private Coord[][] fieldCoords;
	private int cellCount;
	
	private boolean isMoving;
	private long moveTick;
	private Set<Cell> deleted = new HashSet<Cell>();

	@Override
	public void start(String argument) {
		scrW = Gdx.graphics.getWidth();
		scrH = Gdx.graphics.getHeight();
		
		initInput();
		
		CommonResources resources = CommonResources.getInstance();
		
		batch = new SpriteBatch();
		batch.setProjectionMatrix(resources.getCamera().combined);
		
		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(resources.getCamera().combined);
		
		// Пусть с каждого края остается запас размером хотя бы в одну клетку
		int cellWidthPx = scrW / (BOARD_WIDTH + 2);
		int cellHeightPx = scrH / (BOARD_HEIGHT + 2);
		cellSizePx = Math.min(cellWidthPx, cellHeightPx);
		cellVisibleSizePx = (int) (cellSizePx * 0.9f);
		
		dragLength = (int) (Math.min(scrW, scrH) * DRAG_LENGTH);
		
		font = CommonResources.getInstance().getFont(cellVisibleSizePx / 2);
		font.setColor(COLOR_FONT);
		
		field = new Cell[BOARD_WIDTH][BOARD_HEIGHT];
		fieldCoords = new Coord[BOARD_WIDTH][BOARD_HEIGHT];
		int left = (scrW - BOARD_WIDTH * cellSizePx) / 2;
		int bottom = (scrH - BOARD_HEIGHT * cellSizePx) / 2;
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				int cellX = left + i * cellSizePx + (cellSizePx - cellVisibleSizePx) / 2;
				int cellY = bottom + j * cellSizePx + (cellSizePx - cellVisibleSizePx) / 2;
				fieldCoords[i][j] = new Coord(cellX, cellY);
			}
		}
		
		for (int i = 0; i < INITIAL_TILES; i++) {
			addCell();
		}
	}
	
	private void addCell() {
		if (cellCount < BOARD_WIDTH * BOARD_HEIGHT) {
			boolean set = false;
			while (!set) {
				int x = random.nextInt(BOARD_WIDTH);
				int y = random.nextInt(BOARD_HEIGHT);
				if (field[x][y] == null) {
					Cell cell = new Cell();
					cell.value = 2;
					cell.position = new Coord(x, y);
					cell.origin = new Coord(x, y);
					cell.appearFlag = true;
					field[x][y] = cell;
					cellCount ++;
					set = true;
				}
			}
			isMoving = true;
			moveTick = System.currentTimeMillis();
		} else {
			exit(1);
		}
	}

	@Override
	public void render() {
		processAnimation();
		
		float moveProgress = ((float) (System.currentTimeMillis() - moveTick)) / MOVE_PERIOD;
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.begin(ShapeType.Filled);
		
		renderer.setColor(COLOR_BACKGROUND);
		renderer.rect(0, 0, scrW, scrH);
		
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				renderer.setColor(COLOR_EMPTY_TILE);
				renderer.rect(fieldCoords[i][j].x, fieldCoords[i][j].y, cellVisibleSizePx, cellVisibleSizePx);
			}
		}
		
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				if (field[i][j] != null) {
					renderCell(field[i][j], moveProgress);
				}
			}
		}
		
		for (Cell cell : deleted) {
			renderCell(cell, moveProgress);
		}
		renderer.end();
		
		batch.begin();
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < BOARD_HEIGHT; j++) {
				if (field[i][j] != null) {
					font.drawMultiLine(batch, Integer.toString(field[i][j].value),
							fieldCoords[i][j].x, fieldCoords[i][j].y + (cellVisibleSizePx + font.getCapHeight()) / 2,
							cellVisibleSizePx, HAlignment.CENTER);
				}
			}
		}
		batch.end();
	}
	
	private void renderCell(Cell cell, float moveProgress) {
		int sizePx = cellVisibleSizePx;
		if (cell.appearFlag) {
			sizePx = (int) (cellVisibleSizePx * moveProgress);
		}
		int offsetX = 0;
		int offsetY = 0;
		if (cell.moveFlag) {
			offsetX = (int) ((cell.origin.x - cell.position.x) * cellSizePx * (1 - moveProgress));
			offsetY = (int) ((cell.origin.y - cell.position.y) * cellSizePx * (1 - moveProgress));
		}
		renderer.setColor(COLOR_CELL);
		renderer.rect(fieldCoords[cell.position.x][cell.position.y].x + (cellVisibleSizePx - sizePx) / 2 + offsetX,
				fieldCoords[cell.position.x][cell.position.y].y + (cellVisibleSizePx - sizePx) / 2 + offsetY,
				sizePx, sizePx);
	}
	
	private void processAnimation() {
		if (isMoving) {
			int interval = (int) (System.currentTimeMillis() - moveTick);
			if (interval > MOVE_PERIOD) {
				isMoving = false;
				for (int i = 0; i < BOARD_WIDTH; i++) {
					for (int j = 0; j < BOARD_HEIGHT; j++) {
						if (field[i][j] != null) {
							if (field[i][j].increaseFlag) {
								field[i][j].value *= 2;
								if (field[i][j].value == GOAL)
									exit(0);
							}
							field[i][j].clearFlags();
							field[i][j].origin = new Coord(i, j);
						}
					}
				}
				deleted.clear();
			}
		}
	}
	
	private void initInput() {
		Gdx.input.setInputProcessor(new InputProcessor() {
			private boolean isDragging;
			private int startX, startY;
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				if (!isMoving && isDragging) {
					int dx = screenX - startX;
					int dy = screenY - startY;
					if (Math.max(Math.abs(dx), Math.abs(dy)) > dragLength) {
						if (Math.abs(dx) > Math.abs(dy)) {
							if (dx > 0)
								move(Direction.RIGHT);
							else
								move(Direction.LEFT);
						} else {
							if (dy > 0)
								move(Direction.DOWN);
							else
								move(Direction.UP);
						}
						isDragging = false;
						return true;
					}
				}
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				isDragging = true;
				startX = screenX;
				startY = screenY;
				return true;
			}
			
			@Override
			public boolean scrolled(int amount) {
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				boolean result = true;
				if (!isMoving) {
					switch (keycode) {
					case Input.Keys.UP:
						move(Direction.UP);
						break;
					case Input.Keys.DOWN:
						move(Direction.DOWN);
						break;
					case Input.Keys.LEFT:
						move(Direction.LEFT);
						break;
					case Input.Keys.RIGHT:
						move(Direction.RIGHT);
						break;
					case Input.Keys.ESCAPE:
						exit(1);
						break;
					case Input.Keys.ENTER:
						exit(0);
						break;
					default:
						result = false;
						break;
					}
				}
				return result;
			}
		});
	}
	
	private enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}
	
	private void move(Direction direction) {
		int startX = 0, startY = 0, dx = 0, dy = 0, seekX = 0, seekY = 0;
		switch (direction) {
		case UP:
			startX = 0;
			startY = BOARD_HEIGHT - 1;
			dx = 1;
			dy = -1;
			seekY = 1;
			break;
		case DOWN:
			startX = 0;
			startY = 0;
			dx = 1;
			dy = 1;
			seekY = -1;
			break;
		case LEFT:
			startX = 0;
			startY = 0;
			dx = 1;
			dy = 1;
			seekX = -1;
			break;
		case RIGHT:
			startX = BOARD_WIDTH - 1;
			startY = 0;
			dx = -1;
			dy = 1;
			seekX = 1;
			break;
		}
		Set<Cell> lockedCells = new HashSet<Cell>();
		boolean moved = false;
		for (int i = startX; i >= 0 && i < BOARD_WIDTH; i += dx) {
			for (int j = startY; j >= 0 && j < BOARD_HEIGHT; j += dy) {
				if (field[i][j] != null) {
					for (int k = i + seekX, l = j + seekY; k >= 0 && k < BOARD_WIDTH && l >= 0 && l < BOARD_HEIGHT;
							k += seekX, l += seekY) {
						if (field[k][l] == null) {
							field[k][l] = field[k - seekX][l - seekY];
							field[k][l].position = new Coord(k, l);
							field[k][l].moveFlag = true;
							field[k - seekX][l - seekY] = null;
							moved = true;
						} else {
							if (!(lockedCells.contains(field[k][l]) || lockedCells.contains(field[k - seekX][l - seekY])) 
									&& field[k][l].value == field[k - seekX][l - seekY].value) {
								field[k][l].increaseFlag = true;
								lockedCells.add(field[k][l]);
								field[k - seekX][l - seekY].position = new Coord(k, l);
								field[k - seekX][l - seekY].moveFlag = true;
								deleted.add(field[k - seekX][l - seekY]);
								field[k - seekX][l - seekY] = null;
								cellCount--;
								moved = true;
							}
						}
					}
				}
			}
		}
		if (moved) {
			addCell();
		}
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
		font.dispose();
	}
	
	private static class Cell {
		public int value;
		public Coord position, origin;
		public boolean moveFlag, increaseFlag, appearFlag;
		
		public void clearFlags() {
			moveFlag = false;
			increaseFlag = false;
			appearFlag = false;
		}
	}
	
	private static class Coord {
		public int x, y;
		
		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
