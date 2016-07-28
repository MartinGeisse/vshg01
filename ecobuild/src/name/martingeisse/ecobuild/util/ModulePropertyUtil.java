/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util;

import java.util.Properties;

import name.martingeisse.ecobuild.UserMessageBuildException;

/**
 * Utility methods to deal with module properties.
 */
public class ModulePropertyUtil {

	/**
	 * Gets a property that is expected to contain a list of strings.
	 * A {@link UserMessageBuildException} is thrown if the property does not exist.
	 * The property value is split into strings at spaces; one or more spaces and/or
	 * commas may be used. The returned strings will not contain commas or spaces.
	 * @param moduleProperties the properties to read from
	 * @param propertyName the name of the property to parse
	 * @return the strings
	 */
	public static String[] getMandatoryStringList(Properties moduleProperties, String propertyName) {
		String[] result = getOptionalStringList(moduleProperties, propertyName);
		if (result == null) {
			throw new UserMessageBuildException("missing property: " + propertyName);
		}
		return result;
	}

	/**
	 * Gets a property that is expected to contain a list of strings.
	 * Returns null if the property does not exist.
	 * The property value is split into strings at spaces; one or more spaces and/or
	 * commas may be used. The returned strings will not contain commas or spaces.
	 * @param moduleProperties the properties to read from
	 * @param propertyName the name of the property to parse
	 * @return the strings
	 */
	public static String[] getOptionalStringList(Properties moduleProperties, String propertyName) {
		String value = moduleProperties.getProperty(propertyName);
		if (value == null) {
			return null;
		}
		return removeEmptyStrings(value.split("(\\s|\\,)+"), true);
	}
	
	/**
	 * Returns an array that contains the strings from the argument, with all empty strings
	 * removed. If mayReuse is true then the input array is returned in case it only contains
	 * nonempty strings; if mayReuse is false, then a new array is returned in any case.
	 * 
	 * @param strings the input strings
	 * @param mayReuse whether the input array may be re-used
	 * @return the nonempty strings
	 */
	public static String[] removeEmptyStrings(String[] strings, boolean mayReuse) {
		
		// count the nonempty strings
		int nonemptyCount = 0;
		for (String s : strings) {
			if (!s.isEmpty()) {
				nonemptyCount++;
			}
		}
		
		// possible shortcut
		if (mayReuse && nonemptyCount == strings.length) {
			return strings;
		}

		// create the result array
		String[] result = new String[nonemptyCount];
		int writeIndex = 0;
		for (String s : strings) {
			if (!s.isEmpty()) {
				result[writeIndex] = s;
				writeIndex++;
			}
		}
		return result;
		
	}
	
}
