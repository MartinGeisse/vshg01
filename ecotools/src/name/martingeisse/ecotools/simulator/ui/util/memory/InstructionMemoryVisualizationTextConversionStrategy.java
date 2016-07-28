/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.ui.instruction.InstructionRenderer;

/**
 * TODO: properly unit-test this class
 */
public class InstructionMemoryVisualizationTextConversionStrategy extends InstructionRenderer implements IMemoryVisualizationTextConversionStrategy {

	/**
	 * the INSTANCE
	 */
	private static final InstructionMemoryVisualizationTextConversionStrategy INSTANCE = new InstructionMemoryVisualizationTextConversionStrategy();
	
	/**
	 * @return Returns the singleton instance of this class.
	 */
	public static final InstructionMemoryVisualizationTextConversionStrategy getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Constructor
	 */
	private InstructionMemoryVisualizationTextConversionStrategy() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStrategy#supportsByteUnits()
	 */
	@Override
	public boolean supportsByteUnits() {
		return false;
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
		return true;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationTextConversionStrategy#valueToText(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public String valueToText(int value, BusAccessSize accessSize) throws MemoryVisualizationException {
		return renderInstruction(value);
	}

}
