/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import java.util.Arrays;

/**
 * This class handles the "frozen" blocks on the ground as well as the
 * current shape being moved by the player.
 */
public class GameArea {

	/**
	 * x offet of the game area in screen coordinates
	 */
	private static final int GAME_AREA_X_ON_SCREEN = 6;

	/**
	 * y offet of the game area in screen coordinates
	 */
	private static final int GAME_AREA_Y_ON_SCREEN = 5;

	/**
	 * State of the game area matrix. This array is filled with block character values, with 0 being 'free'.
	 * The size is expected to be 10*20 and contains the game are row-wise.
	 */
	private final int[] matrix;

	/**
	 * Position of the currently moving shape. These values may be negative and/or exceed 9 since they only
	 * indicate the position of the upper left corner of the shape's 4x4 box, not of the shape itself.
	 * Also, when entering the game area, the actual shape also crosses the y=0 border.
	 */
	private int shapeX, shapeY;

	/**
	 * The current shape.
	 */
	private Shape shape;

	/**
	 * Drawing block index of the current shape.
	 */
	private int blockIndex;

	/**
	 * Constructor.
	 */
	public GameArea() {
		this.matrix = new int[10 * 20];
	}

	/**
	 * Causes the specified shape to enter the game.
	 * @param shape the shape to enter
	 * @param blockIndex the block index to use to draw the shape
	 */
	public void enterShape(final Shape shape, final int blockIndex) {
		this.shapeX = 3;
		this.shapeY = -4;
		this.shape = shape;
		this.blockIndex = blockIndex;
	}

