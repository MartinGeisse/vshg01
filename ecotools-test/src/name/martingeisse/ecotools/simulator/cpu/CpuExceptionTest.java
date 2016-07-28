/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class CpuExceptionTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testSetRegister30OnException() {
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		assertEquals(0, cpu.getGeneralRegisters().read(30, false));
		cpu.step();
		assertEquals(0xe0000000, cpu.getGeneralRegisters().read(30, false));
	}

	/**
	 * 
	 */
	@Test
	public void testSetRegister30OnInterrupt() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		assertEquals(0, cpu.getGeneralRegisters().read(30, false));
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals(0xe0000000, cpu.getGeneralRegisters().read(30, false));
	}

	/**
	 * 
	 */
	@Test
	public void testPswOnInterruptPreviouslyZero() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT | 0xffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testPswOnInterruptPreviouslyUserMode() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.USER_MODE_BIT | ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | ProcessorStatusWord.PREVIOUS_USER_MODE_BIT | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT | 0xffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testPswOnInterruptPreviouslyInterruptsEnabled() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(9);
		cpu.step();
		assertEquals((9 << 16) | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT | 0xffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testPswOnExceptionPreviouslyZero() {
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals(24 << 16, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * Note: this cannot test with the user mode bit set, as the instruction fetch occurs
	 * from a privileged address.
	 */
	@Test
	public void testPswOnExceptionPreviouslyPreviousUserModeBitSet() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.PREVIOUS_USER_MODE_BIT, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((24 << 16) | ProcessorStatusWord.OLD_USER_MODE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testPswOnExceptionPreviouslyInterruptsEnabled() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((24 << 16) | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptBeforeException() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		bus.setActiveInterrupt(9);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((9 << 16) | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT | 0xffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptPswHandlingUpperBits() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0xf000ffff | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | 0xf000ffff | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptPswHandlingVectorBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x0800ffff | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | 0x0800ffff | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptPswHandlingUserModeBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x0400ffff | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | 0x0200ffff | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptPswHandlingPreviousUserModeBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x0200ffff | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | 0x0100ffff | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptPswHandlingOldUserModeBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x0100ffff | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | 0x0000ffff | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptPswHandlingAllUserModeBits() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x0700ffff | ProcessorStatusWord.INTERRUPT_ENABLE_BIT, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals((7 << 16) | 0x0300ffff | ProcessorStatusWord.PREVIOUS_INTERRUPT_ENABLE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionPswHandlingInterruptEnableBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00800000, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((24 << 16) | 0x00400000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionPswHandlingPreviousInterruptEnableBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00400000, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((24 << 16) | 0x00200000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionPswHandlingOldInterruptEnableBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00200000, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((24 << 16) | 0x00000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionPswHandlingAllInterruptEnableBits() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00e00000, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((24 << 16) | 0x00600000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testExceptionPswHandlingInterruptMask() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x0000abcd, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals((24 << 16) | 0x0000abcd, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testInterruptPswHandlingAllBitsSet() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0xffffffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals(0xfb67ffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingUpperBits() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0xf0000000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0xf0000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingVectorBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x08000000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x08000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingPreviousUserModeBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x02000000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x04000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingOldUserModeBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x01000000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x03000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingPreviousAndOldUserModeBits() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x03000000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x07000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingInterruptEnableBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00800000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x00000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingPreviousInterruptEnableBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00400000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x00800000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingOldInterruptEnableBit() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00200000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x00600000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingAllInterruptEnableBits() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x00e00000, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x00e00000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingExceptionCode() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 14 << 16, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(14 << 16, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingInterruptMask() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x0000abcd, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x0000abcd, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testRfxPswHandlingAllBitsExceptUserModeSet() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0xfbffffff, false);
		addLoadTestWord(30, 0xdeadbee0);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0xffffffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0xdeadbee0, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testHandlerAddressOnExceptionVector0() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals(0xe0000004, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testHandlerAddressOnInterruptVector0() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals(0xe0000004, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testHandlerAddressOnExceptionVector1() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.VECTOR_BIT | ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 1); // illegal address
		cpu.step();
		assertEquals(0xc0000004, cpu.getPc().getValue());
	}

	/**
	 * 
	 */
	@Test
	public void testHandlerAddressOnInterruptVector1() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.VECTOR_BIT | ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertEquals(0xc0000004, cpu.getPc().getValue());
	}

}
