/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

/**
 * This class contains the main method for building.
 */
public class MainBuild {

	/**
	 * The main method
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		
		// check for command-line arguments
		if (args.length == 1) {
			Constants.initializeForCygwin(args[0]);
		} else if (args.length > 1) {
			System.out.println("usage: MainBuild [cygwinPath]");
			return;
		}
		
		// run the build process
		Builder builder = new Builder();
		// builder.setVerboseMode(true);
		builder.registerDefaultTools();
		System.out.println("starting build...");
		builder.build();
		System.out.println("build finished!");
		
	}
	
}
