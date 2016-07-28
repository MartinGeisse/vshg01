/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.bus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class BusMapEntryTest {

	/**
	 * 
	 */
	@Test
	public void testBasic() {

		DummyDevice device = new DummyDevice(5);
		BusMapEntry entry = new BusMapEntry(1024, device, new int[0]);
		assertEquals(1024, entry.getBaseAddress());
		assertEquals(1055, entry.getAddressRangeEnd());
		assertTrue(device == entry.getDevice());
		assertEquals(0xffffffe0, entry.getSelectorAddressMask());
		assertEquals(0x0000001f, entry.getLocalAddressMask());
		
	}

	/**
	 * 
	 */
	@Test(expected = BusConfigurationException.class)
	public void testBaseAddressWithLocalBits() {
		new BusMapEntry(1026, new DummyDevice(5), new int[0]);		
	}

	/**
	 * @throws Exception ...
	 */
	@Test
	public void testAddressMapping() throws Exception {
		BusMapEntry entry = new BusMapEntry(1024, new DummyDevice(5), new int[0]);
		assertFalse(entry.maps(1023));
		assertTrue(entry.maps(1024));
		assertTrue(entry.maps(1030));
		assertTrue(entry.maps(1055));
		assertFalse(entry.maps(1056));		
		assertEquals(44, entry.read(1026, BusAccessSize.BYTE));
		assertEquals(2, entry.extractLocalAddress(1026));
		assertEquals(31, entry.extractLocalAddress(0xffffffff));
	}
	
	/**
	 * 
	 */
	@Test
	public void testDisjointOverlap() {
		
		BusMapEntry entry1 = new BusMapEntry(1024, new DummyDevice(5), new int[0]);
		BusMapEntry entry2 = new BusMapEntry(1056, new DummyDevice(5), new int[0]);
		BusMapEntry entry3 = new BusMapEntry(2048, new DummyDevice(5), new int[0]);
		
		assertTrue(entry1.overlapsAddressRange(entry1));
		assertFalse(entry1.overlapsAddressRange(entry2));
		assertFalse(entry1.overlapsAddressRange(entry3));

		assertFalse(entry2.overlapsAddressRange(entry1));
		assertTrue(entry2.overlapsAddressRange(entry2));
		assertFalse(entry2.overlapsAddressRange(entry3));

		assertFalse(entry3.overlapsAddressRange(entry1));
		assertFalse(entry3.overlapsAddressRange(entry2));
		assertTrue(entry3.overlapsAddressRange(entry3));

	}

	/**
	 * 
	 */
	@Test
	public void testNonDisjointOverlap() {

		BusMapEntry entry1 = new BusMapEntry(1024, new DummyDevice(5), new int[0]);
		BusMapEntry entry2 = new BusMapEntry(1040, new DummyDevice(2), new int[0]);
		
		assertTrue(entry1.overlapsAddressRange(entry1));
		assertTrue(entry1.overlapsAddressRange(entry2));

		assertTrue(entry2.overlapsAddressRange(entry1));
		assertTrue(entry2.overlapsAddressRange(entry2));

	}

	/**
	 * 
	 */
	@Test
	public void testOverlapCornerCases() {
		
		BusMapEntry entry1 = new BusMapEntry(1024, new DummyDevice(5), new int[0]);
		BusMapEntry entry2 = new BusMapEntry(1055, new DummyDevice(0), new int[0]);
		BusMapEntry entry3 = new BusMapEntry(1056, new DummyDevice(0), new int[0]);
		
		assertTrue(entry1.overlapsAddressRange(entry1));
		assertTrue(entry1.overlapsAddressRange(entry2));
		assertFalse(entry1.overlapsAddressRange(entry3));

		assertTrue(entry2.overlapsAddressRange(entry1));
		assertTrue(entry2.overlapsAddressRange(entry2));
		assertFalse(entry2.overlapsAddressRange(entry3));

		assertFalse(entry3.overlapsAddressRange(entry1));
		assertFalse(entry3.overlapsAddressRange(entry2));
		assertTrue(entry3.overlapsAddressRange(entry3));

	}

	/**
	 * 
	 */
	@Test(expected = BusConfigurationException.class)
	public void testInterruptLineCountMismatch() {
		DummyInterruptDevice device = new DummyInterruptDevice(); 
		new BusMapEntry(1024, device, new int[] {5, 7});
	}

	/**
	 * 
	 */
	@Test
	public void testConnectInterruptLines() {
		
		DummyInterruptDevice device = new DummyInterruptDevice(); 
		InterruptBus interruptBus = new InterruptBus();
		BusMapEntry entry = new BusMapEntry(1024, device, new int[] {5});
		entry.connectInterrupts(interruptBus);
		assertTrue(interruptBus.getLine(5).equals(device.getInterruptLine()));
		assertFalse(interruptBus.getLine(4).equals(device.getInterruptLine()));

	}
	
	/**
	 * 
	 */
	@Test
	public void testInterruptIndicesOverlap() {
		BusMapEntry entry1 = testInterruptIndicesOverlapHelper(5);
		BusMapEntry entry2 = testInterruptIndicesOverlapHelper(5);
		BusMapEntry entry3 = testInterruptIndicesOverlapHelper(6);
		BusMapEntry entry4 = testInterruptIndicesOverlapHelper(5, 7);
		BusMapEntry entry5 = testInterruptIndicesOverlapHelper(7, 9);
		BusMapEntry entry6 = testInterruptIndicesOverlapHelper(8, 9);

		assertTrue(entry1.overlapsInterruptIndices(entry1));
		assertTrue(entry1.overlapsInterruptIndices(entry2));
		assertFalse(entry1.overlapsInterruptIndices(entry3));
		assertTrue(entry1.overlapsInterruptIndices(entry4));
		assertFalse(entry1.overlapsInterruptIndices(entry5));
		assertFalse(entry1.overlapsInterruptIndices(entry6));

		assertTrue(entry2.overlapsInterruptIndices(entry1));
		assertTrue(entry2.overlapsInterruptIndices(entry2));
		assertFalse(entry2.overlapsInterruptIndices(entry3));
		assertTrue(entry2.overlapsInterruptIndices(entry4));
		assertFalse(entry2.overlapsInterruptIndices(entry5));
		assertFalse(entry2.overlapsInterruptIndices(entry6));

		assertFalse(entry3.overlapsInterruptIndices(entry1));
		assertFalse(entry3.overlapsInterruptIndices(entry2));
		assertTrue(entry3.overlapsInterruptIndices(entry3));
		assertFalse(entry3.overlapsInterruptIndices(entry4));
		assertFalse(entry3.overlapsInterruptIndices(entry5));
		assertFalse(entry3.overlapsInterruptIndices(entry6));

		assertTrue(entry4.overlapsInterruptIndices(entry1));
		assertTrue(entry4.overlapsInterruptIndices(entry2));
		assertFalse(entry4.overlapsInterruptIndices(entry3));
		assertTrue(entry4.overlapsInterruptIndices(entry4));
		assertTrue(entry4.overlapsInterruptIndices(entry5));
		assertFalse(entry4.overlapsInterruptIndices(entry6));

		assertFalse(entry5.overlapsInterruptIndices(entry1));
		assertFalse(entry5.overlapsInterruptIndices(entry2));
		assertFalse(entry5.overlapsInterruptIndices(entry3));
		assertTrue(entry5.overlapsInterruptIndices(entry4));
		assertTrue(entry5.overlapsInterruptIndices(entry5));
		assertTrue(entry5.overlapsInterruptIndices(entry6));

		assertFalse(entry6.overlapsInterruptIndices(entry1));
		assertFalse(entry6.overlapsInterruptIndices(entry2));
		assertFalse(entry6.overlapsInterruptIndices(entry3));
		assertFalse(entry6.overlapsInterruptIndices(entry4));
		assertTrue(entry6.overlapsInterruptIndices(entry5));
		assertTrue(entry6.overlapsInterruptIndices(entry6));
		
	}
	
	/**
	 * @return
	 */
	private BusMapEntry testInterruptIndicesOverlapHelper(int... interruptIndices) {
		DummyInterruptDevice device = new DummyInterruptDevice(interruptIndices.length); 
		BusMapEntry entry = new BusMapEntry(0, device, interruptIndices);
		return entry;
	}
}
