/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 */
public class LabelByAddressComparatorTest {

	/**
	 * 
	 */
	@Test
	public void test() {

		MemoryVisualizationLabel l1 = new MemoryVisualizationLabel(10, "foo");
		MemoryVisualizationLabel l1a = new MemoryVisualizationLabel(10, "foo");
		MemoryVisualizationLabel l2 = new MemoryVisualizationLabel(10, "bar");
		MemoryVisualizationLabel l3 = new MemoryVisualizationLabel(11, "foo");
		MemoryVisualizationLabel l4 = new MemoryVisualizationLabel(11, "bar");
		
		LabelByAddressComparator c = new LabelByAddressComparator();

		/** test equality for those labels that are equal **/
		
		assertEquals(0, c.compare(l1, l1));
		assertEquals(0, c.compare(l1, l1a));
		assertEquals(0, c.compare(l1a, l1));
		assertEquals(0, c.compare(l1a, l1a));
		assertEquals(0, c.compare(l2, l2));
		assertEquals(0, c.compare(l3, l3));
		assertEquals(0, c.compare(l4, l4));

		/** test ordering **/
		
		assertTrue(c.compare(l1, l2) > 0);
		assertTrue(c.compare(l1, l3) < 0);
		assertTrue(c.compare(l1, l4) < 0);

		assertTrue(c.compare(l2, l1) < 0);
		assertTrue(c.compare(l2, l3) < 0);
		assertTrue(c.compare(l2, l4) < 0);

		assertTrue(c.compare(l3, l1) > 0);
		assertTrue(c.compare(l3, l2) > 0);
		assertTrue(c.compare(l3, l4) > 0);

		assertTrue(c.compare(l4, l1) > 0);
		assertTrue(c.compare(l4, l2) > 0);
		assertTrue(c.compare(l4, l3) < 0);

	}
	
}
