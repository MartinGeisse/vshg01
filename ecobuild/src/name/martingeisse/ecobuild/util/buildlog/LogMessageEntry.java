/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

/**
 * Each instance holds one logged message.
 */
public class LogMessageEntry {
	
	/**
	 * the logLevel
	 */
	private final LogLevel logLevel;
	
	/**
	 * the message
	 */
	private final String message;
	
	/**
	 * the exception
	 */
	private final Throwable exception;
	
	/**
	 * Constructor.
	 * @param logLevel the log level
	 * @param message the message
	 * @param exception an optional exception
	 */
	public LogMessageEntry(final LogLevel logLevel, final String message, final Throwable exception) {
		this.logLevel = logLevel;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Getter method for the logLevel.
	 * @return the logLevel
	 */
	public LogLevel getLogLevel() {
		return logLevel;
	}

	/**
	 * Getter method for the message.
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Getter method for the exception.
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

}