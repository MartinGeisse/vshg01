/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.eco32.tools;

import java.io.File;

import name.martingeisse.ecobuild.Constants;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.CommandLineToolInvocation;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecobuild.util.buildlog.LogLevel;

/**
 * This class is specialized in understanding LD's error messages.
 */
public class LdInvocation extends CommandLineToolInvocation {

	/**
	 * the ldBuilt
	 */
	private static boolean ldBuilt = false;
	
	/**
	 * the ldCommand
	 */
	private static String ldCommand = null;

	/**
	 * Builds the dev-host LD as a dependency. Actually, this method builds
	 * the whole devtools module, so don't call it from within that module!
	 * @param context the context used to build the LD if not yet built
	 */
	public static void buildLd(final IModuleToolContext context) {
		if (!ldBuilt) {
			context.buildDependency("/devtools");
			ldBuilt = true;
		}
	}
	
	/**
	 * @param context the context used to initialize the command if not present
	 * @return the LD command
	 */
	public static String getLdCommand(final IModuleToolContext context) {
		if (ldCommand == null) {
			ldCommand = "$" + context.resolveMainBuildPath("/devtools/ld" + Constants.EXE_SUFFIX) + "$";
		}
		return ldCommand;
	}
	
	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public LdInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
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
