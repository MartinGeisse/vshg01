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
public class InterruptBusTest {

	/**
	 * 
	 */
	@Test
	public void test() {
		
		InterruptBus bus = new InterruptBus();
		assertEquals(-1, bus.getFirstActiveIndex(0xffff));
		assertEquals(-1, bus.getFirstActiveIndex(0x0400));
		assertEquals(-1, bus.getFirstActiveIndex(0x1000));
		assertEquals(-1, bus.getFirstActiveIndex(0x1f00));
		
		bus.getLine(10).setActive(true);
		assertEquals(10, bus.getFirstActiveIndex(0xffff));
		assertEquals(10, bus.getFirstActiveIndex(0x0400));
		assertEquals(-1, bus.getFirstActiveIndex(0x1000));
		assertEquals(10, bus.getFirstActiveIndex(0x1f00));

		bus.getLine(12).setActive(true);
		assertEquals(10, bus.getFirstActiveIndex(0xffff));
		assertEquals(10, bus.getFirstActiveIndex(0x0400));
		assertEquals(12, bus.getFirstActiveIndex(0x1000));
		assertEquals(10, bus.getFirstActiveIndex(0x1f00));

		bus.getLine(8).setActive(true);
		assertEquals(8, bus.getFirstActiveIndex(0xffff));
		assertEquals(10, bus.getFirstActiveIndex(0x0400));
		assertEquals(12, bus.getFirstActiveIndex(0x1000));
		assertEquals(8, bus.getFirstActiveIndex(0x1f00));

		bus.getLine(8).setActive(false);
		assertEquals(10, bus.getFirstActiveIndex(0xffff));
		assertEquals(10, bus.getFirstActiveIndex(0x0400));
		assertEquals(12, bus.getFirstActiveIndex(0x1000));
		assertEquals(10, bus.getFirstActiveIndex(0x1f00));

	}
}
