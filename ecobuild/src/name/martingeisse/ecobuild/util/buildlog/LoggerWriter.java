/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

import java.io.IOException;
import java.io.Writer;

/**
 * A simple adapter class that acts as a {@link Writer} and writes
 * each completed line as a message to an {@link AbstractLogger}. The log
 * level is specified at construction.
 */
public class LoggerWriter extends Writer {

	/**
	 * the logger
	 */
	private final AbstractLogger logger;
	
	/**
	 * the lineBuilder
	 */
	private final StringBuilder lineBuilder;
	
	/**
	 * Constructor.
	 * @param logger the logger to log to
	 */
	public LoggerWriter(AbstractLogger logger) {
		this.logger = logger;
		this.lineBuilder = new StringBuilder();
	}
	
	/**
	 * Getter method for the logger.
	 * @return the logger
	 */
	public final AbstractLogger getLogger() {
		return logger;
	}
	
	/**
	 * Getter method for the lineBuilder.
	 * @return the lineBuilder
	 */
	public final StringBuilder getLineBuilder() {
		return lineBuilder;
	}
	
	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		while (len > 0) {
			write(cbuf[off]);
			off++;
			len--;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.io.Writer#write(int)
	 */
	@Override
	public void write(int c) throws IOException {
		if (c == '\r') {
			// do nothing
		} else if (c == '\n') {
			log(lineBuilder.toString());
			lineBuilder.setLength(0);
		} else {
			lineBuilder.append((char)c);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() {
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() {
	}

	/**
	 * "Writes" a newline character only if a non-finished line is still buffered.
	 */
	public void endStartedLine() {
		if (lineBuilder.length() > 0) {
			log(lineBuilder.toString());
			lineBuilder.setLength(0);
		}
	}
	
	/**
	 * Logs a message text. This method may end up logging zero or arbitrary many messages
	 * with arbitrary log levels and exceptions, at the discretion of the actual subclass.
	 * 
	 * The default implementation invokes determineLogLevel on the message and logs the
	 * message with that level and no exception. If determineLogLevel returns null,
	 * the message is suppressed.
	 *  
	 * @param message the message to log
	 */
	public void log(String message) {
		LogLevel logLevel = determineLogLevel(message);
		if (logLevel != null) {
			log(logLevel, message, null);
		}
	}
	
	/**
	 * Determines the log level based on a message text. For the default implementation
	 * of log(message), this method may return null to suppress a message.
	 * 
	 * The default implementation returns LogLevel.UNKNOWN for all messages since no
	 * known messages exist without knowing the command-line tool being invoked.
	 * 
	 * @param message the log message
	 * @return the log level
	 */
	protected LogLevel determineLogLevel(String message) {
		return LogLevel.UNKNOWN;
	}
	
	/**
	 * Convenience method to log a message to the underlying logger.
	 * @param logLevel the log level
	 * @param message the message
	 * @param exception the exception
	 */
	public void log(LogLevel logLevel, String message, Throwable exception) {
		logger.log(logLevel, message, exception);
	}
	
}
