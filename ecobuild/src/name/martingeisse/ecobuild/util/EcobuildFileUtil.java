/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecobuild.FatalBuildException;

import org.apache.commons.io.FileUtils;

/**
 * File-handling utilities. The name was chosen because {@link FileUtils}
 * already exists in Apache Commons.
 */
public class EcobuildFileUtil {

	/**
	 * Like file.getCanonicalPath(), but wraps {@link IOException} in {@link FatalBuildException}.
	 * @param file the file to get the canonical path from
	 * @return the canonical path
	 */
	public static String getCanonicalPath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			throw new FatalBuildException("File.getCanonicalPath() failed", e);
		}
	}

	/**
	 * Like file.getCanonicalFile(), but wraps {@link IOException} in {@link FatalBuildException}.
	 * @param file the file to get the canonical file from
	 * @return the canonical file
	 */
	public static File getCanonicalFile(File file) {
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			throw new FatalBuildException("getCanonicalFile() failed", e);
		}
	}

	/**
	 * Returns either the minimum or maximum timestamp of all files within the
	 * specified root, which can either be a folder or a file.
	 * @param root the root to start from
	 * @param max true to determine the maximum timestamp, false to determine the minimum timestamp
	 * @return the timestamp
	 */
	public static long getMinMaxTimestampRecursively(File root, boolean max) {
		if (root.isDirectory()) {
			long timestamp = root.lastModified();
			for (File sub : root.listFiles()) {
				timestamp = minMax(timestamp, sub.lastModified(), max);
			}
			return timestamp;
		} else {
			return root.lastModified();
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @param max
	 * @return
	 */
	private static long minMax(long x, long y, boolean max) {
		if (max) {
			return (x > y ? x : y);
		} else {
			return (x < y ? x : y);
		}
	}

	/**
	 * Returns a file name with the specified newExtension. This method takes the originalName
	 * and looks for the typicalOriginalExtension. If found, it is replaced by the newExtension,
	 * otherwise the newExtension is appended to the originalName (separated by a dot).
	 * 
	 * All specified extensions should start with a dot character. (This method does not expect
	 * a dot character and will work in the obvious way without one, but in that case does
	 * not actually deal with filename extensions anymore but with generic suffixes, for which
	 * it does not have a useful meaning).
	 * 
	 * @param originalName the original file name
	 * @param typicalOriginalExtension the extension typically expected for the originalName
	 * @param newExtension the new file name extension to use in the returned file name
	 * @return the new file name
	 */
	public static String replaceFilenameExtension(String originalName, String typicalOriginalExtension, String newExtension) {
		if (originalName.endsWith(typicalOriginalExtension)) {
			return originalName.substring(0, originalName.length() - typicalOriginalExtension.length()) + newExtension;
		} else {
			return originalName + newExtension;
		}
	}
	
}
