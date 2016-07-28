/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

/**
 * Commonly used colors for this plugin.
 */
public class Colors {

	/**
	 * the initialized
	 */
	private static boolean initialized;

	/**
	 * the black
	 */
	public static Color black;
	
	/**
	 * the grey
	 */
	public static Color grey;

	/**
	 * the green
	 */
	public static Color green;
	
	/**
	 * Initializes (allocates) colors.
	 * @param device the device to initialize colors for
	 */
	public static void initialize(Device device) {
		if (initialized) {
			return;
		}
		black = new Color(device, 0, 0, 0);
		grey = new Color(device, 128, 128, 128);
		green = new Color(device, 0, 255, 0);
		initialized = true;
	}
	
	/**
	 * Disposes of all allocated colors.
	 */
	public static void dispose() {
		if (!initialized) {
			return;
		}

		if (black != null) {
			black.dispose();
			black = null;
		}
		
		if (grey != null) {
			grey.dispose();
			grey = null;
		}
		
		if (green != null) {
			green.dispose();
			green = null;
		}
		
		initialized = false;
	}
}
