/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.keyboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
public class KeyboardTest {

	/**
	 * the keyboard
	 */
	private Keyboard keyboard;
	
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
		keyboard = new Keyboard();
		assertEquals(3, keyboard.getLocalAddressBitCount());
		assertEquals(1, keyboard.getInterruptLineCount());
		keyboard.connectInterruptLines(new IInterruptLine[] {interruptLine});
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
		assertEquals(3, Keyboard.TICKS_PER_BYTE);
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		assertFalse(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertEquals(0, keyboard.getData());
		assertNull(keyboard.getUserInterface());
		assertFalse(interruptLine.isActive());
		
		keyboard.setReady(true);
		assertTrue(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertEquals(0, keyboard.getData());
		assertNull(keyboard.getUserInterface());
		assertFalse(interruptLine.isActive());

		keyboard.setInterruptEnable(true);
		assertTrue(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertEquals(0, keyboard.getData());
		assertNull(keyboard.getUserInterface());
		assertTrue(interruptLine.isActive());

		keyboard.setReady(false);
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertEquals(0, keyboard.getData());
		assertNull(keyboard.getUserInterface());
		assertFalse(interruptLine.isActive());

		keyboard.setData((byte)99);
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertEquals(99, keyboard.getData());
		assertNull(keyboard.getUserInterface());
		assertFalse(interruptLine.isActive());

		MyUserInterface ui = new MyUserInterface();
		keyboard.setUserInterface(ui);
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertEquals(99, keyboard.getData());
		assertSame(ui, keyboard.getUserInterface());
		assertFalse(interruptLine.isActive());
	
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadControl() throws BusTimeoutException {
		assertEquals(0, keyboard.readWord(0));
		keyboard.setReady(true);
		assertEquals(1, keyboard.readWord(0));
		keyboard.setInterruptEnable(true);
		assertEquals(3, keyboard.readWord(0));
		keyboard.setReady(false);
		assertEquals(2, keyboard.readWord(0));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWriteControl() throws BusTimeoutException {

		keyboard.writeWord(0, 0);
		assertFalse(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		keyboard.writeWord(0, 1);
		assertTrue(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		keyboard.writeWord(0, 2);
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		keyboard.writeWord(0, 3);
		assertTrue(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		keyboard.writeWord(0, 4);
		assertFalse(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		keyboard.writeWord(0, 0xffffffff);
		assertEquals(3, keyboard.readWord(0));

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadData() throws BusTimeoutException {
		assertEquals(0, keyboard.getData());
		keyboard.setReady(true);
		keyboard.setInterruptEnable(true);
		keyboard.setData((byte)0x4f);
		assertEquals(0x4f, (byte)keyboard.readWord(4));
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testWriteData() throws BusTimeoutException {
		keyboard.writeWord(4, 99);
	}
	
	/**
	 * 
	 */
	@Test
	public void testNoReadyWithoutInput() {
		keyboard.tick();
		keyboard.tick();
		keyboard.tick();
		keyboard.tick();
		assertFalse(keyboard.isReady());
	}
	
	/**
	 * 
	 */
	@Test
	public void testSingleInputWithoutInterrupts() {
		MyUserInterface ui = new MyUserInterface();
		keyboard.setUserInterface(ui);
		
		assertFalse(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		ui.hasInput = true;
		ui.data = (byte)99;
		keyboard.onInputAvailable();
		assertFalse(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		ui.hasInput = false;
		ui.data = (byte)123;
		keyboard.tick();
		assertFalse(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		keyboard.tick();
		assertFalse(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(99, keyboard.getData());

		/** ensure that no further byte is received **/
		keyboard.tick();
		keyboard.tick();
		keyboard.tick();
		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertFalse(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(99, keyboard.getData());

	}

	/**
	 * 
	 */
	@Test
	public void testSingleInputWithInterrupts() {
		MyUserInterface ui = new MyUserInterface();
		keyboard.setUserInterface(ui);
		keyboard.setInterruptEnable(true);
		
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		ui.hasInput = true;
		ui.data = (byte)99;
		keyboard.onInputAvailable();
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		ui.hasInput = false;
		ui.data = (byte)123;
		keyboard.tick();
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		keyboard.tick();
		assertFalse(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertEquals(0, keyboard.getData());

		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertEquals(99, keyboard.getData());

		/** ensure that no further byte is received **/
		keyboard.tick();
		keyboard.tick();
		keyboard.tick();
		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertTrue(keyboard.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertEquals(99, keyboard.getData());

	}
	
	/**
	 * 
	 */
	@Test
	public void testMultiInput() {
		MyUserInterface ui = new MyUserInterface();
		keyboard.setUserInterface(ui);
		ui.hasInput = true;
		ui.data = (byte)99;

		keyboard.onInputAvailable();
		assertFalse(keyboard.isReady());
		assertEquals(0, keyboard.getData());

		keyboard.tick();
		assertFalse(keyboard.isReady());
		assertEquals(0, keyboard.getData());

		/** this should not interfere with the first byte **/
		ui.data = 100;
		keyboard.onInputAvailable();
		
		keyboard.tick();
		assertFalse(keyboard.isReady());
		assertEquals(0, keyboard.getData());

		/** this tick receives the first byte **/
		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertEquals(99, keyboard.getData());

		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertEquals(99, keyboard.getData());

		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertEquals(99, keyboard.getData());

		/** this tick receives the second byte **/
		keyboard.tick();
		assertTrue(keyboard.isReady());
		assertEquals(100, keyboard.getData());

	}

	/**
	 * This class tests for an expected {@link IllegalStateException} when onInputAvailable()
	 * is called while the UI reports no input.
	 */
	@Test(expected = IllegalStateException.class)
	public void testWrongNotification() {
		MyUserInterface ui = new MyUserInterface();
		keyboard.setUserInterface(ui);
		keyboard.onInputAvailable();
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testByteToWordExtension() throws BusTimeoutException {
		MyUserInterface ui = new MyUserInterface();
		keyboard.setUserInterface(ui);
		ui.data = (byte)0xff;
		ui.hasInput = true;
		
		keyboard.onInputAvailable();
		keyboard.tick();
		keyboard.tick();
		keyboard.tick();
		
		assertEquals(0xff, keyboard.read(4, BusAccessSize.WORD));
	}

	/**
	 * 
	 */
	private class MyUserInterface implements IKeyboardUserInterface {

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

	}
	
}
