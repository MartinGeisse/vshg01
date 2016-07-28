/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.devices.pixeldisplay;

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
public class PixelDisplayTest {

	/**
	 * the display
	 */
	private PixelDisplay display = new PixelDisplay();

	/**
	 * 
	 */
	@Test
	public void testAddressRange() {
		assertEquals(21, display.getLocalAddressBitCount());
	}
	
	/**
	 * 
	 */
	@Test
	public void testNoInterrupts() {
		assertEquals(0, display.getInterruptLineCount());
		display.connectInterruptLines(new IInterruptLine[0]);
	}

	/**
	 * 
	 */
	@Test
	public void testGetSetUserInterface() {
		assertNull(display.getUserInterface());
		
		MyPixelDisplayUserInterface ui = new MyPixelDisplayUserInterface();
		display.setUserInterface(ui);
		assertSame(ui, display.getUserInterface());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetPixel() {
		display.setUserInterface(new NullPixelDisplayUserInterface());
		display.setPixel(5, 9, 0x12345678);
		display.setPixel(5, 6, 0xabcdef01);
		display.setPixel(5, 9, 0x2468ace0);
		assertEquals(0xabcdef01, display.getPixel(5, 6));
		assertEquals(0x2468ace0, display.getPixel(5, 9));
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteRead() throws BusTimeoutException {
		display.setUserInterface(new NullPixelDisplayUserInterface());
		display.read(0, BusAccessSize.BYTE);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordRead() throws BusTimeoutException {
		display.setUserInterface(new NullPixelDisplayUserInterface());
		display.read(0, BusAccessSize.HALFWORD);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testByteWrite() throws BusTimeoutException {
		display.setUserInterface(new NullPixelDisplayUserInterface());
		display.write(0, BusAccessSize.BYTE, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test(expected = RuntimeException.class)
	public void testHalfwordWrite() throws BusTimeoutException {
		display.setUserInterface(new NullPixelDisplayUserInterface());
		display.write(0, BusAccessSize.HALFWORD, 0);
	}

	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testWriteThenRead() throws BusTimeoutException {
		display.setUserInterface(new NullPixelDisplayUserInterface());
		
		int localAddress = 3 * 640 * 4 + 37 * 4;
		display.write(localAddress, BusAccessSize.WORD, 0x12345678);
		assertEquals(0, display.read(localAddress, BusAccessSize.WORD));
		assertEquals(0, display.getPixel(36, 3));
		assertEquals(0x12345678, display.getPixel(37, 3));
		assertEquals(0, display.getPixel(38, 3));
	}
	
	/**
	 * @throws BusTimeoutException ...
	 */
	@Test
	public void testUserInterfaceNotification() throws BusTimeoutException {
		MyPixelDisplayUserInterface ui = new MyPixelDisplayUserInterface();
		display.setUserInterface(ui);
		
		assertEquals(0, ui.count);
		display.setPixel(5, 9, 77);
		assertEquals(1, ui.count);
		display.write(9 * 640 * 4 + 5 * 4, BusAccessSize.WORD, 'a');
		assertEquals(2, ui.count);
	}

	/**
	 * 
	 */
	private static class MyPixelDisplayUserInterface implements IPixelDisplayUserInterface {

		int count = 0;
		
		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterfaceSocket, int, int)
		 */
		@Override
		public void update(IPixelDisplayUserInterfaceSocket pixelDisplayUserInterfaceSocket, int x, int y) {
			assertTrue(pixelDisplayUserInterfaceSocket instanceof PixelDisplay);
			assertEquals(5, x);
			assertEquals(9, y);
			count++;
		}

	}

	/**
	 * 
	 */
	private static class NullPixelDisplayUserInterface implements IPixelDisplayUserInterface {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterfaceSocket, int, int)
		 */
		@Override
		public void update(IPixelDisplayUserInterfaceSocket pixelDisplayUserInterfaceSocket, int x, int y) {
		}

	}

}
