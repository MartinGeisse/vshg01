/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This test ensures that the correct exception codes are used everywhere.
 */
public class CpuExceptionCodeTest extends AbstractCpuTest {

	/**
	 * @param priority
	 */
	private void assertPriority(int expectedPriority) {
		assertEquals(expectedPriority, (cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false) >> 16) & 0x1f);
	}
	
	/**
	 * @param expectedPcValue
	 */
	private void assertPc(int expectedPcValue) {
		assertEquals(expectedPcValue, cpu.getPc().getValue());
	}
	
	/**
	 * 
	 */
	@Test
	public void testInterruptCode() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.INTERRUPT_ENABLE_BIT | 0xffff, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		bus.setActiveInterrupt(7);
		cpu.step();
		assertPriority(7);
		assertPc(0xe0000004);
	}
	
	/**
	 * 
	 */
	@Test
	public void testBusTimeout() {
		/** specifically load a different code here to test for 0 **/
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0x1f << 16, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, -4);
		bus.setTimeout(true);
		cpu.step();
		assertPriority(16 + CpuException.CODE_BUS_TIMEOUT);
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testIllegalInstruction() {
		addRRR(0x3f, 0, 0, 0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_ILLEGAL_INSTRUCTION);
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testPrivilegedInstruction() {
		cpu.getPc().setValue(0x10000000, true);
		cpu.getMemoryManagementUnit().setTlbEntry(0, 0x10000000, 0x00000003, false);
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.USER_MODE_BIT, false);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_PRIVILEGED_INSTRUCTION);
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testDivision() {
		addRRR(Instruction.OPCODE_DIV, 0, 0, 0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_DIVISION);
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testTrap() {
		addRRR(Instruction.OPCODE_TRAP, 0, 0, 0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_TRAP);
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testUserTlbMiss() {
		addRRI(Instruction.OPCODE_STW, 0, 0, 0x7ff0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_TLB_MISS);
		assertEquals(0x00007000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x00007ff0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000008);
	}

	/**
	 * 
	 */
	@Test
	public void testKernelTlbMiss() {
		addLoadTestWord(1, 0x99999990);
		addRRI(Instruction.OPCODE_STW, 1, 0, 0);
		cpu.step();
		cpu.step();
		assertPriority(16 + CpuException.CODE_TLB_MISS);
		assertEquals(0x99999000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x99999990, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testUserTlbMissWithVectorBitSet() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.VECTOR_BIT, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 0x7ff0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_TLB_MISS);
		assertEquals(0x00007000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x00007ff0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xc0000008);
	}

	/**
	 * 
	 */
	@Test
	public void testKernelTlbMissWithVectorBitSet() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.VECTOR_BIT, false);
		addLoadTestWord(1, 0x99999990);
		addRRI(Instruction.OPCODE_STW, 1, 0, 0);
		cpu.step();
		cpu.step();
		assertPriority(16 + CpuException.CODE_TLB_MISS);
		assertEquals(0x99999000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x99999990, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xc0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testTlbInvalid() {
		cpu.getMemoryManagementUnit().setTlbEntry(0, 0x00007000, 0x00000000, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 0x7ff0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_TLB_INVALID);
		assertEquals(0x00007000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x00007ff0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testTlbWrite() {
		cpu.getMemoryManagementUnit().setTlbEntry(0, 0x00007000, 0x00000001, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, 0x7ff0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_TLB_WRITE);
		assertEquals(0x00007000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x00007ff0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testInstructionFetchIllegalAddress() {
		cpu.getPc().setValue(0xe0000001, true);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_ILLEGAL_ADDRESS);
		assertEquals(0xe0000001, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testStwIllegalAddress() {
		addRRI(Instruction.OPCODE_STW, 0, 0, -15);
		cpu.step();
		assertPriority(16 + CpuException.CODE_ILLEGAL_ADDRESS);
		assertEquals(0xfffffff1, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testLdwIllegalAddress() {
		addRRI(Instruction.OPCODE_LDW, 0, 0, -15);
		cpu.step();
		assertPriority(16 + CpuException.CODE_ILLEGAL_ADDRESS);
		assertEquals(0xfffffff1, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testInstructionFetchPrivilegedAddress() {
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.USER_MODE_BIT, false);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		assertPriority(16 + CpuException.CODE_PRIVILEGED_ADDRESS);
		assertEquals(0xe0000000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testStwPrivilegedAddress() {
		cpu.getPc().setValue(0x10000000, true);
		cpu.getMemoryManagementUnit().setTlbEntry(0, 0x10000000, 0x00000003, false);
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.USER_MODE_BIT, false);
		addRRI(Instruction.OPCODE_STW, 0, 0, -4);
		cpu.step();
		assertPriority(16 + CpuException.CODE_PRIVILEGED_ADDRESS);
		assertEquals(0xfffffffc, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

	/**
	 * 
	 */
	@Test
	public void testLdwPrivilegedAddress() {
		cpu.getPc().setValue(0x10000000, true);
		cpu.getMemoryManagementUnit().setTlbEntry(0, 0x10000000, 0x00000003, false);
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.USER_MODE_BIT, false);
		addRRI(Instruction.OPCODE_LDW, 0, 0, -4);
		cpu.step();
		assertPriority(16 + CpuException.CODE_PRIVILEGED_ADDRESS);
		assertEquals(0xfffffffc, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertPc(0xe0000004);
	}

}
