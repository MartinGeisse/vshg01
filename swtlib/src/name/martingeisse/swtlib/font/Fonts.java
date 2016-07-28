/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.font;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;

/**
 * 
 */
public class Fonts {

	/**
	 * the courier12
	 */
	private static Font courier12;

	/**
	 * Initializes the fonts stored in this class.
	 * @param device the device used to allocate fonts
	 */
	public static void initialize(Device device) {
		if (courier12 != null) {
			throw new IllegalStateException("Fonts have already been initialized!");
		}
		courier12 = new Font(device, "Courier", 12, SWT.NORMAL);
	}

	/**
	 * Disposes of the fonts stored in this class.
	 */
	public static void dispose() {
		courier12.dispose();
		courier12 = null;
	}

	/**
	 * @return Returns the courier12.
	 */
	public static Font getCourier12() {
		return courier12;
	}

}
