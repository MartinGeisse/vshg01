/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import name.martingeisse.ecobuild.ToolBuildException;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;

import org.apache.commons.io.IOUtils;

/**
 * This class is used to run tools directly on the host system.
 */
public class CommandLineToolInvocation extends AbstractCommandLineToolInvocation {

	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public CommandLineToolInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
		super(context, workingDirectory, command, logger);
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractCommandLineToolInvocation#invoke(java.util.List)
	 */
	@Override
	protected void invoke(List<String> tokens) throws IOException {

		final ProcessBuilder processBuilder = new ProcessBuilder(tokens);
		processBuilder.directory(getWorkingDirectory());
		processBuilder.redirectErrorStream(true);
		final Process process;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			getLogger().logError("Exception while starting tool. Command Line: " + getCommand() + " / " + tokens.get(0));
			throw e;
		}
		final MyLoggerWriter loggerWriter = new MyLoggerWriter(getLogger());
		IOUtils.copy(process.getInputStream(), loggerWriter);
		loggerWriter.endStartedLine();
		try {
			process.waitFor();
		} catch (final InterruptedException e) {
			throw new ToolBuildException("An InterruptedException occurred", e);
		}

	}

}