	/**
	 * Checks whether the specified shape would be blocked or unblocked by existing blocks at the specified position.
	 * @param x the x position of the shape box
	 * @param y the y position of the shape box
	 * @param shapeToTest the shape
	 * @return true iff unblocked
	 */
	private boolean unblockedShapePosition(final int x, final int y, final Shape shapeToTest) {
		int i, j;
		final int[] occupationMatrix = shapeToTest.getOccupationMatrix();

		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				int x2, y2;

				if (occupationMatrix[j * 4 + i] == 0) {
					continue;
				}
				x2 = x + i;
				y2 = y + j;
				if (x2 < 0 || x2 > 9 || y2 > 19) {
					return false;
				}
				if (y2 < 0) {
					continue;
				}

				if (this.matrix[y2 * 10 + x2] != 0) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Tries to move the current shape downwards. This is possible if there is no collision
	 * with existing blocks.
	 * @return true if successful, false if blocked.
	 */
	public boolean moveCurrentShapeDown() {
		if (!unblockedShapePosition(shapeX, shapeY + 1, shape)) {
			return false;
		}

		shapeY++;
		return true;
	}

	/**
	 * Tries to move the current shape left. This is possible if there is no collision
	 * with existing blocks.
	 * @return true if successful, false if blocked.
	 */
	public boolean moveCurrentShapeLeft() {
		if (unblockedShapePosition(shapeX - 1, shapeY, shape)) {
			shapeX--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tries to move the current shape right. This is possible if there is no collision
	 * with existing blocks.
	 * @return true if successful, false if blocked.
	 */
	public boolean moveCurrentShapeRight() {
		if (unblockedShapePosition(shapeX + 1, shapeY, shape)) {
			shapeX++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tries to rotate the current shape clockwise. This is possible if there is no collision
	 * with existing blocks.
	 * @return true if successful, false if blocked.
	 */
	public boolean rotateCurrentShapeClockwise() {
		final Shape nextShape = shape.getNextShapeClockwise();
		final boolean success = unblockedShapePosition(shapeX, shapeY, nextShape);
		if (success) {
			shape = nextShape;
		}
		return success;
	}

	/**
	 * Tries to rotate the current shape counter-clockwise. This is possible if there is no collision
	 * with existing blocks.
	 * @return true if successful, false if blocked.
	 */
	public boolean rotateCurrentShapeCounterClockwise() {
		final Shape nextShape = shape.getNextShapeCounterclockwise();
		final boolean success = unblockedShapePosition(shapeX, shapeY, nextShape);
		if (success) {
			shape = nextShape;
		}
		return success;
	}

	/**
	 * Pastes the current shape into the game area as fixed blocks.
	 * @return true iff the shape had to be clipped against the game area.
	 */
	public boolean pasteCurrentShape() {
		int i, j;
		final int[] occupationMatrix = shape.getOccupationMatrix();
		boolean crossedBorder = false;

		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				int x2, y2;

				if (occupationMatrix[j * 4 + i] == 0) {
					continue;
				}
				x2 = shapeX + i;
				y2 = shapeY + j;
				if (x2 < 0 || x2 > 9 || y2 < 0 || y2 > 19) {
					crossedBorder = true;
					continue;
				}
				this.matrix[y2 * 10 + x2] = blockIndex;
			}
		}

		shapeY = -20;
		return crossedBorder;
	}

	/**
	 * Tests if the specified row is completed (filled)
	 * @param y the y coordinate of the row to check
	 * @return true iff the row is completed
	 */
	private boolean isRowCompleted(final int y) {
		final int base = y * 10;
		for (int i = 0; i < 10; i++) {
			if (matrix[base + i] == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Clears the specified row to empty.
	 * @param y the y coordinate of the row to clear
	 */
	private void clearRow(final int y) {
		final int base = y * 10;
		for (int i = 0; i < 10; i++) {
			matrix[base + i] = 0;
		}
	}

	/**
	 * Copies the specified row.
	 * @param source the source row
	 * @param dest the desintation row
	 */
	private void copyRow(final int source, final int dest) {
		final int sourceBase = source * 10;
		final int destBase = dest * 10;
		for (int i = 0; i < 10; i++) {
			matrix[destBase + i] = matrix[sourceBase + i];
		}
	}

	/**
	 * Looks for completed rows and returns their indices
	 * @return the completed rows
	 */
	public int[] findCompletedRows() {
		int[] tempResult = new int[20];
		int count = 0;
		for (int i=0; i<20; i++) {
			if (isRowCompleted(i)) {
				tempResult[count] = i;
				count++;
			}
		}
		return Arrays.copyOfRange(tempResult, 0, count);
	}

	/**
	 * Tests whether one of the elements of the specified array is a specific value.
	 * @param array the array
	 * @param item the value to look for
	 * @return true if the item was found
	 */
	private static boolean intArrayContains(final int[] array, final int item) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == item) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the specified rows from the game
	 * @param rowIndices the array containing the row indices
	 */
	public void removeRows(final int[] rowIndices) {
		int stack = 19;
		for (int r = 19; r >= 0; r--) {
			if (intArrayContains(rowIndices, r)) {
				continue;
			}
			copyRow(r, stack);
			stack--;
		}
		while (stack >= 0) {
			clearRow(stack);
			stack--;
		}
	}

	/**
	 * Replaces all non-space blocks with the specified block index.
	 * @param blockIndex the block index used for drawing
	 */
	public void recolor(final int blockIndex) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 20; j++) {
				if (matrix[j * 10 + i] != 0) {
					matrix[j * 10 + i] = blockIndex;
				}
			}
		}
	}

	/**
	 * Draws the game area.
	 */
	public void drawMatrix() {
		int i, j;
		for (i = 0; i < 10; i++) {
			for (j = 0; j < 20; j++) {
				DrawUtil.drawBlock(GAME_AREA_X_ON_SCREEN + i, GAME_AREA_Y_ON_SCREEN + j, matrix[j * 10 + i]);
			}
		}
	}

	/**
	 * Fills a row of the game area all with the same block.
	 * @param y the y coordinate relative to the game area of the row to fill 
	 * @param blockIndex the block index used to draw the shape
	 */
	public static void drawGameRowFilled(final int y, final int blockIndex) {
		int i;
		for (i = 0; i < 10; i++) {
			DrawUtil.drawBlock(GAME_AREA_X_ON_SCREEN + i, GAME_AREA_Y_ON_SCREEN + y, blockIndex);
		}
	}

	/**
	 * Fills multiple rows of the game area all with the same block.
	 * @param rows the indices of the rows to fill
	 * @param blockIndex the block index used to draw the shape
	 */
	public static void fillGameRows(final int[] rows, final int blockIndex) {
		for (int i = 0; i < rows.length; i++) {
			drawGameRowFilled(rows[i], blockIndex);
		}
	}

	/**
	 * Draws the current shape.
	 */
	public void drawCurrentShape() {
		shape.drawClipped(GAME_AREA_X_ON_SCREEN + shapeX, GAME_AREA_Y_ON_SCREEN + shapeY, blockIndex, GAME_AREA_X_ON_SCREEN, GAME_AREA_Y_ON_SCREEN, GAME_AREA_X_ON_SCREEN + 9, GAME_AREA_Y_ON_SCREEN + 19);
	}

	/**
	 * 
	 */
	public void draw() {
		drawMatrix();
		drawCurrentShape();
	}
}
