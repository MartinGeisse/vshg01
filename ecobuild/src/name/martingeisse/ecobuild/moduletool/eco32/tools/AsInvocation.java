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
 * This class is specialized in understanding AS's error messages.
 */
public class AsInvocation extends CommandLineToolInvocation {

	/**
	 * the asBuilt
	 */
	private static boolean asBuilt = false;
	
	/**
	 * the asCommand
	 */
	private static String asCommand = null;

	/**
	 * Builds the dev-host AS as a dependency. Actually, this method builds
	 * the whole devtools module, so don't call it from within that module!
	 * @param context the context used to build the AS if not yet built
	 */
	public static void buildAs(final IModuleToolContext context) {
		if (!asBuilt) {
			context.buildDependency("/devtools");
			asBuilt = true;
		}
	}
	
	/**
	 * @param context the context used to initialize the command if not present
	 * @return the AS command
	 */
	public static String getAsCommand(final IModuleToolContext context) {
		if (asCommand == null) {
			asCommand = "$" + context.resolveMainBuildPath("/devtools/as" + Constants.EXE_SUFFIX) + "$";
		}
		return asCommand;
	}
	
	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public AsInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
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
