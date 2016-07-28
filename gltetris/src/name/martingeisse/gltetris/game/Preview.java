/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import java.util.Random;

/**
 * This class encapsulates the preview boxes and is able to shift random
 * pieces into it, as well as return shifted-out pieces.
 * 
 * Preview place index 0 is the piece to enter the game area next.
 */
public class Preview {

	/**
	 * x offet of the (leftmost) preview box screen coordinates
	 */
	private static final int PREVIEW_X_ON_SCREEN = 20;
	
	/**
	 * y offet of the (leftmost) preview box screen coordinates
	 */
	private static final int PREVIEW_Y_ON_SCREEN = 7;

	/**
	 * x offset of one preview box to the next
	 */
	private static final int PREVIEW_X_DELTA = 5;

	/**
	 * the random
	 */
	private Random random;
	
	/**
	 * the piece0
	 */
	private Piece piece0;

	/**
	 * the blockIndex0
	 */
	private int blockIndex0;

	/**
	 * the piece1
	 */
	private Piece piece1;

	/**
	 * the blockIndex1
	 */
	private int blockIndex1;

	/**
	 * the piece2
	 */
	private Piece piece2;

	/**
	 * the blockIndex2
	 */
	private int blockIndex2;

	/**
	 * Constructor.
	 * @param random the random number generator used to generate new pieces
	 */
	public Preview(Random random) {
		this.random = random;
		reset();
	}

	/**
	 * Generates new pieces for all three preview boxes.
	 */
	public void reset() {
		shiftRandomPiece();
		shiftRandomPiece();
		shiftRandomPiece();
	}
	
	/**
	 * Getter method for the piece0.
	 * @return the piece0
	 */
	public Piece getPiece0() {
		return piece0;
	}

	/**
	 * Getter method for the blockIndex0.
	 * @return the blockIndex0
	 */
	public int getBlockIndex0() {
		return blockIndex0;
	}

	/**
	 * This function moves the preview pieces by one position; fills the now-free
	 * slot with a random piece, and discards the shifted-out piece. This
	 * implies that the shifted-out piece must be used by the caller before calling
	 * this method.
	 */
	public void shiftRandomPiece() {
		piece0 = piece1;
		blockIndex0 = blockIndex1;
		piece1 = piece2;
		blockIndex1 = blockIndex2;
		piece2 = Piece.values()[random.nextInt(Piece.values().length)];
		blockIndex2 = random.nextInt(7) + 1;
		if (blockIndex2 == 7) {
			blockIndex2 = 8;
		}
	}

	/**
	 * Draws the pieces in the preview boxes.
	 */
	public void draw() {
		piece0.getInitialShape().draw(PREVIEW_X_ON_SCREEN + 0 * PREVIEW_X_DELTA, PREVIEW_Y_ON_SCREEN, blockIndex0);
		piece1.getInitialShape().draw(PREVIEW_X_ON_SCREEN + 1 * PREVIEW_X_DELTA, PREVIEW_Y_ON_SCREEN, blockIndex1);
		piece2.getInitialShape().draw(PREVIEW_X_ON_SCREEN + 2 * PREVIEW_X_DELTA, PREVIEW_Y_ON_SCREEN, blockIndex2);
	}

	/**
	 * Draws the pieces in the preview boxes using only space blocks.
	 */
	public void undraw() {
		piece0.getInitialShape().draw(PREVIEW_X_ON_SCREEN + 0 * PREVIEW_X_DELTA, PREVIEW_Y_ON_SCREEN, 0);
		piece1.getInitialShape().draw(PREVIEW_X_ON_SCREEN + 1 * PREVIEW_X_DELTA, PREVIEW_Y_ON_SCREEN, 0);
		piece2.getInitialShape().draw(PREVIEW_X_ON_SCREEN + 2 * PREVIEW_X_DELTA, PREVIEW_Y_ON_SCREEN, 0);
	}

}
