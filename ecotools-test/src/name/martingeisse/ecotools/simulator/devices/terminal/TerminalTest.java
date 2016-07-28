/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.terminal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
public class TerminalTest {

	/**
	 * the terminal
	 */
	private Terminal terminal;
	
	/**
	 * the receiverInterruptLine
	 */
	private DefaultInterruptLine receiverInterruptLine;

	/**
	 * the transmitterInterruptLine
	 */
	private DefaultInterruptLine transmitterInterruptLine;
	
	/**
	 * the ui
	 */
	private MyUserInterface ui;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		terminal = new Terminal();
		receiverInterruptLine = new DefaultInterruptLine();
		transmitterInterruptLine = new DefaultInterruptLine();
		ui = new MyUserInterface();
		terminal.connectInterruptLines(new IInterruptLine[] {receiverInterruptLine, transmitterInterruptLine});
		terminal.setUserInterface(ui);
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
		assertEquals(4, terminal.getLocalAddressBitCount());
		assertEquals(2, terminal.getInterruptLineCount());
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testControlRegisters() throws BusTimeoutException {
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(0, terminal.read(0, BusAccessSize.WORD));
		assertEquals(1, terminal.read(8, BusAccessSize.WORD));
		
		terminal.write(0, BusAccessSize.WORD, 1);
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(1, terminal.read(0, BusAccessSize.WORD));
		assertEquals(1, terminal.read(8, BusAccessSize.WORD));

		terminal.write(8, BusAccessSize.WORD, 2);
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(1, terminal.read(0, BusAccessSize.WORD));
		assertEquals(2, terminal.read(8, BusAccessSize.WORD));

		terminal.write(0, BusAccessSize.WORD, 0xffffffff);
		assertTrue(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(3, terminal.read(0, BusAccessSize.WORD));
		assertEquals(2, terminal.read(8, BusAccessSize.WORD));

		terminal.write(8, BusAccessSize.WORD, 0xffffffff);
		assertTrue(receiverInterruptLine.isActive());
		assertTrue(transmitterInterruptLine.isActive());
		assertEquals(3, terminal.read(0, BusAccessSize.WORD));
		assertEquals(3, terminal.read(8, BusAccessSize.WORD));

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void writeReceiverDataRegister() throws BusTimeoutException {
		terminal.write(4, BusAccessSize.WORD, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = BusTimeoutException.class)
	public void readTransmitterDataRegister() throws BusTimeoutException {
		terminal.read(12, BusAccessSize.WORD);
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void receiveByte() throws BusTimeoutException {

		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		
		terminal.write(0, BusAccessSize.WORD, 2);
		ui.receiverHasInput = true;
		ui.receiverData = 99;
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(2, terminal.read(0, BusAccessSize.WORD));
		assertEquals(0, terminal.read(4, BusAccessSize.WORD));

		terminal.onInputAvailable();
		terminal.tick();
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(2, terminal.read(0, BusAccessSize.WORD));
		assertEquals(0, terminal.read(4, BusAccessSize.WORD));

		terminal.tick();
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(2, terminal.read(0, BusAccessSize.WORD));
		assertEquals(0, terminal.read(4, BusAccessSize.WORD));

		terminal.tick();
		assertTrue(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(3, terminal.read(0, BusAccessSize.WORD));
		assertEquals(99, terminal.read(4, BusAccessSize.WORD));
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(2, terminal.read(0, BusAccessSize.WORD));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void sendByteSuccessfully() throws BusTimeoutException {

		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(1, terminal.read(8, BusAccessSize.WORD));

		terminal.write(8, BusAccessSize.WORD, 3);
		assertFalse(receiverInterruptLine.isActive());
		assertTrue(transmitterInterruptLine.isActive());
		assertEquals(3, terminal.read(8, BusAccessSize.WORD));
		assertFalse(ui.transmitterSent);
		assertEquals(0, ui.transmitterData);
		assertFalse(ui.transmitterCorrupted);

		terminal.write(12, BusAccessSize.WORD, 77);
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(2, terminal.read(8, BusAccessSize.WORD));
		assertFalse(ui.transmitterSent);
		assertEquals(0, ui.transmitterData);
		assertFalse(ui.transmitterCorrupted);
		
		terminal.tick();
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(2, terminal.read(8, BusAccessSize.WORD));
		assertFalse(ui.transmitterSent);
		assertEquals(0, ui.transmitterData);
		assertFalse(ui.transmitterCorrupted);

		terminal.tick();
		assertFalse(receiverInterruptLine.isActive());
		assertFalse(transmitterInterruptLine.isActive());
		assertEquals(2, terminal.read(8, BusAccessSize.WORD));
		assertFalse(ui.transmitterSent);
		assertEquals(0, ui.transmitterData);
		assertFalse(ui.transmitterCorrupted);

		terminal.tick();
		assertFalse(receiverInterruptLine.isActive());
		assertTrue(transmitterInterruptLine.isActive());
		assertEquals(3, terminal.read(8, BusAccessSize.WORD));
		assertTrue(ui.transmitterSent);
		assertEquals(77, ui.transmitterData);
		assertFalse(ui.transmitterCorrupted);

	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void sendByteCorrupted() throws BusTimeoutException {
		terminal.write(12, BusAccessSize.WORD, 77);
		terminal.write(12, BusAccessSize.WORD, 77);
		assertTrue(ui.transmitterSent);
		assertTrue(ui.transmitterCorrupted);
	}

	/**
	 * 
	 */
	private class MyUserInterface implements ITerminalUserInterface {

		/**
		 * the hasInput
		 */
		private boolean receiverHasInput;
		
		/**
		 * the data
		 */
		private byte receiverData;
		
		/**
		 * the sent
		 */
		private boolean transmitterSent;
		
		/**
		 * the data
		 */
		private byte transmitterData;
		
		/**
		 * the corrupted
		 */
		private boolean transmitterCorrupted;
		
		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#hasInput()
		 */
		@Override
		public boolean hasInput() {
			return receiverHasInput;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#receiveByte()
		 */
		@Override
		public byte receiveByte() throws IllegalStateException {
			return receiverData;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendByte(byte)
		 */
		@Override
		public void sendByte(byte b) {
			transmitterSent = true;
			transmitterData = b;
			transmitterCorrupted = false;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendCorruptedByte()
		 */
		@Override
		public void sendCorruptedByte() {
			transmitterSent = true;
			transmitterData = 0;
			transmitterCorrupted = true;
		}
		
	}

}
