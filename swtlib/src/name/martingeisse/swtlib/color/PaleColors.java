/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.color;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

/**
 * This class wraps the SWT color objects for pale colored backgrounds.
 */
public class PaleColors {

	/**
	 * the red
	 */
	private static Color red;

	/**
	 * the green
	 */
	private static Color green;

	/**
	 * the blue
	 */
	private static Color blue;

	/**
	 * Initializes the colors in this class.
	 * @param device the device to allocate the colors from
	 */
	public static void initialize(Device device) {
		if (red != null) {
			throw new IllegalStateException("colors are already initialized");
		}
		red = new Color(device, 255, 192, 192);
		green = new Color(device, 192, 255, 192);
		blue = new Color(device, 192, 192, 255);
	}

	/**
	 * @return Returns the red.
	 */
	public static Color getRed() {
		return red;
	}

	/**
	 * @return Returns the green.
	 */
	public static Color getGreen() {
		return green;
	}

	/**
	 * @return Returns the blue.
	 */
	public static Color getBlue() {
		return blue;
	}

	/**
	 * Disposes of the colors allocated by this class.
	 */
	public static void dispose() {
		red.dispose();
		red = null;
		green.dispose();
		green = null;
		blue.dispose();
		blue = null;
	}

}
