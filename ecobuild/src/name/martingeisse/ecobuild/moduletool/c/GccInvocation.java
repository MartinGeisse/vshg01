/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.c;

import java.io.File;

import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.CommandLineToolInvocation;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecobuild.util.buildlog.LogLevel;

/**
 * This class is specialized in understanding GCC's error messages.
 */
public class GccInvocation extends CommandLineToolInvocation {

	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public GccInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
		super(context, workingDirectory, command, logger);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.CommandLineToolInvocation#determineToolOutputLogLevel(name.martingeisse.ecobuild.util.CommandLineToolInvocation.MyLoggerWriter, java.lang.String)
	 */
	@Override
	protected LogLevel determineToolOutputLogLevel(final MyLoggerWriter loggerWriter, final String message) {

		// errors while compiling
		int index = message.indexOf(':');
		if (index != -1) {
			index = message.indexOf(':', index + 1);
			if (index != -1) {
				final int index2 = message.indexOf(':', index + 1);
				if (index2 != -1) {
					final String severity = message.substring(index + 1, index2).trim();
					if (severity.equals("error")) {
						return LogLevel.ERROR;
					} else if (severity.equals("warning")) {
						return LogLevel.WARNING;
					}
				}
			}
		}
		
		// file not found messages
		if (message.endsWith("No such file or directory")) {
			return LogLevel.ERROR;
		}
		if (message.endsWith("no input files")) {
			return LogLevel.ERROR;
		}
		
		// TODO: This is not safe -- the source code could legally contain this
		if (message.contains(": In function '")) {
			return LogLevel.CONTEXT;
		}

		return super.determineToolOutputLogLevel(loggerWriter, message);
	}

}
