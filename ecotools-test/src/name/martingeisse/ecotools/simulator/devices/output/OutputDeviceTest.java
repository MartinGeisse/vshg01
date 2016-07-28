/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.output;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class OutputDeviceTest {

	/**
	 * the file to write to
	 */
	private static File file;

	/**
	 * the dev
	 */
	private OutputDevice dev;

	/**
	 * @throws IOException ...
	 */
	@Before
	public void setUp() throws IOException {
		file = new File("OutputDevice.bin");
		if (file.exists()) {
			file.delete();
		}
		dev = new OutputDevice(file);
	}
	
	/**
	 * 
	 */
	@After
	public void tearDown() {
		try {
			if (dev != null) {
				dev.dispose();
			}
		} catch (Exception e) {
		}
		if (file != null && file.exists()) {
			file.delete();
		}
		dev = null;
		file = null;
	}
	
	/**
	 * @throws IOException ...
	 */
	@Test
	public void testDeviceProperties() throws IOException {
		assertEquals(0, dev.getLocalAddressBitCount());
		assertEquals(0, dev.getInterruptLineCount());
	}
	
	/**
	 * @throws BusTimeoutException ...
	 * @throws IOException ...
	 */
	@Test
	public void test() throws BusTimeoutException, IOException {
		assertEquals(0, dev.readWord(0));
		assertEquals(0, dev.read(0, BusAccessSize.WORD));
		
		dev.writeWord(0, 42);
		assertEquals(0, dev.readWord(0));
		assertEquals(0, dev.read(0, BusAccessSize.WORD));
		
		dev.write(0, BusAccessSize.WORD, 99);
		assertEquals(0, dev.readWord(0));
		assertEquals(0, dev.read(0, BusAccessSize.WORD));
		
		dev.write(0, BusAccessSize.WORD, 0x12345678);
		assertEquals(0, dev.readWord(0));
		assertEquals(0, dev.read(0, BusAccessSize.WORD));
		
		dev.dispose();
		dev = null;
		
		FileInputStream fis = new FileInputStream(file);
		assertEquals(42, fis.read());
		assertEquals(99, fis.read());
		assertEquals(0x78, fis.read());
		assertEquals(-1, fis.read());
		fis.close();
		
	}

	/**
	 * @throws BusTimeoutException ...
	 * @throws IOException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteRead() throws BusTimeoutException, IOException {
		dev.read(0, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusTimeoutException ...
	 * @throws IOException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordRead() throws BusTimeoutException, IOException {
		dev.read(0, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusTimeoutException ...
	 * @throws IOException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteWrite() throws BusTimeoutException, IOException {
		dev.write(0, BusAccessSize.BYTE, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 * @throws IOException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordWrite() throws BusTimeoutException, IOException {
		dev.write(0, BusAccessSize.HALFWORD, 0);
	}

}
