/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.common.util.HexNumberUtil;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

/**
 * This text conversion strategy simply formats memory values
 * as hexadecimal numbers using two, four, or eight hex digits
 * depending on the access size.
 */
public final class DefaultMemoryVisualizationTextConversionStrategy implements IMemoryVisualizationTextConversionStrategy {

	/**
	 * the INSTANCE
	 */
	private static final DefaultMemoryVisualizationTextConversionStrategy INSTANCE = new DefaultMemoryVisualizationTextConversionStrategy();
	
	/**
	 * @return Returns the singleton instance of this class.
	 */
	public static final DefaultMemoryVisualizationTextConversionStrategy getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Constructor
	 */
	private DefaultMemoryVisualizationTextConversionStrategy() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationTextConversionStrategy#valueToText(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public String valueToText(int value, BusAccessSize accessSize) throws MemoryVisualizationException {
		switch (accessSize) {
		
		case BYTE:
			return HexNumberUtil.unsignedByteToString(value);
			
		case HALFWORD:
			return HexNumberUtil.unsignedHalfwordToString(value);
			
		case WORD:
			return HexNumberUtil.unsignedWordToString(value);
		
		default:
			throw new IllegalArgumentException("unexpected access size: " + accessSize);
			
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
		return true;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStrategy#supportsWordUnits()
	 */
	@Override
	public boolean supportsWordUnits() {
		return true;
	}

}
