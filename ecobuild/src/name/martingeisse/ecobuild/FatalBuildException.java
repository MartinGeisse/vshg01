/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

/**
 * This exception type indicates an internal problem with the
 * build system itself. No tools or other user code should
 * throw this exception.
 * 
 * The typical way to handle this exception is to stop the
 * build altogether since it indicates a fundamental problem
 * that cannot be guaranteed to be confined to single modules.
 * 
 * The stack trace is usually included when printing this
 * exception.
 */
@SuppressWarnings("serial")
public class FatalBuildException extends AbstractBuildException {

	/**
	 * Constructor.
	 * @param message the exception message
	 */
	public FatalBuildException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 * @param cause the exception that caused this exception
	 */
	public FatalBuildException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
