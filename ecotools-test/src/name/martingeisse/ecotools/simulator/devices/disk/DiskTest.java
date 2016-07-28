/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.disk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecotools.simulator.BufferTools;
import name.martingeisse.ecotools.simulator.SimulatorUtils;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.bus.DefaultInterruptLine;
import name.martingeisse.ecotools.simulator.bus.IInterruptLine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class DiskTest {

	/**
	 * the disk
	 */
	private Disk disk;
	
	/**
	 * the interruptLine
	 */
	private DefaultInterruptLine interruptLine;
	
	/**
	 * @throws IOException ...
	 */
	@Before
	public void setUp() throws IOException {
		File file = new File("disk.bin");
		disk = new Disk(file);
		interruptLine = new DefaultInterruptLine();
		disk.connectInterruptLines(new IInterruptLine[] {interruptLine});
		assertSame(file, disk.getFile());
	}
	
	/**
	 * 
	 */
	@After
	public void tearDown() {
		disk.dispose();
	}
	
	/**
	 * 
	 */
	@Test
	public void testDeviceProperties() {
		assertEquals(20, disk.getLocalAddressBitCount());
		assertEquals(1, disk.getInterruptLineCount());
	}

	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(8 * Disk.SECTOR_SIZE, disk.getBuffer().length);
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());

		disk.setInterruptEnable(true);
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertTrue(disk.isInterruptEnable());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());

		disk.setDone(true);
		assertFalse(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isInterruptEnable());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertTrue(interruptLine.isActive());

		disk.setInterruptEnable(false);
		assertFalse(disk.isReady());
		assertTrue(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());

		disk.setDone(false);
		disk.setError(true);
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertTrue(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());

		disk.setWrite(true);
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertTrue(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());

		disk.setSector(0x12345678);
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertTrue(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0x12345678, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());

		disk.setSectorCount(0x1a2b3c4d);
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertTrue(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0x12345678, disk.getSector());
		assertEquals(0x1a2b3c4d, disk.getSectorCount());
		assertFalse(interruptLine.isActive());

	}
	
	/**
	 * 
	 */
	@Test
	public void testDiskInitialization() {
		
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY - 1);
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());
		
		disk.tick();
		assertTrue(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(65536, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());
		
	}
	
	/**
	 * 
	 */
	@Test
	public void testStartBeforeInitializationComplete() {
		
		disk.setControlRegisterValue(1);
		assertFalse(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertTrue(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());
		
		SimulatorUtils.multiTick(disk, 2 * Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isInterruptEnable());
		assertTrue(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(65536, disk.getCapacity());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());
		assertFalse(interruptLine.isActive());
		
	}
	
	/**
	 * This method tests loading prepared data in the test file.
	 */
	@Test
	public void testLoad() {

		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(0, disk.getSectorCount());

		disk.setSector(0);
		disk.setSectorCount(1);
		assertTrue(disk.isReady());
		assertFalse(disk.isDone());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(1, disk.getSectorCount());

		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(1, disk.getSectorCount());
		assertEquals(0, BufferTools.readBigEndian32(disk.getBuffer(), 0x600));
		assertEquals(0, BufferTools.readBigEndian32(disk.getBuffer(), 0x604));

		disk.setSector(0);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(4, disk.getSectorCount());
		assertEquals(0x19283746, BufferTools.readBigEndian32(disk.getBuffer(), 0x600));
		assertEquals(0x55667788, BufferTools.readBigEndian32(disk.getBuffer(), 0x604));

		disk.setSector(2);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(2, disk.getSector());
		assertEquals(4, disk.getSectorCount());
		assertEquals(0x19283746, BufferTools.readBigEndian32(disk.getBuffer(), 0x200));
		assertEquals(0x55667788, BufferTools.readBigEndian32(disk.getBuffer(), 0x204));

	}
	
	/**
	 * This method relies on "load" operations working.
	 */
	@Test
	public void testSaveThenLoad() {
		
		/** prepare data at buffer sector 1 **/
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(20);
		disk.setSectorCount(4);
		BufferTools.writeBigEndian32(disk.getBuffer(), 0x200, 0x12abcdef);

		/** save data to sector 21 (20+1) **/
		disk.setControlRegisterValue(5);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertFalse(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(20, disk.getSector());
		assertEquals(4, disk.getSectorCount());

		/** load data from sector 21 (19+2) **/
		disk.setSector(19);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(1);
		assertEquals(0, BufferTools.readBigEndian32(disk.getBuffer(), 0x400));
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(0x12abcdef, BufferTools.readBigEndian32(disk.getBuffer(), 0x400));

		/** save data to sector 21 (19+2) **/
		BufferTools.writeBigEndian32(disk.getBuffer(), 0x400, 0x45456767);
		disk.setControlRegisterValue(5);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(0x45456767, BufferTools.readBigEndian32(disk.getBuffer(), 0x400));
		
		/** load data from sector 21 (20+1) **/
		disk.setSector(20);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(1);
		assertEquals(0, BufferTools.readBigEndian32(disk.getBuffer(), 0x200));
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(0x45456767, BufferTools.readBigEndian32(disk.getBuffer(), 0x200));
		
	}
	
	/**
	 * 
	 */
	@Test
	public void testNegativeSectorLoad() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(-1);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(-1, disk.getSector());
		assertEquals(4, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testNegativeSectorSave() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(-1);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(5);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(-1, disk.getSector());
		assertEquals(4, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testNegativeSectorCountLoad() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(5);
		disk.setSectorCount(-1);
		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(5, disk.getSector());
		assertEquals(-1, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testNegativeSectorCountSave() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(5);
		disk.setSectorCount(-1);
		disk.setControlRegisterValue(5);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(5, disk.getSector());
		assertEquals(-1, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testSectorOverflowCountLoad() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(127);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(127, disk.getSector());
		assertEquals(4, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testSectorOverflowCountSave() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(127);
		disk.setSectorCount(4);
		disk.setControlRegisterValue(5);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(127, disk.getSector());
		assertEquals(4, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testMaxAllowedLoadSectorCountOkay() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(0);
		disk.setSectorCount(8);
		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertFalse(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(8, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testMaxAllowedSaveSectorCountOkay() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(0);
		disk.setSectorCount(8);
		disk.setControlRegisterValue(5);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertFalse(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(8, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testMaxAllowedLoadSectorCountError() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(0);
		disk.setSectorCount(9);
		disk.setControlRegisterValue(1);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertFalse(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(9, disk.getSectorCount());
	}

	/**
	 * 
	 */
	@Test
	public void testMaxAllowedSaveSectorCountError() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		disk.setSector(0);
		disk.setSectorCount(9);
		disk.setControlRegisterValue(5);
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertTrue(disk.isReady());
		assertTrue(disk.isDone());
		assertTrue(disk.isError());
		assertTrue(disk.isWrite());
		assertEquals(0, disk.getSector());
		assertEquals(9, disk.getSectorCount());
	}
	
	/**
	 * 
	 */
	@Test
	public void testSetControlRegisterValue() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(32, disk.getControlRegisterValue());
		disk.setControlRegisterValue(1);
		assertEquals(33, disk.getControlRegisterValue());
		disk.setControlRegisterValue(1);
		assertEquals(56, disk.getControlRegisterValue());
		disk.setControlRegisterValue(1);
		assertEquals(33, disk.getControlRegisterValue());
		disk.setControlRegisterValue(1);
		assertEquals(56, disk.getControlRegisterValue());
		disk.setControlRegisterValue(0xfffffffe);
		assertEquals(62, disk.getControlRegisterValue());
		disk.setControlRegisterValue(0xfffffffe);
		assertEquals(62, disk.getControlRegisterValue());
		disk.setControlRegisterValue(0xffffffff);
		assertEquals(39, disk.getControlRegisterValue());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetControlRegisterValue() {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(32, disk.getControlRegisterValue());
		disk.setInterruptEnable(true);
		assertEquals(34, disk.getControlRegisterValue());
		disk.setWrite(true);
		assertEquals(38, disk.getControlRegisterValue());
		disk.setError(true);
		assertEquals(46, disk.getControlRegisterValue());
		disk.setDone(true);
		assertEquals(62, disk.getControlRegisterValue());
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteRead() throws BusTimeoutException {
		disk.read(0, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordRead() throws BusTimeoutException {
		disk.read(0, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteWrite() throws BusTimeoutException {
		disk.write(0, BusAccessSize.BYTE, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordWrite() throws BusTimeoutException {
		disk.write(0, BusAccessSize.HALFWORD, 0);
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadWriteControlRegister() throws BusTimeoutException {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(32, disk.readWord(0));
		disk.writeWord(0, 0xfffffffe);
		assertEquals(62, disk.readWord(0));
		assertEquals(62, disk.getControlRegisterValue());
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadWriteSectorCountRegister() throws BusTimeoutException {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(0, disk.readWord(4));
		disk.writeWord(4, 42);
		assertEquals(42, disk.readWord(4));
		assertEquals(42, disk.getSectorCount());
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadWriteSectorRegister() throws BusTimeoutException {
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(0, disk.readWord(8));
		disk.writeWord(8, 42);
		assertEquals(42, disk.readWord(8));
		assertEquals(42, disk.getSector());
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadCapacityRegister() throws BusTimeoutException {
		assertEquals(0, disk.readWord(12));
		SimulatorUtils.multiTick(disk, Disk.DISK_DELAY);
		assertEquals(65536, disk.readWord(12));
	}

}
