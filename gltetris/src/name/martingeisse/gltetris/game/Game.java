/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import name.martingeisse.gltetris.main.Main;
import name.martingeisse.meltdown.engine.Engine;
import name.martingeisse.meltdown.engine.SoundChain;

/**
 * TODO: document me
 *
 */
public class Game implements IFrameHandler {

	/**
	 * the engine
	 */
	private Engine engine;
	
	/**
	 * the gameState
	 */
	private GameState gameState;

	/**
	 * fast-drop accumulation (for additional score)
	 */
	private int fastDrop;

	/**
	 * the bipSound
	 */
	private static SoundChain bipSound = new SoundChain(1500, 1);
	
	/**
	 * the landSound
	 */
	private static SoundChain landSound = new SoundChain(500, 2, 300, 2);

	/**
	 * the rotateSound
	 */
	private static SoundChain rotateSound = new SoundChain(1300, 1, 800, 2);

	/**
	 * the completeRowSound
	 */
	private static SoundChain completeRowSound = new SoundChain(500, 2, 800, 2, 1000, 2, 1600, 2, 1000, 2, 800, 2, 500, 2, 800, 2, 1000, 2, 1600, 2);

	/**
	 * the gameOverFillSound
	 */
	private static SoundChain gameOverFillSound = new SoundChain(800, 2, 500, 2);

	/**
	 * the nextLevelSound
	 */
	private static SoundChain nextLevelSound = new SoundChain(500, 3, 1000, 3, 2000, 3, 500, 3, 1000, 3, 2000, 3, 500, 3, 1000, 3, 2000, 3);

	/**
	 * the delayByLevel
	 */
	private static final int delayByLevel[] = {
		30, 27, 24, 21, 18, 15, 12, 8, 7, 6, 5, 4, 3, 2, 1
	};

	/**
	 * the delayLevels
	 */
	private static final int delayLevels = 15;
	
	/**
	 * Constructor.
	 * @param engine the engine
	 */
	public Game(Engine engine) {
		this.engine = engine;
	}
	
	/**
	 * 
	 */
	public void engineNewGame() {
		this.gameState = new GameState();
		Controls.resetCooldowns();
		this.fastDrop = 0;

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.gltetris.game.IFrameHandler#handleFrame(int)
	 */
	@Override
	public void handleFrame(int frameCounter) {
		gameStep(frameCounter);
		draw();
	}

	/**
	 * @param stepCounter
	 */
	private void gameStep(final int stepCounter) {
		if (Controls.keyLeft()) {
			if (gameState.getGameArea().moveCurrentShapeLeft()) {
				engine.startSoundChain(bipSound);
			}
		}
		if (Controls.keyRight()) {
			if (gameState.getGameArea().moveCurrentShapeRight()) {
				engine.startSoundChain(bipSound);
			}
		}
		if (Controls.keyDown()) {
			engine.stopSound();
			this.fastDrop++;
			moveCurrentShapeDownwards();
		}
		if (Controls.keyClockwise()) {
			if (gameState.getGameArea().rotateCurrentShapeClockwise()) {
				engine.startSoundChain(rotateSound);
			}
		}
		if (Controls.keyCounterClockwise()) {
			if (gameState.getGameArea().rotateCurrentShapeCounterClockwise()) {
				engine.startSoundChain(rotateSound);
			}
		}
		if (!Controls.keyDownHeld()) {
			final int level = this.gameState.getRows() / 10;
			this.fastDrop = 0;
			if ((level > delayLevels) || (stepCounter % delayByLevel[level] == 0)) {
				moveCurrentShapeDownwards();
			}
		}
	}

	/**
	 * 
	 */
	private void moveCurrentShapeDownwards() {
		final boolean success = gameState.getGameArea().moveCurrentShapeDown();
		gameState.getGameArea().drawCurrentShape();
		if (success) {
			return;
		}

		if (gameState.getGameArea().pasteCurrentShape()) {
			Main.currentFrameHandler = Main.titleScreen;
			gameOverFill();
			return;
		}

		this.gameState.setScore(this.gameState.getScore() + this.fastDrop);
		this.fastDrop = 0;

		final int[] completedRows = gameState.getGameArea().findCompletedRows();
		if (completedRows.length > 0) {
			engine.startSoundChain(completeRowSound);
			flashRows(completedRows);
			gameState.getGameArea().removeRows(completedRows);
			if (gameState.addRows(completedRows.length)) {
				newLevel();
			} else {
				gameState.getGameArea().drawMatrix();
			}
		} else {
			engine.startSoundChain(landSound);
		}

		gameState.nextPiece();
	}
	
	/**
	 * 
	 */
	public void draw() {
		Background.draw();
		gameState.getPreview().draw();
		gameState.getGameArea().draw();
		DrawUtil.drawNumber(26, 21, this.gameState.getRows() / 10, 2, 43);
		DrawUtil.drawNumber(23, 18, this.gameState.getScore(), 8, 0);
	}
	
	//
	private void flashRows(final int[] rows) {
		flashRows(rows, 7);
		flashRows(rows, 8);
		flashRows(rows, 0);
		flashRows(rows, 8);
		flashRows(rows, 7);
		flashRows(rows, 8);
		flashRows(rows, 0);
	}
	
	//
	private void flashRows(final int[] rows, int blockIndex) {
		for (int i = 0; i < 5; i++) {
			Main.nextFrame();
			draw();
			GameArea.fillGameRows(rows, blockIndex);
		}
	}

	private void newLevel() {
		engine.startSoundChain(nextLevelSound);
		recolorTiles(4);
		recolorTiles(6);
		recolorTiles(7);
		recolorTiles(6);
		recolorTiles(4);
		recolorTiles(8);
	}

	private void recolorTiles(final int blockIndex) {
		for (int i = 0; i < 5; i++) {
			Main.nextFrame();
			gameState.getGameArea().recolor(blockIndex);
			draw();
		}
	}

	void gameOverFill() {
		for (int i = 19; i >= 0; i--) {
			for (int j = 0; j < 5; j++) {
				Main.nextFrame();
				draw();
				for (int k=i; k<20; k++) {
					GameArea.drawGameRowFilled(k, 7);
				}
			}
			engine.startSoundChain(gameOverFillSound);
		}
	}

}
