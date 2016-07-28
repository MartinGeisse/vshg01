/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 */
public class HexNumberUtilTest {

	/**
	 */
	@Test
	public void testUnsignedByteToString() {
		assertEquals("00", HexNumberUtil.unsignedByteToString(0));
		assertEquals("05", HexNumberUtil.unsignedByteToString(5));
		assertEquals("14", HexNumberUtil.unsignedByteToString(20));
		assertEquals("7f", HexNumberUtil.unsignedByteToString(0x7f));
		assertEquals("ff", HexNumberUtil.unsignedByteToString(-1));
	}

	/**
	 */
	@Test
	public void testUnsignedHalfwordToString() {
		assertEquals("0000", HexNumberUtil.unsignedHalfwordToString(0));
		assertEquals("0005", HexNumberUtil.unsignedHalfwordToString(5));
		assertEquals("0014", HexNumberUtil.unsignedHalfwordToString(20));
		assertEquals("0123", HexNumberUtil.unsignedHalfwordToString(0x123));
		assertEquals("7fff", HexNumberUtil.unsignedHalfwordToString(0x7fff));
		assertEquals("ffff", HexNumberUtil.unsignedHalfwordToString(-1));
	}

	/**
	 */
	@Test
	public void testUnsignedWordToString() {
		assertEquals("00000000", HexNumberUtil.unsignedWordToString(0));
		assertEquals("00000005", HexNumberUtil.unsignedWordToString(5));
		assertEquals("00000014", HexNumberUtil.unsignedWordToString(20));
		assertEquals("00000123", HexNumberUtil.unsignedWordToString(0x123));
		assertEquals("7fffffff", HexNumberUtil.unsignedWordToString(0x7fffffff));
		assertEquals("12345678", HexNumberUtil.unsignedWordToString(0x12345678));
		assertEquals("abcdef01", HexNumberUtil.unsignedWordToString(0xabcdef01));
		assertEquals("ffffffff", HexNumberUtil.unsignedWordToString(-1));
	}

}
