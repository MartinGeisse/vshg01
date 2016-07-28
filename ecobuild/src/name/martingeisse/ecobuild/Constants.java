/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

/**
 * Defines global constants of the build process.
 */
public class Constants {

	/**
	 * The name of the source code folder within the whole project.
	 */
	public static final String SOURCE_FOLDER_NAME = "source";
	
	/**
	 * The name of the main build folder within the whole project.
	 */
	public static final String MAIN_BUILD_FOLDER_NAME = "build";

	/**
	 * The name of the module build folder within the main build folder.
	 */
	public static final String MODULE_BUILD_FOLDER_NAME = "modules";

	/**
	 * The path to the bash program.
	 */
	public static String BASH_PATH = "bash";

	/**
	 * The path to the gcc program.
	 */
	public static String GCC_PATH = "gcc";

	/**
	 * The suffix for executable programs.
	 */
	public static String EXE_SUFFIX = ""; 

	/**
	 * Whether the unix2dos tool must be applied to generated EXO files
	 */
	public static boolean NEEDS_UNIX_TO_DOS_FOR_EXO = true;
	
	/**
	 * Initializes the constants for use with Cygwin.
	 * @param cygwinPath the path to the Cygwin installation,
	 * without a trailing slash.
	 */
	public static void initializeForCygwin(String cygwinPath) {
		BASH_PATH = cygwinPath + "\\bin\\bash.exe";
		GCC_PATH = cygwinPath + "\\bin\\gcc-3.exe";
		EXE_SUFFIX = ".exe";
		NEEDS_UNIX_TO_DOS_FOR_EXO = false;
	}
	
}
