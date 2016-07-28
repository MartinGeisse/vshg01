/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import name.martingeisse.ecotools.simulator.ui.blockconsole.BlockConsoleContributor;
import name.martingeisse.ecotools.simulator.ui.console.ConsoleContributor;
import name.martingeisse.ecotools.simulator.ui.core.CoreContributor;
import name.martingeisse.ecotools.simulator.ui.disk.DiskContributor;
import name.martingeisse.ecotools.simulator.ui.extension.ExtensionContributor;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;
import name.martingeisse.ecotools.simulator.ui.operations.OperationsContributor;
import name.martingeisse.ecotools.simulator.ui.output.FileOutputContributor;
import name.martingeisse.ecotools.simulator.ui.pixeldisplay.PixelDisplayContributor;
import name.martingeisse.ecotools.simulator.ui.profiler.ProfilerContributor;
import name.martingeisse.ecotools.simulator.ui.sound.SoundContributor;
import name.martingeisse.ecotools.simulator.ui.terminal.TerminalContributor;
import name.martingeisse.ecotools.simulator.ui.timer.TimerContributor;

/**
 * 
 */
public class SimulatorConfiguration {

	/**
	 * the interactive
	 */
	private boolean interactive;

	/**
	 * the programFilename
	 */
	private String programFilename;

	/**
	 * the romFilename
	 */
	private String romFilename;

	/**
	 * the diskFilename
	 */
	private String diskFilename;

	/**
	 * the terminalCount
	 */
	private int terminalCount;

	/**
	 * the graphics
	 */
	private boolean graphics;

	/**
	 * the console
	 */
	private boolean console;

	/**
	 * the blockConsole
	 */
	private boolean blockConsole;

	/**
	 * the outputFilename
	 */
	private String outputFilename;

	/**
	 * the sound
	 */
	private boolean sound;
	
	/**
	 * Constructor
	 */
	public SimulatorConfiguration() {
		this.interactive = false;
		this.programFilename = null;
		this.romFilename = null;
		this.diskFilename = null;
		this.terminalCount = 0;
		this.graphics = false;
		this.console = false;
		this.blockConsole = false;
		this.outputFilename = null;
		this.sound = false;
	}

	/**
	 * @return Returns the interactive.
	 */
	public boolean isInteractive() {
		return interactive;
	}

