/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;

import org.junit.Test;

/**
 * Note: This class assumes that RamTest passes, and test only the
 * specifics of ROMs compared to RAMs.
 */
public class RomTest {

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testReadOnly() throws BusTimeoutException {
		Rom rom = new Rom(5);
		rom.write(4, BusAccessSize.BYTE, 0x11);
	}
	
}
