/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.keyboard;

/**
 * This interface provides information about a scan code to
 * which no key is mapped in an {@link IKeyboardMap}. Such scan
 * codes may, for example, be produced by a GUI button, and this
 * interface provides the necessary information to generate
 * such buttons automatically using information queried from
 * the keyboard map.
 */
public interface IKeyboardMapMissedKey {

	/**
	 * @return Returns a label text for the missed key. This text
	 * can, for example, be used for a button that generates the
	 * missed key's scan code.
	 */
	public String getLabel();
	
	/**
	 * @return Returns the make scancode sequence for the missed key.
	 */
	public int[] getMakeCodeSequence();

	
	/**
	 * @return Returns the break scancode sequence for the missed key.
	 */
	public int[] getBreakCodeSequence();

}
