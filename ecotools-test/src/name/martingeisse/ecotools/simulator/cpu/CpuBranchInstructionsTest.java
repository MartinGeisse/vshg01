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
public class CpuBranchInstructionsTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testBeq() {
		performBranchInstructionTest(Instruction.OPCODE_BEQ, 0, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BEQ, 0, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BEQ, 1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BEQ, 1, 1, true);
	}

	/**
	 * 
	 */
	@Test
	public void testBne() {
		performBranchInstructionTest(Instruction.OPCODE_BNE, 0, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BNE, 0, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BNE, 1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BNE, 1, 1, false);
	}

	/**
	 * 
	 */
	@Test
	public void testBlt() {
		performBranchInstructionTest(Instruction.OPCODE_BLT, 0, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLT, 0, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLT, 1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLT, 1, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BLT, 0, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BLT, -1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BLT, 1, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BLT, -1, 1, true);
	}

	/**
	 * 
	 */
	@Test
	public void testBltu() {
		performBranchInstructionTest(Instruction.OPCODE_BLTU, 0, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLTU, 0, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLTU, 1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLTU, 1, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BLTU, 0, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLTU, -1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLTU, 1, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLTU, -1, 1, false);
	}

	/**
	 * 
	 */
	@Test
	public void testBle() {
		performBranchInstructionTest(Instruction.OPCODE_BLE, 0, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BLE, 0, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLE, 1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLE, 1, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLE, 0, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BLE, -1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BLE, 1, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BLE, -1, 1, true);
	}

	/**
	 * 
	 */
	@Test
	public void testBleu() {
		performBranchInstructionTest(Instruction.OPCODE_BLEU, 0, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BLEU, 0, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLEU, 1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLEU, 1, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLEU, 0, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLEU, -1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BLEU, 1, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BLEU, -1, 1, false);
	}

	/**
	 * 
	 */
	@Test
	public void testBgt() {
		performBranchInstructionTest(Instruction.OPCODE_BGT, 0, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BGT, 0, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGT, 1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGT, 1, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGT, 0, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BGT, -1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BGT, 1, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BGT, -1, 1, false);
	}

	/**
	 * 
	 */
	@Test
	public void testBgtu() {
		performBranchInstructionTest(Instruction.OPCODE_BGTU, 0, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BGTU, 0, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGTU, 1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGTU, 1, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGTU, 0, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGTU, -1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGTU, 1, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGTU, -1, 1, true);
	}

	/**
	 * 
	 */
	@Test
	public void testBge() {
		performBranchInstructionTest(Instruction.OPCODE_BGE, 0, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGE, 0, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGE, 1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGE, 1, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BGE, 0, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BGE, -1, 0, false);
		performBranchInstructionTest(Instruction.OPCODE_BGE, 1, -1, true);
		performBranchInstructionTest(Instruction.OPCODE_BGE, -1, 1, false);
	}

	/**
	 * 
	 */
	@Test
	public void testBgeu() {
		performBranchInstructionTest(Instruction.OPCODE_BGEU, 0, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGEU, 0, 1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGEU, 1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGEU, 1, 1, true);
		performBranchInstructionTest(Instruction.OPCODE_BGEU, 0, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGEU, -1, 0, true);
		performBranchInstructionTest(Instruction.OPCODE_BGEU, 1, -1, false);
		performBranchInstructionTest(Instruction.OPCODE_BGEU, -1, 1, true);
	}

}
