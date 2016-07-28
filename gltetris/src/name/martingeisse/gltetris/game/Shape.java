/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

/**
 * This enum represents the different shapes in the game.
 */
public enum Shape {

	/**
	 * the O_0
	 */
	O_0("O", "O_0", "O_0"),

	/**
	 * the I_0
	 */
	I_0("I", "I_1", "I_1"),

	/**
	 * the I_1
	 */
	I_1("I", "I_0", "I_0"),

	/**
	 * the T_0
	 */
	T_0("T", "T_3", "T_1"),

	/**
	 * the T_1
	 */
	T_1("T", "T_0", "T_2"),

	/**
	 * the T_2
	 */
	T_2("T", "T_1", "T_3"),

	/**
	 * the T_3
	 */
	T_3("T", "T_2", "T_0"),

	/**
	 * the L_0
	 */
	L_0("L", "L_1", "L_3"),

	/**
	 * the L_1
	 */
	L_1("L", "L_2", "L_0"),

	/**
	 * the L_2
	 */
	L_2("L", "L_3", "L_1"),

	/**
	 * the L_3
	 */
	L_3("L", "L_0", "L_2"),

	/**
	 * the J_0
	 */
	J_0("J", "J_3", "J_1"),

	/**
	 * the J_1
	 */
	J_1("J", "J_0", "J_2"),

	/**
	 * the J_2
	 */
	J_2("J", "J_1", "J_3"),

	/**
	 * the J_3
	 */
	J_3("J", "J_2", "J_0"),

	/**
	 * the S_0
	 */
	S_0("S", "S_1", "S_1"),

	/**
	 * the S_1
	 */
	S_1("S", "S_0", "S_0"),

	/**
	 * the Z_0
	 */
	Z_0("Z", "Z_1", "Z_1"),

	/**
	 * the Z_1
	 */
	Z_1("Z", "Z_0", "Z_0");

	/**
	 * the pieceName
	 */
	private final String pieceName;

	/**
	 * the nextShapeCounterclockwiseName
	 */
	private final String nextShapeCounterclockwiseName;

	/**
	 * the nextShapeClockwiseName
	 */
	private final String nextShapeClockwiseName;

	/**
	 * the piece
	 */
	private Piece piece;

	/**
	 * the nextShapeCounterclockwise
	 */
	private Shape nextShapeCounterclockwise;

	/**
	 * the nextShapeClockwise
	 */
	private Shape nextShapeClockwise;

	/**
	 * Constructor.
	 */
	private Shape(final String pieceName, final String nextShapeCounterclockwiseName, final String nextShapeClockwiseName) {
		this.pieceName = pieceName;
		this.nextShapeCounterclockwiseName = nextShapeCounterclockwiseName;
		this.nextShapeClockwiseName = nextShapeClockwiseName;
	}

	/**
	 * Initializes the fields for this shape.
	 */
	private void initializeThis() {
		this.piece = Piece.valueOf(pieceName);
		this.nextShapeCounterclockwise = valueOf(nextShapeCounterclockwiseName);
		this.nextShapeClockwise = valueOf(nextShapeClockwiseName);
	}

	/**
	 * Initializes the shape fields. This method must be invoked before shapes are used, but after
	 * static initializers for this class and for {@link Piece} have been executed.
	 */
	public static void initialize() {
		for (final Shape shape : values()) {
			shape.initializeThis();
		}
	}

	/**
	 * Getter method for the pieceName.
	 * @return the pieceName
	 */
	public String getPieceName() {
		return pieceName;
	}

	/**
	 * Getter method for the nextShapeCounterclockwiseName.
	 * @return the nextShapeCounterclockwiseName
	 */
	public String getNextShapeCounterclockwiseName() {
		return nextShapeCounterclockwiseName;
	}

	/**
	 * Getter method for the nextShapeClockwiseName.
	 * @return the nextShapeClockwiseName
	 */
	public String getNextShapeClockwiseName() {
		return nextShapeClockwiseName;
	}

	/**
	 * Getter method for the piece.
	 * @return the piece
	 */
	public Piece getPiece() {
		return piece;
	}

	/**
	 * Getter method for the nextShapeCounterclockwise.
	 * @return the nextShapeCounterclockwise
	 */
	public Shape getNextShapeCounterclockwise() {
		return nextShapeCounterclockwise;
	}

	/**
	 * Getter method for the nextShapeClockwise.
	 * @return the nextShapeClockwise
	 */
	public Shape getNextShapeClockwise() {
		return nextShapeClockwise;
	}

	/**
	 * @return the occupation matrix for this shape
	 */
	public int[] getOccupationMatrix() {
		return occupationMatrices[ordinal()];
	}

