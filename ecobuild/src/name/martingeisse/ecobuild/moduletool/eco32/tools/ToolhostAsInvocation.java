/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.eco32.tools;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.ToolhostCommandInvocation;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecobuild.util.buildlog.LogLevel;

/**
 * This class is specialized in understanding AS's error messages.
 */
public class ToolhostAsInvocation extends ToolhostCommandInvocation {

	/**
	 * the asCommand
	 */
	public static final String asCommand;

	//
	static {
		try {
			asCommand = "$" + new File("resource/devtools/as").getCanonicalPath() + "$";
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public ToolhostAsInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
		super(context, workingDirectory, command, logger);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.CommandLineToolInvocation#determineToolOutputLogLevel(name.martingeisse.ecobuild.util.CommandLineToolInvocation.MyLoggerWriter, java.lang.String)
	 */
	@Override
	protected LogLevel determineToolOutputLogLevel(final MyLoggerWriter loggerWriter, final String message) {
		if (message.startsWith("Assembling module ")) {
			return LogLevel.TRACE;
		}
		return super.determineToolOutputLogLevel(loggerWriter, message);
	}

}
