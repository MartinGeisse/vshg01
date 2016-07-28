/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.DummyDevice;
import name.martingeisse.ecotools.simulator.bus.IPeripheralDevice;
import name.martingeisse.ecotools.simulator.cpu.Cpu;
import name.martingeisse.ecotools.simulator.devices.memory.Ram;
import name.martingeisse.ecotools.simulator.ui.util.memory.DefaultMemoryVisualizationStorageStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class GlobalMemoryVisualizationStorageStrategyTest {

	/**
	 * the strategy
	 */
	private MainMemoryGlobalVisualizationStorageStrategy freshStrategy;

	/**
	 * the strategy
	 */
	private MainMemoryGlobalVisualizationStorageStrategy configuredStrategy;

	/**
	 * the cpu
	 */
	private Cpu cpu;

	/**
	 * the bus
	 */
	private Bus bus;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		freshStrategy = new MainMemoryGlobalVisualizationStorageStrategy();
		configuredStrategy = new MainMemoryGlobalVisualizationStorageStrategy();
		cpu = new Cpu();
		configuredStrategy.setCpu(cpu);
		bus = new Bus();
		configuredStrategy.setBus(bus);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
	}

	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		assertNull(freshStrategy.getCpu());
		assertNull(freshStrategy.getBus());

		freshStrategy.setCpu(cpu);
		assertSame(cpu, freshStrategy.getCpu());

		freshStrategy.setBus(bus);
		assertSame(bus, freshStrategy.getBus());
	}

	/**
	 * 
	 */
	@Test
	public void testSetNullStrategy() {
		IPeripheralDevice device = new DummyDevice(5);
		freshStrategy.addDeviceStrategy(device, null);
	}

	/**
	 * 
	 */
	@Test
	public void testSetActualStrategy() {
		IPeripheralDevice device = new DummyDevice(5);
		IMemoryVisualizationStorageStrategy deviceStrategy = new DefaultMemoryVisualizationStorageStrategy(new Ram(2));
		freshStrategy.addDeviceStrategy(device, deviceStrategy);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testSetTwoDifferentStrategiesForSameDevice() {
		IPeripheralDevice device = new DummyDevice(5);
		IMemoryVisualizationStorageStrategy deviceStrategy1 = new DefaultMemoryVisualizationStorageStrategy(new Ram(2));
		IMemoryVisualizationStorageStrategy deviceStrategy2 = new DefaultMemoryVisualizationStorageStrategy(new Ram(2));
		freshStrategy.addDeviceStrategy(device, deviceStrategy1);
		freshStrategy.addDeviceStrategy(device, deviceStrategy2);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testSetSameStrategyTwice() {
		IPeripheralDevice device = new DummyDevice(5);
		IMemoryVisualizationStorageStrategy deviceStrategy = new DefaultMemoryVisualizationStorageStrategy(new Ram(2));
		freshStrategy.addDeviceStrategy(device, deviceStrategy);
		freshStrategy.addDeviceStrategy(device, deviceStrategy);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testSetStrategyAndNull() {
		IPeripheralDevice device = new DummyDevice(5);
		IMemoryVisualizationStorageStrategy deviceStrategy = new DefaultMemoryVisualizationStorageStrategy(new Ram(2));
		freshStrategy.addDeviceStrategy(device, deviceStrategy);
		freshStrategy.addDeviceStrategy(device, null);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testSetNullTwice() {
		IPeripheralDevice device = new DummyDevice(5);
		freshStrategy.addDeviceStrategy(device, null);
		freshStrategy.addDeviceStrategy(device, null);
	}

	/**
	 * Asserts that all needles can be found in the exception message of the specified exception (the haystack).
	 * @param e the exception to check
	 * @param needles the needles to look for
	 */
	private void assertExceptionMessage(MemoryVisualizationException e, String... needles) {
		assertNotNull(e);
		for (String needle : needles) {
			if (e.getMessage().indexOf(needle) == -1) {
				fail("could not find expected part of the MemoryVisualizationException: " + needle + ", message: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 */
	@Test
	public void testReadWithoutDevice() {
		try {
			bus.buildBusMap();
			configuredStrategy.read(0, BusAccessSize.WORD);
			fail();
		} catch (MemoryVisualizationException e) {
			assertExceptionMessage(e, "no device");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testReadMissDevice() {
		DummyDevice device = new DummyDevice(5);
		bus.add(1024, device, new int[] {});
		try {
			bus.buildBusMap();
			configuredStrategy.read(0, BusAccessSize.WORD);
			fail();
		} catch (MemoryVisualizationException e) {
			assertExceptionMessage(e, "no device");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testReadBarelyMissDeviceLow() {
		DummyDevice device = new DummyDevice(5);
		bus.add(1024, device, new int[] {});
		try {
			bus.buildBusMap();
			configuredStrategy.read(1023, BusAccessSize.BYTE);
			fail();
		} catch (MemoryVisualizationException e) {
			assertExceptionMessage(e, "no device");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testReadBarelyMissDeviceHigh() {
		DummyDevice device = new DummyDevice(5);
		bus.add(1056, device, new int[] {});
		try {
			bus.buildBusMap();
			configuredStrategy.read(1023, BusAccessSize.BYTE);
			fail();
		} catch (MemoryVisualizationException e) {
			assertExceptionMessage(e, "no device");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testReadNoStrategyDevice() {
		DummyDevice device = new DummyDevice(5);
		bus.add(1024, device, new int[] {});
		try {
			bus.buildBusMap();
			configuredStrategy.read(1032, BusAccessSize.WORD);
			fail();
		} catch (MemoryVisualizationException e) {
			assertExceptionMessage(e, "device", "DummyDevice");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testReadNullStrategyDevice() {
		DummyDevice device = new DummyDevice(5);
		bus.add(1024, device, new int[] {});
		try {
			bus.buildBusMap();
			configuredStrategy.addDeviceStrategy(device, null);
			configuredStrategy.read(1032, BusAccessSize.WORD);
			fail();
		} catch (MemoryVisualizationException e) {
			assertExceptionMessage(e, "device", "DummyDevice");
		}
	}

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test
	public void testStrategyDevice() throws MemoryVisualizationException {
		DummyDevice device = new DummyDevice(5);
		bus.add(1024, device, new int[] {});
		bus.buildBusMap();
		configuredStrategy.addDeviceStrategy(device, new MyDeviceStrategy());
		assertEquals(108, configuredStrategy.read(1032, BusAccessSize.WORD));
	}

	/**
	 * 
	 */
	private static class MyDeviceStrategy implements IMemoryVisualizationStorageStrategy {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy#read(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
		 */
		@Override
		public int read(int address, BusAccessSize accessSize) throws MemoryVisualizationException {
			return 100 + address;
		}

	}

}
