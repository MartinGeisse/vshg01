/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

/**
 * This exception type indicates an internal problem with a tool
 * being used. Tools may throw this exception, and exceptions of
 * unknown type that occur within tools are wrapped in this type.
 * 
 * The typical way to handle this exception is: If the tool output
 * was needed by the caller to continue, it should be re-thrown.
 * Otherwise, it may be re-thrown or caught and printed.
 * 
 * The stack trace is usually included when printing this
 * exception.
 */
@SuppressWarnings("serial")
public class ToolBuildException extends AbstractBuildException {

	/**
	 * Constructor.
	 * @param message the exception message
	 */
	public ToolBuildException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 * @param cause the exception that caused this exception
	 */
	public ToolBuildException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
