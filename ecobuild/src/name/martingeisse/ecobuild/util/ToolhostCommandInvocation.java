/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecotools.simulator.cpu.toolhost.ToolhostFileSystem;
import name.martingeisse.ecotools.simulator.cpu.toolhost.ToolhostProcess;
import name.martingeisse.ecotools.simulator.cpu.toolhost.ToolhostProcessSet;
import name.martingeisse.ecotools.simulator.cpu.usermode.unix.EmptyFilePointer;
import name.martingeisse.ecotools.simulator.cpu.usermode.unix.IFilePointer;
import name.martingeisse.ecotools.simulator.cpu.usermode.unix.StreamFilePointer;

import org.apache.commons.io.output.WriterOutputStream;

/**
 * This class is used to run tools on the Toolhost EOS simulator.
 */
public class ToolhostCommandInvocation extends AbstractCommandLineToolInvocation {

	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public ToolhostCommandInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
		super(context, workingDirectory, command, logger);
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractCommandLineToolInvocation#invoke(java.util.List)
	 */
	@Override
	protected void invoke(List<String> tokenList) throws IOException {
		try {

			final MyLoggerWriter loggerWriter = new MyLoggerWriter(getLogger());
			final OutputStream loggerOutputStream = new WriterOutputStream(loggerWriter, "ascii");
			IFilePointer stdin = new EmptyFilePointer();
			IFilePointer stdout = new StreamFilePointer(loggerOutputStream);
			IFilePointer stderr = stdout;
			
			String[] tokens = tokenList.toArray(new String[tokenList.size()]);
			ToolhostProcessSet processSet = new ToolhostProcessSet(new ToolhostFileSystem());
			ToolhostProcess process = processSet.createInitialProcess(getWorkingDirectory(), stdin, stdout, stderr);
			stdin.releaseReference();
			stdout.releaseReference();
			process.exec(tokens[0], tokens);
			processSet.run();
			loggerOutputStream.flush();
			loggerWriter.endStartedLine();
			
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
