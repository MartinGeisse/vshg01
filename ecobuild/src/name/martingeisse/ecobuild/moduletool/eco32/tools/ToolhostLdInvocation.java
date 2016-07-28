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
 * This class is specialized in understanding LD's error messages.
 */
public class ToolhostLdInvocation extends ToolhostCommandInvocation {

	/**
	 * the ldCommand
	 */
	public static final String ldCommand;

	//
	static {
		try {
			ldCommand = "$" + new File("resource/devtools/ld").getCanonicalPath() + "$";
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
	public ToolhostLdInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
		super(context, workingDirectory, command, logger);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.CommandLineToolInvocation#determineToolOutputLogLevel(name.martingeisse.ecobuild.util.CommandLineToolInvocation.MyLoggerWriter, java.lang.String)
	 */
	@Override
	protected LogLevel determineToolOutputLogLevel(final MyLoggerWriter loggerWriter, final String message) {
		if (message.startsWith("Reading module ") || message.equals("Linking modules...") || message.equals("Relocating segments...")) {
			return LogLevel.TRACE;
		}
		return super.determineToolOutputLogLevel(loggerWriter, message);
	}

}
