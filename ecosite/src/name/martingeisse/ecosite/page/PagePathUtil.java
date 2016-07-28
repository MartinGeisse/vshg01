/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.page;

import java.util.regex.Pattern;

/**
 * Utility methods to deal with page paths.
 */
public class PagePathUtil {

	/**
	 * the validPagePathPattern
	 */
	private static Pattern validRelativePagePathPattern = Pattern.compile("([a-z0-9\\-\\.]+(\\/[a-z0-9\\-\\.]+)*)?");
	
	/**
	 * Returns the specified string with all leading slashes removed.
	 * @param s the input string
	 * @return the trimmed string
	 */
	public static String leftTrimSlashes(String s) {
		while (s.startsWith("/")) {
			s = s.substring(1);
		}
		return s;
	}

	/**
	 * Returns the specified string with all trailing slashes removed.
	 * @param s the input string
	 * @return the trimmed string
	 */
	public static String rightTrimSlashes(String s) {
		while (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	/**
	 * Returns the specified string with all leading and trailing slashes removed.
	 * @param s the input string
	 * @return the trimmed string
	 */
	public static String trimSlashes(String s) {
		return rightTrimSlashes(leftTrimSlashes(s));
	}

	/**
	 * Checks if the specified string is a valid relative page path.
	 * @param path the path to check
	 * @return true if valid, false if not
	 */
	public static boolean isValidRelativePagePath(String path) {
		return validRelativePagePathPattern.matcher(path).matches();
	}

}
