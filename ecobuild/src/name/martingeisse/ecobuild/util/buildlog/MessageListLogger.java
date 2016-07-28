/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

import java.util.ArrayList;
import java.util.List;

/**
 * This logger "logs" messages by storing them in a list,
 * together with log levels. This can be used to decide
 * what happens with the messages *after* the log was
 * written, and take log levels into account in that decision.
 * 
 * For example, this class is used to keep a log for a tool
 * run, and decide whether to show trace-level messages
 * only after the tool has either completed or failed.
 */
public final class MessageListLogger extends AbstractLogger {

	/**
	 * the entries
	 */
	private final List<LogMessageEntry> entries;
	
	/**
	 * Constructor.
	 */
	public MessageListLogger() {
		this.entries = new ArrayList<LogMessageEntry>();
	}
	
	/**
	 * Getter method for the entries.
	 * @return the entries
	 */
	public List<LogMessageEntry> getEntries() {
		return entries;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.buildlog.AbstractLogger#log(name.martingeisse.ecobuild.util.buildlog.LogLevel, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(LogLevel level, String message, Throwable e) {
		entries.add(new LogMessageEntry(level, message, e));
	}

	/**
	 * Deletes all accumulated log messages. This is a convenience
	 * method for getEntries().clear().
	 */
	public void clear() {
		entries.clear();
	}

}
