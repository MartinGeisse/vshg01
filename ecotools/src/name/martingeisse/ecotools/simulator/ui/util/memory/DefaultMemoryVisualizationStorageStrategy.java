/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.devices.memory.AbstractMemory;

/**
 * This storage strategy uses an {@link AbstractMemory} to load and
 * store values.
 */
public class DefaultMemoryVisualizationStorageStrategy implements IMemoryVisualizationStorageStrategy {

	/**
	 * the memory
	 */
	private AbstractMemory memory;
	
	/**
	 * the addressModifier
	 */
	private int addressModifier;

	/**
	 * Constructor
	 * @param memory the underlying memory
	 */
	public DefaultMemoryVisualizationStorageStrategy(AbstractMemory memory) {
		this(memory, 0);
	}

	/**
	 * Constructor
	 * @param memory the underlying memory
	 * @param addressModifier this modifier is added to all addresses passed to access methods. For example,
	 * to make the visualization panel show a mapped address space in which the memory is simply moved
	 * to start address 0x10000000, a value of -0x10000000 must be passed for this modifier to modify
	 * "virtual" address 0x10000000 to local address 0 of the memory.
	 */
	public DefaultMemoryVisualizationStorageStrategy(AbstractMemory memory, int addressModifier) {
		this.memory = memory;
		this.addressModifier = addressModifier;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy#read(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public int read(int address, BusAccessSize accessSize) throws MemoryVisualizationException {
		try {
			return memory.read(address + addressModifier, accessSize);
		} catch (BusTimeoutException e) {
			throw new MemoryVisualizationException("bus timeout");
		} catch (IndexOutOfBoundsException e) {
			throw new MemoryVisualizationException("invalid address");
		}
	}

}
