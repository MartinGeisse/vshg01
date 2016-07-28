/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.bus.IInterruptLine;

import org.junit.Test;

/**
 * 
 */
public class RamTest {

	/**
	 * 
	 */
	@Test
	public void testSize() {
		Ram ram = new Ram(8);
		assertEquals(256, ram.getContents().length);
		assertEquals(8, ram.getLocalAddressBitCount());
	}
	
	/**
	 * 
	 */
	@Test
	public void testNoInterrupts() {
		Ram ram = new Ram(8);
		assertEquals(0, ram.getInterruptLineCount());
		ram.connectInterruptLines(new IInterruptLine[0]);
	}
	
	/**
	 * 
	 */
	@Test
	public void testReadByte() {
		Ram ram = createCountedRam8();
		assertEquals(0x11, ram.readByte(0));
		assertEquals(0x22, ram.readByte(1));
		assertEquals(0x33, ram.readByte(2));
		assertEquals(0x44, ram.readByte(3));
		assertEquals(0x55, ram.readByte(4));
		assertEquals(0x66, ram.readByte(5));
		assertEquals(0x77, ram.readByte(6));
		assertEquals(0x88, ram.readByte(7));
	}

	/**
	 * 
	 */
	@Test
	public void testWriteByte() {
		Ram ram = new Ram(5);
		ram.writeByte(0, 0x31);
		ram.writeByte(1, 0x32);
		ram.writeByte(2, 0x33);
		ram.writeByte(3, 0x34);
		ram.writeByte(4, 0x35);
		ram.writeByte(5, 0x36);
		ram.writeByte(6, 0x37);
		ram.writeByte(7, 0x38);
		byte[] expected = new byte[] {(byte)0x31, (byte)0x32, (byte)0x33, (byte)0x34, (byte)0x35, (byte)0x36, (byte)0x37, (byte)0x38};
		assertTrue(Arrays.equals(expected, Arrays.copyOf(ram.getContents(), 8)));
	}

	/**
	 * 
	 */
	@Test
	public void testClearContents() {
		Ram ram = createCountedRam8();
		ram.clearContents();
		assertTrue(Arrays.equals(new byte[32], ram.getContents()));
	}
	
	/**
	 * 
	 */
	@Test
	public void testRandomizeContents() {
		Ram ram = createCountedRam8();
		ram.randomizeContents();
		byte[] contents = ram.getContents();
		
		int matches = 0;
		int zeroes = 0;
		for (int i=0; i<contents.length; i++) {
			for (int j=0; j<contents.length; j++) {
				if (contents[i] == contents[j]) {
					matches++;
				}
			}
			if (contents[i] == 0) {
				zeroes++;
			}
		}
		
		assertTrue(matches < 100);
		assertTrue(zeroes < 5);
	}

	/**
	 * @throws IOException ...
	 */
	@Test(expected = IOException.class)
	public void testReadContentsFromNonexistentFile() throws IOException {
		Ram ram = createCountedRam8();
		File file = new File("src/name/martingeisse/ecotools/simulator/devices/memory/nonexistent.bin");
		ram.readContentsFromFile(file);
	}

	/**
	 * @throws IOException ...
	 */
	@Test
	public void testReadContentsFromFile() throws IOException {
		Ram ram = createCountedRam8();
		File file = new File("src/name/martingeisse/ecotools/simulator/devices/memory/testram.bin");
		ram.readContentsFromFile(file);
		byte[] expected = new byte[] {(byte)0x15, (byte)0x77, (byte)0xb1, (byte)0x9f};
		byte[] actual = Arrays.copyOf(ram.getContents(), 4);
		assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testRead() throws BusTimeoutException {
		Ram ram = createCountedRam8();
		assertEquals(0x00000011, ram.read(0, BusAccessSize.BYTE));
		assertEquals(0x00000022, ram.read(1, BusAccessSize.BYTE));
		assertEquals(0x00000033, ram.read(2, BusAccessSize.BYTE));
		assertEquals(0x00000044, ram.read(3, BusAccessSize.BYTE));
		assertEquals(0x00000055, ram.read(4, BusAccessSize.BYTE));
		assertEquals(0x00000066, ram.read(5, BusAccessSize.BYTE));
		assertEquals(0x00000077, ram.read(6, BusAccessSize.BYTE));
		assertEquals(0x00000088, ram.read(7, BusAccessSize.BYTE));
		assertEquals(0x00001122, ram.read(0, BusAccessSize.HALFWORD));
		assertEquals(0x00003344, ram.read(2, BusAccessSize.HALFWORD));
		assertEquals(0x00005566, ram.read(4, BusAccessSize.HALFWORD));
		assertEquals(0x00007788, ram.read(6, BusAccessSize.HALFWORD));
		assertEquals(0x11223344, ram.read(0, BusAccessSize.WORD));
		assertEquals(0x55667788, ram.read(4, BusAccessSize.WORD));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWrite() throws BusTimeoutException {
		Ram ram = new Ram(5);

		ram.write(0, BusAccessSize.WORD, 0x12345678);
		ram.write(4, BusAccessSize.WORD, 0x1a1b1c1d);
		byte[] expected1 = new byte[] {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x1a, (byte)0x1b, (byte)0x1c, (byte)0x1d};
		byte[] actual1 = Arrays.copyOf(ram.getContents(), 8);
		assertTrue(Arrays.equals(expected1, actual1));

		ram.write(0, BusAccessSize.HALFWORD, 0xabcd);
		ram.write(6, BusAccessSize.HALFWORD, 0x9876);
		byte[] expected2 = new byte[] {(byte)0xab, (byte)0xcd, (byte)0x56, (byte)0x78, (byte)0x1a, (byte)0x1b, (byte)0x98, (byte)0x76};
		byte[] actual2 = Arrays.copyOf(ram.getContents(), 8);
		assertTrue(Arrays.equals(expected2, actual2));

		ram.write(0, BusAccessSize.BYTE, 0x4a);
		ram.write(3, BusAccessSize.BYTE, 0x4b);
		ram.write(5, BusAccessSize.BYTE, 0x4c);
		ram.write(6, BusAccessSize.BYTE, 0x4d);
		byte[] expected3 = new byte[] {(byte)0x4a, (byte)0xcd, (byte)0x56, (byte)0x4b, (byte)0x1a, (byte)0x4c, (byte)0x4d, (byte)0x76};
		byte[] actual3 = Arrays.copyOf(ram.getContents(), 8);
		assertTrue(Arrays.equals(expected3, actual3));
		
	}
	
	/**
	 * @param localAddressBitCount
	 * @param contents
	 * @return
	 */
	private static Ram createRam(int localAddressBitCount, byte... contents) {
		Ram ram = new Ram(localAddressBitCount);
		System.arraycopy(contents, 0, ram.getContents(), 0, contents.length);
		return ram;
	}

	/**
	 * @param localAddressBitCount
	 * @param contents
	 * @return
	 */
	private static Ram createCountedRam8() {
		return createRam(5, (byte)0x11, (byte)0x22, (byte)0x33, (byte)0x44, (byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88);
	}
	
}
