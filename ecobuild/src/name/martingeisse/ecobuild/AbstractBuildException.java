/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

/**
 * Superclass for all kinds of exceptions that can occur during a build.
 */
@SuppressWarnings("serial")
public class AbstractBuildException extends RuntimeException {

	/**
	 * Constructor.
	 */
	public AbstractBuildException() {
		super();
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 */
	public AbstractBuildException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param cause the exception that caused this exception
	 */
	public AbstractBuildException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 * @param cause the exception that caused this exception
	 */
	public AbstractBuildException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
