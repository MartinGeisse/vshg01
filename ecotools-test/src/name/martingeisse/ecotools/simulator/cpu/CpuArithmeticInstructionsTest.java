/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

import org.junit.Test;

/**
 * 
 */
public class CpuArithmeticInstructionsTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testAdd() {
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 0, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 0, 1, 1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 1, 0, 1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 1, 1, 2);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 123, 456, 579);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 0, -1, -1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 0x70000000, 0x70000000, 0xe0000000);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 0x70000000, 0x70000000, -0x20000000);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, 0x80000000, 0x80000000, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, -1, -1, -2);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_ADD, -1, 1, 0);
	}

	/**
	 * 
	 */
	@Test
	public void testAddi() {
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 0, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 0, 1, 1);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 1, 0, 1);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 1, 1, 2);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 123, 456, 579);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 0, -1, -1);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 0, 0x7fff, 0x7fff);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, 0, 0x8000, 0xffff8000);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, -1, -1, -2);
		performRRIInstructionTest(Instruction.OPCODE_ADDI, -1, 1, 0);
	}

	/**
	 * 
	 */
	@Test
	public void testSub() {
		performRRRInstructionTest(Instruction.OPCODE_SUB, 0, 0, 0);
		performRRRInstructionTest(Instruction.OPCODE_SUB, 0, 1, -1);
		performRRRInstructionTest(Instruction.OPCODE_SUB, 1, 0, 1);
		performRRRInstructionTest(Instruction.OPCODE_SUB, 1, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_SUB, 123, 456, -333);
		performRRRInstructionTest(Instruction.OPCODE_SUB, 0, -1, 1);
		performRRRInstructionTest(Instruction.OPCODE_SUB, 0x70000000, 0x70000000, 0);
		performRRRInstructionTest(Instruction.OPCODE_SUB, 0x80000000, 0x80000000, 0);
		performRRRInstructionTest(Instruction.OPCODE_SUB, -1, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_SUB, -1, 1, -2);
	}

	/**
	 * 
	 */
	@Test
	public void testSubi() {
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 0, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 0, 1, -1);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 1, 0, 1);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 1, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 123, 456, -333);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 0, -1, 1);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 0, 0x7fff, 0xffff8001);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, 0, 0x8000, 0x8000);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, -1, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_SUBI, -1, 1, -2);
	}

	/**
	 * TODO: determine expected behavior in corner cases.
	 * Currently implements Java behavior: Signed multiplication
	 * yields the result of mathematical multiplication, represented
	 * in a sufficiently large two's complement format and then
	 * truncated to 32 bits.
	 */
	@Test
	public void testMul() {
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0, 1, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 1, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 1, 1, 1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 12, 45, 540);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0, -1, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, -1, -1, 1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, -1, 1, -1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x7fffffff, 1, 0x7fffffff);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x80000000, 1, 0x80000000);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x80000001, 1, 0x80000001);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x7fffffff, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x80000000, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x80000001, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x7fffffff, -1, 0x80000001);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x80000000, -1, 0x80000000);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MUL, 0x80000001, -1, 0x7fffffff);
	}

	/**
	 * 
	 */
	@Test
	public void testMuli() {
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 1, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 1, 1, 1);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 12, 45, 540);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULI, -1, -1, 1);
		performRRIInstructionTest(Instruction.OPCODE_MULI, -1, 1, -1);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x7fffffff, 1, 0x7fffffff);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x80000000, 1, 0x80000000);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x80000001, 1, 0x80000001);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x7fffffff, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x80000000, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x80000001, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x7fffffff, -1, 0x80000001);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x80000000, -1, 0x80000000);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 0x80000001, -1, 0x7fffffff);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 2, 0x7fff, 65534);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 2, 0x8000, -65536);
		performRRIInstructionTest(Instruction.OPCODE_MULI, 2, 0x8001, -65534);
	}

	/**
	 * in a 32x32 -> 32 multiplication, "unsigned" and "signed" are
	 * the same, so the result are just copied from MUL
	 */
	@Test
	public void testMulu() {
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0, 1, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 1, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 1, 1, 1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 12, 45, 540);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0, -1, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, -1, -1, 1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, -1, 1, -1);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x7fffffff, 1, 0x7fffffff);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x80000000, 1, 0x80000000);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x80000001, 1, 0x80000001);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x7fffffff, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x80000000, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x80000001, 0, 0);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x7fffffff, -1, 0x80000001);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x80000000, -1, 0x80000000);
		performCommutativeRRRInstructionTest(Instruction.OPCODE_MULU, 0x80000001, -1, 0x7fffffff);
	}

	/**
	 * See above: the only difference to MULI is that the immediate
	 * operand is zero-extended.
	 */
	@Test
	public void testMului() {
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 1, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 1, 1, 1);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 12, 45, 540);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, -1, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, -1, -1, -0xffff); // -1 * 0xffff
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 1, -1, 0xffff);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, -1, 1, -1);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x7fffffff, 1, 0x7fffffff);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x80000000, 1, 0x80000000);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x80000001, 1, 0x80000001);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x7fffffff, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x80000000, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x80000001, 0, 0);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x7fffffff, -1, 0x7fff0001);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x80000000, -1, 0x80000000);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 0x80000001, -1, 0x8000ffff);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 2, 0x7fff, 0x0000fffe);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 2, 0x8000, 0x00010000);
		performRRIInstructionTest(Instruction.OPCODE_MULUI, 2, 0x8001, 0x00010002);
	}

	/**
	 * TODO: determine expected behavior in corner cases.
	 * Currently implements Java behavior: Rounding towards 0,
	 * and 0x80000000 / -1 = 0x80000000
	 */
	@Test
	public void testDiv() {
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 1, 1, 1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 12, 3, 4);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 14, 3, 4);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIV, -1, 1, -1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, -12, 3, -4);
		performRRRInstructionTest(Instruction.OPCODE_DIV, -14, 3, -4);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 1, -1, -1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 12, -3, -4);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 14, -3, -4);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIV, -1, -1, 1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, -12, -3, 4);
		performRRRInstructionTest(Instruction.OPCODE_DIV, -14, -3, 4);

		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x7fffffff, 1, 0x7fffffff);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000000, 1, 0x80000000);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000001, 1, 0x80000001);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x7fffffff, -1, 0x80000001);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000000, -1, 0x80000000);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000001, -1, 0x7fffffff);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x7fffffff, 0x7fffffff, 1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000000, 0x7fffffff, -1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000001, 0x7fffffff, -1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x7fffffff, 0x80000000, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000000, 0x80000000, 1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000001, 0x80000000, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x7fffffff, 0x80000001, -1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000000, 0x80000001, 1);
		performRRRInstructionTest(Instruction.OPCODE_DIV, 0x80000001, 0x80000001, 1);
	}

	/**
	 * Tests DIV in division by zero.
	 */
	@Test
	public void testDiv0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addLoadTestWord(2, 0);
		addRRR(Instruction.OPCODE_DIV, 1, 2, 3);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testDivi() {
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 1, 1, 1);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 12, 3, 4);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 14, 3, 4);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, -1, 1, -1);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, -12, 3, -4);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, -14, 3, -4);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 0, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 1, -1, -1);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 12, -3, -4);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 14, -3, -4);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, 0, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, -1, -1, 1);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, -12, -3, 4);
		performRRIInstructionTest(Instruction.OPCODE_DIVI, -14, -3, 4);
	}

	/**
	 * Tests DIVI in division by zero.
	 */
	@Test
	public void testDivi0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addRRI(Instruction.OPCODE_DIVI, 1, 2, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testDivu() {
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 0, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 1, 1, 1);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 12, 3, 4);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 14, 3, 4);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 0, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 0xffffffff, 1, 0xffffffff);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 0xffffffff, 3, 0x55555555);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 0xffffffff, 0x100000, 0xfff);
		performRRRInstructionTest(Instruction.OPCODE_DIVU, 1, -1, 0);
	}

	/**
	 * Tests DIVU in division by zero.
	 */
	@Test
	public void testDivu0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addLoadTestWord(2, 0);
		addRRR(Instruction.OPCODE_DIVU, 1, 2, 3);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testDivui() {
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 1, 1, 1);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 12, 3, 4);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 14, 3, 4);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 0xffffffff, 1, 0xffffffff);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 0xffffffff, 3, 0x55555555);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 0xffffffff, 0x8000, 0x1ffff);
		performRRIInstructionTest(Instruction.OPCODE_DIVUI, 1, -1, 0);
	}

	/**
	 * Tests DIV in division by zero.
	 */
	@Test
	public void testDivui0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addRRI(Instruction.OPCODE_DIVUI, 1, 2, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false);
		bus.assertAllRead();
	}

	/**
	 * TODO: determine expected behavior in corner cases.
	 * Currently implements Java behavior:
	 *   1. (a / b) * b + (a % b)
	 *      even in special cases such as a = 0x80000000, b = -1
	 *   2. sign(a%b) = sign(a)
	 *   3. abs(a%b) < abs(b)
	 */
	@Test
	public void testRem() {
		performRRRInstructionTest(Instruction.OPCODE_REM, 0, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 1, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 12, 3, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 14, 3, 2);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, -1, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, -12, 3, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, -14, 3, -2);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 1, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 12, -3, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 14, -3, 2);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, -1, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, -12, -3, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, -14, -3, -2);

		performRRRInstructionTest(Instruction.OPCODE_REM, 0x7fffffff, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000000, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000001, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x7fffffff, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000000, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000001, -1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x7fffffff, 0x7fffffff, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000000, 0x7fffffff, -1);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000001, 0x7fffffff, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x7fffffff, 0x80000000, 0x7fffffff);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000000, 0x80000000, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000001, 0x80000000, 0x80000001);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x7fffffff, 0x80000001, 0);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000000, 0x80000001, -1);
		performRRRInstructionTest(Instruction.OPCODE_REM, 0x80000001, 0x80000001, 0);
	}

	/**
	 * Tests REM in division by zero.
	 */
	@Test
	public void testRem0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addLoadTestWord(2, 0);
		addRRR(Instruction.OPCODE_REM, 1, 2, 3);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testRemi() {
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 1, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 12, 3, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 14, 3, 2);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, -1, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, -12, 3, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, -14, 3, -2);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 1, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 12, -3, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 14, -3, 2);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, -1, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, -12, -3, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, -14, -3, -2);

		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x7fffffff, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000000, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000001, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x7fffffff, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000000, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000001, -1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x7fffffff, 0x7fff, 1); // = 0xffff%0x7fff = 0x8000%0x7fff = 1
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000000, 0x7fff, -2); // one more in magnitude, but negative
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000001, 0x7fff, -1); // original magnitude but negative
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x7fffffff, 0x8000, 0x7fff);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000000, 0x8000, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000001, 0x8000, 0xffff8001);
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x7fffffff, 0x8001, 1); // same as above. sign of divisor doesn't matter
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000000, 0x8001, -2); // same as above. sign of divisor doesn't matter
		performRRIInstructionTest(Instruction.OPCODE_REMI, 0x80000001, 0x8001, -1); // same as above. sign of divisor doesn't matter
	}

	/**
	 * Tests REMI in division by zero.
	 */
	@Test
	public void testRemi0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addRRI(Instruction.OPCODE_REMI, 1, 2, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testRemu() {
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 1, 1, 0);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 12, 3, 0);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 14, 3, 2);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0, 1, 0);

		performRRRInstructionTest(Instruction.OPCODE_REMU, 0x7fffffff, 4, 3);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0x80000000, 4, 0);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0x80000001, 4, 1);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0xffffffff, 4, 3);

		performRRRInstructionTest(Instruction.OPCODE_REMU, 0x7fffffff, 0xfffffff0, 0x7fffffff);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0x80000000, 0xfffffff0, 0x80000000);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0x80000001, 0xfffffff0, 0x80000001);
		performRRRInstructionTest(Instruction.OPCODE_REMU, 0xffffffff, 0xfffffff0, 0xf);

	}

	/**
	 * Tests REMU in division by zero.
	 */
	@Test
	public void testRemu0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addLoadTestWord(2, 0);
		addRRR(Instruction.OPCODE_REMU, 1, 2, 3);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testRemui() {
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 1, 1, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 12, 3, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 14, 3, 2);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0, 1, 0);

		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0x7fffffff, 4, 3);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0x80000000, 4, 0);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0x80000001, 4, 1);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0xffffffff, 4, 3);

		/**
		 * 0x7fffffff == 0x7000000f == 7 * 0x10000000 + 0xf
		 * == 7 * 0x00010000 + 0xf
		 * == 7 * 0x00000010 + 0xf
		 * == 0x7f
		 */
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0x7fffffff, 0xfff0, 0x7f);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0x80000000, 0xfff0, 0x80);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0x80000001, 0xfff0, 0x81);
		performRRIInstructionTest(Instruction.OPCODE_REMUI, 0xffffffff, 0xfff0, 0xff);

	}

	/**
	 * Tests REMUI in division by zero.
	 */
	@Test
	public void testRemui0() {
		cpu.reset();
		addLoadTestWord(1, 1);
		addRRI(Instruction.OPCODE_REMUI, 1, 2, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000004); // last one is the interrupt entry
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false);
		bus.assertAllRead();
	}
}
