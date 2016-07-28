/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.blockdisplay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.bus.IInterruptLine;

import org.junit.Test;

/**
 * 
 */
public class BlockDisplayTest {

	/**
	 * 
	 */
	@Test
	public void testAddressRange() {
		BlockDisplay display = new BlockDisplay();
		assertEquals(13, display.getLocalAddressBitCount());
	}
	
	/**
	 * 
	 */
	@Test
	public void testNoInterrupts() {
		BlockDisplay display = new BlockDisplay();
		assertEquals(0, display.getInterruptLineCount());
		display.connectInterruptLines(new IInterruptLine[0]);
	}

	/**
	 * 
	 */
	@Test
	public void testGetSetUserInterface() {
		BlockDisplay display = new BlockDisplay();
		assertNull(display.getUserInterface());
		
		MyBlockDisplayUserInterface ui = new MyBlockDisplayUserInterface();
		display.setUserInterface(ui);
		assertSame(ui, display.getUserInterface());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetCharacterAndAttribute() {
		BlockDisplay display = new BlockDisplay();
		display.setUserInterface(new NullBlockDisplayUserInterface());
		display.setBlock(5, 6, 30);
		display.setBlock(5, 7, 0xff);
		display.setBlock(5, 9, 1);
		
		assertEquals(30, display.getBlock(5, 6));
		assertEquals(63, display.getBlock(5, 7));
		assertEquals(1, display.getBlock(5, 9));
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteRead() throws BusTimeoutException {
		BlockDisplay display = new BlockDisplay();
		display.setUserInterface(new NullBlockDisplayUserInterface());
		display.read(0, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordRead() throws BusTimeoutException {
		BlockDisplay display = new BlockDisplay();
		display.setUserInterface(new NullBlockDisplayUserInterface());
		display.read(0, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteWrite() throws BusTimeoutException {
		BlockDisplay display = new BlockDisplay();
		display.setUserInterface(new NullBlockDisplayUserInterface());
		display.write(0, BusAccessSize.BYTE, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordWrite() throws BusTimeoutException {
		BlockDisplay display = new BlockDisplay();
		display.setUserInterface(new NullBlockDisplayUserInterface());
		display.write(0, BusAccessSize.HALFWORD, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWriteThenRead() throws BusTimeoutException {
		BlockDisplay display = new BlockDisplay();
		display.setUserInterface(new NullBlockDisplayUserInterface());
		
		int localAddress = 3 * 64 * 4 + 37 * 4;
		display.write(localAddress, BusAccessSize.WORD, 0x12345678);
		assertEquals(0x00000038, display.read(localAddress, BusAccessSize.WORD));
		assertEquals(0, display.getBlock(36, 3));
		assertEquals(0x38, display.getBlock(37, 3));
		assertEquals(0, display.getBlock(38, 3));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testUserInterfaceNotification() throws BusTimeoutException {
		BlockDisplay display = new BlockDisplay();
		MyBlockDisplayUserInterface ui = new MyBlockDisplayUserInterface();
		display.setUserInterface(ui);
		
		assertEquals(0, ui.count);
		display.setBlock(5, 9, 77);
		assertEquals(1, ui.count);
		display.setBlock(5, 9, 20);
		assertEquals(2, ui.count);
		display.write(9 * 64 * 4 + 5 * 4, BusAccessSize.WORD, 10);
		assertEquals(3, ui.count);
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testByteToWordExtension() throws BusTimeoutException {
		BlockDisplay display = new BlockDisplay();
		display.setUserInterface(new NullBlockDisplayUserInterface());
		display.write(0, BusAccessSize.WORD, 0xffffffff);
		assertEquals(0xff, display.read(0, BusAccessSize.WORD));
	}

	/**
	 * 
	 */
	private static class MyBlockDisplayUserInterface implements IBlockDisplayUserInterface {

		int count = 0;
		
		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.blockdisplay.IBlockDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.blockdisplay.IBlockDisplayUserInterfaceSocket, int, int)
		 */
		@Override
		public void update(IBlockDisplayUserInterfaceSocket blockDisplayUserInterfaceSocket, int x, int y) {
			assertTrue(blockDisplayUserInterfaceSocket instanceof BlockDisplay);
			assertEquals(5, x);
			assertEquals(9, y);
			count++;
		}

	}

	/**
	 * 
	 */
	private static class NullBlockDisplayUserInterface implements IBlockDisplayUserInterface {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.blockdisplay.IBlockDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.blockdisplay.IBlockDisplayUserInterfaceSocket, int, int)
		 */
		@Override
		public void update(IBlockDisplayUserInterfaceSocket blockDisplayUserInterfaceSocket, int x, int y) {
		}

	}

}
