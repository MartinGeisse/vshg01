/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

import java.util.List;


/**
 * This interface is used by the builder to associate log
 * fragments with source tree nodes. Basically, for each
 * built module, the builder hands the log fragment for that
 * module and the module identifier to this interface.
 * When the whole build is complete, this class generates
 * the final log output.
 * 
 * Implementations cannot expect the log fragments to
 * arrive in a specific order because modules can trigger
 * arbitrary dependency builds -- the fragments will
 * arrive in the order of completed module builds just
 * as they occur.
 */
public interface ITreeLogOrganizer {

	/**
	 * Handles the log fragment for a tree node. This method stores the
	 * log fragment in some implementation-dependent manner to use
	 * it later when the final log output is generated.
	 * 
	 * Note that this method can be invoked multiple times for each node.
	 * The logs from all invocations should be concatenated in some
	 * meaningful way. Mulitple invocations occur when a module is
	 * referred to as a dependency from multiple referrers and
	 * typically add little more than a check that ensures that the
	 * module is up-to-date.
	 * 
	 * @param absoluteModuleIdentifier the absolute module identifier of the module
	 * for which the fragment applies
	 * @param entries the list of log entries
	 * @param success whether building this module was successful
	 */
	public void handleNodeLog(String absoluteModuleIdentifier, List<LogMessageEntry> entries, boolean success);
	
	/**
	 * Generates the final log output from the log fragments that have been inserted
	 * via handleNodeLog().
	 */
	public void generateLogOutput();
	
}
