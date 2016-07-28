/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.testmain;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecotools.simulator.ui.console.ConsoleContributor;
import name.martingeisse.ecotools.simulator.ui.core.CoreContributor;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;
import name.martingeisse.ecotools.simulator.ui.terminal.TerminalContributor;

/**
 * 
 */
public class Main {

	/**
	 * @param args command-line arguments
	 * @throws IOException ...
	 */
	public static void main(String[] args) throws IOException {
		
//		String romFilename = "programs/test.bin";
//		String romFilename = "programs/ChattyTerminalEcho.bin";
		String romFilename = "programs/IncrementMemoryCell.bin";
//		String romFilename = "programs/ScanCodeEcho.bin";
		
		EcoSimulatorFramework framework = new EcoSimulatorFramework();
		framework.addContributor(new CoreContributor(new File(romFilename)));
		framework.addContributor(new ConsoleContributor());
		framework.addContributor(new TerminalContributor());

		framework.create();
		framework.open();
		framework.mainLoop();
		framework.dispose();
		framework.exit();
		
	}

}
