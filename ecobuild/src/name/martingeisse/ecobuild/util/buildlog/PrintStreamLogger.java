/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

import java.io.PrintStream;

/**
 * A logger that logs to a {@link PrintStream}.
 */
public class PrintStreamLogger extends AbstractLogger {

	/**
	 * the printStream
	 */
	private final PrintStream printStream;
	
	/**
	 * Constructor.
	 * @param printStream the {@link PrintStream} to log to
	 */
	public PrintStreamLogger(PrintStream printStream) {
		this.printStream = printStream;
	}
	
	/**
	 * Getter method for the printStream.
	 * @return the printStream
	 */
	public PrintStream getPrintStream() {
		return printStream;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.buildlog.AbstractLogger#log(name.martingeisse.ecobuild.util.buildlog.LogLevel, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(LogLevel level, String message, Throwable e) {
		printStream.println(level.name().charAt(0) + ": " + message);
		if (e != null) {
			e.printStackTrace(printStream);
			printStream.println();
		}
	}

}
