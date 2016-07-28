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
public class SpecialRegisterFileTest {

	/**
	 * 
	 */
	@Test
	public void testRegular() {
		SpecialRegisterFile regs = new SpecialRegisterFile();
		regs.write(ISpecialRegisterFile.INDEX_PSW, 99, false);
		regs.write(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, 1234, false);
		regs.write(ISpecialRegisterFile.INDEX_PSW, 5678, false);
		assertEquals(1234, regs.read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertEquals(5678, regs.read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testRetentionMask() {
		SpecialRegisterFile regs = new SpecialRegisterFile();
		regs.write(ISpecialRegisterFile.INDEX_TLB_INDEX, 0xffffffff, false);
		assertEquals(0x0000001f, regs.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		regs.forceWrite(ISpecialRegisterFile.INDEX_TLB_INDEX, 0xffffffff, false);
		assertEquals(0xffffffff, regs.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
	}

	/**
	 * 
	 */
	@Test
	public void testTransformOnException() {
		int baseValue = 0x60001234;
		assertEquals(baseValue, ProcessorStatusWord.transformOnException(baseValue, 0));
		assertEquals(baseValue + 0x00090000, ProcessorStatusWord.transformOnException(baseValue, 9));
		assertEquals(baseValue | ProcessorStatusWord.VECTOR_BIT, ProcessorStatusWord.transformOnException(baseValue | ProcessorStatusWord.VECTOR_BIT, 0));
		assertEquals(baseValue | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, ProcessorStatusWord.transformOnException(baseValue | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, 0));
		assertEquals(baseValue | ProcessorStatusWord.OLD_INTERRUPT_ENABLE_BIT, ProcessorStatusWord.transformOnException(baseValue | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, 0));
		assertEquals(baseValue, ProcessorStatusWord.transformOnException(baseValue | ProcessorStatusWord.OLD_INTERRUPT_ENABLE_BIT, 0));
		assertEquals(baseValue | ProcessorStatusWord.PREVIOUS_USER_MODE_BIT, ProcessorStatusWord.transformOnException(baseValue | ProcessorStatusWord.USER_MODE_BIT, 0));
		assertEquals(baseValue | ProcessorStatusWord.OLD_USER_MODE_BIT, ProcessorStatusWord.transformOnException(baseValue | ProcessorStatusWord.PREVIOUS_USER_MODE_BIT, 0));
		assertEquals(baseValue, ProcessorStatusWord.transformOnException(baseValue | ProcessorStatusWord.OLD_USER_MODE_BIT, 0));
		assertEquals(0xFb7aFFFF, ProcessorStatusWord.transformOnException(0xFFFFFFFF, 0x1a));
	}

	/**
	 * 
	 */
	@Test
	public void testTransformOnReturnFromException() {
		int baseValue = 0x60001234;
		assertEquals(baseValue, ProcessorStatusWord.transformOnReturnFromException(baseValue));
		assertEquals(baseValue | ProcessorStatusWord.VECTOR_BIT, ProcessorStatusWord.transformOnReturnFromException(baseValue | ProcessorStatusWord.VECTOR_BIT));
		assertEquals(baseValue, ProcessorStatusWord.transformOnReturnFromException(baseValue | ProcessorStatusWord.INTERRUPT_ENABLE_BIT));
		assertEquals(baseValue | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, ProcessorStatusWord.transformOnReturnFromException(baseValue | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT));
		assertEquals(baseValue | ProcessorStatusWord.OLD_INTERRUPT_ENABLE_BIT | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, ProcessorStatusWord.transformOnReturnFromException(baseValue | ProcessorStatusWord.OLD_INTERRUPT_ENABLE_BIT));
		assertEquals(baseValue, ProcessorStatusWord.transformOnReturnFromException(baseValue | ProcessorStatusWord.USER_MODE_BIT));
		assertEquals(baseValue | ProcessorStatusWord.USER_MODE_BIT, ProcessorStatusWord.transformOnReturnFromException(baseValue | ProcessorStatusWord.PREVIOUS_USER_MODE_BIT));
		assertEquals(baseValue | ProcessorStatusWord.OLD_USER_MODE_BIT | ProcessorStatusWord.PREVIOUS_USER_MODE_BIT, ProcessorStatusWord.transformOnReturnFromException(baseValue | ProcessorStatusWord.OLD_USER_MODE_BIT));
		assertEquals(0xFFFFFFFF, ProcessorStatusWord.transformOnReturnFromException(0xFFFFFFFF));
	}

	/**
	 * 
	 */
	@Test
	public void testAllowsInterrupt() {
		assertFalse(ProcessorStatusWord.allowsInterrupt(0, 5));
		assertFalse(ProcessorStatusWord.allowsInterrupt(32, 5));
		assertFalse(ProcessorStatusWord.allowsInterrupt(ProcessorStatusWord.INTERRUPT_ENABLE_BIT, 5));
		assertTrue(ProcessorStatusWord.allowsInterrupt(ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 32, 5));
	}

	/**
	 * 
	 */
	@Test
	public void testWithNullUserInterfaceNotification() {
		SpecialRegisterFile regs = new SpecialRegisterFile();
		assertEquals(0, regs.read(0, true));
		regs.write(0, 99, true);
		assertEquals(99, regs.read(0, true));
	}

	/**
	 * 
	 */
	@Test
	public void testWithMockUserInterfaceWithoutNotification() {
		MockCpuUserInterface ui = new MockCpuUserInterface();
		SpecialRegisterFile regs = new SpecialRegisterFile();
		regs.setUserInterface(ui);
		assertTrue(regs.getUserInterface() == ui);

		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		
		regs.read(3, false);
		
		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());

		regs.write(2, 15, false);

		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());

		regs.forceWrite(1, 15, false);

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
		SpecialRegisterFile regs = new SpecialRegisterFile();
		regs.setUserInterface(ui);

		assertEquals(-1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		
		regs.read(3, true);
		
		assertEquals(3, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertTrue(ui.isReadSpecialRegister());
		assertFalse(ui.isWriteSpecialRegister());
		
		ui.setReadSpecialRegister(false);
		assertFalse(ui.isReadGeneralRegister());
		
		regs.write(2, 15, true);

		assertEquals(2, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertTrue(ui.isWriteSpecialRegister());

		ui.setWriteSpecialRegister(false);
		assertFalse(ui.isWriteGeneralRegister());

		regs.forceWrite(1, 10, true);

		assertEquals(1, ui.getIndex());
		assertFalse(ui.isReadGeneralRegister());
		assertFalse(ui.isWriteGeneralRegister());
		assertFalse(ui.isReadSpecialRegister());
		assertTrue(ui.isWriteSpecialRegister());

	}

}
