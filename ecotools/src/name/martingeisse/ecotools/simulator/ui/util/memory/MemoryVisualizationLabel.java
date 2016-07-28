/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

/**
 * Simple implementation of {@link IMemoryVisualizationLabel} that contains
 * the address and name of the label as fields.
 */
public class MemoryVisualizationLabel implements IMemoryVisualizationLabel {

	/**
	 * the address
	 */
	private int address;

	/**
	 * the name
	 */
	private String name;

	/**
	 * Constructor
	 */
	public MemoryVisualizationLabel() {
	}

	/**
	 * Constructor
	 * @param address the address of this label
	 * @param name the name of this label
	 */
	public MemoryVisualizationLabel(int address, String name) {
		this.address = address;
		this.name = name;
	}

	/**
	 * @return Returns the address.
	 */
	@Override
	public int getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 * @param address the new value to set
	 */
	public void setAddress(int address) {
		this.address = address;
	}

	/**
	 * @return Returns the name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name the new value to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
