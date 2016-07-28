/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class ProgramCounterTest {

	/**
	 * 
	 */
	@Test
	public void testUserInterfaceGetterSetter() {
		ProgramCounter pc = new ProgramCounter();
		assertNull(pc.getUserInterface());
		ICpuUserInterface ui = new MockCpuUserInterface();
		pc.setUserInterface(ui);
		assertSame(ui, pc.getUserInterface());
	}
	
	/**
	 * 
	 */
	@Test
	public void testValueGetterSetter() {
		ProgramCounter pc = new ProgramCounter();
		assertEquals(0xe0000000, pc.getValue());
		pc.setValue(0x12345678, true);
		assertEquals(0x12345678, pc.getValue());
	}
	
	/**
	 * 
	 */
	@Test
	public void testUserInteraceNotification() {
		MockCpuUserInterface ui = new MockCpuUserInterface();
		ProgramCounter pc = new ProgramCounter();
		pc.setUserInterface(ui);
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		assertFalse(ui.isWritePc());

		pc.setValue(12, false);
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		assertFalse(ui.isWritePc());

		pc.setValue(12, true);
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		assertTrue(ui.isWritePc());
	}
	
}
