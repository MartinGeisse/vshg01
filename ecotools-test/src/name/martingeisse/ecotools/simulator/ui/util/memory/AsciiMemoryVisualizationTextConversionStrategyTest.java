/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

import org.junit.Test;

/**
 * 
 */
public class AsciiMemoryVisualizationTextConversionStrategyTest {

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test
	public void test() throws MemoryVisualizationException {
		assertTrue(AsciiMemoryVisualizationTextConversionStrategy.getInstance().supportsByteUnits());
		assertFalse(AsciiMemoryVisualizationTextConversionStrategy.getInstance().supportsHalfwordUnits());
		assertFalse(AsciiMemoryVisualizationTextConversionStrategy.getInstance().supportsWordUnits());
		assertEquals(".", AsciiMemoryVisualizationTextConversionStrategy.getInstance().valueToText(0, BusAccessSize.BYTE));
		assertEquals(".", AsciiMemoryVisualizationTextConversionStrategy.getInstance().valueToText(31, BusAccessSize.BYTE));
		assertEquals(" ", AsciiMemoryVisualizationTextConversionStrategy.getInstance().valueToText(32, BusAccessSize.BYTE));
		assertEquals("a", AsciiMemoryVisualizationTextConversionStrategy.getInstance().valueToText('a', BusAccessSize.BYTE));
	}

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test(expected = MemoryVisualizationException.class)
	public void testHalfword() throws MemoryVisualizationException {
		AsciiMemoryVisualizationTextConversionStrategy.getInstance().valueToText(0, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test(expected = MemoryVisualizationException.class)
	public void testWord() throws MemoryVisualizationException {
		AsciiMemoryVisualizationTextConversionStrategy.getInstance().valueToText(0, BusAccessSize.WORD);
	}

}
