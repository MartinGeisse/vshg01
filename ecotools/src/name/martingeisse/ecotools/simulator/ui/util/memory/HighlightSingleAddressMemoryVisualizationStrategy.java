/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.swtlib.color.Colors;
import name.martingeisse.swtlib.color.PaleColors;

import org.eclipse.swt.custom.StyleRange;

/**
 * This coloring strategy highlights a single memory cell
 * (recognizing its address).
 */
public class HighlightSingleAddressMemoryVisualizationStrategy implements IMemoryVisualizationColoringStrategy {

	/**
	 * the highlightedAddress
	 */
	private int highlightedAddress;

	/**
	 * Constructor
	 * @param highlightedAddress the address to highlight
	 */
	public HighlightSingleAddressMemoryVisualizationStrategy(int highlightedAddress) {
		this.highlightedAddress = highlightedAddress;
	}

	/**
	 * @return Returns the highlightedAddress.
	 */
	public int getHighlightedAddress() {
		return highlightedAddress;
	}

	/**
	 * Sets the highlightedAddress.
	 * @param highlightedAddress the new value to set
	 */
	public void setHighlightedAddress(int highlightedAddress) {
		this.highlightedAddress = highlightedAddress;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationColoringStrategy#configureStyleRange(int, int, name.martingeisse.ecotools.simulator.bus.BusAccessSize, org.eclipse.swt.custom.StyleRange)
	 */
	@Override
	public void configureStyleRange(int address, int value, BusAccessSize accessSize, StyleRange styleRange) throws MemoryVisualizationException {
		if (address == highlightedAddress) {
			styleRange.foreground = Colors.getBlack();
			styleRange.background = PaleColors.getBlue();
		} else {
			styleRange.foreground = Colors.getBlack();
			styleRange.background = Colors.getWhite();
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationColoringStrategy#dispose()
	 */
	@Override
	public void dispose() {
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
