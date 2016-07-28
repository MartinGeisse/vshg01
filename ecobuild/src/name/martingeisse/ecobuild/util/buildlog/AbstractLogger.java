/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

/**
 * Superclass for all loggers. This class provides convenience methods
 * but does not implement the actual logging functionality.
 */
public abstract class AbstractLogger {

	/**
	 * Logs a message.
	 * @param level the log level to use
	 * @param message the message to log
	 */
	public final void log(LogLevel level, String message) {
		log(level, message, null);
	}

	/**
	 * Logs a message about an exception.
	 * @param level the log level to use
	 * @param message the message to log
	 * @param e the exception
	 */
	public abstract void log(LogLevel level, String message, Throwable e);

	/**
	 * Logs an error-level message.
	 * @param message the message to log
	 */
	public final void logError(String message) {
		log(LogLevel.ERROR, message, null);
	}

	/**
	 * Logs an error-level message about an exception.
	 * @param message the message to log
	 * @param e the exception
	 */
	public final void logError(String message, Throwable e) {
		log(LogLevel.ERROR, message, e);
	}

	/**
	 * Logs a warning-level message.
	 * @param message the message to log
	 */
	public final void logWarning(String message) {
		log(LogLevel.WARNING, message, null);
	}

	/**
	 * Logs an warning-level message about an exception.
	 * @param message the message to log
	 * @param e the exception
	 */
	public final void logWarning(String message, Throwable e) {
		log(LogLevel.WARNING, message, e);
	}

	/**
	 * Logs a trace-level message.
	 * @param message the message to log
	 */
	public final void logTrace(String message) {
		log(LogLevel.TRACE, message, null);
	}

	/**
	 * Logs an trace-level message about an exception.
	 * @param message the message to log
	 * @param e the exception
	 */
	public final void logTrace(String message, Throwable e) {
		log(LogLevel.TRACE, message, e);
	}

	/**
	 * Logs a context-level message.
	 * @param message the message to log
	 */
	public final void logContext(String message) {
		log(LogLevel.CONTEXT, message, null);
	}

	/**
	 * Logs an context-level message about an exception.
	 * @param message the message to log
	 * @param e the exception
	 */
	public final void logContext(String message, Throwable e) {
		log(LogLevel.CONTEXT, message, e);
	}

	/**
	 * Logs a unknown-level message.
	 * @param message the message to log
	 */
	public final void logUnknown(String message) {
		log(LogLevel.UNKNOWN, message, null);
	}

	/**
	 * Logs an unknown-level message about an exception.
	 * @param message the message to log
	 * @param e the exception
	 */
	public final void logUnknown(String message, Throwable e) {
		log(LogLevel.UNKNOWN, message, e);
	}
	
}
