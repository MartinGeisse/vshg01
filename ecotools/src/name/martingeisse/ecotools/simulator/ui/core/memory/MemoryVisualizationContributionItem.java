/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.memory;

import name.martingeisse.ecotools.simulator.bus.IPeripheralDevice;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributionItem;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy;

/**
 * Default implementation of {@link IMainMemoryVisualizationContributionItem}.
 */
public class MemoryVisualizationContributionItem implements IMainMemoryVisualizationContributionItem {

	/**
	 * the device
	 */
	private IPeripheralDevice device;

	/**
	 * the localStorageStrategy
	 */
	private IMemoryVisualizationStorageStrategy localStorageStrategy;

	/**
	 * Constructor
	 * @param device the device for which a strategy is contributed
	 * @param localStorageStrategy the contributed strategy
	 */
	public MemoryVisualizationContributionItem(IPeripheralDevice device, IMemoryVisualizationStorageStrategy localStorageStrategy) {
		this.device = device;
		this.localStorageStrategy = localStorageStrategy;
	}

	/**
	 * @return Returns the device.
	 */
	@Override
	public IPeripheralDevice getDevice() {
		return device;
	}

	/**
	 * @return Returns the localStorageStrategy.
	 */
	@Override
	public IMemoryVisualizationStorageStrategy getLocalStorageStrategy() {
		return localStorageStrategy;
	}

}
