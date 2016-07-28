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
public class CpuJumpInstructionsTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testJump() {
		addJ(Instruction.OPCODE_J, 0x10);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x20000044, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJumpFarAway() {
		addJ(Instruction.OPCODE_J, 0x01000000);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x24000004, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJumpBackwards() {
		addJ(Instruction.OPCODE_J, -0x10);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x1fffffc4, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJumpFarAwayBackwards() {
		addJ(Instruction.OPCODE_J, 0x02000000);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x18000004, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJumpNotInfluencedByRegister31() {
		addLoadTestWord(31, 0x12345678);
		addJ(Instruction.OPCODE_J, 0x10);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000048, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0x12345678);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJumpRegister() {
		addLoadTestWord(7, 0xcdef1234);
		addRRR(Instruction.OPCODE_JR, 7, 0, 0);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x0def1234, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJumpRegisterToMisaligned() {
		addLoadTestWord(7, 0xcdef0123);
		addRRR(Instruction.OPCODE_JR, 7, 0, 0);
		addOutputRegister(31);
		cpu.step(); // load destination address
		cpu.step(); // jump
		cpu.step(); // exception: misaligned PC
		cpu.step(); // first instruction of the exception handler
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000004, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJal() {
		addJ(Instruction.OPCODE_JAL, 0x10);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x20000044, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0xe0000004);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJalFarAway() {
		addJ(Instruction.OPCODE_JAL, 0x01000000);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x24000004, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0xe0000004);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJalBackwards() {
		addJ(Instruction.OPCODE_JAL, -0x10);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x1fffffc4, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0xe0000004);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJalFarAwayBackwards() {
		addJ(Instruction.OPCODE_JAL, 0x02000000);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x18000004, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, true);
		bus.assertWriteValues(0xe0000004);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJalNotInfluencedByRegister31() {
		addLoadTestWord(31, 0x12345678);
		addJ(Instruction.OPCODE_JAL, 0x10);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000048, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0xe0000008);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJalr() {
		addLoadTestWord(7, 0xcdef1234);
		addRRR(Instruction.OPCODE_JALR, 7, 0, 0);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x0def1234, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0xe0000008);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJalrToMisaligned() {
		addLoadTestWord(7, 0xcdef0123);
		addRRR(Instruction.OPCODE_JALR, 7, 0, 0);
		addOutputRegister(31);
		cpu.step(); // load destination address
		cpu.step(); // jump
		cpu.step(); // exception: misaligned PC
		cpu.step(); // first instruction of the exception handler
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000004, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0xe0000008);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testJalr31() {
		addLoadTestWord(31, 0xcdef1234);
		addRRR(Instruction.OPCODE_JALR, 31, 0, 0);
		addOutputRegister(31);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x0def1234, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0xe0000008);
		bus.assertAllRead();
	}

}
