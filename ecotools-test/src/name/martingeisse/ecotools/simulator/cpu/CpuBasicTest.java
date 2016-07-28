/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

import org.junit.Test;

/**
 * This test ensures that basic operation of the CPU is working to
 * allow further testing. If this test case fails, the other
 * test cases usually do not provide useful information.
 */
public class CpuBasicTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		assertEquals(0xe0000000, cpu.getPc().getValue());
		cpu.getPc().setValue(0x12345678, true);
		assertEquals(0x12345678, cpu.getPc().getValue());
		assertTrue(cpu.getBus() == bus);
		assertTrue(cpu.getGeneralRegisters() != null);
		assertTrue(cpu.getSpecialRegisters() != null);
		assertTrue(cpu.getMemoryManagementUnit() != null);
	}
	
	/**
	 * 
	 */
	@Test
	public void testUserInterfaceGetterSetter() {
		assertNull(cpu.getUserInterface());
		assertNull(cpu.getGeneralRegisters().getUserInterface());
		assertNull(cpu.getSpecialRegisters().getUserInterface());
		assertNull(cpu.getPc().getUserInterface());
		
		ICpuUserInterface ui1 = new MockCpuUserInterface();
		cpu.setUserInterface(ui1);
		assertSame(ui1, cpu.getUserInterface());
		assertSame(ui1, cpu.getGeneralRegisters().getUserInterface());
		assertSame(ui1, cpu.getSpecialRegisters().getUserInterface());
		assertSame(ui1, cpu.getPc().getUserInterface());
		
		ICpuUserInterface ui2 = new MockCpuUserInterface();
		ICpuUserInterface ui3 = new MockCpuUserInterface();
		ICpuUserInterface ui4 = new MockCpuUserInterface();
		cpu.getGeneralRegisters().setUserInterface(ui2);
		cpu.getSpecialRegisters().setUserInterface(ui3);
		cpu.getPc().setUserInterface(ui4);
		assertSame(ui1, cpu.getUserInterface());
		assertSame(ui2, cpu.getGeneralRegisters().getUserInterface());
		assertSame(ui3, cpu.getSpecialRegisters().getUserInterface());
		assertSame(ui4, cpu.getPc().getUserInterface());

		ICpuUserInterface ui5 = new MockCpuUserInterface();
		cpu.setUserInterface(ui5);
		assertSame(ui5, cpu.getUserInterface());
		assertSame(ui5, cpu.getGeneralRegisters().getUserInterface());
		assertSame(ui5, cpu.getSpecialRegisters().getUserInterface());
		assertSame(ui5, cpu.getPc().getUserInterface());

	}
	
	/**
	 * tests that the PC is working
	 */
	@Test
	public void testNopSequence() {
		bus.addReadValues(0, 0, 0);
		assertEquals(0xe0000000, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0xe0000004, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0xe0000008, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0xe000000c, cpu.getPc().getValue());
		bus.assertAddresses(0x20000000, 0x20000004, 0x20000008);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
		
		bus.addReadValues(0);
		cpu.step();
		bus.assertAddresses(0x2000000c);
		bus.assertAccessSizes(BusAccessSize.WORD);
		bus.assertWrites(false);
		bus.assertWriteValues();
		bus.assertAllRead();

		bus.addReadValues(0);
		cpu.reset();
		cpu.step();
		bus.assertAddresses(0x20000000);
		bus.assertAccessSizes(BusAccessSize.WORD);
		bus.assertWrites(false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}

	/**
	 * Simple test sequence to test basic testing functionality. This program loads
	 * the value 0x1234 into a data register, 0x0344 into an address register, and
	 * stores that value (word-sized) with an immediate offset of -0x8000. The
	 * latter offset makes the target address a physical address.
	 */
	@Test
	public void testAddZeroImmediate() {
		int loadDataInstruction = buildLoadSignedImmediate(0x01, 0x1234);
		int loadAddressInstruction = buildLoadSignedImmediate(0x02, 0x0344);
		int outputInstruction = buildRRI(0x35, 2, 1, 0x00008000);
		bus.addReadValues(loadDataInstruction, loadAddressInstruction, outputInstruction);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x20000004, 0x20000008, 0x3fff8344);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, true);
		bus.assertWriteValues(0x1234);
		bus.assertAllRead();
	}
	
	/**
	 * Tests the LW instruction to load test values.
	 */
	@Test
	public void testLoadTestWord() {
		addLoadTestWord(1, 0x12345678);
		addOutputRegister(1);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, true);
		bus.assertWriteValues(0x12345678);
		bus.assertAllRead();
	}
	
	/**
	 * Ensure that we have indeed 32 registers. This also tests that sign-extension of the
	 * loaded values works and that the zero register is immutable.
	 */
	@Test
	public void testRegisters() {
		
		/** the test values **/
		int[] loadValues = new int[] {
			0x00001234,
			0x00002345,
			0x00003456,
			0x00004567,
			0x00005678,
			0x00006789,
			0x0000789a,
			0x000089ab,
			0x00009abc,
			0x0000abcd,
			0x0000bcde,
			0x0000cdef,
			0x0000def0,
			0x00000001,
			0x00000002,
			0x00000003,
			0x00000004,
			0x00000005,
			0x00000006,
			0x00000007,
			0x00000008,
			0x00000009,
			0x0000000a,
			0x0000000b,
			0x0000000c,
			0x0000000d,
			0x0000000e,
			0x0000000f,
			0x00000010,
			0x00000011,
			0x00000012,
			0x00000013,
		};
		int[] expectedValues = new int[] {
			0x00000000,
			0x00002345,
			0x00003456,
			0x00004567,
			0x00005678,
			0x00006789,
			0x0000789a,
			0xffff89ab,
			0xffff9abc,
			0xffffabcd,
			0xffffbcde,
			0xffffcdef,
			0xffffdef0,
			0x00000001,
			0x00000002,
			0x00000003,
			0x00000004,
			0x00000005,
			0x00000006,
			0x00000007,
			0x00000008,
			0x00000009,
			0x0000000a,
			0x0000000b,
			0x0000000c,
			0x0000000d,
			0x0000000e,
			0x0000000f,
			0x00000010,
			0x00000011,
			0x00000012,
			0x00000013,
		};		
		/** add "load immediate" instructions **/
		for (int i=0; i<32; i++) {
			addLoadSignedImmediate(i, loadValues[i]);
		}
		
		/** add "output" instructions **/
		for (int i=0; i<32; i++) {
			addRRI(0x35, 0, i, 0x0000fff0);
		}
		
		/** run the CPU **/
		for (int i=0; i<64; i++) {
			cpu.step();
		}

		/** expected addresses (mixed instruction / data) **/
		int[] addresses = new int[3 * 32];
		for (int i=0; i<32; i++) {
			addresses[i] = 0x20000000 + 4*i;
		}
		for (int i=0; i<32; i++) {
			addresses[32 + 2*i] = 0x20000080 + 4*i;
			addresses[32 + 2*i + 1] = 0x3ffffff0;
		}

		/** expected access sizes (mixed instruction / data) **/
		BusAccessSize[] accessSizes = new BusAccessSize[3 * 32];
		Arrays.fill(accessSizes, BusAccessSize.WORD);

		/** expected write modes (mixed instruction / data) **/
		boolean[] writes = new boolean[3 * 32];
		for (int i=0; i<32; i++) {
			writes[i] = false;
		}
		for (int i=0; i<32; i++) {
			writes[32 + 2*i] = false;
			writes[32 + 2*i + 1] = true;
		}

		bus.assertAddresses(addresses);
		bus.assertAccessSizes(accessSizes);
		bus.assertWrites(writes);
		bus.assertWriteValues(expectedValues);
		bus.assertAllRead();
	}

	/**
	 * Basic testing for the TLB. Some tests rely on at least basic mapping
	 * to work. These tests observe behavior of the CPU while in user mode,
	 * which implies that no privileged addresses (and thus no direct-mapped
	 * addresses) can be used.
	 */
	@Test
	public void executeInstructionsFromMappedAddresses() {

		/** setup some mappings **/
		cpu.getMemoryManagementUnit().setTlbEntry(0, 0x12345000, 0x23ab8003, false);
		cpu.getMemoryManagementUnit().setTlbEntry(1, 0x22222000, 0x11111003, false);
		cpu.getMemoryManagementUnit().setTlbEntry(2, 0x33333000, 0x11111003, false);

		/** define instructions from that position, and also load/store from/to there **/
		addLoadTestWord(1, 0x12345678);
		addRRR(Instruction.OPCODE_JR, 1, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addLoadTestWord(2, 0x22222334);
		addRRI(Instruction.OPCODE_STW, 2, 1, 0x10);
		addRRI(Instruction.OPCODE_LDW, 2, 3, 0x50);
		bus.addReadValues(0);
		
		/** execute the instructions **/
		cpu.step();
		assertEquals(0xe0000004, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0x12345678, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0x1234567c, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0x12345680, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0x12345684, cpu.getPc().getValue());
		cpu.step();
		assertEquals(0x12345688, cpu.getPc().getValue());
		
		/** ensure that the bus operations were correct **/
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x23ab8678, 0x23ab867c, 0x3ffffff0, 0x23ab8680, 0x11111344, 0x23ab8684, 0x11111384);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false, false, true, false, false);
		bus.assertWriteValues(0x12345678);
		bus.assertAllRead();
		
	}
}
