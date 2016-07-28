/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.osmtools.mapdata.filter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

/**
 * Filters a .mapdata file accoring to hardcoded rules.
 */
public class Main {

	/**
	 * Main method.
	 * @param args command-line arguments
	 * @throws IOException on I/O errors
	 */
	public static void main(String[] args) throws IOException {
		
		// check command-line arguments
		if (args.length != 2) {
			System.err.println("usage: java ...Main infile.mapdata outfile.mapdata");
			System.exit(1);
		}
		if (args[0].equals(args[1])) {
			System.err.println("infile and outfile must not be the same");
			System.exit(1);
		}
		if (!args[0].endsWith(".mapdata")) {
			System.err.println("infile does not end with .mapdata");
			System.exit(1);
		}
		if (!args[1].endsWith(".mapdata")) {
			System.err.println("outfile does not end with .mapdata");
			System.exit(1);
		}
		
		// do the filtering
		FileInputStream inStream = new FileInputStream(args[0]);
		InputStreamReader inReader = new InputStreamReader(inStream, "utf-8");
		LineNumberReader lineNumberReader = new LineNumberReader(inReader);
		FileOutputStream outStream = new FileOutputStream(args[1]);
		OutputStreamWriter outWriter = new OutputStreamWriter(outStream, "utf-8");
		while (true) {
			String line = lineNumberReader.readLine();
			if (line == null) {
				break;
			}
			if (accepts(line)) {
				outWriter.write(line);
				outWriter.write('\n');
			}
		}
		lineNumberReader.close();
		inReader.close();
		inStream.close();
		outWriter.flush();
		outWriter.close();
		outStream.flush();
		outStream.close();
		
	}
	
	/**
	 * @param line
	 * @return
	 */
	private static boolean accepts(String line) {
		switch (line.charAt(0)) {
		
		case 'N':
			return true;
		
		case 'T':
			if (line.startsWith("TTMC:")) {
				return false;
			}
			return true;
			
		default:
			throw new RuntimeException("unknown line code: " + line.charAt(0));
		}

	}
	
}
