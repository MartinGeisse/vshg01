/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.model;

import name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterface;

/**
 * Fake implementation of {@link IKeyboardUserInterface}.
 */
public class KeyboardUserInterface implements IKeyboardUserInterface {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterface#hasInput()
	 */
	@Override
	public boolean hasInput() {
		return false;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterface#receiveByte()
	 */
	@Override
	public byte receiveByte() throws IllegalStateException {
		return 0;
	}

}
