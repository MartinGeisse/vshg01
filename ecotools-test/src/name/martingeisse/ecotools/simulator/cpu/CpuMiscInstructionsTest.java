/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;

import org.junit.Test;

/**
 * 
 */
public class CpuMiscInstructionsTest extends AbstractCpuTest {

	/**
	 * 
	 */
	@Test
	public void testLdhi() {
		performRRIInstructionTest(Instruction.OPCODE_LDHI, 0x12345678, 0xabcd, 0xabcd0000);
	}
	
	/**
	 * 
	 */
	@Test
	public void testLdhiIntoRegisterWithPreviousContents() {
		cpu.reset();
		addLoadTestWord(1, 0x12345678);
		addRRI(Instruction.OPCODE_LDHI, 0, 1, 0xabcd);
		addOutputRegister(1);
		cpu.step();
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004, 0x20000008, 0x3ffffff0);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, true);
		bus.assertWriteValues(0xabcd0000);
		bus.assertAllRead();
	}

	/**
	 * 
	 */
	@Test
	public void testTrap() {
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_TRAP, 7, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step(); // first NOP
		cpu.step(); // second NOP
		cpu.step(); // third NOP
		cpu.step(); // fourth NOP
		cpu.step(); // trap
		cpu.step(); // first instruction of the exception handler
		bus.assertAddresses(0x20000000, 0x20000004, 0x20000008, 0x2000000c, 0x20000010, 0x20000004);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false, false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
	}
	
	/**
	 * 
	 */
	@Test
	public void testRfx() {
		addLoadTestWord(30, 0x12345678);
		addRRR(Instruction.OPCODE_RFX, 0, 0, 0);
		cpu.step();
		cpu.step();
		assertEquals(0x12345678, cpu.getPc().getValue());
	}
	
	/**
	 * 
	 */
	@Test
	public void testMvtsMvfsPsw() {
		assertEquals(0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		
		addLoadTestWord(4, 0x12345678);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_PSW);
		cpu.step();
		cpu.step();
		bus.assertAddresses(0x20000000, 0x3ffffff0, 0x20000004);
		bus.assertAccessSizes(BusAccessSize.WORD, BusAccessSize.WORD, BusAccessSize.WORD);
		bus.assertWrites(false, false, false);
		bus.assertWriteValues();
		bus.assertAllRead();
		
		assertEquals(0x12345678, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0x12345678, cpu.getGeneralRegisters().read(4, false));
		assertEquals(0, cpu.getGeneralRegisters().read(7, false));

		addRRI(Instruction.OPCODE_MVFS, 0, 7, ISpecialRegisterFile.INDEX_PSW);
		cpu.step();
		bus.assertAddresses(0x20000008);
		bus.assertAccessSizes(BusAccessSize.WORD);
		bus.assertWrites(false);
		bus.assertWriteValues();
		bus.assertAllRead();
		
		assertEquals(0x12345678, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		assertEquals(0x12345678, cpu.getGeneralRegisters().read(4, false));
		assertEquals(0x12345678, cpu.getGeneralRegisters().read(7, false));
		
	}

	/**
	 * 
	 */
	@Test
	public void testPswRetainedBits() {
		addLoadTestWord(4, 0xffffffff);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_PSW);
		cpu.step();
		cpu.step();
		assertEquals(0xffffffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, 0xffffffff, false);
		assertEquals(0xffffffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testTlbIndexRegister() {
		assertEquals(0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));

		addLoadTestWord(4, 0xffffffff);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_INDEX);
		cpu.step();
		cpu.step();
		
		assertEquals(0x1f, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		
		addLoadTestWord(4, 0x12345678);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_INDEX);
		cpu.step();
		cpu.step();

		assertEquals(0x18, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));

		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_INDEX, 0xffffffff, false);
		assertEquals(0x1f, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
	}

	/**
	 * 
	 */
	@Test
	public void testTlbEntryHighRegister() {
		assertEquals(0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));

		addLoadTestWord(4, 0xffffffff);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH);
		cpu.step();
		cpu.step();
		
		assertEquals(0xfffff000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		
		addLoadTestWord(4, 0x12345678);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH);
		cpu.step();
		cpu.step();

		assertEquals(0x12345000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));

		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0xffffffff, false);
		assertEquals(0xfffff000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
	}

	/**
	 * 
	 */
	@Test
	public void testTlbEntryLowRegister() {
		assertEquals(0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

		addLoadTestWord(4, 0xffffffff);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW);
		cpu.step();
		cpu.step();
		
		assertEquals(0xfffff003, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));
		
		addLoadTestWord(4, 0x12345678);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW);
		cpu.step();
		cpu.step();

		assertEquals(0x12345000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, 0xffffffff, false);
		assertEquals(0xfffff003, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));
	}

	/**
	 * 
	 */
	@Test
	public void testTlbEntryBadAddressRegister() {
		assertEquals(0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));

		addLoadTestWord(4, 0xffffffff);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS);
		cpu.step();
		cpu.step();
		
		assertEquals(0xffffffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		
		addLoadTestWord(4, 0x12345678);
		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS);
		cpu.step();
		cpu.step();

		assertEquals(0x12345678, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));

		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, 0xffffffff, false);
		assertEquals(0xffffffff, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
	}

	/**
	 * 
	 */
	@Test
	public void testMvtsInvalidRegisterIndex() {

		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		assertEquals(cpu.getPc().getValue(), 0xe0000010);

		addRRI(Instruction.OPCODE_MVTS, 0, 4, ISpecialRegisterFile.SIZE);
		cpu.step();
		assertEquals(cpu.getPc().getValue(), 0xe0000004);
		
	}

	/**
	 * 
	 */
	@Test
	public void testMvfsInvalidRegisterIndex() {

		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		addRRR(Instruction.OPCODE_ADD, 0, 0, 0);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		assertEquals(cpu.getPc().getValue(), 0xe0000010);

		addRRI(Instruction.OPCODE_MVFS, 0, 4, ISpecialRegisterFile.SIZE);
		cpu.step();
		assertEquals(cpu.getPc().getValue(), 0xe0000004);
		
	}
	
	/**
	 * Test that TBS can be executed at all.
	 * The main test for this instruction is in {@link MemoryManagementUnitTest}.
	 */
	@Test
	public void testTbs() {

		/** add some mappings (one page number occurs twice) **/
		cpu.getMemoryManagementUnit().setTlbEntry(3, 0x12345000, 0x55555003, false);
		cpu.getMemoryManagementUnit().setTlbEntry(6, 0x22222000, 0x66666003, false);
		cpu.getMemoryManagementUnit().setTlbEntry(7, 0x22222000, 0x77777003, false);
		
		/** have the CPU execute TBS **/
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345678, false);
		addRRR(Instruction.OPCODE_TBS, 0, 0, 0);
		cpu.step();
		
		/** ensure that the TLB index register has the correct value **/
		assertEquals(3, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertEquals(0x12345000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

	}
	
	/**
	 * The invalid TLB index must be correctly visible to the CPU.
	 */
	@Test
	public void testTbsInvalid() {

		/** have the CPU execute TBS **/
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345678, false);
		addRRR(Instruction.OPCODE_TBS, 0, 0, 0);
		addRRI(Instruction.OPCODE_MVFS, 0, 1, ISpecialRegisterFile.INDEX_TLB_INDEX);
		cpu.step();
		cpu.step();
		
		/** ensure that the general-purpose register 1 has the invalid index value **/
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, cpu.getGeneralRegisters().read(1, false));

	}
	
	/**
	 * 
	 */
	@Test
	public void testTbwr() {
		
		/** set the random counter to 7 **/
		cpu.getMemoryManagementUnit().updateRandomCounter();
		cpu.getMemoryManagementUnit().updateRandomCounter();
		cpu.getMemoryManagementUnit().updateRandomCounter();
		assertEquals(7, cpu.getMemoryManagementUnit().getRandomCounter());

		/** have the CPU execute TBWR to write something to the TLB **/
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345000, false);
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, 0xabcde003, false);
		addRRR(Instruction.OPCODE_TBWR, 0, 0, 0);
		cpu.step();

		/** ensure that TLB entry 7 has been modified **/
		assertEquals(0x12345000, cpu.getMemoryManagementUnit().getTlbEntryHigh(7));
		assertEquals(0xabcde003, cpu.getMemoryManagementUnit().getTlbEntryLow(7));

	}

	/**
	 * 
	 */
	@Test
	public void testTbwi() {
		
		/** setup special registers for indexed writing to the TLB **/
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_INDEX, 14, false);
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345000, false);
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, 0xabcde003, false);
		
		/** have the CPU execute TBWI to write something to the TLB **/
		addRRR(Instruction.OPCODE_TBWI, 0, 0, 0);
		cpu.step();

		/** ensure that TLB entry 14 has been modified **/
		assertEquals(0x12345000, cpu.getMemoryManagementUnit().getTlbEntryHigh(14));
		assertEquals(0xabcde003, cpu.getMemoryManagementUnit().getTlbEntryLow(14));

	}

	/**
	 * 
	 */
	@Test
	public void testTbri() {
		
		/** put some fake values into the TLB **/
		cpu.getMemoryManagementUnit().setTlbEntry(14, 0x12345000, 0xabcde003, false);
		
		/** setup special registers for indexed reading from the TLB **/
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_TLB_INDEX, 14, false);
		
		/** have the CPU execute TBRI to read something from the TLB **/
		addRRR(Instruction.OPCODE_TBRI, 0, 0, 0);
		cpu.step();

		/** ensure that TLB entry 14 has been read to the special registers **/
		assertEquals(0x12345000, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0xabcde003, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

	}

}
