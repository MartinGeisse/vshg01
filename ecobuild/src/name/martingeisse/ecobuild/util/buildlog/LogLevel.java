/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

/**
 * Logging levels during a build.
 */
public enum LogLevel {

	/**
	 * A build error that causes the current module build to fail.
	 */
	ERROR,
	
	/**
	 * A warning that should be fixed by the user, but does not prevent
	 * the current module from being built.
	 */
	WARNING,
	
	/**
	 * A trace warning that should only be visible if the current
	 * module build fails, or if specifically requested by the user.
	 */
	TRACE,
	
	/**
	 * Messages of this type give contextual information about other messages.
	 * Like trace-level messages, they do not cause a build to fail.
	 * Unlike trace-level messages, however, they are not hidden for
	 * successful builds.
	 */
	CONTEXT,
	
	/**
	 * Messages of unknown severity -- usually unrecognized tool output
	 * messages. This text should not be presented in a "dramatic" way
	 * (i.e. not like an error or warning would be presented), but
	 * cannot be suppressed like trace messages either. 
	 */
	UNKNOWN;
	
}
