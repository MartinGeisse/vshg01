/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.timer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
public class TimerTest {

	/**
	 * the timer
	 */
	private Timer timer;

	/**
	 * the interruptLine
	 */
	private DefaultInterruptLine interruptLine;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		timer = new Timer();
		interruptLine = new DefaultInterruptLine();
		timer.connectInterruptLines(new IInterruptLine[] {
			interruptLine
		});
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
	public void testDeviceProperties() {
		assertEquals(1, timer.getInterruptLineCount());
		assertEquals(3, timer.getLocalAddressBitCount());
	}

	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		assertEquals(0xffffffff, timer.getCounter());
		assertEquals(0xffffffff, timer.getDivisor());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());

		timer.setCounter(0x12345678);
		assertEquals(0x12345678, timer.getCounter());
		assertEquals(0xffffffff, timer.getDivisor());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.setDivisor(0xabcdef01);
		assertEquals(0x12345678, timer.getCounter());
		assertEquals(0xabcdef01, timer.getDivisor());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.setExpired(true);
		assertEquals(0x12345678, timer.getCounter());
		assertEquals(0xabcdef01, timer.getDivisor());
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.setInterruptEnable(true);
		assertEquals(0x12345678, timer.getCounter());
		assertEquals(0xabcdef01, timer.getDivisor());
		assertTrue(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		timer.setExpired(false);
		assertEquals(0x12345678, timer.getCounter());
		assertEquals(0xabcdef01, timer.getDivisor());
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
	}

	/**
	 * 
	 */
	@Test
	public void testControlRegister() {
		timer.writeControlRegister(0);
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 0);

		timer.writeControlRegister(1);
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 1);

		timer.writeControlRegister(2);
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 2);

		timer.writeControlRegister(3);
		assertTrue(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 3);

		timer.writeControlRegister(4);
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 0);

		timer.writeControlRegister(5);
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 1);

		timer.writeControlRegister(6);
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 2);

		timer.writeControlRegister(7);
		assertTrue(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertTrue(timer.readControlRegister() == 3);

		timer.writeControlRegister(0xffffffff);
		assertTrue(timer.readControlRegister() == 3);
	}

	/**
	 * 
	 */
	@Test
	public void testDivisorRegister() {

		/** writing the divisor register also affects the counter **/
		timer.writeDivisorRegister(99);
		assertEquals(99, timer.readDivisorRegister());
		assertEquals(99, timer.getDivisor());
		assertEquals(99, timer.getCounter());

		/** even if writing a smaller value into the divisor than the counter **/
		timer.writeDivisorRegister(50);
		assertEquals(50, timer.readDivisorRegister());
		assertEquals(50, timer.getDivisor());
		assertEquals(50, timer.getCounter());

		/** or writing a larger value **/
		timer.writeDivisorRegister(100);
		assertEquals(100, timer.readDivisorRegister());
		assertEquals(100, timer.getDivisor());
		assertEquals(100, timer.getCounter());

	}

	/**
	 * 
	 */
	@Test
	public void testRegisterInteraction() {

		/** writing the divisor does not affect the control register or the interrupt lines **/

		timer.writeControlRegister(0);
		timer.writeDivisorRegister(0);
		assertEquals(timer.readControlRegister(), 0);
		assertFalse(interruptLine.isActive());

		timer.writeControlRegister(3);
		timer.writeDivisorRegister(0);
		assertEquals(timer.readControlRegister(), 3);
		assertTrue(interruptLine.isActive());

		timer.writeControlRegister(0);
		timer.writeDivisorRegister(99);
		assertEquals(timer.readControlRegister(), 0);
		assertFalse(interruptLine.isActive());

		timer.writeControlRegister(3);
		timer.writeDivisorRegister(99);
		assertEquals(timer.readControlRegister(), 3);
		assertTrue(interruptLine.isActive());

		/** writing the control register does not affect the divisor **/

		timer.writeDivisorRegister(0);
		timer.writeControlRegister(0);
		assertEquals(timer.readDivisorRegister(), 0);

		timer.writeDivisorRegister(99);
		timer.writeControlRegister(0);
		assertEquals(timer.readDivisorRegister(), 99);

		timer.writeDivisorRegister(0);
		timer.writeControlRegister(3);
		assertEquals(timer.readDivisorRegister(), 0);

		timer.writeDivisorRegister(99);
		timer.writeControlRegister(3);
		assertEquals(timer.readDivisorRegister(), 99);

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testBusRead() throws BusTimeoutException {

		assertEquals(0, timer.read(0, BusAccessSize.WORD));
		assertEquals(0xffffffff, timer.read(4, BusAccessSize.WORD));

		timer.writeControlRegister(3);
		assertEquals(3, timer.read(0, BusAccessSize.WORD));
		assertEquals(0xffffffff, timer.read(4, BusAccessSize.WORD));

		timer.writeDivisorRegister(0x12345678);
		assertEquals(3, timer.read(0, BusAccessSize.WORD));
		assertEquals(0x12345678, timer.read(4, BusAccessSize.WORD));

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testBusWrite() throws BusTimeoutException {

		timer.write(0, BusAccessSize.WORD, 3);
		assertEquals(3, timer.readControlRegister());
		assertEquals(0xffffffff, timer.readDivisorRegister());
		assertTrue(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		timer.write(0, BusAccessSize.WORD, 0);
		assertEquals(0, timer.readControlRegister());
		assertEquals(0xffffffff, timer.readDivisorRegister());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.write(4, BusAccessSize.WORD, 0x12345678);
		assertEquals(0, timer.readControlRegister());
		assertEquals(0x12345678, timer.readDivisorRegister());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.write(4, BusAccessSize.WORD, 0);
		assertEquals(0, timer.readControlRegister());
		assertEquals(0, timer.readDivisorRegister());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.write(0, BusAccessSize.WORD, 3);
		assertEquals(3, timer.readControlRegister());
		assertEquals(0, timer.readDivisorRegister());
		assertTrue(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		timer.write(4, BusAccessSize.WORD, 0x12345678);
		assertEquals(3, timer.readControlRegister());
		assertEquals(0x12345678, timer.readDivisorRegister());
		assertTrue(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		timer.write(0, BusAccessSize.WORD, 1);
		assertEquals(1, timer.readControlRegister());
		assertEquals(0x12345678, timer.readDivisorRegister());
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.write(0, BusAccessSize.WORD, 2);
		assertEquals(2, timer.readControlRegister());
		assertEquals(0x12345678, timer.readDivisorRegister());
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

	}

	/**
	 * 
	 */
	@Test
	public void testTick() {

		timer.setInterruptEnable(true);

		/** normal tick **/
		
		timer.writeDivisorRegister(50);
		assertEquals(50, timer.getDivisor());
		assertEquals(50, timer.getCounter());
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		/** prescaler takes 10 ticks to decrease the counter by one, so test 9 first **/
		
		SimulatorUtils.multiTick(timer, 9);
		assertEquals(50, timer.getDivisor());
		assertEquals(50, timer.getCounter());
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		
		/** the 10th tick actually decreases the counter **/
		
		SimulatorUtils.multiTick(timer, 1);
		assertEquals(50, timer.getDivisor());
		assertEquals(49, timer.getCounter());
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		/** expire with interrupts enabled **/
		
		timer.setCounter(1);
		assertEquals(50, timer.getDivisor());
		assertEquals(1, timer.getCounter());
		assertFalse(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		SimulatorUtils.multiTick(timer, 10);
		assertEquals(50, timer.getDivisor());
		assertEquals(50, timer.getCounter());
		assertTrue(timer.isExpired());
		assertTrue(timer.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		/** expire with interrupts disabled **/
		
		timer.setCounter(1);
		timer.setExpired(false);
		timer.setInterruptEnable(false);
		assertEquals(50, timer.getDivisor());
		assertEquals(1, timer.getCounter());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		SimulatorUtils.multiTick(timer, 10);
		assertEquals(50, timer.getDivisor());
		assertEquals(50, timer.getCounter());
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		/** expire while expired flag still set **/
		
		timer.setCounter(1);
		assertEquals(50, timer.getDivisor());
		assertEquals(1, timer.getCounter());
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		SimulatorUtils.multiTick(timer, 10);
		assertEquals(50, timer.getDivisor());
		assertEquals(50, timer.getCounter());
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

	}

	/**
	 * 
	 */
	@Test
	public void testZeroDivisor() {

		timer.setDivisor(0);
		timer.setCounter(1);
		assertEquals(0, timer.getDivisor());
		assertEquals(1, timer.getCounter());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		SimulatorUtils.multiTick(timer, 10);
		assertEquals(0, timer.getDivisor());
		assertEquals(0, timer.getCounter());
		assertTrue(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		timer.setExpired(false);
		assertEquals(0, timer.getDivisor());
		assertEquals(0, timer.getCounter());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		
		SimulatorUtils.multiTick(timer, 10);
		assertEquals(0, timer.getDivisor());
		assertEquals(0xffffffff, timer.getCounter());
		assertFalse(timer.isExpired());
		assertFalse(timer.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteRead() throws BusTimeoutException {
		timer.read(0, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordRead() throws BusTimeoutException {
		timer.read(0, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteWrite() throws BusTimeoutException {
		timer.write(0, BusAccessSize.BYTE, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordWrite() throws BusTimeoutException {
		timer.write(0, BusAccessSize.HALFWORD, 0);
	}
	
}