	/**
	 * Sets the interactive.
	 * @param interactive the new value to set
	 */
	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}

	/**
	 * @return Returns the programFilename.
	 */
	public String getProgramFilename() {
		return programFilename;
	}

	/**
	 * Sets the programFilename.
	 * @param programFilename the new value to set
	 */
	public void setProgramFilename(String programFilename) {
		this.programFilename = programFilename;
	}

	/**
	 * @return Returns the romFilename.
	 */
	public String getRomFilename() {
		return romFilename;
	}

	/**
	 * Sets the romFilename.
	 * @param romFilename the new value to set
	 */
	public void setRomFilename(String romFilename) {
		this.romFilename = romFilename;
	}

	/**
	 * @return Returns the diskFilename.
	 */
	public String getDiskFilename() {
		return diskFilename;
	}

	/**
	 * Sets the diskFilename.
	 * @param diskFilename the new value to set
	 */
	public void setDiskFilename(String diskFilename) {
		this.diskFilename = diskFilename;
	}

	/**
	 * @return Returns the terminalCount.
	 */
	public int getTerminalCount() {
		return terminalCount;
	}

	/**
	 * Sets the terminalCount.
	 * @param terminalCount the new value to set
	 */
	public void setTerminalCount(int terminalCount) {
		this.terminalCount = terminalCount;
	}

	/**
	 * @return Returns the graphics.
	 */
	public boolean isGraphics() {
		return graphics;
	}

	/**
	 * Sets the graphics.
	 * @param graphics the new value to set
	 */
	public void setGraphics(boolean graphics) {
		this.graphics = graphics;
	}

	/**
	 * @return Returns the console.
	 */
	public boolean isConsole() {
		return console;
	}

	/**
	 * Sets the console.
	 * @param console the new value to set
	 */
	public void setConsole(boolean console) {
		this.console = console;
	}

	/**
	 * @return Returns the blockConsole.
	 */
	public boolean isBlockConsole() {
		return blockConsole;
	}

	/**
	 * Sets the blockConsole.
	 * @param blockConsole the new value to set
	 */
	public void setBlockConsole(boolean blockConsole) {
		this.blockConsole = blockConsole;
	}

	/**
	 * @return Returns the outputFilename.
	 */
	public String getOutputFilename() {
		return outputFilename;
	}

	/**
	 * Sets the outputFilename.
	 * @param outputFilename the new value to set
	 */
	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	/**
	 * @return Returns the sound.
	 */
	public boolean isSound() {
		return sound;
	}

	/**
	 * Sets the sound.
	 * @param sound the new value to set
	 */
	public void setSound(boolean sound) {
		this.sound = sound;
	}
	
	/**
	 * Loads a configuration file and applies the options to this configuration.
	 * @param filename the name of the configuration file to load
	 * @throws IOException on I/O errors
	 */
	public void loadConfigurationFile(String filename) throws IOException {
		loadConfigurationFile(new File(filename));
	}

	/**
	 * Loads a configuration file and applies the options to this configuration.
	 * @param file the configuration file to load
	 * @throws IOException on I/O errors
	 */
	public void loadConfigurationFile(File file) throws IOException {
		System.out.println("loading configuration file: " + file.getPath());
		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(file);
		properties.load(fileInputStream);
		fileInputStream.close();
		// TODO: ...
	}

	/**
	 * Ensures that this configuration is consistent.
	 * @throws InconsistentConfigurationException if the configuration is inconsistent
	 */
	public void checkConsistency() throws InconsistentConfigurationException {
		
		/** rom and program at the same time doesn't work -- we would not know which to run **/
		if (romFilename != null && programFilename != null) {
			throw new InconsistentConfigurationException("Cannot specify both a ROM and a program.");
		}
		
		/** check numer of terminals **/
		if (terminalCount < 0 || terminalCount > 2) {
			throw new InconsistentConfigurationException("invalid number of terminals (0, 1 or 2 allowed): " + terminalCount);
		}
		
		/** check console vs. block-console **/
		if (console && blockConsole) {
			throw new InconsistentConfigurationException("Console and Block-Console cannot be enabled at the same time.");
		}
		
	}
	
	/**
	 * Derives settings by certain rules. For example, if neither a ROM nor
	 * a program is specified, the simulator is set to interactive mode.
	 */
	public void deriveSettings() {
		if (romFilename == null && programFilename == null) {
			interactive = true;
		}
	}
	
	/**
	 * @return Returns the initial PC value
	 */
	public int getEntryPoint() {
		return (programFilename != null) ? 0xc0000000 : 0xe0000000;
	}
	
	/**
	 * Applies settings from this configuration to the specified simulator framework.
	 * Applies only those settings that must be applied before the create() method of the
	 * framework is called (i.e. before the GUI components are created).
	 * TODO: Is there any way to unit-test this method in a meaningful way?
	 * @param framework the simulator framework to apply this configuration to
	 * @throws IOException on I/O errors
	 */
	public void applyPreCreate(EcoSimulatorFramework framework) throws IOException {

		/** add the core contributor **/
		CoreContributor coreContributor = (romFilename == null) ? new CoreContributor() : new CoreContributor(new File(romFilename));
		framework.addContributor(coreContributor);
		
		/** add the timer contributor **/
		TimerContributor timerContributor = new TimerContributor();
		framework.addContributor(timerContributor);

		/** load a program (if specified) **/
		if (programFilename != null) {
			coreContributor.getRam().readContentsFromFile(new File(programFilename));
		}
		
		/** add a disk contributor (if specified) **/
		DiskContributor diskContributor;
		if (diskFilename != null) {
			diskContributor = new DiskContributor(new File(diskFilename));
			framework.addContributor(diskContributor);
		} else {
			diskContributor = null; 
		}

		/** add terminals (if specified) **/
		if (terminalCount == 1) {
			framework.addContributor(new TerminalContributor());
		} else if (terminalCount == 2) {
			framework.addContributor(new TerminalContributor(0));
			framework.addContributor(new TerminalContributor(1));
		} else if (terminalCount < 0) {
			throw new RuntimeException("invalid number of terminals: " + terminalCount);
		} else if (terminalCount > 2) {
			throw new RuntimeException("more than 2 terminals not supported");
		}

		/** add graphics contributor (if specified) **/
		if (graphics) {
			framework.addContributor(new PixelDisplayContributor());
		}
		
		/** add console contributor (if specified) **/
		if (console) {
			framework.addContributor(new ConsoleContributor());
		}
		
		/** add block-console contributor (if specified) **/
		if (blockConsole) {
			framework.addContributor(new BlockConsoleContributor());
		}
		
		/** add output device contributor (if specified) **/
		if (outputFilename != null) {
			framework.addContributor(new FileOutputContributor(new File(outputFilename)));
		}

		/** add sound contributor (if specified) **/
		if (sound) {
			framework.addContributor(new SoundContributor());
		}
		
		/** add operations contributor **/
		OperationsContributor operationsContributor = new OperationsContributor();
		framework.addContributor(operationsContributor);
		
		/** add profiling contributor **/
		ProfilerContributor profilerContributor = new ProfilerContributor();
		framework.addContributor(profilerContributor);
		
		/** add extension contributor **/
		ExtensionContributor extensionContributor = new ExtensionContributor(diskContributor);
		framework.addContributor(extensionContributor);

	}

	/**
	 * Applies settings from this configuration to the specified simulator framework.
	 * Applies only those settings that must be applied after the create() method of the
	 * framework is called (i.e. after the GUI components are created).
	 * TODO: Is there any way to unit-test this method in a meaningful way?
	 * @param framework the simulator framework to apply this configuration to
	 * @throws IOException on I/O errors
	 */
	public void applyPostCreate(EcoSimulatorFramework framework) throws IOException {
		
		/** set the PC to the entry point **/
		// TODO: we currently don't set the entry point if it is the default entry
		// point, because that colors the PC GUI widget red for no good reason (it
		// does so for non-default entry points too, but we can't avoid that). There is
		// currently no way to broadcast widget clearing to all GUI contributors
		// (the 'step' button accesses the general/special register widgets manually).
		if (getEntryPoint() != 0xe0000000) {
			framework.getCpu().getPc().setValue(getEntryPoint(), true);
		}
		
		/** start fast simulation mode if not interactive **/
		if (!interactive) {
			framework.setFastSimulationMode(true);
		}
		
	}

}
