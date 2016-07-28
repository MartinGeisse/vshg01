/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusErrorException;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.bus.IBusMasterAccess;

/**
 * This is an {@link IBusMasterAccess} implementation that fakes the bus
 * and attached devices. This class is instantiated and a list of
 * read values stored. The cpu is then simulated. Any bus access is logged
 * in this object, including the address, access size, read/write mode,
 * and write data (for writes). Read values are de-queued from the preconfigured
 * list. Note that the write value log is typically shorter than the
 * bus access list since not every access is a write access.  
 * 
 * This class also supports simulation of interrupts and bus timeouts simply
 * by setting the desired behavior prior to the corresponding access.
 */
public class MockBus implements IBusMasterAccess {

	/**
	 * The logged bus addresses. This list is filled during simulation.
	 */
	private LinkedList<Integer> addresses;

	/**
	 * The logged bus write modes (true for write, false for read). This list is filled during simulation.
	 */
	private LinkedList<Boolean> writes;

	/**
	 * The logged bus access sizes. This list is filled during simulation.
	 */
	private LinkedList<BusAccessSize> accessSizes;

	/**
	 * The preconfigured read values. This list should be initialized before the mock bus is used.
	 */
	private LinkedList<Integer> readValues;

	/**
	 * The logged write values. This list is filled during simulation.
	 */
	private LinkedList<Integer> writeValues;

	/**
	 * The active interrupt index that is reported to the CPU.
	 */
	private int activeInterrupt;

	/**
	 * Whether the following bus cycles should cause a bus timeout.
	 */
	private boolean timeout;

	/**
	 * Constructor
	 */
	public MockBus() {
		this.addresses = new LinkedList<Integer>();
		this.writes = new LinkedList<Boolean>();
		this.accessSizes = new LinkedList<BusAccessSize>();
		this.readValues = new LinkedList<Integer>();
		this.writeValues = new LinkedList<Integer>();
		this.activeInterrupt = -1;
		this.timeout = false;
	}

	/**
	 * Adds one or more preconfigured read values.
	 * @param readValues the value(s) to add
	 */
	public void addReadValues(int... readValues) {
		for (int value : readValues) {
			this.readValues.add(value);
		}
	}
	
	/**
	 * Sets the activeInterrupt, i.e. the active interrupt reported to the CPU (-1 for none).
	 * @param activeInterrupt the new value to set
	 */
	public void setActiveInterrupt(int activeInterrupt) {
		this.activeInterrupt = activeInterrupt;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.IBusMasterAccess#getActiveInterrupt()
	 */
	@Override
	public int getActiveInterrupt(int mask) {
		if ((mask & (1 << activeInterrupt)) != 0) {
			return activeInterrupt;
		} else {
			return -1;
		}
	}

	/**
	 * @return Returns the timeout flag (true to signal bus timeout on following accesses, false for normal access)
	 */
	public boolean isTimeout() {
		return timeout;
	}

	/**
	 * Sets the timeout flag (true to signal bus timeout on following accesses, false for normal access).
	 * @param timeout the new value to set
	 */
	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.IBusMasterAccess#read(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public int read(int address, BusAccessSize size) throws BusErrorException {
		if (timeout) {
			throw new BusTimeoutException(address);
		} else {
			addresses.add(address);
			writes.add(false);
			accessSizes.add(size);
			return readValues.removeFirst();
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.IBusMasterAccess#write(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize, int)
	 */
	@Override
	public void write(int address, BusAccessSize size, int value) throws BusErrorException {
		if (timeout) {
			throw new BusTimeoutException(address);
		} else {
			addresses.add(address);
			writes.add(true);
			accessSizes.add(size);
			writeValues.add(value);
		}
	}
	
	/**
	 * Ensures that the address log contains the specified values.
	 * @param expectedAddresses the expected addresses
	 */
	public void assertAddresses(int... expectedAddresses) {
		for (int a : expectedAddresses) {
			assertEquals(a, (int)addresses.removeFirst());
		}
		assertTrue(addresses.isEmpty());
	}

	/**
	 * Ensures that the write mode log contains the specified values.
	 * @param expectedWrites the expected writes
	 */
	public void assertWrites(boolean... expectedWrites) {
		for (boolean w : expectedWrites) {
			assertEquals(w, (boolean)writes.removeFirst());
		}
		assertTrue(writes.isEmpty());
	}

	/**
	 * Ensures that the access size log contains the specified values.
	 * @param expectedAccessSizes the expected bus access sizes
	 */
	public void assertAccessSizes(BusAccessSize... expectedAccessSizes) {
		for (BusAccessSize s : expectedAccessSizes) {
			assertSame(s, accessSizes.removeFirst());
		}
		assertTrue(accessSizes.isEmpty());
	}

	/**
	 * Ensures that the write value log contains the specified values.
	 * @param expectedValues the expected write values
	 */
	public void assertWriteValues(int... expectedValues) {
		for (int v : expectedValues) {
			assertEquals(v, (int)writeValues.removeFirst());
		}
		assertTrue(writeValues.isEmpty());
	}
	
	/**
	 * Ensures that the list of read values is empty.
	 */
	public void assertAllRead() {
		assertTrue(readValues.isEmpty());
	}

}
