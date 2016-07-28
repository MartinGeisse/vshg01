/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.layout;

/**
 * A layout tree element must be able to produce a pixel value for an x, y location
 * that is relative to the upper left corner of the element's region.
 */
public interface ILayoutTreeElement {

	/**
	 * Returns the pixel at the specified location.
	 * @param x the x position relative to the left element boundary
	 * @param y the y position relative to the upper element boundary
	 * @return the pixel value, with the R byte in the LSB, G in the next
	 * byte, then B, and the MSB is ignored by the caller.
	 */
	public int getPixel(int x, int y);
	
}
