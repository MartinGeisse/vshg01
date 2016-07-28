/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

import org.junit.Test;

/**
 * 
 */
public class CpuBusInstructionsTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testStw() {
		MockCpuUserInterface userInterface = new MockCpuUserInterface();
		cpu.setUserInterface(userInterface);
		assertFalse(userInterface.isStore());
		
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_STW, 5, 7, 4);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x0234567c, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, true, false, true);
		bus.assertWriteValues(0xabcd0123, 0xabcd0123);
		bus.assertAllRead();
		
		assertTrue(userInterface.isStore());
	}

	/**
	 * 
	 */
	@Test
	public void testStwMisaligned() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_STW, 5, 7, 2);
		addRRI(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testSth() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_STH, 5, 7, 2);
		addRRI(Instruction.OPCODE_STH, 5, 7, -2);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x0234567a, 0x2000000c, 0x02345676);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.WORD, BusAccessSize.HALFWORD);
		bus.assertWrites(false, false, false, false, false, true, false, true);
		bus.assertWriteValues(0xabcd0123, 0xabcd0123);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testSthMisaligned() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_STW, 5, 7, 1);
		addRRI(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testStb() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_STB, 5, 7, 1);
		addRRI(Instruction.OPCODE_STB, 5, 7, -1);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x02345679, 0x2000000c, 0x02345677);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.BYTE, BusAccessSize.WORD, BusAccessSize.BYTE);
		bus.assertWrites(false, false, false, false, false, true, false, true);
		bus.assertWriteValues(0xabcd0123, 0xabcd0123);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdw() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDW, 5, 7, 4);
		bus.addReadValues(0x50607080);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x0234567c, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0x50607080);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdwMisaligned() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDW, 5, 7, 2);
		addRRI(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdhPositive() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDH, 5, 7, 2);
		bus.addReadValues(0x12345678);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x0234567a, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0x00005678);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdhNegative() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDH, 5, 7, 2);
		bus.addReadValues(0xdeadbeef);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x0234567a, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0xffffbeef);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdhMisaligned() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDH, 5, 7, 1);
		addRRI(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdhuSmall() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDHU, 5, 7, 2);
		bus.addReadValues(0x12345678);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x0234567a, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0x00005678);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdhuBig() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDHU, 5, 7, 2);
		bus.addReadValues(0xdeadbeef);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x0234567a, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.HALFWORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0x0000beef);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdhuMisaligned() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDHU, 5, 7, 1);
		addRRI(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x20000004);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdbPositive() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDB, 5, 7, 1);
		bus.addReadValues(0x12345678);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x02345679, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.BYTE, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0x00000078);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdbNegative() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDB, 5, 7, 1);
		bus.addReadValues(0xdeadbeef);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x02345679, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.BYTE, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0xffffffef);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdbuSmall() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDBU, 5, 7, 1);
		bus.addReadValues(0x12345678);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x02345679, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.BYTE, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0x00000078);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testLdbuBig() {
		addLoadTestWord(5, 0xc2345678);
		addLoadTestWord(7, 0xabcd0123);
		addRRI(Instruction.OPCODE_LDBU, 5, 7, 1);
		bus.addReadValues(0xdeadbeef);
		addRRI(Instruction.OPCODE_STW, 5, 7, -4);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0, 0x20000008, 0x02345679, 0x2000000c, 0x02345674);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.BYTE, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true);
		bus.assertWriteValues(0x000000ef);
		bus.assertAllRead();
	}

}
