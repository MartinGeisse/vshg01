/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 */
public class SimulatorConfigurationTest {

	/**
	 * the configuration
	 */
	private SimulatorConfiguration configuration = new SimulatorConfiguration();
	
	/**
	 * 
	 */
	@Test
	public void testGetSetInteractive() {
		assertFalse(configuration.isInteractive());
		configuration.setInteractive(true);
		assertTrue(configuration.isInteractive());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetProgramFilename() {
		assertNull(configuration.getProgramFilename());
		configuration.setProgramFilename("foo");
		assertEquals("foo", configuration.getProgramFilename());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetRomFilename() {
		assertNull(configuration.getRomFilename());
		configuration.setRomFilename("foo");
		assertEquals("foo", configuration.getRomFilename());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetDiskFilename() {
		assertNull(configuration.getDiskFilename());
		configuration.setDiskFilename("foo");
		assertEquals("foo", configuration.getDiskFilename());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetTerminalCount() {
		assertEquals(0, configuration.getTerminalCount());
		configuration.setTerminalCount(2);
		assertEquals(2, configuration.getTerminalCount());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetGraphics() {
		assertFalse(configuration.isGraphics());
		configuration.setGraphics(true);
		assertTrue(configuration.isGraphics());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetConsole() {
		assertFalse(configuration.isConsole());
		configuration.setConsole(true);
		assertTrue(configuration.isConsole());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetSetOutputFilename() {
		assertNull(configuration.getOutputFilename());
		configuration.setOutputFilename("foo");
		assertEquals("foo", configuration.getOutputFilename());
	}
	
	/**
	 * @throws InconsistentConfigurationException ...
	 */
	@Test
	public void testCheckConsistentNeitherProgramNorRom() throws InconsistentConfigurationException {
		configuration.setInteractive(true);
		configuration.setProgramFilename(null);
		configuration.setRomFilename(null);
		configuration.setDiskFilename("disk.img");
		configuration.setTerminalCount(2);
		configuration.setGraphics(false);
		configuration.setConsole(true);
		configuration.setOutputFilename("output.bin");
		configuration.checkConsistency();
	}
	
	/**
	 * @throws InconsistentConfigurationException ...
	 */
	@Test
	public void testCheckConsistentProgramOnly() throws InconsistentConfigurationException {
		configuration.setInteractive(true);
		configuration.setProgramFilename("foo.bin");
		configuration.setRomFilename(null);
		configuration.setDiskFilename("disk.img");
		configuration.setTerminalCount(2);
		configuration.setGraphics(false);
		configuration.setConsole(true);
		configuration.setOutputFilename("output.bin");
		configuration.checkConsistency();
	}
	
	/**
	 * @throws InconsistentConfigurationException ...
	 */
	@Test
	public void testCheckConsistentRomOnly() throws InconsistentConfigurationException {
		configuration.setInteractive(true);
		configuration.setProgramFilename(null);
		configuration.setRomFilename("foo.bin");
		configuration.setDiskFilename("disk.img");
		configuration.setTerminalCount(2);
		configuration.setGraphics(false);
		configuration.setConsole(true);
		configuration.setOutputFilename("output.bin");
		configuration.checkConsistency();
	}
	
	/**
	 * @throws InconsistentConfigurationException ...
	 */
	@Test(expected = InconsistentConfigurationException.class)
	public void testCheckInconsistentBothProgramAndRom() throws InconsistentConfigurationException {
		configuration.setInteractive(true);
		configuration.setProgramFilename("foo.bin");
		configuration.setRomFilename("bar.bin");
		configuration.setDiskFilename("disk.img");
		configuration.setTerminalCount(2);
		configuration.setGraphics(false);
		configuration.setConsole(true);
		configuration.setOutputFilename("output.bin");
		configuration.checkConsistency();
	}

	/**
	 * @throws InconsistentConfigurationException ...
	 */
	@Test(expected = InconsistentConfigurationException.class)
	public void testCheckInconsistentNegativeTerminalCount() throws InconsistentConfigurationException {
		configuration.setInteractive(true);
		configuration.setProgramFilename(null);
		configuration.setRomFilename("bar.bin");
		configuration.setDiskFilename("disk.img");
		configuration.setTerminalCount(-1);
		configuration.setGraphics(false);
		configuration.setConsole(true);
		configuration.setOutputFilename("output.bin");
		configuration.checkConsistency();
	}

	/**
	 * @throws InconsistentConfigurationException ...
	 */
	@Test(expected = InconsistentConfigurationException.class)
	public void testCheckInconsistentTooManyTerminals() throws InconsistentConfigurationException {
		configuration.setInteractive(true);
		configuration.setProgramFilename(null);
		configuration.setRomFilename("bar.bin");
		configuration.setDiskFilename("disk.img");
		configuration.setTerminalCount(3);
		configuration.setGraphics(false);
		configuration.setConsole(true);
		configuration.setOutputFilename("output.bin");
		configuration.checkConsistency();
	}

	/**
	 * 
	 */
	@Test
	public void checkDeriveSettings() {
		
		/** if either program file or rom file is set, non-interactive mode is possible **/
		
		configuration.setInteractive(false);
		configuration.setProgramFilename("foo");
		configuration.setRomFilename(null);
		configuration.deriveSettings();
		assertFalse(configuration.isInteractive());
		
		configuration.setRomFilename("bar");
		configuration.deriveSettings();
		assertFalse(configuration.isInteractive());
		
		configuration.setProgramFilename(null);
		configuration.deriveSettings();
		assertFalse(configuration.isInteractive());

		/** if neither is set, non-interactive mode is pointless **/
		
		configuration.setRomFilename(null);
		configuration.deriveSettings();
		assertTrue(configuration.isInteractive());

		/** if interactive mode is already set, specifying a program or rom doesn't negate that **/
		
		configuration.setProgramFilename("foo");
		configuration.deriveSettings();
		assertTrue(configuration.isInteractive());

		configuration.setRomFilename("bar");
		configuration.deriveSettings();
		assertTrue(configuration.isInteractive());

		configuration.setProgramFilename(null);
		configuration.deriveSettings();
		assertTrue(configuration.isInteractive());

	}
	
	/**
	 * 
	 */
	@Test
	public void testGetEntryPoint() {
		assertEquals(0xe0000000, configuration.getEntryPoint());
		configuration.setProgramFilename("foo");
		assertEquals(0xc0000000, configuration.getEntryPoint());
		configuration.setProgramFilename(null);
		configuration.setRomFilename("foo");
		assertEquals(0xe0000000, configuration.getEntryPoint());
	}
	
}
