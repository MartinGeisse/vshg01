/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.color;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

/**
 * This class wraps standard SWT color objects.
 */
public class Colors {

	/**
	 * the colors by index
	 */
	private static Color[] colors;

	/**
	 * the black
	 */
	private static Color black;

	/**
	 * the darkBlue
	 */
	private static Color darkBlue;

	/**
	 * the darkGreen
	 */
	private static Color darkGreen;

	/**
	 * the darkCyan
	 */
	private static Color darkCyan;

	/**
	 * the darkRed
	 */
	private static Color darkRed;

	/**
	 * the darkPurple
	 */
	private static Color darkPurple;

	/**
	 * the darkYellow
	 */
	private static Color darkYellow;

	/**
	 * the lightGray
	 */
	private static Color lightGray;

	/**
	 * the darkGray
	 */
	private static Color darkGray;

	/**
	 * the lightBlue
	 */
	private static Color lightBlue;

	/**
	 * the lightGreen
	 */
	private static Color lightGreen;

	/**
	 * the lightCyan
	 */
	private static Color lightCyan;

	/**
	 * the lightRed
	 */
	private static Color lightRed;

	/**
	 * the lightPurple
	 */
	private static Color lightPurple;

	/**
	 * the lightYellow
	 */
	private static Color lightYellow;

	/**
	 * the white
	 */
	private static Color white;

	/**
	 * Initializes the colors.
	 * @param device the device to allocate the colors from
	 */
	public static void initialize(Device device) {
		if (colors != null) {
			throw new IllegalStateException("colors are already initialized");
		}
		colors = new Color[] {
			black = new Color(device, 0, 0, 0),
			darkBlue = new Color(device, 0, 0, 127),
			darkGreen = new Color(device, 0, 127, 0),
			darkCyan = new Color(device, 0, 127, 127),
			darkRed = new Color(device, 127, 0, 0),
			darkPurple = new Color(device, 127, 0, 127),
			darkYellow = new Color(device, 127, 127, 0),
			lightGray = new Color(device, 192, 192, 192),
			darkGray = new Color(device, 127, 127, 127),
			lightBlue = new Color(device, 0, 0, 255),
			lightGreen = new Color(device, 0, 255, 0),
			lightCyan = new Color(device, 0, 255, 255),
			lightRed = new Color(device, 255, 0, 0),
			lightPurple = new Color(device, 255, 0, 255),
			lightYellow = new Color(device, 255, 255, 0),
			white = new Color(device, 255, 255, 255),
		};
	}

	/**
	 * @param n the color index
	 * @return Returns the color with that index
	 */
	public static Color getColor(int n) {
		return colors[n];
	}

	/**
	 * @return Returns the black.
	 */
	public static Color getBlack() {
		return black;
	}

	/**
	 * @return Returns the darkBlue.
	 */
	public static Color getDarkBlue() {
		return darkBlue;
	}

	/**
	 * @return Returns the darkGreen.
	 */
	public static Color getDarkGreen() {
		return darkGreen;
	}

	/**
	 * @return Returns the darkCyan.
	 */
	public static Color getDarkCyan() {
		return darkCyan;
	}

	/**
	 * @return Returns the darkRed.
	 */
	public static Color getDarkRed() {
		return darkRed;
	}

	/**
	 * @return Returns the darkPurple.
	 */
	public static Color getDarkPurple() {
		return darkPurple;
	}

	/**
	 * @return Returns the darkYellow.
	 */
	public static Color getDarkYellow() {
		return darkYellow;
	}

	/**
	 * @return Returns the lightGray.
	 */
	public static Color getLightGray() {
		return lightGray;
	}

	/**
	 * @return Returns the darkGray.
	 */
	public static Color getDarkGray() {
		return darkGray;
	}

	/**
	 * @return Returns the lightBlue.
	 */
	public static Color getLightBlue() {
		return lightBlue;
	}

	/**
	 * @return Returns the lightGreen.
	 */
	public static Color getLightGreen() {
		return lightGreen;
	}

	/**
	 * @return Returns the lightCyan.
	 */
	public static Color getLightCyan() {
		return lightCyan;
	}

	/**
	 * @return Returns the lightRed.
	 */
	public static Color getLightRed() {
		return lightRed;
	}

	/**
	 * @return Returns the lightPurple.
	 */
	public static Color getLightPurple() {
		return lightPurple;
	}

	/**
	 * @return Returns the lightYellow.
	 */
	public static Color getLightYellow() {
		return lightYellow;
	}

	/**
	 * @return Returns the white.
	 */
	public static Color getWhite() {
		return white;
	}

	/**
	 * 
	 */
	public static void dispose() {
		for (Color c : colors) {
			c.dispose();
		}
		colors = null;
		black = null;
		darkBlue = null;
		darkGreen = null;
		darkCyan = null;
		darkRed = null;
		darkPurple = null;
		darkYellow = null;
		lightGray = null;
		darkGray = null;
		lightBlue = null;
		lightGreen = null;
		lightCyan = null;
		lightRed = null;
		lightPurple = null;
		lightYellow = null;
		white = null;
	}

}
