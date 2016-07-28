/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class MemoryManagementUnitTest {

	/**
	 * the specialRegisterFile
	 */
	private SpecialRegisterFile specialRegisterFile;
	
	/**
	 * the memoryManagementUnit
	 */
	private MemoryManagementUnit memoryManagementUnit;
	
	/**
	 * 
	 */
	@Before
	public void setUp() {
		specialRegisterFile = new SpecialRegisterFile();
		memoryManagementUnit = new MemoryManagementUnit(specialRegisterFile);
	}
	
	/**
	 * 
	 */
	@After
	public void tearDown() {
	}
	
	/**
	 * 
	 */
	@Test
	public void testEntryGetterSetter() {
		
		/** initial state **/
		assertEquals(0, memoryManagementUnit.getTlbEntryHigh(0));
		assertEquals(0, memoryManagementUnit.getTlbEntryLow(0));
		assertEquals(0, memoryManagementUnit.getTlbEntryHigh(5));
		assertEquals(0, memoryManagementUnit.getTlbEntryLow(5));
		assertEquals(0, memoryManagementUnit.getTlbEntryHigh(31));
		assertEquals(0, memoryManagementUnit.getTlbEntryLow(31));
		
		/** write some values into the TLB **/
		memoryManagementUnit.setTlbEntry(0, 0x12345678, 0x23456789, false);
		memoryManagementUnit.setTlbEntry(1, 0xffffffff, 0xffffffff, false);
		memoryManagementUnit.setTlbEntry(5, 0xabababab, 0xcdcdcdcd, false);
		memoryManagementUnit.setTlbEntry(34, 0x67676767, 0x43434343, false);
		
		/** read the values **/
		assertEquals(0x12345000, memoryManagementUnit.getTlbEntryHigh(0));
		assertEquals(0x23456001, memoryManagementUnit.getTlbEntryLow(0));
		assertEquals(0xfffff000, memoryManagementUnit.getTlbEntryHigh(1));
		assertEquals(0xfffff003, memoryManagementUnit.getTlbEntryLow(1));
		assertEquals(0x67676000, memoryManagementUnit.getTlbEntryHigh(2));
		assertEquals(0x43434003, memoryManagementUnit.getTlbEntryLow(2));
		assertEquals(0, memoryManagementUnit.getTlbEntryHigh(3));
		assertEquals(0, memoryManagementUnit.getTlbEntryLow(3));
		assertEquals(0xababa000, memoryManagementUnit.getTlbEntryHigh(5));
		assertEquals(0xcdcdc001, memoryManagementUnit.getTlbEntryLow(5));

	}
	
	/**
	 * 
	 */
	@Test
	public void testRandomCounter() {
		
		/** test getter / setter **/
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		memoryManagementUnit.setRandomCounter(17);
		assertEquals(17, memoryManagementUnit.getRandomCounter());
		memoryManagementUnit.updateRandomCounter();
		assertEquals(18, memoryManagementUnit.getRandomCounter());
		
		/** the setter must accept these values and handle the counter correctly **/
		memoryManagementUnit.setRandomCounter(4);
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		memoryManagementUnit.updateRandomCounter();
		assertEquals(5, memoryManagementUnit.getRandomCounter());
		
		memoryManagementUnit.setRandomCounter(31);
		assertEquals(31, memoryManagementUnit.getRandomCounter());
		memoryManagementUnit.updateRandomCounter();
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetRandomCounterTooLow() {
		memoryManagementUnit.setRandomCounter(3);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetRandomCounterTooHigh() {
		memoryManagementUnit.setRandomCounter(32);
	}
	
	/**
	 * 
	 */
	@Test
	public void testFindTlbEntry() {
		
		/** empty TLB -> no entry **/
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, memoryManagementUnit.findTlbEntry(0x12345678));
		
		/** insert a single entry, then find it **/
		memoryManagementUnit.setTlbEntry(3, 0x12345678, 0x10203040, false);
		assertEquals(3, memoryManagementUnit.findTlbEntry(0x12345678));
		
		/** entries with other page numbers should not hurt **/
		memoryManagementUnit.setTlbEntry(7, 0xabababab, 0x00000000, false);
		memoryManagementUnit.setTlbEntry(15, 0xdfdfdfdf, 0x00000000, false);
		assertEquals(3, memoryManagementUnit.findTlbEntry(0x12345678));
		
		/** if we have the same page number multiple times, the greatest index wins **/
		memoryManagementUnit.setTlbEntry(6, 0x12345678, 0x10000000, false);
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x12345678));

		memoryManagementUnit.setTlbEntry(1, 0x12345678, 0x20000000, false);
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x12345678));
		
		/** test different virtual addresses in the same page **/
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x12345000));
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x12345fff));
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x12345aba));
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x123455b0));

		/** test different virtual addresses in different pages **/
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, memoryManagementUnit.findTlbEntry(0x12346000));
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, memoryManagementUnit.findTlbEntry(0x12344fff));
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, memoryManagementUnit.findTlbEntry(0x12344678));

		/** the WRITE and VALID bits should have no influence **/
		memoryManagementUnit.setTlbEntry(6, 0x12345678, 0x10000003, false);
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x12345000));
		assertEquals(6, memoryManagementUnit.findTlbEntry(0x12345fff));

	}
	
	/**
	 * @throws CpuException ...
	 */
	@Test
	public void testMapDirectMappedAddresses() throws CpuException {
		
		/** test some addresses **/
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x00000000, memoryManagementUnit.mapAddressForCpu(0xc0000000, false));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x20000000, memoryManagementUnit.mapAddressForCpu(0xe0000000, true));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x0def1234, memoryManagementUnit.mapAddressForCpu(0xcdef1234, false));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		
		/** even if we insert a mapping for such an address, it is still direct-mapped **/
		memoryManagementUnit.setTlbEntry(0, 0x0def1234, 0x56756003, false);
		assertEquals(0x0def1234, memoryManagementUnit.mapAddressForCpu(0xcdef1234, true));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		
	}

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test
	public void testVisualizeDirectMappedAddresses() throws MemoryVisualizationException {
		
		/** test some addresses **/
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x00000000, memoryManagementUnit.mapAddressForVisualization(0xc0000000));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x0def1234, memoryManagementUnit.mapAddressForVisualization(0xcdef1234));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		
		/** even if we insert a mapping for such an address, it is still direct-mapped **/
		memoryManagementUnit.setTlbEntry(0, 0x0def1234, 0x56756003, false);
		assertEquals(0x0def1234, memoryManagementUnit.mapAddressForVisualization(0xcdef1234));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		
	}

	/**
	 * @throws CpuException ...
	 */
	@Test
	public void testPageMapping() throws CpuException {
		
		/** add some mappings (one page number occurs twice) **/
		memoryManagementUnit.setTlbEntry(3, 0x12345000, 0x55555003, false);
		memoryManagementUnit.setTlbEntry(6, 0x22222000, 0x66666003, false);
		memoryManagementUnit.setTlbEntry(7, 0x22222000, 0x77777003, false);
		
		/** test mapping **/
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x55555678, memoryManagementUnit.mapAddressForCpu(0x12345678, false));
		assertEquals(5, memoryManagementUnit.getRandomCounter());
		assertEquals(0x55555000, memoryManagementUnit.mapAddressForCpu(0x12345000, true));
		assertEquals(6, memoryManagementUnit.getRandomCounter());
		assertEquals(0x55555fff, memoryManagementUnit.mapAddressForCpu(0x12345fff, false));
		assertEquals(7, memoryManagementUnit.getRandomCounter());
		assertEquals(0x77777222, memoryManagementUnit.mapAddressForCpu(0x22222222, true));
		assertEquals(8, memoryManagementUnit.getRandomCounter());
		
	}

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test
	public void testVisualizePageMapping() throws MemoryVisualizationException {
		
		/** add some mappings (one page number occurs twice) **/
		memoryManagementUnit.setTlbEntry(3, 0x12345000, 0x55555003, false);
		memoryManagementUnit.setTlbEntry(6, 0x22222000, 0x66666003, false);
		memoryManagementUnit.setTlbEntry(7, 0x22222000, 0x77777003, false);
		
		/** test mapping **/
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x55555678, memoryManagementUnit.mapAddressForVisualization(0x12345678));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x55555000, memoryManagementUnit.mapAddressForVisualization(0x12345000));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x55555fff, memoryManagementUnit.mapAddressForVisualization(0x12345fff));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0x77777222, memoryManagementUnit.mapAddressForVisualization(0x22222222));
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		
	}

	/**
	 * 
	 */
	@Test
	public void testTlbMiss() {
		int code = -1;
		try {
			memoryManagementUnit.mapAddressForCpu(0x12345678, false);
		} catch (CpuException e) {
			code = e.getCode();
		}
		assertEquals(CpuException.CODE_TLB_MISS, code);
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));
		assertEquals(0x12345000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x12345678, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
	}

	/**
	 * 
	 */
	@Test
	public void testVisualizeTlbMiss() {
		try {
			memoryManagementUnit.mapAddressForVisualization(0x12345678);
			fail();
		} catch (MemoryVisualizationException e) {
			assertTrue(e.getMessage().indexOf("miss") != -1);
		}
	}

	/**
	 * 
	 */
	@Test
	public void testTlbInvalidException() {
		int code = -1;
		try {
			memoryManagementUnit.setTlbEntry(15, 0x12345000, 0x22222000, false);
			memoryManagementUnit.mapAddressForCpu(0x12345678, false);
		} catch (CpuException e) {
			code = e.getCode();
		}
		assertEquals(CpuException.CODE_TLB_INVALID, code);
		assertEquals(15, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0x22222000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));
		assertEquals(0x12345000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x12345678, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
	}

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test
	public void testVisualizeTlbInvalidBit() throws MemoryVisualizationException {
		memoryManagementUnit.setTlbEntry(0, 0x12345000, 0x22222000, false);
		assertEquals(0x22222678, memoryManagementUnit.mapAddressForVisualization(0x12345678));
	}

	/**
	 * 
	 */
	@Test
	public void testTlbWriteException() {
		int code = -1;
		try {
			memoryManagementUnit.setTlbEntry(15, 0x12345000, 0x22222001, false);
			memoryManagementUnit.mapAddressForCpu(0x12345678, true);
		} catch (CpuException e) {
			code = e.getCode();
		}
		assertEquals(CpuException.CODE_TLB_WRITE, code);
		assertEquals(15, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0x22222001, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));
		assertEquals(0x12345000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x12345678, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
	}

	/**
	 * @throws MemoryVisualizationException ...
	 */
	@Test
	public void testVisualizeTlbWriteBit() throws MemoryVisualizationException {
		memoryManagementUnit.setTlbEntry(0, 0x12345000, 0x22222001, false);
		assertEquals(0x22222678, memoryManagementUnit.mapAddressForVisualization(0x12345678));
	}

	/**
	 * @throws CpuException ...
	 */
	@Test
	public void testTlbReadWriteProtected() throws CpuException {
		memoryManagementUnit.setTlbEntry(0, 0x12345000, 0x22222001, false);
		assertEquals(0x22222678, memoryManagementUnit.mapAddressForCpu(0x12345678, false));
	}

	/**
	 * 
	 */
	@Test
	public void testTlbInvalidHasPrecendeOverTlbWrite() {
		int code = -1;
		try {
			memoryManagementUnit.setTlbEntry(0, 0x12345000, 0x22222000, false);
			memoryManagementUnit.mapAddressForCpu(0x12345678, true);
		} catch (CpuException e) {
			code = e.getCode();
		}
		assertEquals(CpuException.CODE_TLB_INVALID, code);
	}

	/**
	 * 
	 */
	@Test
	public void testTbsInstruction() {

		/** add some mappings (one page number occurs twice) **/
		memoryManagementUnit.setTlbEntry(3, 0x12345000, 0x55555003, false);
		memoryManagementUnit.setTlbEntry(6, 0x22222000, 0x66666003, false);
		memoryManagementUnit.setTlbEntry(7, 0x22222000, 0x77777003, false);
		
		/** test the TBS instruction with an address that cannot be found **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x33333333, false);
		memoryManagementUnit.executeTbsInstruction();
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertEquals(0x33333000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

		/**
		 * Test the TBS instruction with an address that can be found.
		 * Note that the TLB entry low register is not affected.
		 */
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345678, false);
		memoryManagementUnit.executeTbsInstruction();
		assertEquals(3, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertEquals(0x12345000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

		/**
		 * Test with a page number that occurs multiple times.
		 */
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x22222222, false);
		memoryManagementUnit.executeTbsInstruction();
		assertEquals(7, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_BAD_ADDRESS, false));
		assertEquals(0x22222000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

	}
	
	/**
	 * 
	 */
	@Test
	public void testTbwrInstruction() {
		
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345000, false);
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, 0xabcde003, false);
		memoryManagementUnit.executeTbwrInstruction();
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		
		memoryManagementUnit.updateRandomCounter();
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x99999000, false);
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, 0x88888000, false);
		memoryManagementUnit.executeTbwrInstruction();
		assertEquals(5, memoryManagementUnit.getRandomCounter());

		assertEquals(0x12345000, memoryManagementUnit.getTlbEntryHigh(4));
		assertEquals(0xabcde003, memoryManagementUnit.getTlbEntryLow(4));
		assertEquals(0x99999000, memoryManagementUnit.getTlbEntryHigh(5));
		assertEquals(0x88888000, memoryManagementUnit.getTlbEntryLow(5));

	}

	/**
	 * 
	 */
	@Test
	public void testTbwiTbriInstruction() {
		
		/** be sure that loading nonsense into the index register does not hurt **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_INDEX, 0xffffffff, false);
		assertEquals(31, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		
		/** write entries with TBWI **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345000, false);
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, 0xabcde003, false);
		memoryManagementUnit.executeTbwiInstruction();
		assertEquals(4, memoryManagementUnit.getRandomCounter());
		assertEquals(0, memoryManagementUnit.getTlbEntryHigh(30));
		assertEquals(0, memoryManagementUnit.getTlbEntryLow(30));
		assertEquals(0x12345000, memoryManagementUnit.getTlbEntryHigh(31));
		assertEquals(0xabcde003, memoryManagementUnit.getTlbEntryLow(31));

		/** fill another entry for TBRI testing **/
		memoryManagementUnit.setTlbEntry(13, 0x35353000, 0x1b1ba000, false);
		
		/** test an empty entry with TBRI **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_INDEX, 20, false);
		memoryManagementUnit.executeTbriInstruction();
		assertEquals(20, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

		/** test the TBWI-written entry with TBRI **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_INDEX, 31, false);
		memoryManagementUnit.executeTbriInstruction();
		assertEquals(31, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0x12345000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0xabcde003, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

		/** test the other written entry with TBRI **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_INDEX, 13, false);
		memoryManagementUnit.executeTbriInstruction();
		assertEquals(13, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		assertEquals(0x35353000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x1b1ba000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

	}
	
	/**
	 * 
	 */
	@Test
	public void testTbwiTbriInvalid() {
		
		/** load an invalid index into the TLB index register **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0x12345000, false);
		memoryManagementUnit.executeTbsInstruction();
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		
		/** write something into that register with TBWI **/
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, 0xabcde000, false);
		specialRegisterFile.write(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, 0x98765003, false);
		memoryManagementUnit.executeTbwiInstruction();
		
		/** the invalid index should be mapped to TLB entry 0 **/
		assertEquals(0xabcde000, memoryManagementUnit.getTlbEntryHigh(0));
		assertEquals(0x98765003, memoryManagementUnit.getTlbEntryLow(0));
		assertEquals(0, memoryManagementUnit.getTlbEntryHigh(1));
		assertEquals(0, memoryManagementUnit.getTlbEntryLow(1));
		
		/** write something else to that position manually **/
		memoryManagementUnit.setTlbEntry(0, 0x55555000, 0x44444000, false);
		assertEquals(IMemoryManagementUnit.INVALID_TLB_INDEX, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_INDEX, false));
		
		/** read that new value with TBRI **/
		memoryManagementUnit.executeTbriInstruction();
		assertEquals(0x55555000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_HIGH, false));
		assertEquals(0x44444000, specialRegisterFile.read(ISpecialRegisterFile.INDEX_TLB_ENTRY_LOW, false));

	}

	/**
	 * 
	 */
	@Test
	public void testUserInterfaceNotificationOnManualSetting() {
		MockCpuUserInterface ui = new MockCpuUserInterface();
		memoryManagementUnit.setUserInterface(ui);
		
		assertFalse(ui.isWriteTlb());
		memoryManagementUnit.setTlbEntry(0, 0, 0, false);
		assertFalse(ui.isWriteTlb());
		memoryManagementUnit.setTlbEntry(0, 0, 0, true);
		assertTrue(ui.isWriteTlb());
	}

	/**
	 * 
	 */
	@Test
	public void testUserInterfaceNotificationOnTbwr() {
		MockCpuUserInterface ui = new MockCpuUserInterface();
		memoryManagementUnit.setUserInterface(ui);
		
		assertFalse(ui.isWriteTlb());
		memoryManagementUnit.executeTbwrInstruction();
		assertTrue(ui.isWriteTlb());
	}
	
	/**
	 * 
	 */
	@Test
	public void testUserInterfaceNotificationOnTbwi() {
		MockCpuUserInterface ui = new MockCpuUserInterface();
		memoryManagementUnit.setUserInterface(ui);
		
		assertFalse(ui.isWriteTlb());
		memoryManagementUnit.executeTbwiInstruction();
		assertTrue(ui.isWriteTlb());
	}
}
