/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class GeneralRegisterFileTest {

	/**
	 * 
	 */
	@Test
	public void testRegular() {
		GeneralRegisterFile regs = new GeneralRegisterFile();
		regs.write(20, 99, false);
		regs.write(21, 1234, false);
		regs.write(20, 5678, false);
		assertEquals(1234, regs.read(21, false));
		assertEquals(5678, regs.read(20, false));
	}
	
	/**
	 * 
	 */
	@Test
	public void testZeroRegister() {
		GeneralRegisterFile regs = new GeneralRegisterFile();
		assertEquals(0, regs.read(0, false));
		regs.write(0, 99, false);
		assertEquals(0, regs.read(0, false));
	}
	
	/**
	 * 
	 */
	@Test
	public void testWithNullUserInterfaceNotification() {
		GeneralRegisterFile regs = new GeneralRegisterFile();
		assertEquals(0, regs.read(0, true));
		regs.write(0, 99, true);
		assertEquals(0, regs.read(0, true));
	}

	/**
	 * 
	 */
	@Test
	public void testWithMockUserInterfaceWithoutNotification() {
		MockCpuUserInterface ui = new MockCpuUserInterface();
		GeneralRegisterFile regs = new GeneralRegisterFile();
		regs.setUserInterface(ui);
		assertTrue(regs.getUserInterface() == ui);

		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		
		regs.read(5, false);
		
		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());

		regs.write(7, 15, false);

		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());

		regs.setUserInterface(null);
		assertTrue(regs.getUserInterface() == null);

	}

	/**
	 * 
	 */
	@Test
	public void testWithMockUserInterfaceWithNotification() {
		MockCpuUserInterface ui = new MockCpuUserInterface();
		GeneralRegisterFile regs = new GeneralRegisterFile();
		regs.setUserInterface(ui);

		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		
		regs.read(5, true);
		
		assertEquals(5, ui.getIndex());
		assertTrue(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		
		ui.setReadGeneralRegister(false);
		assertFalse(ui.isReadGeneralRegister());
		
		regs.write(7, 15, true);

		assertEquals(7, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertTrue(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());

	}

}
