/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.chardisplay;

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
public class CharacterDisplayTest {

	/**
	 * 
	 */
	@Test
	public void testAddressRange() {
		CharacterDisplay display = new CharacterDisplay();
		assertEquals(14, display.getLocalAddressBitCount());
	}
	
	/**
	 * 
	 */
	@Test
	public void testNoInterrupts() {
		CharacterDisplay display = new CharacterDisplay();
		assertEquals(0, display.getInterruptLineCount());
		display.connectInterruptLines(new IInterruptLine[0]);
	}

	/**
	 * 
	 */
	@Test
	public void testGetSetUserInterface() {
		CharacterDisplay display = new CharacterDisplay();
		assertNull(display.getUserInterface());
		
		MyCharacterDisplayUserInterface ui = new MyCharacterDisplayUserInterface();
		display.setUserInterface(ui);
		assertSame(ui, display.getUserInterface());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetCharacterAndAttribute() {
		CharacterDisplay display = new CharacterDisplay();
		display.setUserInterface(new NullCharacterDisplayUserInterface());
		display.setCharacter(5, 6, 'g');
		display.setAttribute(5, 9, 99);
		display.setCharacter(5, 7, 'h');
		display.setAttribute(5, 7, 42);
		
		assertEquals('g', display.getCharacter(5, 6));
		assertEquals('h', display.getCharacter(5, 7));
		assertEquals(0, display.getCharacter(5, 9));
		assertEquals(0, display.getAttribute(5, 6));
		assertEquals(42, display.getAttribute(5, 7));
		assertEquals(99, display.getAttribute(5, 9));
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteRead() throws BusTimeoutException {
		CharacterDisplay display = new CharacterDisplay();
		display.setUserInterface(new NullCharacterDisplayUserInterface());
		display.read(0, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordRead() throws BusTimeoutException {
		CharacterDisplay display = new CharacterDisplay();
		display.setUserInterface(new NullCharacterDisplayUserInterface());
		display.read(0, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteWrite() throws BusTimeoutException {
		CharacterDisplay display = new CharacterDisplay();
		display.setUserInterface(new NullCharacterDisplayUserInterface());
		display.write(0, BusAccessSize.BYTE, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordWrite() throws BusTimeoutException {
		CharacterDisplay display = new CharacterDisplay();
		display.setUserInterface(new NullCharacterDisplayUserInterface());
		display.write(0, BusAccessSize.HALFWORD, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWriteThenRead() throws BusTimeoutException {
		CharacterDisplay display = new CharacterDisplay();
		display.setUserInterface(new NullCharacterDisplayUserInterface());
		
		int localAddress = 3 * 128 * 4 + 37 * 4;
		display.write(localAddress, BusAccessSize.WORD, 0x12345678);
		assertEquals(0x00005678, display.read(localAddress, BusAccessSize.WORD));
		assertEquals(0, display.getAttribute(36, 3));
		assertEquals(0, display.getCharacter(36, 3));
		assertEquals(0x56, display.getAttribute(37, 3));
		assertEquals(0x78, display.getCharacter(37, 3));
		assertEquals(0, display.getAttribute(38, 3));
		assertEquals(0, display.getCharacter(38, 3));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testUserInterfaceNotification() throws BusTimeoutException {
		CharacterDisplay display = new CharacterDisplay();
		MyCharacterDisplayUserInterface ui = new MyCharacterDisplayUserInterface();
		display.setUserInterface(ui);
		
		assertEquals(0, ui.count);
		display.setAttribute(5, 9, 77);
		assertEquals(1, ui.count);
		display.setCharacter(5, 9, 'a');
		assertEquals(2, ui.count);
		display.write(9 * 128 * 4 + 5 * 4, BusAccessSize.WORD, 'a');
		assertEquals(3, ui.count);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testCellToWordExtension() throws BusTimeoutException {
		CharacterDisplay display = new CharacterDisplay();
		display.setUserInterface(new NullCharacterDisplayUserInterface());
		display.write(0, BusAccessSize.WORD, 0xffffffff);
		assertEquals(0xffff, display.read(0, BusAccessSize.WORD));
	}

	/**
	 * 
	 */
	private static class MyCharacterDisplayUserInterface implements ICharacterDisplayUserInterface {

		int count = 0;
		
		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterfaceSocket, int, int)
		 */
		@Override
		public void update(ICharacterDisplayUserInterfaceSocket characterDisplayUserInterfaceSocket, int x, int y) {
			assertTrue(characterDisplayUserInterfaceSocket instanceof CharacterDisplay);
			assertEquals(5, x);
			assertEquals(9, y);
			count++;
		}

	}

	/**
	 * 
	 */
	private static class NullCharacterDisplayUserInterface implements ICharacterDisplayUserInterface {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterfaceSocket, int, int)
		 */
		@Override
		public void update(ICharacterDisplayUserInterfaceSocket characterDisplayUserInterfaceSocket, int x, int y) {
		}

	}

}
