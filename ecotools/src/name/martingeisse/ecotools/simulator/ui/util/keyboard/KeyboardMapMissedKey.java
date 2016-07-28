/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.keyboard;

/**
 * Default implementation for {@link IKeyboardMapMissedKey}.
 */
public class KeyboardMapMissedKey implements IKeyboardMapMissedKey {

	/**
	 * the label
	 */
	private String label;

	/**
	 * the makeCodeSequence
	 */
	private int[] makeCodeSequence;

	/**
	 * the breakCodeSequence
	 */
	private int[] breakCodeSequence;

	/**
	 * Constructor
	 */
	public KeyboardMapMissedKey() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMapMissedKey#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 * @param label the new value to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMapMissedKey#getMakeCodeSequence()
	 */
	@Override
	public int[] getMakeCodeSequence() {
		return makeCodeSequence;
	}

	/**
	 * Sets the makeCodeSequence.
	 * @param makeCodeSequence the new value to set
	 */
	public void setMakeCodeSequence(int... makeCodeSequence) {
		this.makeCodeSequence = makeCodeSequence;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMapMissedKey#getBreakCodeSequence()
	 */
	@Override
	public int[] getBreakCodeSequence() {
		return breakCodeSequence;
	}

	/**
	 * Sets the breakCodeSequence.
	 * @param breakCodeSequence the new value to set
	 */
	public void setBreakCodeSequence(int... breakCodeSequence) {
		this.breakCodeSequence = breakCodeSequence;
	}

}
