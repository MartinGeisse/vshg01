/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

/**
 * This class contains the main method for cleaning.
 */
public class MainClean {

	/**
	 * The main method
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		
		// currently, no command-line arguments are allowed
		if (args.length > 0) {
			System.out.println("currently, no command-line arguments are allowed");
			return;
		}
		
		// run the clean process
		Builder builder = new Builder();
		// builder.setVerboseMode(true);
		System.out.println("starting clean...");
		builder.clean();
		System.out.println("clean finished!");
		
	}
	
}
