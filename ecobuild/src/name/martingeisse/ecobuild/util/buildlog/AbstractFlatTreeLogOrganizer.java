/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Base implementation of {@link ITreeLogOrganizer} that
 * stores tree nodes in a list sorted by module identifier,
 * but without any explicit hierarchy.
 */
public abstract class AbstractFlatTreeLogOrganizer implements ITreeLogOrganizer {

	/**
	 * the treeEntries
	 */
	private final SortedMap<String, TreeEntry> treeEntries;

	/**
	 * Constructor.
	 */
	public AbstractFlatTreeLogOrganizer() {
		treeEntries = new TreeMap<String, TreeEntry>();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.ITreeLogOrganizer#handleNodeLog(java.lang.String, java.util.List, boolean)
	 */
	@Override
	public void handleNodeLog(final String absoluteModuleIdentifier, List<LogMessageEntry> messageEntries, final boolean success) {
		TreeEntry existing = treeEntries.remove(absoluteModuleIdentifier);
		if (existing != null) {
			existing.getMessageEntries().addAll(messageEntries);
			messageEntries = existing.getMessageEntries();
		}
		treeEntries.put(absoluteModuleIdentifier, new TreeEntry(absoluteModuleIdentifier, messageEntries, success));
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.ITreeLogOrganizer#generateLogOutput()
	 */
	@Override
	public void generateLogOutput() {
		beginTree();
		for (final TreeEntry treeEntry : treeEntries.values()) {
			beginNode(treeEntry);
			for (final LogMessageEntry logMessageEntry : treeEntry.messageEntries) {
				handleMessage(logMessageEntry);
			}
			endNode(treeEntry);
		}
		endTree();
	}

	/**
	 * This method should be implemented by subclasses and is called
	 * when beginning to generate the log output for the whole tree.
	 */
	protected abstract void beginTree();

	/**
	 * This method should be implemented by subclasses and is called
	 * when finishing the log output for the whole tree.
	 */
	protected abstract void endTree();

	/**
	 * This method should be implemented by subclasses and is called
	 * when beginning to generate the log output for a singel tree node.
	 */
	protected abstract void beginNode(TreeEntry entry);

	/**
	 * This method should be implemented by subclasses and is called
	 * when finishing the log output for a singel tree node.
	 */
	protected abstract void endNode(TreeEntry entry);

	/**
	 * This method should be implemented by subclasses and is called
	 * once for each log message.
	 */
	protected abstract void handleMessage(LogMessageEntry entry);

	/**
	 * This class represents log tree nodes.
	 */
	public static class TreeEntry {

		/**
		 * the absoluteModuleIdentifier
		 */
		private final String absoluteModuleIdentifier;

		/**
		 * the messageEntries
		 */
		private final List<LogMessageEntry> messageEntries;

		/**
		 * the success
		 */
		private final boolean success;

		/**
		 * Constructor.
		 * @param absoluteModuleIdentifier the absolute module identifier of the module
		 * for which this log node applies
		 * @param messageEntries the list of log messages
		 * @param success whether the module was built successfully
		 */
		public TreeEntry(final String absoluteModuleIdentifier, final List<LogMessageEntry> messageEntries, final boolean success) {
			this.absoluteModuleIdentifier = absoluteModuleIdentifier;
			this.messageEntries = messageEntries;
			this.success = success;
		}

		/**
		 * Getter method for the absoluteModuleIdentifier.
		 * @return the absoluteModuleIdentifier
		 */
		public String getAbsoluteModuleIdentifier() {
			return absoluteModuleIdentifier;
		}

		/**
		 * Getter method for the messageEntries.
		 * @return the messageEntries
		 */
		public List<LogMessageEntry> getMessageEntries() {
			return messageEntries;
		}

		/**
		 * Getter method for the success.
		 * @return the success
		 */
		public boolean isSuccess() {
			return success;
		}

	}

}
