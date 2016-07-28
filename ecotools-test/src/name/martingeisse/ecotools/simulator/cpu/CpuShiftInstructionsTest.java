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
public class CpuShiftInstructionsTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testSll() {
		performRRRInstructionTest(Instruction.OPCODE_SLL, 0x12345678, 0, 0x12345678);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 0x12345678, 1, 0x2468acf0);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 1, 5, 32);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 1, 31, 0x80000000);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 1, 32, 1);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 2, 30, 0x80000000);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 2, 31, 0);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 2, 32, 2);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 35, 0xffffffe1, 70);
	}

	/**
	 * 
	 */
	@Test
	public void testSlli() {
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 0x12345678, 0, 0x12345678);
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 0x12345678, 1, 0x2468acf0);
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 1, 5, 32);
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 1, 31, 0x80000000);
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 1, 32, 1);
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 2, 30, 0x80000000);
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 2, 31, 0);
		performRRIInstructionTest(Instruction.OPCODE_SLLI, 2, 32, 2);
		performRRRInstructionTest(Instruction.OPCODE_SLL, 35, 0xffe1, 70);
	}

	/**
	 * 
	 */
	@Test
	public void testSlr() {
		performRRRInstructionTest(Instruction.OPCODE_SLR, 0x12345678, 0, 0x12345678);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 0x12345678, 1, 0x091a2b3c);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 0, 4);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 1, 2);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 2, 1);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 3, 0);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 4, 0);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 31, 0);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 32, 4);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 4, 33, 2);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 20, 0xffffffe1, 10);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 0xffffffff, 1, 0x7fffffff);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 0xffffffff, 2, 0x3fffffff);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 0xc0000000, 1, 0x60000000);
		performRRRInstructionTest(Instruction.OPCODE_SLR, 0xc0000000, 2, 0x30000000);
	}

	/**
	 * 
	 */
	@Test
	public void testSlri() {
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 0x12345678, 0, 0x12345678);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 0x12345678, 1, 0x091a2b3c);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 0, 4);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 1, 2);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 2, 1);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 3, 0);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 4, 0);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 31, 0);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 32, 4);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 4, 33, 2);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 20, 0xffe1, 10);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 0xffffffff, 1, 0x7fffffff);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 0xffffffff, 2, 0x3fffffff);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 0xc0000000, 1, 0x60000000);
		performRRIInstructionTest(Instruction.OPCODE_SLRI, 0xc0000000, 2, 0x30000000);
	}

	/**
	 * 
	 */
	@Test
	public void testSar() {
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0x12345678, 0, 0x12345678);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0x12345678, 1, 0x091a2b3c);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 0, 4);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 1, 2);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 2, 1);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 3, 0);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 4, 0);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 31, 0);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 32, 4);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 4, 33, 2);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 20, 0xffffffe1, 10);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0xffffffff, 1, 0xffffffff);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0xffffffff, 2, 0xffffffff);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0xc0000000, 1, 0xe0000000);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0xc0000000, 2, 0xf0000000);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0x7fffffff, 1, 0x3fffffff);
		performRRRInstructionTest(Instruction.OPCODE_SAR, 0x80000000, 1, 0xc0000000);
	}

	/**
	 * 
	 */
	@Test
	public void testSari() {
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0x12345678, 0, 0x12345678);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0x12345678, 1, 0x091a2b3c);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 0, 4);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 1, 2);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 2, 1);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 3, 0);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 4, 0);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 31, 0);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 32, 4);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 4, 33, 2);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 20, 0xffe1, 10);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0xffffffff, 1, 0xffffffff);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0xffffffff, 2, 0xffffffff);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0xc0000000, 1, 0xe0000000);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0xc0000000, 2, 0xf0000000);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0x7fffffff, 1, 0x3fffffff);
		performRRIInstructionTest(Instruction.OPCODE_SARI, 0x80000000, 1, 0xc0000000);
	}

}
