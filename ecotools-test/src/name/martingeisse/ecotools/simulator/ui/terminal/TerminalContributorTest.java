/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.terminal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.devices.terminal.Terminal;

import org.junit.Test;

/**
 * 
 */
public class TerminalContributorTest {

	/**
	 * the terminalContributor
	 */
	private TerminalContributor terminalContributor = new TerminalContributor();
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testRegisterPeripheralDevices() throws BusTimeoutException {
		Bus bus = new Bus();
		terminalContributor.registerPeripheralDevices(bus);
		bus.buildBusMap();
		
		assertNull(bus.getBusMapEntryForAddress(0x30000000));
		assertNull(bus.getBusMapEntryForAddress(0x30100000));
		assertNull(bus.getBusMapEntryForAddress(0x30200000));
		assertTrue(bus.getBusMapEntryForAddress(0x30300000).getDevice() instanceof Terminal);
		assertNull(bus.getBusMapEntryForAddress(0x30400000));
		
		Terminal terminal = (Terminal)(bus.getBusMapEntryForAddress(0x30300000).getDevice());
		assertEquals(-1, bus.getActiveInterrupt(0xFFFFFFFF));
		terminal.write(0, BusAccessSize.WORD, 3);
		assertEquals(1, bus.getActiveInterrupt(0xFFFFFFFF));
		terminal.write(8, BusAccessSize.WORD, 3);
		assertEquals(0, bus.getActiveInterrupt(0xFFFFFFFF));
	}

	/**
	 * 
	 */
	public void testMemoryVisualization() {
		assertNull(terminalContributor.getMemoryVisualizationContributor());
	}
	
}
