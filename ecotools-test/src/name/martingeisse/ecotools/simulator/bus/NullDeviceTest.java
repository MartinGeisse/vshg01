/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.bus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class NullDeviceTest {

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void test() throws BusTimeoutException {
		
		NullDevice device = new NullDevice(8, 3);
		Bus bus = new Bus();
		bus.add(0x1000, device, new int[] {1, 8, 3});
		
		assertEquals(8, device.getLocalAddressBitCount());
		assertEquals(3, device.getInterruptLineCount());
		assertEquals(0, device.read(4, BusAccessSize.WORD));
		device.write(4, BusAccessSize.WORD, 13);
		assertEquals(0, device.read(4, BusAccessSize.WORD));
		device.tick();

	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testNonwordAccess() throws BusTimeoutException {
		NullDevice device = new NullDevice(8, 3);
		device.read(2, BusAccessSize.HALFWORD);
	}
	
}
