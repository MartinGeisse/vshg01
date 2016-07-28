/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.terminal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.bus.DefaultInterruptLine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class TerminalReceiverTest {

	/**
	 * the receiver
	 */
	private TerminalReceiver receiver;
	
	/**
	 * the interruptLine
	 */
	private DefaultInterruptLine interruptLine;
	
	/**
	 * 
	 */
	@Before
	public void setUp() {
		interruptLine = new DefaultInterruptLine();
		receiver = new TerminalReceiver();
		receiver.connectInterruptLine(interruptLine);
	}
	
	/**
	 * 
	 */
	@After
	public void tearDown() {
	}
	
	/**
	 * This parameter value is assumed in the tests below.
	 */
	@Test
	public void testParameters() {
		assertEquals(3, TerminalReceiver.TICKS_PER_BYTE);
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		assertFalse(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertEquals(0, receiver.getData());
		assertNull(receiver.getUserInterface());
		assertFalse(interruptLine.isActive());
		
		receiver.setReady(true);
		assertTrue(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertEquals(0, receiver.getData());
		assertNull(receiver.getUserInterface());
		assertFalse(interruptLine.isActive());

		receiver.setInterruptEnable(true);
		assertTrue(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertEquals(0, receiver.getData());
		assertNull(receiver.getUserInterface());
		assertTrue(interruptLine.isActive());

		receiver.setReady(false);
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertEquals(0, receiver.getData());
		assertNull(receiver.getUserInterface());
		assertFalse(interruptLine.isActive());

		receiver.setData((byte)99);
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertEquals(99, receiver.getData());
		assertNull(receiver.getUserInterface());
		assertFalse(interruptLine.isActive());

		MyUserInterface ui = new MyUserInterface();
		receiver.setUserInterface(ui);
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertEquals(99, receiver.getData());
		assertSame(ui, receiver.getUserInterface());
		assertFalse(interruptLine.isActive());
	
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadControl() throws BusTimeoutException {
		assertEquals(0, receiver.readWord(0));
		receiver.setReady(true);
		assertEquals(1, receiver.readWord(0));
		receiver.setInterruptEnable(true);
		assertEquals(3, receiver.readWord(0));
		receiver.setReady(false);
		assertEquals(2, receiver.readWord(0));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWriteControl() throws BusTimeoutException {

		receiver.writeWord(0, 0);
		assertFalse(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		receiver.writeWord(0, 1);
		assertTrue(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		receiver.writeWord(0, 2);
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		receiver.writeWord(0, 3);
		assertTrue(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		receiver.writeWord(0, 4);
		assertFalse(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		receiver.writeWord(0, 0xffffffff);
		assertEquals(3, receiver.readWord(0));

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadData() throws BusTimeoutException {
		assertEquals(0, receiver.getData());
		receiver.setReady(true);
		receiver.setInterruptEnable(true);
		receiver.setData((byte)0x4f);
		assertEquals(0x4f, (byte)receiver.readWord(4));
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testWriteData() throws BusTimeoutException {
		receiver.writeWord(4, 99);
	}
	
	/**
	 * 
	 */
	@Test
	public void testNoReadyWithoutInput() {
		receiver.tick();
		receiver.tick();
		receiver.tick();
		receiver.tick();
		assertFalse(receiver.isReady());
	}
	
	/**
	 * 
	 */
	@Test
	public void testSingleInputWithoutInterrupts() {
		MyUserInterface ui = new MyUserInterface();
		receiver.setUserInterface(ui);
		
		assertFalse(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		ui.hasInput = true;
		ui.data = (byte)99;
		receiver.onInputAvailable();
		assertFalse(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		ui.hasInput = false;
		ui.data = (byte)123;
		receiver.tick();
		assertFalse(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		receiver.tick();
		assertFalse(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		receiver.tick();
		assertTrue(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(99, receiver.getData());

		/** ensure that no further byte is received **/
		receiver.tick();
		receiver.tick();
		receiver.tick();
		receiver.tick();
		assertTrue(receiver.isReady());
		assertFalse(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(99, receiver.getData());

	}

	/**
	 * 
	 */
	@Test
	public void testSingleInputWithInterrupts() {
		MyUserInterface ui = new MyUserInterface();
		receiver.setUserInterface(ui);
		receiver.setInterruptEnable(true);
		
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		ui.hasInput = true;
		ui.data = (byte)99;
		receiver.onInputAvailable();
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		ui.hasInput = false;
		ui.data = (byte)123;
		receiver.tick();
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		receiver.tick();
		assertFalse(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, receiver.getData());

		receiver.tick();
		assertTrue(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertEquals(99, receiver.getData());

		/** ensure that no further byte is received **/
		receiver.tick();
		receiver.tick();
		receiver.tick();
		receiver.tick();
		assertTrue(receiver.isReady());
		assertTrue(receiver.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertEquals(99, receiver.getData());

	}
	
	/**
	 * 
	 */
	@Test
	public void testMultiInput() {
		MyUserInterface ui = new MyUserInterface();
		receiver.setUserInterface(ui);
		ui.hasInput = true;
		ui.data = (byte)99;

		receiver.onInputAvailable();
		assertFalse(receiver.isReady());
		assertEquals(0, receiver.getData());

		receiver.tick();
		assertFalse(receiver.isReady());
		assertEquals(0, receiver.getData());

		/** this should not interfere with the first byte **/
		ui.data = 100;
		receiver.onInputAvailable();
		
		receiver.tick();
		assertFalse(receiver.isReady());
		assertEquals(0, receiver.getData());

		/** this tick receives the first byte **/
		receiver.tick();
		assertTrue(receiver.isReady());
		assertEquals(99, receiver.getData());

		receiver.tick();
		assertTrue(receiver.isReady());
		assertEquals(99, receiver.getData());

		receiver.tick();
		assertTrue(receiver.isReady());
		assertEquals(99, receiver.getData());

		/** this tick receives the second byte **/
		receiver.tick();
		assertTrue(receiver.isReady());
		assertEquals(100, receiver.getData());

	}

	/**
	 * This class tests for an expected {@link IllegalStateException} when onInputAvailable()
	 * is called while the UI reports no input.
	 */
	@Test(expected = IllegalStateException.class)
	public void testWrongNotification() {
		MyUserInterface ui = new MyUserInterface();
		receiver.setUserInterface(ui);
		receiver.onInputAvailable();
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testByteToWordExtension() throws BusTimeoutException {
		MyUserInterface ui = new MyUserInterface();
		receiver.setUserInterface(ui);
		ui.data = (byte)0xff;
		ui.hasInput = true;
		
		receiver.onInputAvailable();
		receiver.tick();
		receiver.tick();
		receiver.tick();
		
		assertEquals(0xff, receiver.readWord(4));
	}

	/**
	 * 
	 */
	private class MyUserInterface implements ITerminalUserInterface {

		/**
		 * the hasInput
		 */
		private boolean hasInput;
		
		/**
		 * the data
		 */
		private byte data;
		
		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#hasInput()
		 */
		@Override
		public boolean hasInput() {
			return hasInput;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#receiveByte()
		 */
		@Override
		public byte receiveByte() throws IllegalStateException {
			return data;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendByte(byte)
		 */
		@Override
		public void sendByte(byte b) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendCorruptedByte()
		 */
		@Override
		public void sendCorruptedByte() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
