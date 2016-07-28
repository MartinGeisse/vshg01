/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.wicket;

/**
 * An instance of this class represents in updated character in the
 * character display.
 */
public class CharacterDisplayPatch {

	/**
	 * the x
	 */
	private final int x;

	/**
	 * the y
	 */
	private final int y;

	/**
	 * the c
	 */
	private final int character;

	/**
	 * the a
	 */
	private final int attribute;

	/**
	 * Constructor.
	 * @param x the x position of the updated character cell
	 * @param y the y position of the updated character cell
	 * @param character the new character value
	 * @param attribute the new attribute vale
	 */
	public CharacterDisplayPatch(final int x, final int y, final int character, final int attribute) {
		super();
		this.x = x;
		this.y = y;
		this.character = character;
		this.attribute = attribute;
	}

	/**
	 * Getter method for the x.
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Getter method for the y.
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Getter method for the character.
	 * @return the character
	 */
	public int getCharacter() {
		return character;
	}

	/**
	 * Getter method for the attribute.
	 * @return the attribute
	 */
	public int getAttribute() {
		return attribute;
	}

}
