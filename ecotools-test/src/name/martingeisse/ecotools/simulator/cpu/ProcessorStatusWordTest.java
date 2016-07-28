/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class ProcessorStatusWordTest {

	/**
	 * 
	 */
	@Test
	public void testGetVector() {
		assertFalse(ProcessorStatusWord.getVector(0x12345678));
		assertTrue(ProcessorStatusWord.getVector(0x1a345678));
	}

	/**
	 * 
	 */
	@Test
	public void testGetUserMode() {
		assertFalse(ProcessorStatusWord.getUserMode(0x12345678));
		assertTrue(ProcessorStatusWord.getUserMode(0x16345678));
	}

}
