/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterDisplay;
import name.martingeisse.ecotools.simulator.devices.keyboard.Keyboard;

import org.junit.Test;

/**
 * 
 */
public class ConsoleContributorTest {

	/**
	 * the consoleContributor
	 */
	private ConsoleContributor consoleContributor = new ConsoleContributor();
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testRegisterPeripheralDevices() throws BusTimeoutException {
		Bus bus = new Bus();
		consoleContributor.registerPeripheralDevices(bus);
		bus.buildBusMap();
		
		assertNull(bus.getBusMapEntryForAddress(0x30000000));
		assertTrue(bus.getBusMapEntryForAddress(0x30100000).getDevice() instanceof CharacterDisplay);
		assertTrue(bus.getBusMapEntryForAddress(0x30200000).getDevice() instanceof Keyboard);
		assertNull(bus.getBusMapEntryForAddress(0x30300000));
		
		Keyboard keyboard = (Keyboard)(bus.getBusMapEntryForAddress(0x30200000).getDevice());
		assertEquals(-1, bus.getActiveInterrupt(0xFFFFFFFF));
		keyboard.write(0, BusAccessSize.WORD, 3);
		assertEquals(4, bus.getActiveInterrupt(0xFFFFFFFF));
	}

	/**
	 * 
	 */
	public void testMemoryVisualization() {
		assertNull(consoleContributor.getMemoryVisualizationContributor());
	}
	
}
