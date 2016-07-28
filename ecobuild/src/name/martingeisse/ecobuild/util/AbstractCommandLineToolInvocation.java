/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecobuild.util.buildlog.LogLevel;
import name.martingeisse.ecobuild.util.buildlog.LoggerWriter;

/**
 * This class simplifies the invocation of command-line tools such as
 * C compilers.
 * 
 * This class can be configured whether unrecognized output messages
 * shall cause the build to fail. The default is that this is the case.
 * Error-level messages will always cause the build to fail,
 * warning-level and trace-level messages never (a similar switch
 * may be added for warning-level messages in the future).
 * 
 *  This base class does not actually invoke a tool. This allows subclasses
 *  to implement this in different ways, most notably running a tool
 *  either directly on the host system or within the Toolhost
 *  EOS simulator.
 */
public abstract class AbstractCommandLineToolInvocation {

	/**
	 * the context
	 */
	private final IModuleToolContext context;

	/**
	 * the workingDirectory
	 */
	private final File workingDirectory;

	/**
	 * the command
	 */
	private final String command;

	/**
	 * the logger
	 */
	private final AbstractLogger logger;

	/**
	 * the triggerErrorOnUnrecognizedLogMessages
	 */
	private boolean triggerErrorOnUnrecognizedLogMessages;

	/**
	 * Constructor.
	 * @param context the build context
	 * @param workingDirectory the working directory to invoke the command in
	 * @param command the command to run
	 * @param logger the logger to log to
	 */
	public AbstractCommandLineToolInvocation(final IModuleToolContext context, final File workingDirectory, final String command, final AbstractLogger logger) {
		this.context = context;
		this.workingDirectory = workingDirectory;
		this.command = command;
		this.logger = logger;
		this.triggerErrorOnUnrecognizedLogMessages = true;
	}

	/**
	 * Getter method for the context.
	 * @return the context
	 */
	public IModuleToolContext getContext() {
		return context;
	}

	/**
	 * Getter method for the workingDirectory.
	 * @return the workingDirectory
	 */
	public File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * Getter method for the command.
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Getter method for the logger.
	 * @return the logger
	 */
	public AbstractLogger getLogger() {
		return logger;
	}

	/**
	 * Getter method for the triggerErrorOnUnrecognizedLogMessages.
	 * @return the triggerErrorOnUnrecognizedLogMessages
	 */
	public boolean isTriggerErrorOnUnrecognizedLogMessages() {
		return triggerErrorOnUnrecognizedLogMessages;
	}

	/**
	 * Setter method for the triggerErrorOnUnrecognizedLogMessages.
	 * @param triggerErrorOnUnrecognizedLogMessages the triggerErrorOnUnrecognizedLogMessages to set
	 */
	public void setTriggerErrorOnUnrecognizedLogMessages(final boolean triggerErrorOnUnrecognizedLogMessages) {
		this.triggerErrorOnUnrecognizedLogMessages = triggerErrorOnUnrecognizedLogMessages;
	}

	/**
	 * Invokes the command line tool.
	 * @throws IOException on I/O errors
	 */
	public void invoke() throws IOException {
		logger.logTrace("(in working folder: " + workingDirectory.getCanonicalPath() + ") exec: " + command + " (" + getClass().getName() + ")");
		final StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(command.replace("\\", "\\\\")));
		tokenizer.resetSyntax();
		tokenizer.whitespaceChars(0, 32);
		tokenizer.wordChars(33, 35);
		tokenizer.quoteChar(36);
		tokenizer.wordChars(37, 255);
		final List<String> commandTokens = new ArrayList<String>();
		while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
			commandTokens.add(tokenizer.sval);
		}
		invoke(commandTokens);
	}
	
	/**
	 * Invokes the command using the specified command tokens. The token list isn't used any
	 * further by the caller, so it can be modified at will.
	 * @param tokens the command tokens
	 * @throws IOException on I/O errors
	 */
	protected abstract void invoke(List<String> tokens) throws IOException;

	/**
	 * See {@link LoggerWriter#log(String)}; it delegates to this method. 
	 * @param message the message
	 */
	protected void logToolOutput(final MyLoggerWriter loggerWriter, final String message) {
		loggerWriter.superLog(message);
	}

	/**
	 * See {@link LoggerWriter#determineLogLeve}; it delegates to this method. 
	 * @param message the message
	 * @return the log level
	 */
	protected LogLevel determineToolOutputLogLevel(final MyLoggerWriter loggerWriter, final String message) {
		return loggerWriter.superDetermineLogLevel(message);
	}

	/**
	 * Customized version of {@link LoggerWriter}.
	 */
	public final class MyLoggerWriter extends LoggerWriter {

		/**
		 * Constructor.
		 * @param logger the logger to log to
		 */
		public MyLoggerWriter(final AbstractLogger logger) {
			super(logger);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.util.buildlog.LoggerWriter#log(java.lang.String)
		 */
		@Override
		public void log(final String message) {
			logToolOutput(this, message);
		}

		/**
		 * Invokes the log(message) method of LoggerWriter.
		 * @param message the message to log
		 */
		public void superLog(final String message) {
			super.log(message);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.util.buildlog.LoggerWriter#determineLogLevel(java.lang.String)
		 */
		@Override
		protected LogLevel determineLogLevel(final String message) {
			return determineToolOutputLogLevel(this, message);
		}

		/**
		 * Invokes the determineLogLevel(message) method of LoggerWriter.
		 * @param message the message
		 * @return the log level
		 */
		protected LogLevel superDetermineLogLevel(final String message) {
			return super.determineLogLevel(message);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.util.buildlog.LoggerWriter#log(name.martingeisse.ecobuild.util.buildlog.LogLevel, java.lang.String, java.lang.Throwable)
		 */
		@Override
		public void log(final LogLevel logLevel, final String message, final Throwable exception) {
			if (logLevel == LogLevel.ERROR) {
				context.notifyAboutFailure();
			} else if (triggerErrorOnUnrecognizedLogMessages && logLevel == LogLevel.UNKNOWN) {
				super.log(LogLevel.ERROR, "Module build has failed because of unknown command-line tool output.", null);
				context.notifyAboutFailure();
			}
			super.log(logLevel, message, exception);
		}

	}

}
