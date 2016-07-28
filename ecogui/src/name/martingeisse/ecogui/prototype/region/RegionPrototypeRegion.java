/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.region;

/**
 * A single region, e.g. a window on the desktop.
 */
public class RegionPrototypeRegion {

	/**
	 * the x
	 */
	private int x;
	
	/**
	 * the y
	 */
	private int y;
	
	/**
	 * the width
	 */
	private int width;
	
	/**
	 * the height
	 */
	private int height;
	
	/**
	 * Constructor.
	 */
	public RegionPrototypeRegion() {
	}

	/**
	 * Getter method for the x.
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Setter method for the x.
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Getter method for the y.
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Setter method for the y.
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Getter method for the width.
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Setter method for the width.
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Getter method for the height.
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Setter method for the height.
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
}
