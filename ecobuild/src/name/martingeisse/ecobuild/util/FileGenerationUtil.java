/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util;

import java.io.File;
import java.io.FileWriter;

import name.martingeisse.ecobuild.ToolBuildException;

/**
 * Utility methods to help with generating output files.
 */
public class FileGenerationUtil {

	/**
	 * Creates a text file with the specified contents. The file will be
	 * UTF-8 encoded.
	 * @param file the file to create
	 * @param contents the file contents
	 */
	public static void createTextFile(File file, String contents) {
		createTextFile(file, contents, "utf-8");
	}

	/**
	 * Creates a text file with the specified contents.
	 * @param file the file to create
	 * @param contents the file contents
	 * @param encoding the character encoding
	 */
	public static void createTextFile(File file, String contents, String encoding) {
		try {
			FileWriter w = new FileWriter(file);
			w.write(contents);
			w.close();
		} catch (Exception e) {
			throw new ToolBuildException("could not create file " + EcobuildFileUtil.getCanonicalPath(file), e);
		}
	}
	
}
