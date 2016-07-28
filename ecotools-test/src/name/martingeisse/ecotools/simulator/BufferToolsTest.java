/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * 
 */
public class BufferToolsTest {

	/**
	 * 
	 */
	@Test
	public void testWrite8() {
		byte[] buffer = new byte[8];
		BufferTools.write8(buffer, 3, 10);
		BufferTools.write8(buffer, 4, 20);
		BufferTools.write8(buffer, 7, 30);
		BufferTools.write8(buffer, 4, 40);
		assertTrue(Arrays.equals(new byte[] {0, 0, 0, 10, 40, 0, 0, 30}, buffer));
	}
	
	/**
	 * 
	 */
	@Test
	public void testReadSigned8() {
		byte[] buffer = new byte[] {0, 1, 2, -1, -2};
		assertEquals(0, BufferTools.readSigned8(buffer, 0));
		assertEquals(1, BufferTools.readSigned8(buffer, 1));
		assertEquals(2, BufferTools.readSigned8(buffer, 2));
		assertEquals(-1, BufferTools.readSigned8(buffer, 3));
		assertEquals(-2, BufferTools.readSigned8(buffer, 4));
	}

	/**
	 * 
	 */
	@Test
	public void testReadUnsigned8() {
		byte[] buffer = new byte[] {0, 1, 2, -1, -2};
		assertEquals(0, BufferTools.readUnsigned8(buffer, 0));
		assertEquals(1, BufferTools.readUnsigned8(buffer, 1));
		assertEquals(2, BufferTools.readUnsigned8(buffer, 2));
		assertEquals(255, BufferTools.readUnsigned8(buffer, 3));
		assertEquals(254, BufferTools.readUnsigned8(buffer, 4));
	}

	/**
	 * 
	 */
	@Test
	public void testReadBigEndian32() {
		byte[] buffer = new byte[] {0x12, 0x34, 0x56, 0x78, (byte)0x9a, (byte)0xbc, (byte)0xde, (byte)0xf0};
		assertEquals(0x12345678, BufferTools.readBigEndian32(buffer, 0));
		assertEquals(0x3456789a, BufferTools.readBigEndian32(buffer, 1));
		assertEquals(0x56789abc, BufferTools.readBigEndian32(buffer, 2));
		assertEquals(0x789abcde, BufferTools.readBigEndian32(buffer, 3));
	}

	/**
	 * 
	 */
	@Test
	public void testReadLittleEndian32() {
		byte[] buffer = new byte[] {0x12, 0x34, 0x56, 0x78, (byte)0x9a, (byte)0xbc, (byte)0xde, (byte)0xf0};
		assertEquals(0x78563412, BufferTools.readLittleEndian32(buffer, 0));
		assertEquals(0x9a785634, BufferTools.readLittleEndian32(buffer, 1));
		assertEquals(0xbc9a7856, BufferTools.readLittleEndian32(buffer, 2));
		assertEquals(0xdebc9a78, BufferTools.readLittleEndian32(buffer, 3));
	}
	
	/**
	 * 
	 */
	@Test
	public void testWriteBigEndian32() {
		byte[] buffer = new byte[8];
		BufferTools.writeBigEndian32(buffer, 2, 0x12345678);
		assertTrue(Arrays.equals(new byte[] {0, 0, 0x12, 0x34, 0x56, 0x78, 0, 0}, buffer));
		BufferTools.writeBigEndian32(buffer, 2, -2);
		assertTrue(Arrays.equals(new byte[] {0, 0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xfe, 0, 0}, buffer));
	}

	/**
	 * 
	 */
	@Test
	public void testWriteLittleEndian32() {
		byte[] buffer = new byte[8];
		BufferTools.writeLittleEndian32(buffer, 2, 0x12345678);
		assertTrue(Arrays.equals(new byte[] {0, 0, 0x78, 0x56, 0x34, 0x12, 0, 0}, buffer));
		BufferTools.writeLittleEndian32(buffer, 2, -2);
		assertTrue(Arrays.equals(new byte[] {0, 0, (byte)0xfe, (byte)0xff, (byte)0xff, (byte)0xff, 0, 0}, buffer));
	}

}
