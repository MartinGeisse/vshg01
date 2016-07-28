/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

/**
 * This text conversion strategy accepts only bytes and
 * displays them using the corresponding ASCII character.
 */
public final class AsciiMemoryVisualizationTextConversionStrategy implements IMemoryVisualizationTextConversionStrategy {

	/**
	 * the INSTANCE
	 */
	private static final AsciiMemoryVisualizationTextConversionStrategy INSTANCE = new AsciiMemoryVisualizationTextConversionStrategy();
	
	/**
	 * @return Returns the singleton instance of this class.
	 */
	public static final AsciiMemoryVisualizationTextConversionStrategy getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Constructor
	 */
	private AsciiMemoryVisualizationTextConversionStrategy() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationTextConversionStrategy#valueToText(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public String valueToText(int value, BusAccessSize accessSize) throws MemoryVisualizationException {
		if (accessSize == BusAccessSize.BYTE) {
			return (value < 32) ? "." : Character.toString((char)value);
		} else {
			throw new MemoryVisualizationException("--- wrong access size: " + accessSize + " ---");
		}
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
		return false;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStrategy#supportsWordUnits()
	 */
	@Override
	public boolean supportsWordUnits() {
		return false;
	}

}
