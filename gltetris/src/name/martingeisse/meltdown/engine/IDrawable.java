/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

/**
 * This interface hides the differences between images, animations, and similar.
 * Any object that can be drawn to a position on screen can implement this interface.
 * 
 * A far as the implementation of this interface has a size, the (x, y) coordinates
 * specified when drawing refer to the upper left corner of the object.
 */
public interface IDrawable {

	/**
	 * @param x the x position to draw to
	 * @param y the y position to draw to
	 * @param startState the start state for animation
	 */
	public void draw(int x, int y, int startState);
	
}
