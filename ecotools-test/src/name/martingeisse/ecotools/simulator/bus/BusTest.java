/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.bus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * 
 */
public class BusTest {

	/**
	 * 
	 */
	@Test
	public void testBasic() {
		Bus bus = new Bus();
		bus.buildBusMap();
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testBuildMapTwice() {
		Bus bus = new Bus();
		bus.buildBusMap();
		bus.buildBusMap();
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testAddDeviceAfterBuildMap() {
		Bus bus = new Bus();
		bus.buildBusMap();
		bus.add(1024, new DummyDevice(5), new int[0]);
	}

	/**
	 * 
	 */
	@Test(expected = BusConfigurationException.class)
	public void testOverlap() {
		Bus bus = new Bus();
		bus.add(1024, new DummyDevice(5), new int[0]);
		bus.add(1040, new DummyDevice(2), new int[0]);
		bus.buildBusMap();
	}

	/**
	 * @throws Exception ...
	 */
	@Test
	public void testBasicAccess() throws Exception {
		Bus bus = new Bus();
		bus.add(1024, new DummyDevice(5), new int[0]);
		bus.buildBusMap();
		bus.write(1026, BusAccessSize.BYTE, 0);
		assertEquals(46, bus.read(1028, BusAccessSize.BYTE));		
	}

	/**
	 * @throws Exception ...
	 */
	@Test(expected = MisalignedBusAccessException.class)
	public void testMisalignedWrite() throws Exception {
		Bus bus = new Bus();
		bus.add(1024, new DummyDevice(5), new int[0]);
		bus.buildBusMap();
		bus.write(1027, BusAccessSize.HALFWORD, 0);
	}

	/**
	 * @throws Exception ...
	 */
	@Test(expected = MisalignedBusAccessException.class)
	public void testMisalignedRead() throws Exception {
		Bus bus = new Bus();
		bus.add(1024, new DummyDevice(5), new int[0]);
		bus.buildBusMap();
		bus.read(1027, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws Exception ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testWriteTimeout() throws Exception {
		Bus bus = new Bus();
		bus.add(1024, new DummyDevice(5), new int[0]);
		bus.buildBusMap();
		bus.write(1020, BusAccessSize.HALFWORD, 0);
	}

	/**
	 * @throws Exception ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testReadTimeout() throws Exception {
		Bus bus = new Bus();
		bus.add(1024, new DummyDevice(5), new int[0]);
		bus.buildBusMap();
		bus.read(1020, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws Exception ...
	 */
	@Test
	public void testDeviceMapping() throws Exception {
		DummyDevice device1 = new DummyDevice(5);
		DummyDevice device2 = new DummyDevice(5, 100);
		Bus bus = new Bus();
		bus.add(1024, device1, new int[0]);
		bus.add(2048, device2, new int[0]);
		bus.buildBusMap();
		assertEquals(44, bus.read(1026, BusAccessSize.BYTE));
		assertEquals(102, bus.read(2050, BusAccessSize.BYTE));
		assertSame(device1, bus.getBusMapEntryForAddress(1026).getDevice());
		assertSame(device2, bus.getBusMapEntryForAddress(2050).getDevice());
		assertNull(bus.getBusMapEntryForAddress(1000));
		assertNull(bus.getBusMapEntryForAddress(1100));
		assertNull(bus.getBusMapEntryForAddress(3000));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptBasic() {
		Bus bus = new Bus();
		bus.add(1024, new DummyInterruptDevice(), new int[] {5});
		bus.add(2048, new DummyInterruptDevice(), new int[] {6});
		bus.buildBusMap();
	}

	/**
	 * 
	 */
	@Test(expected = BusConfigurationException.class)
	public void testInterruptOverlap() {
		Bus bus = new Bus();
		bus.add(1024, new DummyInterruptDevice(), new int[] {5});
		bus.add(2048, new DummyInterruptDevice(), new int[] {5});
		bus.buildBusMap();
	}

	/**
	 * 
	 */
	@Test
	public void testConnectInterrupts() {
		DummyInterruptDevice device1 = new DummyInterruptDevice();
		DummyInterruptDevice device2 = new DummyInterruptDevice();
		Bus bus = new Bus();
		bus.add(1024, device1, new int[] {5});
		bus.add(2048, device2, new int[] {6});
		bus.buildBusMap();
		
		assertEquals(-1, bus.getActiveInterrupt(0x1409));
		assertEquals(-1, bus.getActiveInterrupt(0xffff));
		assertEquals(-1, bus.getActiveInterrupt(0x0020));
		assertEquals(-1, bus.getActiveInterrupt(0x0040));
		assertEquals(-1, bus.getActiveInterrupt(0xf12e));
		assertEquals(-1, bus.getActiveInterrupt(0x99c1));
		device2.setActive(true);
		assertEquals(-1, bus.getActiveInterrupt(0x1409));
		assertEquals(6, bus.getActiveInterrupt(0xffff));
		assertEquals(-1, bus.getActiveInterrupt(0x0020));
		assertEquals(6, bus.getActiveInterrupt(0x0040));
		assertEquals(-1, bus.getActiveInterrupt(0xf12e));
		assertEquals(6, bus.getActiveInterrupt(0x99c1));
		device1.setActive(true);
		assertEquals(-1, bus.getActiveInterrupt(0x1409));
		assertEquals(5, bus.getActiveInterrupt(0xffff));
		assertEquals(5, bus.getActiveInterrupt(0x0020));
		assertEquals(6, bus.getActiveInterrupt(0x0040));
		assertEquals(5, bus.getActiveInterrupt(0xf12e));
		assertEquals(6, bus.getActiveInterrupt(0x99c1));

	}

	/**
	 * 
	 */
	@Test
	public void testTick() {
		DummyDevice device = new DummyDevice(5);
		Bus bus = new Bus();
		bus.add(1024, device, new int[0]);
		bus.buildBusMap();

		assertEquals(0, device.getTickCount());
		bus.tick();
		assertEquals(1, device.getTickCount());
	}
}
