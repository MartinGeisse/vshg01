/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.bus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class BusAccessSizeTest {

	/**
	 * 
	 */
	@Test
	public void testIsAligned() {
		assertTrue(BusAccessSize.BYTE.isAligned(0));
		assertTrue(BusAccessSize.BYTE.isAligned(1));
		assertTrue(BusAccessSize.BYTE.isAligned(2));
		assertTrue(BusAccessSize.BYTE.isAligned(3));
		assertTrue(BusAccessSize.BYTE.isAligned(4));
		assertTrue(BusAccessSize.BYTE.isAligned(5));
		assertTrue(BusAccessSize.BYTE.isAligned(6));
		assertTrue(BusAccessSize.BYTE.isAligned(7));
		assertTrue(BusAccessSize.HALFWORD.isAligned(0));
		assertFalse(BusAccessSize.HALFWORD.isAligned(1));
		assertTrue(BusAccessSize.HALFWORD.isAligned(2));
		assertFalse(BusAccessSize.HALFWORD.isAligned(3));
		assertTrue(BusAccessSize.HALFWORD.isAligned(4));
		assertFalse(BusAccessSize.HALFWORD.isAligned(5));
		assertTrue(BusAccessSize.HALFWORD.isAligned(6));
		assertFalse(BusAccessSize.HALFWORD.isAligned(7));
		assertTrue(BusAccessSize.WORD.isAligned(0));
		assertFalse(BusAccessSize.WORD.isAligned(1));
		assertFalse(BusAccessSize.WORD.isAligned(2));
		assertFalse(BusAccessSize.WORD.isAligned(3));
		assertTrue(BusAccessSize.WORD.isAligned(4));
		assertFalse(BusAccessSize.WORD.isAligned(5));
		assertFalse(BusAccessSize.WORD.isAligned(6));
		assertFalse(BusAccessSize.WORD.isAligned(7));
	}

	/**
	 * 
	 */
	@Test
	public void testGetByteCount() {
		assertEquals(1, BusAccessSize.BYTE.getByteCount());
		assertEquals(2, BusAccessSize.HALFWORD.getByteCount());
		assertEquals(4, BusAccessSize.WORD.getByteCount());
	}

	/**
	 * 
	 */
	@Test
	public void testGetValidBitMask() {
		assertEquals((1 << 8) - 1, BusAccessSize.BYTE.getValidBitMask());
		assertEquals((1 << 16) - 1, BusAccessSize.HALFWORD.getValidBitMask());
		assertEquals(-1, BusAccessSize.WORD.getValidBitMask());
	}

}
