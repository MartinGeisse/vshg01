/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.mapfile;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 */
public class MapFileTest {

	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {

		List<MapFileSection> sections = new ArrayList<MapFileSection>();
		List<MapFileSymbol> symbols = new ArrayList<MapFileSymbol>();
		
		MapFile mapFile = new MapFile();
		assertNull(mapFile.getSections());
		assertNull(mapFile.getSymbols());
		
		mapFile.setSections(sections);
		mapFile.setSymbols(symbols);
		assertSame(sections, mapFile.getSections());
		assertSame(symbols, mapFile.getSymbols());

	}
	
	/**
	 * 
	 */
	@Test
	public void testFindSectionByNameFindSymbolByName() {
		
		MapFileSection section1 = new MapFileSection();
		section1.setName("foo");
		
		MapFileSection section2 = new MapFileSection();
		section2.setName("bar");

		MapFileSymbol symbol1 = new MapFileSymbol();
		symbol1.setName("flubb");
		
		MapFileSymbol symbol2 = new MapFileSymbol();
		symbol2.setName("blubber");

		MapFile mapFile = new MapFile();
		mapFile.setSections(new ArrayList<MapFileSection>());
		mapFile.getSections().add(section1);
		mapFile.getSections().add(section2);
		mapFile.setSymbols(new ArrayList<MapFileSymbol>());
		mapFile.getSymbols().add(symbol1);
		mapFile.getSymbols().add(symbol2);

		assertNull(mapFile.findSectionByName("x"));
		assertSame(section1, mapFile.findSectionByName("foo"));
		assertSame(section2, mapFile.findSectionByName("bar"));
		assertNull(mapFile.findSectionByName("flubb"));
		assertNull(mapFile.findSectionByName("blubber"));

		assertNull(mapFile.findSymbolByName("x"));
		assertNull(mapFile.findSymbolByName("foo"));
		assertNull(mapFile.findSymbolByName("bar"));
		assertSame(symbol1, mapFile.findSymbolByName("flubb"));
		assertSame(symbol2, mapFile.findSymbolByName("blubber"));
		
	}
	
}
