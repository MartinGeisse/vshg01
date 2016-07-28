/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.mapfile;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 */
public class MapFileSectionTest {

	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		
		MapFile mapFile = new MapFile();
		
		MapFileSection section = new MapFileSection();
		assertNull(section.getName());
		assertEquals(0, section.getStart());
		assertEquals(0, section.getSize());
		
		section.setMapFile(mapFile);
		section.setName("foo");
		section.setStart(5);
		section.setSize(10);
		assertSame(mapFile, section.getMapFile());
		assertEquals("foo", section.getName());
		assertEquals(5, section.getStart());
		assertEquals(10, section.getSize());

	}
	
}
