/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class ValueTransportDelayTest {

	/**
	 * the success
	 */
	private boolean success = false;
	
	/**
	 * the data
	 */
	private byte data = 123;
	
	/**
	 * 
	 */
	@Test
	public void testInactive() {
		
		MySerialLineDelay delay = new MySerialLineDelay(3);
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

	}
	
	/**
	 * 
	 */
	@Test
	public void testNormal() {

		MySerialLineDelay delay = new MySerialLineDelay(3);
		
		delay.send((byte)99);
		assertTrue(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertTrue(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertTrue(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertTrue(success);
		assertEquals((byte)99, data);
		
		data = 100;
		success = false;
		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals((byte)100, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals((byte)100, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals((byte)100, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals((byte)100, data);

	}
	
	/**
	 * 
	 */
	@Test
	public void testCancel() {

		MySerialLineDelay delay = new MySerialLineDelay(3);
		
		delay.send((byte)99);
		assertTrue(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.cancel();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.send((byte)99);
		assertTrue(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertTrue(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertTrue(delay.isActive());
		assertFalse(success);
		assertEquals(123, data);

		delay.tick();
		assertFalse(delay.isActive());
		assertTrue(success);
		assertEquals((byte)99, data);

	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testSendDoubleImmediately() {
		MySerialLineDelay delay = new MySerialLineDelay(3);
		delay.send((byte)99);
		delay.send((byte)99);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testSendDoubleOneTickLater() {
		MySerialLineDelay delay = new MySerialLineDelay(3);
		delay.send((byte)99);
		delay.tick();
		delay.send((byte)99);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testCancelInactiveAfterConstruction() {
		MySerialLineDelay delay = new MySerialLineDelay(3);
		delay.cancel();
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testCancelInactiveAfterArrival() {
		MySerialLineDelay delay = new MySerialLineDelay(3);
		delay.send((byte)99);
		delay.tick();
		delay.tick();
		assertTrue(delay.isActive());
		delay.tick();
		assertFalse(delay.isActive());
		delay.cancel();
	}

	/**
	 * 
	 */
	private class MySerialLineDelay extends AbstractValueTransportDelay<Byte> {

		/**
		 * Constructor
		 * @param delay the delay
		 */
		public MySerialLineDelay(int delay) {
			super(delay);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.AbstractSerialLineDelay#onArrive(byte)
		 */
		@Override
		protected void onArrive(Byte b) {
			success = true;
			data = b;
		}
		
	}

}
