/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

/**
 * This interface is used by memory visualization text conversion strategies.
 * These strategies convert between memory values and their textual representation.
 */
public interface IMemoryVisualizationTextConversionStrategy extends IMemoryVisualizationStrategy {

	/**
	 * Converts a memory value to its textual representation.
	 * @param value the value to convert. This value must be in the
	 * range 0..255 for byte-sized units. It must be in the range
	 * 0..65535 for halfword-sized units.
	 * @param accessSize the unit size of the value being visualized
	 * @return Returns the textual representation of the specified value.
	 * @throws MemoryVisualizationException if visualization failed
	 */
	public String valueToText(int value, BusAccessSize accessSize) throws MemoryVisualizationException;

}
