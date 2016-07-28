/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

import org.eclipse.swt.custom.StyleRange;

/**
 * This interface is used by memory visualization coloring strategies.
 * These strategies choose appropriate text (foreground) colors for
 * memory values being visualized. The background color is generally
 * white, or a lightly-colored "almost white" color used for highlighting.
 */
public interface IMemoryVisualizationColoringStrategy extends IMemoryVisualizationStrategy {

	/**
	 * Configures the style range to use for a visualized memory cell.
	 * Only styling should be configured. The range bounds are set by the caller.
	 * @param address the address from which the value was loaded
	 * @param value the value being visualized. This value must be in the
	 * range 0..255 for byte-sized units. It must be in the range
	 * 0..65535 for halfword-sized units.
	 * @param accessSize the unit size of the value being visualized
	 * @param styleRange the style range to configure
	 * @throws MemoryVisualizationException if visualization failed
	 */
	public void configureStyleRange(int address, int value, BusAccessSize accessSize, StyleRange styleRange) throws MemoryVisualizationException;
	
	/**
	 * Disposes of all allocated GUI objects.
	 */
	public void dispose();
	
}
