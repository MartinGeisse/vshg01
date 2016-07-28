/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class CpuInterruptMaskTest extends AbstractCpuTest {

	/**
	 * @param originalPsw
	 * @param expectSuccess
	 */
	private void testInterrupt(int originalPsw, boolean expectSuccess) {
		cpu.reset();
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, originalPsw, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertTrue(expectSuccess == (originalPsw != cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false)));
	}

	/**
	 * @param originalPsw
	 * @param expectSuccess
	 */
	private void testException(int originalPsw, boolean expectSuccess) {
		cpu.reset();
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, originalPsw, false);
		addRRR(0x3f, 0, 0, 0);
		cpu.step();
		assertTrue(expectSuccess == (originalPsw != cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false)));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptZero() {
		testInterrupt(0, false);
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptEnabled() {
		testInterrupt(ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptMask() {
		testInterrupt(0xffff, false);
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptEnabledMask() {
		testInterrupt(ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, true);
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionZero() {
		testException(0, true);
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionEnabled() {
		testException(ProcessorStatusWord.INTERRUPT_ENABLE_BIT, true);
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionMask() {
		testException(0xffff, true);
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionEnabledMask() {
		testException(ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, true);
	}

	/**
	 * 
	 */
	@Test
	public void testPartialMaskEnabled1() {
		testInterrupt(ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0x0080, true);
	}

	/**
	 * 
	 */
	@Test
	public void testPartialMaskEnabled2() {
		testInterrupt(ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0x1234, false);
	}

	/**
	 * 
	 */
	@Test
	public void testPartialMaskEnabled3() {
		testInterrupt(ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xabcd, true);
	}

	/**
	 * 
	 */
	@Test
	public void testPartialMaskDisabled1() {
		testInterrupt(0x0080, false);
	}

	/**
	 * 
	 */
	@Test
	public void testPartialMaskDisabled2() {
		testInterrupt(0x1234, false);
	}

	/**
	 * 
	 */
	@Test
	public void testPartialMaskDisabled3() {
		testInterrupt(0xabcd, false);
	}

}
