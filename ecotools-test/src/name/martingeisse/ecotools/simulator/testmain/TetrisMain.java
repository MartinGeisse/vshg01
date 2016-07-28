/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.testmain;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecotools.simulator.ui.blockconsole.BlockConsoleContributor;
import name.martingeisse.ecotools.simulator.ui.core.CoreContributor;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;
import name.martingeisse.ecotools.simulator.ui.output.FileOutputContributor;
import name.martingeisse.ecotools.simulator.ui.sound.SoundContributor;
import name.martingeisse.ecotools.simulator.ui.timer.TimerContributor;

/**
 * 
 */
public class TetrisMain {

	/**
	 * @param args command-line arguments
	 * @throws IOException ...
	 */
	public static void main(String[] args) throws IOException {
		
		String romFilename = "programs/tetris.bin";
		
		EcoSimulatorFramework framework = new EcoSimulatorFramework();
		framework.addContributor(new CoreContributor(new File(romFilename)));
		framework.addContributor(new BlockConsoleContributor());
		framework.addContributor(new TimerContributor());
		framework.addContributor(new SoundContributor());
		framework.addContributor(new FileOutputContributor(new File("output.bin")));
		
		framework.create();
		framework.open();
		framework.mainLoop();
		framework.dispose();
		framework.exit();
		
	}

}
