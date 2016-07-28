/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui;

import java.io.PrintWriter;

import name.martingeisse.ecotools.simulator.ui.config.SimulatorConfiguration;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 * The main class.
 */
public class Main {

	/**
	 * The main method.
	 * @param args command-line arguments
	 * @throws Exception Any exceptions are passed to the environment.
	 */
	public static void main(String[] args) throws Exception {

		/** define command-line options **/
		Options options = new Options();
		options.addOption("i", false, "interactive mode (do not start simulation automatically)");
		options.addOption("l", true, "load program");
		options.addOption("r", true, "load ROM contents");
		options.addOption("d", true, "enable disk simulation with the specified disk image file");
		options.addOption("t", true, "enable terminal simulation with the specified number of terminals (0..2)");
		options.addOption("g", false, "enable graphics controller simulation");
		options.addOption("c", false, "enable console simulation");
		options.addOption("C", false, "enable block console simulation");
		options.addOption("o", true, "enable output device simulation and write output to the specified file");
		options.addOption("s", false, "enable null sound device");

		/** print help for the empty command line **/
		if (args.length == 0) {
			showUsgeAndExit(options);
		}
		
		/** parse the command line **/
		CommandLineParser commandLineParser = new PosixParser();
		CommandLine commandLine;
		try {
			commandLine = commandLineParser.parse(options, args);
		} catch (UnrecognizedOptionException e) {
			System.out.println("unrecognized option: " + e.getOption());
			showUsgeAndExit(options);
			return;
		}
		
		/** build the simulator configuration **/
		SimulatorConfiguration configuration = new SimulatorConfiguration();
		
		/** load configuration files **/
		for (String arg : commandLine.getArgs()) {
			configuration.loadConfigurationFile(arg);
		}

		/** interpret the options **/
		
		if (commandLine.hasOption("l")) {
			configuration.setProgramFilename(commandLine.getOptionValue("l"));
		}
		
		if (commandLine.hasOption("r")) {
			configuration.setRomFilename(commandLine.getOptionValue("r"));
		}
		
		if (commandLine.hasOption("d")) {
			configuration.setDiskFilename(commandLine.getOptionValue("d"));
		}
		
		if (commandLine.hasOption("t")) {
			String terminalCountSpecification = commandLine.getOptionValue("t");
			try {
				configuration.setTerminalCount((terminalCountSpecification == null) ? 0 : Integer.parseInt(terminalCountSpecification));
			} catch (NumberFormatException e) {
				System.out.println("number format error in number of terminals: " + terminalCountSpecification);
			}
		}
		
		if (commandLine.hasOption("i")) {
			configuration.setInteractive(true);
		}
		
		if (commandLine.hasOption("g")) {
			configuration.setGraphics(true);
		}
		
		if (commandLine.hasOption("c")) {
			configuration.setConsole(true);
		}
		
		if (commandLine.hasOption("C")) {
			configuration.setBlockConsole(true);
		}
		
		if (commandLine.hasOption("o")) {
			configuration.setOutputFilename(commandLine.getOptionValue("o"));
		}
		
		if (commandLine.hasOption("s")) {
			configuration.setSound(true);
		}

		configuration.checkConsistency();
		configuration.deriveSettings();

		/** setup simulation framework and run **/
		EcoSimulatorFramework framework = new EcoSimulatorFramework();
		configuration.applyPreCreate(framework);
		framework.create();
		configuration.applyPostCreate(framework);
		framework.open();
		framework.mainLoop();
		framework.dispose();
		framework.exit();

	}

	/**
	 * @param options the command-line option definitions
	 */
	private static void showUsgeAndExit(Options options) {
		PrintWriter w = new PrintWriter(System.out);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printUsage(w, 150, "ecosim.sh", options);
		formatter.printOptions(w, 150, options, 2, 3); 
		w.flush();
		System.exit(1);
	}
	
}