	/**
	 * Draws a shape on screen, clipping it against the screen borders.
	 * @param x the x coordinate of the upper left corner of the shape matrix
	 * @param y the y coordinate of the upper left corner of the shape matrix
	 * @param blockIndex the block index used to draw the shape
	 * @param minx the minimal x coordinate on screen where blocks are drawn
	 * @param miny the minimal y coordinate on screen where blocks are drawn
	 * @param maxx the maximal x coordinate on screen where blocks are drawn
	 * @param maxy the maximal y coordinate on screen where blocks are drawn
	 */
	public void drawClipped(final int x, final int y, final int blockIndex, final int minx, final int miny, final int maxx, final int maxy) {
		int i, j;
		final int[] matrix = getOccupationMatrix();

		for (i = 0; i < 4; i++) {
			final int x2 = x + i;
			if (x2 < minx || x2 > maxx) {
				continue;
			}

			for (j = 0; j < 4; j++) {
				final int y2 = y + j;
				if (y2 < miny || y2 > maxy) {
					continue;
				}

				if (matrix[j * 4 + i] != 0) {
					DrawUtil.drawBlock(x2, y2, blockIndex);
				}
			}
		}
	}

	/**
	 * Draws a shape on screen using standard screen clipping borders.
	 * @param x the x coordinate of the upper left corner of the shape matrix
	 * @param y the y coordinate of the upper left corner of the shape matrix
	 * @param blockIndex the block index used to draw the shape
	 */
	public void draw(final int x, final int y, final int blockIndex) {
		drawClipped(x, y, blockIndex, 0, 0, 39, 29);
	}

	/**
	 * This array defines which tiles are occupied by which shape. The size
	 * of an occupation matrix is 4x4. The element type is char, with 0
	 * meaning 'free' and 1 meaning 'occupied'.
	 */
	public static final int[][] occupationMatrices = {
		{
			0, 0, 0, 0,
			0, 1, 1, 0,
			0, 1, 1, 0,
			0, 0, 0, 0
		},
		{
			0, 0, 0, 0,
			0, 0, 0, 0,
			1, 1, 1, 1,
			0, 0, 0, 0
		},
		{
			0, 1, 0, 0,
			0, 1, 0, 0,
			0, 1, 0, 0,
			0, 1, 0, 0
		},
		{
			0, 0, 0, 0,
			1, 1, 1, 0,
			0, 1, 0, 0,
			0, 0, 0, 0
		},
		{
			0, 1, 0, 0,
			1, 1, 0, 0,
			0, 1, 0, 0,
			0, 0, 0, 0
		},
		{
			0, 1, 0, 0,
			1, 1, 1, 0,
			0, 0, 0, 0,
			0, 0, 0, 0
		},
		{
			0, 1, 0, 0,
			0, 1, 1, 0,
			0, 1, 0, 0,
			0, 0, 0, 0
		},
		{
			0, 1, 0, 0,
			0, 1, 0, 0,
			0, 1, 1, 0,
			0, 0, 0, 0
		},
		{
			0, 0, 1, 0,
			1, 1, 1, 0,
			0, 0, 0, 0,
			0, 0, 0, 0
		},
		{
			1, 1, 0, 0,
			0, 1, 0, 0,
			0, 1, 0, 0,
			0, 0, 0, 0
		},
		{
			0, 0, 0, 0,
			1, 1, 1, 0,
			1, 0, 0, 0,
			0, 0, 0, 0
		},
		{
			0, 0, 1, 0,
			0, 0, 1, 0,
			0, 1, 1, 0,
			0, 0, 0, 0
		},
		{
			0, 1, 0, 0,
			0, 1, 1, 1,
			0, 0, 0, 0,
			0, 0, 0, 0
		},
		{
			0, 0, 1, 1,
			0, 0, 1, 0,
			0, 0, 1, 0,
			0, 0, 0, 0
		},
		{
			0, 0, 0, 0,
			0, 1, 1, 1,
			0, 0, 0, 1,
			0, 0, 0, 0
		},
		{
			0, 0, 0, 0,
			0, 0, 1, 1,
			0, 1, 1, 0,
			0, 0, 0, 0
		},
		{
			0, 1, 0, 0,
			0, 1, 1, 0,
			0, 0, 1, 0,
			0, 0, 0, 0
		},
		{
			0, 0, 0, 0,
			0, 1, 1, 0,
			0, 0, 1, 1,
			0, 0, 0, 0
		},
		{
			0, 0, 1, 0,
			0, 1, 1, 0,
			0, 1, 0, 0,
			0, 0, 0, 0
		}
	};

}
