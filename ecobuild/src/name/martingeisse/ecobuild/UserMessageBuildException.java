/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

/**
 * This exception type carries a message that informs the user
 * about an error he/she should fix. This exception type can
 * have a "cause" exception; in that case, the message is usually
 * something like "an exception occurred while doing XYZ".
 */
@SuppressWarnings("serial")
public class UserMessageBuildException extends AbstractBuildException {

	/**
	 * Constructor.
	 * @param message the exception message
	 */
	public UserMessageBuildException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 * @param cause the exception that caused this exception
	 */
	public UserMessageBuildException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
