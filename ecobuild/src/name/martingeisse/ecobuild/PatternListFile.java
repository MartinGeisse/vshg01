/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class reads an stores a file that contains a list of regex patterns,
 * one pattern per line. It then offers methods to check whether an input
 * line matches any of the patterns. This is used to filter expected
 * messages from command-line tools not directly invoked from a
 * module tool.
 */
public class PatternListFile {

	/**
	 * the patterns
	 */
	private final Pattern[] patterns;
	
	/**
	 * Constructor.
	 * @param file the file that contains the patterns
	 * @param optional if true, then a missing input file does not cause a {@link FileNotFoundException}.
	 * Instead, the instance is created without any patterns.
	 * @throws IOException on I/O errors
	 */
	public PatternListFile(File file, boolean optional) throws IOException {
		
		// open the input file
		LineNumberReader reader;
		try {
			reader = new LineNumberReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			if (optional) {
				this.patterns = new Pattern[0];
				return;
			} else {
				throw e;
			}
		}
		
		// read the patterns
		List<Pattern> patterns = new ArrayList<Pattern>();
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			if (!line.isEmpty()) {
				patterns.add(Pattern.compile(line));
			}
		}
		reader.close();
		this.patterns = patterns.toArray(new Pattern[patterns.size()]);
		
	}

	/**
	 * Tests whether the specified string matches any pattern from the file.
	 * @param s the string to test
	 * @return true if the string matches a pattern, false if it matches none
	 */
	public boolean matchesAny(String s) {
		for (Pattern p : patterns) {
			if (p.matcher(s).matches()) {
				return true;
			}
		}
		return false;
	}
	
}
