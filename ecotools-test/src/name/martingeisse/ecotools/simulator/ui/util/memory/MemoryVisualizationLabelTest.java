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
public class MemoryVisualizationLabelTest {

	/**
	 * 
	 */
	@Test
	public void test() {
		
		MemoryVisualizationLabel label = new MemoryVisualizationLabel();
		assertEquals(0, label.getAddress());
		assertNull(label.getName());
		
		label.setAddress(0x12345678);
		assertEquals(0x12345678, label.getAddress());
		
		label.setName("foo");
		assertEquals("foo", label.getName());
		
	}

}
