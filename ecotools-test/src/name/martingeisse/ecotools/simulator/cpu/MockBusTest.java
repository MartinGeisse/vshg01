/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusErrorException;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the mock bus implementation, which is itself used
 * to test the CPU in further test cases.
 */
public class MockBusTest {

	/**
	 * the mockBus
	 */
	private MockBus mockBus;
	
	/**
	 * 
	 */
	@Before
	public void setUp() {
		mockBus = new MockBus();
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
	public void testInitialState() {
		assertEquals(-1, mockBus.getActiveInterrupt(0xffff));
		assertFalse(mockBus.isTimeout());
		mockBus.assertAddresses();
		mockBus.assertAccessSizes();
		mockBus.assertWrites();
		mockBus.assertWriteValues();
		mockBus.assertAllRead();
	}
	
	/**
	 * 
	 */
	@Test(expected = NoSuchElementException.class)
	public void testNoInitialAddress() {
		mockBus.assertAddresses(0);
	}

	/**
	 * 
	 */
	@Test(expected = NoSuchElementException.class)
	public void testNoInitialAccessSize() {
		mockBus.assertAccessSizes(BusAccessSize.WORD);
	}

	/**
	 * 
	 */
	@Test(expected = NoSuchElementException.class)
	public void testNoInitialWrites() {
		mockBus.assertWrites(false);
	}

	/**
	 * 
	 */
	@Test(expected = NoSuchElementException.class)
	public void testNoInitialWriteValues() {
		mockBus.assertWriteValues(0);
	}

	/**
	 * 
	 */
	private void performWrites() throws BusErrorException {
		mockBus.write(123, BusAccessSize.WORD, 44);
		mockBus.write(2, BusAccessSize.HALFWORD, 55);
		mockBus.write(2, BusAccessSize.BYTE, 66);
		mockBus.write(3, BusAccessSize.HALFWORD, 77);
	}
	
	/**
	 * @throws BusErrorException ...
	 */
	@Test
	public void testWriteAndCheckCorrect() throws BusErrorException {
		performWrites();
		mockBus.assertAddresses(123, 2, 2, 3);
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.BYTE, BusAccessSize.HALFWORD);
		mockBus.assertWrites(true, true, true, true);
		mockBus.assertWriteValues(44, 55, 66, 77);
		mockBus.assertAllRead();
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testWriteAndCheckMissingAddress() throws BusErrorException {
		performWrites();
		mockBus.assertAddresses(123, 2, 2, 3, 3);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckWrongAddress() throws BusErrorException {
		performWrites();
		mockBus.assertAddresses(123, 2, 2, 4);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckExcessAddress() throws BusErrorException {
		performWrites();
		mockBus.assertAddresses(123, 2, 2);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testWriteAndCheckMissingAccessSize() throws BusErrorException {
		performWrites();
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.BYTE, BusAccessSize.HALFWORD, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckWrongAccessSize() throws BusErrorException {
		performWrites();
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.BYTE, BusAccessSize.WORD);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckExcessAccessSize() throws BusErrorException {
		performWrites();
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testWriteAndCheckMissingWriteMode() throws BusErrorException {
		performWrites();
		mockBus.assertWrites(true, true, true, true, true);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckWrongWriteMode() throws BusErrorException {
		performWrites();
		mockBus.assertWrites(true, true, true, false);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckExcessWriteMode() throws BusErrorException {
		performWrites();
		mockBus.assertWrites(true, true, true);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testWriteAndCheckMissingWriteValue() throws BusErrorException {
		performWrites();
		mockBus.assertWriteValues(44, 55, 66, 77, 88);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckWrongWriteValue() throws BusErrorException {
		performWrites();
		mockBus.assertWriteValues(44, 55, 66, 88);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testWriteAndCheckExcessWriteValue() throws BusErrorException {
		performWrites();
		mockBus.assertWriteValues(44, 55, 66);
	}
	
	/**
	 * 
	 */
	private void performReads() throws BusErrorException {
		assertEquals(12, mockBus.read(59, BusAccessSize.WORD));
		assertEquals(34, mockBus.read(101010, BusAccessSize.HALFWORD));
		assertEquals(56, mockBus.read(203040, BusAccessSize.BYTE));
	}
	
	/**
	 * @throws BusErrorException ...
	 */
	@Test
	public void testReadAndCheckCorrect() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertAddresses(59, 101010, 203040);
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.BYTE);
		mockBus.assertWrites(false, false, false);
		mockBus.assertWriteValues();
		mockBus.assertAllRead();
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testReadAndCheckReadTooFew() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56, 78);
		performReads();
		mockBus.assertAllRead();
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testReadAndCheckReadTooMany() throws BusErrorException {
		mockBus.addReadValues(12, 34);
		performReads();
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testReadAndCheckReadMissingAddress() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertAddresses(59, 101010, 203040, 1);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testReadAndCheckReadWrongAddress() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertAddresses(59, 101010, 203041, 1);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testReadAndCheckReadExcessAddress() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertAddresses(59, 101010);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testReadAndCheckReadMissingWriteMode() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertWrites(false, false, false, false);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testReadAndCheckReadWrongWriteMode() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertWrites(false, false, true);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testReadAndCheckReadExcessWriteMode() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertWrites(false, false);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testReadAndCheckReadMissingAccessSize() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.BYTE, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testReadAndCheckReadWrongAccessSize() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = AssertionError.class)
	public void testReadAndCheckReadExcessAccessSize() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = NoSuchElementException.class)
	public void testReadAndCheckNoWriteValues() throws BusErrorException {
		mockBus.addReadValues(12, 34, 56);
		performReads();
		mockBus.assertWriteValues(0);
	}
	
	/**
	 * @throws BusErrorException ...
	 */
	@Test
	public void testMixedAccesses() throws BusErrorException {
		mockBus.addReadValues(55, 777, 9999);
		assertEquals(55, mockBus.read(123, BusAccessSize.WORD));
		mockBus.write(9191, BusAccessSize.BYTE, 99);
		mockBus.write(922922, BusAccessSize.WORD, 100);
		assertEquals(777, mockBus.read(1000, BusAccessSize.WORD));
		assertEquals(9999, mockBus.read(1000, BusAccessSize.HALFWORD));
		mockBus.write(12345, BusAccessSize.HALFWORD, 6789);
		
		mockBus.assertAddresses(123, 9191, 922922, 1000, 1000, 12345);
		mockBus.assertWrites(false, true, true, false, false, true);
		mockBus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.BYTE, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.HALFWORD);
		mockBus.assertWriteValues(99, 100, 6789);
		mockBus.assertAllRead();
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testReadTimeout() throws BusErrorException {
		mockBus.setTimeout(true);
		mockBus.read(0, BusAccessSize.WORD);
		assertTrue(mockBus.isTimeout());
	}

	/**
	 * @throws BusErrorException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testWriteTimeout() throws BusErrorException {
		mockBus.setTimeout(true);
		mockBus.write(0, BusAccessSize.WORD, 0);
		assertTrue(mockBus.isTimeout());
	}
	
	
	/**
	 * 
	 */
	@Test
	public void testInterrupts() {
		assertEquals(-1, mockBus.getActiveInterrupt(0xffff));
		mockBus.setActiveInterrupt(5);
		assertEquals(5, mockBus.getActiveInterrupt(0xffff));
		assertEquals(5, mockBus.getActiveInterrupt(0x0020));
		assertEquals(-1, mockBus.getActiveInterrupt(0xffdf));
	}

}
