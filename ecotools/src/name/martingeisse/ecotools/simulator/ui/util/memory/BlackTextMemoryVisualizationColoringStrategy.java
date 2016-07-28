/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.swtlib.color.Colors;

import org.eclipse.swt.custom.StyleRange;

/**
 * This is a basic coloring strategy that uses black text all over.
 */
public class BlackTextMemoryVisualizationColoringStrategy implements IMemoryVisualizationColoringStrategy {

	/**
	 * the INSTANCE
	 */
	private static BlackTextMemoryVisualizationColoringStrategy INSTANCE = new BlackTextMemoryVisualizationColoringStrategy();

	/**
	 * @return Returns the singleton instance of this class.
	 */
	public static BlackTextMemoryVisualizationColoringStrategy getInstance() {
		return INSTANCE;
	}

	/**
	 * Constructor
	 */
	private BlackTextMemoryVisualizationColoringStrategy() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStrategy#supportsByteUnits()
	 */
	@Override
	public boolean supportsByteUnits() {
		return true;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStrategy#supportsHalfwordUnits()
	 */
	@Override
	public boolean supportsHalfwordUnits() {
		return true;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStrategy#supportsWordUnits()
	 */
	@Override
	public boolean supportsWordUnits() {
		return true;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationColoringStrategy#dispose()
	 */
	@Override
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationColoringStrategy#configureStyleRange(int, int, name.martingeisse.ecotools.simulator.bus.BusAccessSize, org.eclipse.swt.custom.StyleRange)
	 */
	@Override
	public void configureStyleRange(int address, int value, BusAccessSize accessSize, StyleRange styleRange) throws MemoryVisualizationException {
		styleRange.foreground = Colors.getBlack();
	}

}
