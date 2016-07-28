/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

import org.junit.Test;

/**
 * 
 */
public class DefaultMemoryVisualizationTextConversionStrategyTest {

	/**
	 * 
	 */
	@Test
	public void testSupportedAccessSizes() {
		assertTrue(DefaultMemoryVisualizationTextConversionStrategy.getInstance().supportsByteUnits());
		assertTrue(DefaultMemoryVisualizationTextConversionStrategy.getInstance().supportsHalfwordUnits());
		assertTrue(DefaultMemoryVisualizationTextConversionStrategy.getInstance().supportsWordUnits());
	}
	
	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test
	public void testValueToText() throws MemoryVisualizationException {
		assertEquals("11223344", DefaultMemoryVisualizationTextConversionStrategy.getInstance().valueToText(0x11223344, BusAccessSize.WORD));
		assertEquals("3344", DefaultMemoryVisualizationTextConversionStrategy.getInstance().valueToText(0x11223344, BusAccessSize.HALFWORD));
		assertEquals("44", DefaultMemoryVisualizationTextConversionStrategy.getInstance().valueToText(0x11223344, BusAccessSize.BYTE));
		assertEquals("0a", DefaultMemoryVisualizationTextConversionStrategy.getInstance().valueToText(10, BusAccessSize.BYTE));
	}
}
