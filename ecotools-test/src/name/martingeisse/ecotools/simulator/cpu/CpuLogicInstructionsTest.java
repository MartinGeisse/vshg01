/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import org.junit.Test;

/**
 * 
 */
public class CpuLogicInstructionsTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testAnd() {
		performCommutativeRRRInstructionTest(Instruction.OPCODE_AND, 0x12345678, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_AND, 0x12345678, 0xffffffff, 0x12345678);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_AND, 0x47474747, 0x12345678, 0x02044640);
	}

	/**
	 * 
	 */
	@Test
	public void testAndi() {
		performRRIInstructionTest(Instruction.OPCODE_ANDI, 0x12345678, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_ANDI, 0x12345678, 0x0fff, 0x00000678);
		performRRIInstructionTest(Instruction.OPCODE_ANDI, 0x12345678, 0xffff, 0x00005678);
		performRRIInstructionTest(Instruction.OPCODE_ANDI, 0x47474747, 0x5678, 0x00004640);
	}

	/**
	 * 
	 */
	@Test
	public void testOr() {
		performCommutativeRRRInstructionTest(Instruction.OPCODE_OR, 0x12345678, 0, 0x12345678);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_OR, 0x12345678, 0xffffffff, 0xffffffff);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_OR, 0x47474747, 0x12345678, 0x5777577f);
	}

	/**
	 * 
	 */
	@Test
	public void testOri() {
		performRRIInstructionTest(Instruction.OPCODE_ORI, 0x12345678, 0, 0x12345678);
		performRRIInstructionTest(Instruction.OPCODE_ORI, 0x12345678, 0x0fff, 0x12345fff);
		performRRIInstructionTest(Instruction.OPCODE_ORI, 0x12345678, 0xffff, 0x1234ffff);
		performRRIInstructionTest(Instruction.OPCODE_ORI, 0x47474747, 0x5678, 0x4747577f);
	}

	/**
	 * 
	 */
	@Test
	public void testXor() {
		performCommutativeRRRInstructionTest(Instruction.OPCODE_XOR, 0x12345678, 0, 0x12345678);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_XOR, 0x12345678, 0xffffffff, 0xedcba987);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_XOR, 0x47474747, 0x12345678, 0x5573113f);
	}

	/**
	 * 
	 */
	@Test
	public void testXori() {
		performRRIInstructionTest(Instruction.OPCODE_XORI, 0x12345678, 0, 0x12345678);
		performRRIInstructionTest(Instruction.OPCODE_XORI, 0x12345678, 0x0fff, 0x12345987);
		performRRIInstructionTest(Instruction.OPCODE_XORI, 0x12345678, 0xffff, 0x1234a987);
		performRRIInstructionTest(Instruction.OPCODE_XORI, 0x47474747, 0x5678, 0x4747113f);
	}

	/**
	 * 
	 */
	@Test
	public void testXnor() {
		performCommutativeRRRInstructionTest(Instruction.OPCODE_XNOR, 0x12345678, 0, 0xedcba987);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_XNOR, 0x12345678, 0xffffffff, 0x12345678);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_XNOR, 0x47474747, 0x12345678, 0xaa8ceec0);
	}

	/**
	 * 
	 */
	@Test
	public void testXnori() {
		performRRIInstructionTest(Instruction.OPCODE_XNORI, 0x12345678, 0, 0xedcba987);
		performRRIInstructionTest(Instruction.OPCODE_XNORI, 0x12345678, 0x0fff, 0xedcba678);
		performRRIInstructionTest(Instruction.OPCODE_XNORI, 0x12345678, 0xffff, 0xedcb5678);
		performRRIInstructionTest(Instruction.OPCODE_XNORI, 0x47474747, 0x5678, 0xb8b8eec0);
	}

}
