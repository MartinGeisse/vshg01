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
public class TerminalTransmitterTest {

	/**
	 * the transmitter
	 */
	private TerminalTransmitter transmitter;
	
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
		transmitter = new TerminalTransmitter();
		transmitter.connectInterruptLine(interruptLine);
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
		assertEquals(3, TerminalTransmitter.TICKS_PER_BYTE);
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		assertTrue(transmitter.isReady());
		assertFalse(transmitter.isInterruptEnable());
		assertNull(transmitter.getUserInterface());
		assertFalse(interruptLine.isActive());
		
		transmitter.setReady(false);
		assertFalse(transmitter.isReady());
		assertFalse(transmitter.isInterruptEnable());
		assertNull(transmitter.getUserInterface());
		assertFalse(interruptLine.isActive());

		transmitter.setInterruptEnable(true);
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertNull(transmitter.getUserInterface());
		assertFalse(interruptLine.isActive());

		transmitter.setReady(true);
		assertTrue(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertNull(transmitter.getUserInterface());
		assertTrue(interruptLine.isActive());

		MyUserInterface ui = new MyUserInterface();
		transmitter.setUserInterface(ui);
		transmitter.setReady(false);
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertSame(ui, transmitter.getUserInterface());
		assertFalse(interruptLine.isActive());
	
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testReadControl() throws BusTimeoutException {
		transmitter.setReady(false);
		assertEquals(0, transmitter.readWord(0));
		transmitter.setReady(true);
		assertEquals(1, transmitter.readWord(0));
		transmitter.setInterruptEnable(true);
		assertEquals(3, transmitter.readWord(0));
		transmitter.setReady(false);
		assertEquals(2, transmitter.readWord(0));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWriteControl() throws BusTimeoutException {

		transmitter.writeWord(0, 0);
		assertFalse(transmitter.isReady());
		assertFalse(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		transmitter.writeWord(0, 1);
		assertTrue(transmitter.isReady());
		assertFalse(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		transmitter.writeWord(0, 2);
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		transmitter.writeWord(0, 3);
		assertTrue(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertTrue(interruptLine.isActive());

		transmitter.writeWord(0, 4);
		assertFalse(transmitter.isReady());
		assertFalse(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());

		transmitter.writeWord(0, 0xffffffff);
		assertEquals(3, transmitter.readWord(0));

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void testReadData() throws BusTimeoutException {
		transmitter.readWord(4);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWriteData() throws BusTimeoutException {
		MyUserInterface ui = new MyUserInterface();
		transmitter.setUserInterface(ui);
		assertFalse(ui.sent);
		
		transmitter.setReady(true);
		transmitter.setInterruptEnable(true);
		transmitter.writeWord(4, 0x4f);
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertFalse(ui.sent);
		assertEquals(0, ui.data);
		
		transmitter.tick();
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertFalse(ui.sent);
		assertEquals(0, ui.data);

		transmitter.tick();
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertFalse(ui.sent);
		assertEquals(0, ui.data);

		transmitter.tick();
		assertTrue(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertTrue(ui.sent);
		assertEquals(0x4f, ui.data);
		assertFalse(ui.corrupted);
		
		ui.sent = false;
		transmitter.tick();
		transmitter.tick();
		transmitter.tick();
		transmitter.tick();
		assertFalse(ui.sent);

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testDataFlooding() throws BusTimeoutException {

		MyUserInterface ui = new MyUserInterface();
		ui.data = 0x13;
		transmitter.setUserInterface(ui);
		assertFalse(ui.sent);
		
		transmitter.setReady(true);
		transmitter.setInterruptEnable(true);
		transmitter.writeWord(4, 0x4f);
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertFalse(ui.sent);
		assertEquals(0x13, ui.data);
		
		transmitter.tick();
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertFalse(ui.sent);
		assertEquals(0x13, ui.data);

		transmitter.writeWord(4, 0x77);
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(ui.sent);
		assertEquals(0, ui.data);
		assertTrue(ui.corrupted);

		transmitter.tick();
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(ui.sent);
		assertEquals(0, ui.data);
		assertTrue(ui.corrupted);

		transmitter.tick();
		assertFalse(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertFalse(interruptLine.isActive());
		assertTrue(ui.sent);
		assertEquals(0, ui.data);
		assertTrue(ui.corrupted);

		transmitter.tick();
		assertTrue(transmitter.isReady());
		assertTrue(transmitter.isInterruptEnable());
		assertTrue(interruptLine.isActive());
		assertTrue(ui.sent);
		assertEquals(0x77, ui.data);
		assertFalse(ui.corrupted);

	}

	/**
	 * 
	 */
	private class MyUserInterface implements ITerminalUserInterface {

		/**
		 * the sent
		 */
		private boolean sent;
		
		/**
		 * the data
		 */
		private byte data;
		
		/**
		 * the corrupted
		 */
		private boolean corrupted;
		
		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#hasInput()
		 */
		@Override
		public boolean hasInput() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#receiveByte()
		 */
		@Override
		public byte receiveByte() throws IllegalStateException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendByte(byte)
		 */
		@Override
		public void sendByte(byte b) {
			sent = true;
			data = b;
			corrupted = false;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendCorruptedByte()
		 */
		@Override
		public void sendCorruptedByte() {
			sent = true;
			data = 0;
			corrupted = true;
		}
		
	}
	
}
