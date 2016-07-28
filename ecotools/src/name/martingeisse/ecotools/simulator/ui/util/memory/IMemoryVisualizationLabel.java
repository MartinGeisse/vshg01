/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

/**
 * This interface provides getter methods for the name and address
 * of a label. It does not have a notion of a section, as assembler
 * labels do.
 */
public interface IMemoryVisualizationLabel {

	/**
	 * @return Returns the address of this label.
	 */
	public int getAddress();
	
	/**
	 * @return Returns the name of this label.
	 */
	public String getName();
	
}
