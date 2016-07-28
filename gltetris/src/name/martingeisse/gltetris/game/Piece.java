/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

/**
 * This enum represents the different pieces in the game.
 */
public enum Piece {

	/**
	 * the O
	 */
	O("O_0"),

	/**
	 * the I
	 */
	I("I_1"),

	/**
	 * the T
	 */
	T("T_3"),

	/**
	 * the L
	 */
	L("L_0"),

	/**
	 * the J
	 */
	J("J_0"),

	/**
	 * the S
	 */
	S("S_1"),

	/**
	 * the Z
	 */
	Z("Z_1");

	/**
	 * the initialShapeName
	 */
	private String initialShapeName;

	/**
	 * the initialShape
	 */
	private Shape initialShape;

	/**
	 * Constructor.
	 */
	private Piece(final String initialShapeName) {
		this.initialShapeName = initialShapeName;
	}

	/**
	 * Initializes the fields for this shape.
	 */
	private void initializeThis() {
		this.initialShape = Shape.valueOf(initialShapeName);
	}

	/**
	 * Initializes the shape fields. This method must be invoked before shapes are used, but after
	 * static initializers for this class and for {@link Piece} have been executed.
	 */
	public static void initialize() {
		for (final Piece shape : values()) {
			shape.initializeThis();
		}
	}

	/**
	 * Getter method for the initialShapeName.
	 * @return the initialShapeName
	 */
	public String getInitialShapeName() {
		return initialShapeName;
	}

	/**
	 * Getter method for the initialShape.
	 * @return the initialShape
	 */
	public Shape getInitialShape() {
		return initialShape;
	}

}
