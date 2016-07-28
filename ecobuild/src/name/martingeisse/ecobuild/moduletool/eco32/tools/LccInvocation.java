/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.eco32.tools;

import java.io.File;
import java.util.regex.Pattern;

import name.martingeisse.ecobuild.Constants;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.CommandLineToolInvocation;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecobuild.util.buildlog.LogLevel;

/**
 * This class is specialized in understanding LCC's error messages.
 */
public class LccInvocation extends CommandLineToolInvocation {

	/**
	 * the lccBuilt
	 */
	private static boolean lccBuilt = false;
	
	/**
	 * the lccCommand
	 */
	private static String lccCommand = null;
	
	/**
	 * the fileContextPattern
	 */
	private static Pattern fileContextPattern = Pattern.compile("[a-zA-Z0-9\\_\\.\\-]+\\.(c|s)\\:");

	/**
	 * the compilerDependentPointerConversionPattern
	 */
	private static Pattern compilerDependentPointerConversionPattern = Pattern.compile(".*\\: warning\\: conversion from \\`pointer to .*\\' to \\`pointer to .*\\' is compiler dependent");
	
	/**
	 * Builds the dev-host LCC as a dependency. Actually, this method builds
	 * the whole devtools module, so don't call it from within that module!
	 * @param context the context used to build the LCC if not yet built
	 */
	public static void buildLcc(final IModuleToolContext context) {
		if (!lccBuilt) {
			context.buildDependency("/devtools");
			lccBuilt = true;
		}
	}
	
	/**
	 * @param context the context used to initialize the command if not present
	 * @return the LCC command
	 */
	public static String getLccCommand(final IModuleToolContext context) {
		if (lccCommand == null) {
			lccCommand = "$" + context.resolveMainBuildPath("/devtools/lcc" + Constants.EXE_SUFFIX) + "$";
		}
		return lccCommand;
	}
	
	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public LccInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
		super(context, workingDirectory, command, logger);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.CommandLineToolInvocation#determineToolOutputLogLevel(name.martingeisse.ecobuild.util.CommandLineToolInvocation.MyLoggerWriter, java.lang.String)
	 */
	@Override
	protected LogLevel determineToolOutputLogLevel(final MyLoggerWriter loggerWriter, final String message) {
		
		// specific trace and context messages
		if (compilerDependentPointerConversionPattern.matcher(message).matches()) {
			return LogLevel.TRACE;
		}
		if (message.startsWith("Assembling module ")) {
			return LogLevel.TRACE;
		}
		if (message.startsWith("Reading module ") || message.equals("Linking modules...") || message.equals("Relocating segments...")) {
			return LogLevel.TRACE;
		}
		if (fileContextPattern.matcher(message).matches()) {
			return LogLevel.CONTEXT;
		}

		// specific error messages
		if (message.contains("Could not find include file")) {
			return LogLevel.ERROR;
		}
		
		// catch-all rules for large classes of messages (must come last, so we can treat certain
		// specific unimportant warnings as trace messages)
		if (message.contains(": warning:")) {
			return LogLevel.WARNING;
		}
		if (message.startsWith("Error:")) {
			return LogLevel.ERROR;
		}
		
		// last fallback
		return super.determineToolOutputLogLevel(loggerWriter, message);
	}

}
