/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.bus;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class DefaultInterruptLineTest {

	/**
	 * 
	 */
	@Test
	public void testAccessors() {
		DefaultInterruptLine line = new DefaultInterruptLine();
		assertFalse(line.isActive());
		line.setActive(true);
		assertTrue(line.isActive());
	}
	
}
