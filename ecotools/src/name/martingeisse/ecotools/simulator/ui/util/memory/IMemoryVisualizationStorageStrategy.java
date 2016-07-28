/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

/**
 * This interface is used by memory visualization storage strategies.
 * These strategies implement the mechanisms to access the underlying storage.
 * 
 * Note that this strategy does not extend {@link IMemoryVisualizationStrategy}.
 * Storage strategies must support accesses with any access size, and return
 * corresponding results according to big-endian byte ordering.
 */
public interface IMemoryVisualizationStorageStrategy {

	/**
	 * Reads a value from the underlying storage.
	 * @param address the address to read from. This address is conceptually
	 * expressed in byte-sized units, even if the actual storage does not support
	 * byte-sized access. That is,
	 * - the addresses of consecutive byte-sized units differ by 1
	 * - the addresses of consecutive halfword-sized units differ by 2
	 * - the addresses of consecutive word-sized units differ by 4.
	 * The address must be aligned to the access size.
	 * @param accessSize the size of the unit transferred from the underlying storage mechanism.
	 * @return Returns the value read from the underlying storage mechanism.
	 * @throws MemoryVisualizationException if visualization failed
	 */
	public int read(int address, BusAccessSize accessSize) throws MemoryVisualizationException;
	
}
