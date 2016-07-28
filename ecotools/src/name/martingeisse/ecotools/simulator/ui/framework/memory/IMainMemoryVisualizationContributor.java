/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

/**
 * This interface must be implemented by simulation contributors
 * that also contribute to the memory visualization system.
 */
public interface IMainMemoryVisualizationContributor {

	/**
	 * Returns the contribution items for this contributor. Each
	 * item contributes the visualization algorithm for a single
	 * peripheral device.
	 * 
	 * This method usually returns the memory visualization for (a subset of)
	 * the peripheral devices contributed by this simulation contributor,
	 * although contributing visualization algorithms for other peripheral
	 * devices is in principle possible.
	 * 
	 * Note that there must not be more than one IMemoryVisualizationContributionItem
	 * for any one peripheral device in the system. If there is no such item
	 * for a specific peripheral device attached to the bus, the corresponding
	 * addresses will be displayed accordingly using a default rendering.
	 * 
	 * @return Returns the memory visualization contribution items.
	 */
	public IMainMemoryVisualizationContributionItem[] getContributionItems();
	
}
