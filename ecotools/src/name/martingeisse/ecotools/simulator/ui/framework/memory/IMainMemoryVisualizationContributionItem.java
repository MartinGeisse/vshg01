/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

import name.martingeisse.ecotools.simulator.bus.IPeripheralDevice;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy;

/**
 * This item describes the contribution of a memory visualization
 * algorithm for a single peripheral device.
 */
public interface IMainMemoryVisualizationContributionItem {

	/**
	 * @return Returns the device to which this contribution item applies.
	 */
	public IPeripheralDevice getDevice();

	/**
	 * @return Returns the local storage strategy. This strategy provides
	 * the memory contents using device-local addresses. This method
	 * may return null to indicate that no inspectable device-local
	 * memory is available.
	 */
	public IMemoryVisualizationStorageStrategy getLocalStorageStrategy();
	
}
