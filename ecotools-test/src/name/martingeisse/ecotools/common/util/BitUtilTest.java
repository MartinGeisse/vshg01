/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 */
public class BitUtilTest {

	/**
	 * 
	 */
	@Test
	public void testRevertBitByteOrder() {
		assertEquals(0, BitUtil.revertByteBitOrder(0));
		assertEquals(0xff, BitUtil.revertByteBitOrder(0xff));
		assertEquals(0xff, BitUtil.revertByteBitOrder(0xffffffff));
		assertEquals(0x80, BitUtil.revertByteBitOrder(0x01));
		assertEquals(0x01, BitUtil.revertByteBitOrder(0x80));
		assertEquals(0x41, BitUtil.revertByteBitOrder(0x82));
	}
	
}
