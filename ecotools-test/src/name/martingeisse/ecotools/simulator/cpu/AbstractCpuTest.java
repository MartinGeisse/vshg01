/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

/**
 * 
 */
public class AbstractCpuTest {

	/**
	 * the cpu
	 */
	protected Cpu cpu;
	
	/**
	 * the bus
	 */
	protected MockBus bus;

	/**
	 * Constructor
	 */
	public AbstractCpuTest() {
		cpu = new Cpu();
		bus = new MockBus();
		assertNull(cpu.getBus());
		cpu.setBus(bus);
		assertSame(bus, cpu.getBus());
	}
	
	/**
	 * Returns an RRR-type instruction
	 * @param opcode the opcode
	 * @param reg1 the first register index
	 * @param reg2 the second register index
	 * @param reg3 the third register index
	 * @return Returns the instruction
	 */
	public int buildRRR(int opcode, int reg1, int reg2, int reg3) {
		return ((opcode & 0x3f) << 26) | ((reg1 & 0x1f) << 21) | ((reg2 & 0x1f) << 16) | ((reg3 & 0x1f) << 11);
	}

	/**
	 * Adds an RRR-type instruction to the bus read value list.
	 * @param opcode the opcode
	 * @param reg1 the first register index
	 * @param reg2 the second register index
	 * @param reg3 the third register index
	 */
	public void addRRR(int opcode, int reg1, int reg2, int reg3) {
		bus.addReadValues(buildRRR(opcode, reg1, reg2, reg3));
	}

	/**
	 * Returns an RRI-type instruction
	 * @param opcode the opcode
	 * @param reg1 the first register index
	 * @param reg2 the second register index
	 * @param immediate the immediate value
	 * @return Returns the instruction
	 */
	public int buildRRI(int opcode, int reg1, int reg2, int immediate) {
		return ((opcode & 0x3f) << 26) | ((reg1 & 0x1f) << 21) | ((reg2 & 0x1f) << 16) | (immediate & 0xffff);
	}

	/**
	 * Adds an RRI-type instruction to the bus read value list.
	 * @param opcode the opcode
	 * @param reg1 the first register index
	 * @param reg2 the second register index
	 * @param immediate the immediate value
	 */
	public void addRRI(int opcode, int reg1, int reg2, int immediate) {
		bus.addReadValues(buildRRI(opcode, reg1, reg2, immediate));
	}

	/**
	 * Returns a J-type instruction
	 * @param opcode the opcode
	 * @param offset the jump offset
	 * @return Returns the instruction
	 */
	public int buildJ(int opcode, int offset) {
		return ((opcode & 0x3f) << 26) | (offset & 0x03ffffff);
	}

	/**
	 * Adds a J-type instruction to the bus read value list.
	 * @param opcode the opcode
	 * @param offset the jump offset
	 */
	public void addJ(int opcode, int offset) {
		bus.addReadValues(buildJ(opcode, offset));
	}

	/**
	 * Returns a "load immediate" instruction for a signed 16-bit value
	 * @param reg the register index of the register to load the value into
	 * @param value the value to load
	 * @return Returns the instruction
	 */
	public int buildLoadSignedImmediate(int reg, int value) {
		return buildRRI(0x01, 0, reg, value);
	}

	/**
	 * Adds a "load immediate" instruction for a signed 16-bit value to the
	 * bus read value list.
	 * @param reg the register index of the register to load the value into
	 * @param value the value to load
	 */
	public void addLoadSignedImmediate(int reg, int value) {
		bus.addReadValues(buildLoadSignedImmediate(reg, value));
	}

	/**
	 * Returns a "load immediate" instruction for an unsigned 16-bit value
	 * @param reg the register index of the register to load the value into
	 * @param value the value to load
	 * @return Returns the instruction
	 */
	public int buildLoadUnsignedImmediate(int reg, int value) {
		return buildRRI(0x13, 0, reg, value);
	}

	/**
	 * Adds a "load immediate" instruction for an unsigned 16-bit value to the
	 * bus read value list.
	 * @param reg the register index of the register to load the value into
	 * @param value the value to load
	 */
	public void addLoadUnsignedImmediate(int reg, int value) {
		bus.addReadValues(buildLoadUnsignedImmediate(reg, value));
	}
	
	/**
	 * Builds an instruction that writes the specified register to physical
	 * address 0x3ffffff0.
	 * @param reg the register to output
	 * @return Returns the instruction.
	 */
	public int buildOutputRegister(int reg) {
		return buildRRI(0x35, 0, reg, 0x0000fff0);
	}
	
	/**
	 * Adds an instruction that writes the specified register to physical
	 * address 0x3ffffff0.
	 * @param reg the register to output
	 */
	public void addOutputRegister(int reg) {
		bus.addReadValues(buildOutputRegister(reg));
	}
	
	/**
	 * Adds an LW instruction and corresponding bus read value to load a
	 * 32-bit value into the specified register. The instruction always
	 * loads from physical address 0x3ffffff0.
	 * @param reg the register index of the register to load the value into
	 * @param value the value to load
	 */
	public void addLoadTestWord(int reg, int value) {
		bus.addReadValues(buildRRI(0x30, 0, reg, 0x0000fff0), value);
	}
	
	/**
	 * Tests the result of an RRR-style instruction that implements a commutative
	 * operator in both directions.
	 * @param opcode the opcode of the operator instruction
	 * @param x the first operand
	 * @param y the second operand
	 * @param expectedResult the expected result
	 */
	public void performCommutativeRRRInstructionTest(int opcode, int x, int y, int expectedResult) {
		performRRRInstructionTest(opcode, x, y, expectedResult);
		performRRRInstructionTest(opcode, y, x, expectedResult);
	}

	/**
	 * Tests the result of an RRR-style instruction that implements an operator.
	 * @param opcode the opcode of the operator instruction
	 * @param x the first operand
	 * @param y the second operand
	 * @param expectedResult the expected result
	 */
	public void performRRRInstructionTest(int opcode, int x, int y, int expectedResult) {
		cpu.reset();
		addLoadTestWord(1, x);
		addLoadTestWord(2, y);
		addRRR(opcode, 1, 2, 3);
		addOutputRegister(3);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x2000000c, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, true);
		bus.assertWriteValues(expectedResult);
		bus.assertAllRead();
	}

	/**
	 * Tests the result of an RRI-style instruction that implements an operator.
	 * @param opcode the opcode of the operator instruction
	 * @param x the first operand
	 * @param y the second operand (16-bit, extended to 32-bit using either sign or zero extension
	 * depending on the operator).
	 * @param expectedResult the expected result
	 */
	public void performRRIInstructionTest(int opcode, int x, int y, int expectedResult) {
		cpu.reset();
		addLoadTestWord(1, x);
		addRRI(opcode, 1, 2, y);
		addOutputRegister(2);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000008, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(expectedResult);
		bus.assertAllRead();
	}
	
	/**
	 * @param opcode the branch instruction opcode
	 * @param x the first operand
	 * @param y the second operand
	 * @param doesBranch whether the branch is expected to be taken
	 */
	public void performBranchInstructionTest(int opcode, int x, int y, boolean doesBranch) {
		cpu.reset();
		addLoadTestWord(1, x);
		addLoadTestWord(2, y);
		addRRI(opcode, 1, 2, 0xf000);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, doesBranch ? 0x1fffc00c : 0x2000000c);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}

}
