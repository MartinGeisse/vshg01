/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.mapfile;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 */
public class MapFileSymbolTest {

	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		
		MapFile mapFile = new MapFile();
		
		MapFileSymbol symbol = new MapFileSymbol();
		assertNull(symbol.getName());
		assertNull(symbol.getSectionName());
		assertEquals(0, symbol.getOffset());
		
		symbol.setMapFile(mapFile);
		symbol.setName("foo");
		symbol.setSectionName("bar");
		symbol.setOffset(10);
		assertSame(mapFile, symbol.getMapFile());
		assertEquals("foo", symbol.getName());
		assertEquals("bar", symbol.getSectionName());
		assertEquals(10, symbol.getOffset());

	}
	
	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testResolveAddressNoMapFile() {
		MapFileSymbol symbol = new MapFileSymbol();
		symbol.setName("flubb");
		symbol.setSectionName("xxx");
		symbol.setOffset(10);
		symbol.resolveAddress();
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testResolveAddressNoSuchSection() {
		
		MapFileSection section1 = new MapFileSection();
		section1.setName("foo");
		section1.setStart(1000);
		
		MapFileSection section2 = new MapFileSection();
		section2.setName("bar");
		section2.setStart(2000);

		MapFileSymbol symbol = new MapFileSymbol();
		symbol.setName("flubb");
		symbol.setSectionName("xxx");
		symbol.setOffset(10);
		
		MapFile mapFile = new MapFile();
		mapFile.setSections(new ArrayList<MapFileSection>());
		mapFile.getSections().add(section1);
		mapFile.getSections().add(section2);
		mapFile.setSymbols(new ArrayList<MapFileSymbol>());
		mapFile.getSymbols().add(symbol);
		section1.setMapFile(mapFile);
		section2.setMapFile(mapFile);
		symbol.setMapFile(mapFile);
		
		symbol.resolveAddress();
		
	}


	/**
	 * 
	 */
	@Test
	public void testResolveAddressSuccess() {
		
		MapFileSection section1 = new MapFileSection();
		section1.setName("foo");
		section1.setStart(1000);
		
		MapFileSection section2 = new MapFileSection();
		section2.setName("bar");
		section2.setStart(2000);

		MapFileSymbol symbol = new MapFileSymbol();
		symbol.setName("flubb");
		symbol.setSectionName("bar");
		symbol.setOffset(10);
		
		MapFile mapFile = new MapFile();
		mapFile.setSections(new ArrayList<MapFileSection>());
		mapFile.getSections().add(section1);
		mapFile.getSections().add(section2);
		mapFile.setSymbols(new ArrayList<MapFileSymbol>());
		mapFile.getSymbols().add(symbol);
		section1.setMapFile(mapFile);
		section2.setMapFile(mapFile);
		symbol.setMapFile(mapFile);
		
		assertEquals(2010, symbol.resolveAddress());
		
	}

}
