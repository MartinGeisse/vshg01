/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import java.util.Random;

/**
 * Each instance represents the state of a game of Tetris.
 */
public class GameState {

	/**
	 * the gameArea
	 */
	private GameArea gameArea;
	
	/**
	 * the preview
	 */
	private Preview preview;
	
	/**
	 * Number of completed rows (this also indicates the game level by rows/10).
	 */
	private int rows;

	/**
	 * The player's score
	 */
	private int score;

	/**
	 * Constructor.
	 */
	public GameState() {
		gameArea = new GameArea();
		preview = new Preview(new Random());
		rows = 0;
		score = 0;
		nextPiece();
	}

	/**
	 * Getter method for the gameArea.
	 * @return the gameArea
	 */
	public GameArea getGameArea() {
		return gameArea;
	}

	/**
	 * Getter method for the preview.
	 * @return the preview
	 */
	public Preview getPreview() {
		return preview;
	}

	/**
	 * Getter method for the rows.
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Getter method for the score.
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Setter method for the score.
	 * @param score the score to set
	 */
	public void setScore(final int score) {
		this.score = score;
	}

	/**
	 * Computes the score to award for the specified number of erased rows in the specified level.
	 * @param level the level
	 * @param num the number of erased rows
	 * @return the score to award
	 */
	private static int getRowScore(final int level, final int num) {
		switch (num) {
		case 1:
			return 40 * (level + 1);
		case 2:
			return 100 * (level + 1);
		case 3:
			return 300 * (level + 1);
		case 4:
			return 1200 * (level + 1);
		default:
			return 0;
		}
	}

	/**
	 * Adds the specified number of completed rows as well as the corresponding score to the game
	 * state. This method also checks if a new level is reached with these rows.
	 * @param num the number of rows
	 * @return true iff a new level was reached.
	 */
	public boolean addRows(final int num) {
		final int oldLevel = rows / 10;
		score += getRowScore(oldLevel, num);
		rows += num;
		return (rows / 10) != oldLevel;
	}

	/**
	 * Combined logic to move to the next piece: Generates a random piece, shifts
	 * it into the preview boxes, and causes the shifted-out piece to enter the
	 * game area.
	 */
	public void nextPiece() {
		gameArea.enterShape(preview.getPiece0().getInitialShape(), preview.getBlockIndex0());
		preview.shiftRandomPiece();
	}

}
