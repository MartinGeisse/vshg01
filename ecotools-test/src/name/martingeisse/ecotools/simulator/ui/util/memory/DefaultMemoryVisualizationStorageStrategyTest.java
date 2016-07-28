/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import static org.junit.Assert.assertEquals;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.devices.memory.Ram;

import org.junit.Test;

/**
 * 
 */
public class DefaultMemoryVisualizationStorageStrategyTest {

	/**
	 * @throws Exception ...
	 */
	@Test
	public void test() throws Exception {
		Ram ram = new Ram(10);
		ram.write(0, BusAccessSize.WORD, 0x11223344);
		ram.write(4, BusAccessSize.WORD, 0xaabbccdd);
		
		DefaultMemoryVisualizationStorageStrategy strategy = new DefaultMemoryVisualizationStorageStrategy(ram);
		assertEquals(0x11223344, strategy.read(0, BusAccessSize.WORD));
		assertEquals(0x1122, strategy.read(0, BusAccessSize.HALFWORD));
		assertEquals(0x3344, strategy.read(2, BusAccessSize.HALFWORD));
		assertEquals(0x11, strategy.read(0, BusAccessSize.BYTE));
		assertEquals(0x22, strategy.read(1, BusAccessSize.BYTE));
		assertEquals(0x33, strategy.read(2, BusAccessSize.BYTE));
		assertEquals(0x44, strategy.read(3, BusAccessSize.BYTE));
		assertEquals(0xaabbccdd, strategy.read(4, BusAccessSize.WORD));
		assertEquals(0xaabb, strategy.read(4, BusAccessSize.HALFWORD));
		assertEquals(0xccdd, strategy.read(6, BusAccessSize.HALFWORD));
		assertEquals(0xaa, strategy.read(4, BusAccessSize.BYTE));
		assertEquals(0xbb, strategy.read(5, BusAccessSize.BYTE));
		assertEquals(0xcc, strategy.read(6, BusAccessSize.BYTE));
		assertEquals(0xdd, strategy.read(7, BusAccessSize.BYTE));

		DefaultMemoryVisualizationStorageStrategy strategy2 = new DefaultMemoryVisualizationStorageStrategy(ram, 4);
		assertEquals(0xaabbccdd, strategy2.read(0, BusAccessSize.WORD));
		assertEquals(0xaabb, strategy2.read(0, BusAccessSize.HALFWORD));
		assertEquals(0xccdd, strategy2.read(2, BusAccessSize.HALFWORD));
		assertEquals(0xaa, strategy2.read(0, BusAccessSize.BYTE));
		assertEquals(0xbb, strategy2.read(1, BusAccessSize.BYTE));
		assertEquals(0xcc, strategy2.read(2, BusAccessSize.BYTE));
		assertEquals(0xdd, strategy2.read(3, BusAccessSize.BYTE));
	
	}
}
