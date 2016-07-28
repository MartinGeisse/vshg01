/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.keyboard;

import org.eclipse.swt.events.KeyEvent;

/**
 * This class translates SWT keyboard events to a sequence of
 * scan codes for the ECO32.
 */
public interface IKeyboardMap {

	/**
	 * Translates an SWT keyboard event to a sequence of AT (set 2)
	 * keyboard scan codes. AT (set 2) scan codes are those sent
	 * from a real keyboard to the host keyboard controller.
	 * @param event the ST key event
	 * @param pressed true if pressed, false if released
	 * @return Returns the AT (set 2) scan codes.
	 */
	public int[] translate(KeyEvent event, boolean pressed);
	
	/**
	 * @return Returns information about "missed keys", i.e. target
	 * scancodes that cannot be reproduced by mapping any SWT keycode.
	 */
	public IKeyboardMapMissedKey[] getMissedKeys();
	
}
