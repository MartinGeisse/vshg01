/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

import java.io.File;
import java.io.IOException;
import java.util.List;

import name.martingeisse.ecobuild.util.EcobuildFileUtil;

/**
 * Base class for module tools for which the modules do
 * not contain other modules in subfolders.
 * 
 * This class provides logic to rebuild the module only
 * if files are outdated.
 */
public abstract class AbstractLeafModuleTool implements IModuleTool {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.IModuleTool#execute(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	public void execute(IModuleToolContext context) throws IOException {
		buildDependencies(context);
		if (determineIfOutdated(context)) {
			onModuleOutdated(context);
		}
	}

	/**
	 * This method is invoked first to build all dependency modules.
	 * @throws IOException on I/O errors
	 */
	protected void buildDependencies(IModuleToolContext context) throws IOException {
	}
	
	/**
	 * This method is invoked if the module is outdated and must be rebuilt.
	 * @throws IOException on I/O errors
	 */
	protected abstract void onModuleOutdated(IModuleToolContext context) throws IOException;
	
	/**
	 * Determines whether the module is outdated and must be rebuilt.
	 * 
	 * The default implementation determines input files, expected output files,
	 * and actual output files. The module is outdated if any expected output
	 * files are missing, or if any input file is newer than any output file
	 * (this is equivalent to checking whether the newest input file is newer
	 * than the oldest output file, which is what is actually checked).
	 * 
	 * @return true if outdated, false if up-to-date
	 */
	protected boolean determineIfOutdated(IModuleToolContext context) {
		
//		context.getLogger().logTrace("begin determineIfOutdated()");
		
		List<File> expectedOutputFiles = determineExpectedOutputFiles(context);
		for (File file : expectedOutputFiles) {
			if (!file.exists()) {
//				context.getLogger().logTrace("end determineIfOutdated(): outdated because of missing file " + EcobuildFileUtil.getCanonicalPath(file));
				return true;
			}
		}
		
		long newestInputFileTimestamp = determineNewestInputFileTimestamp(context);
		long oldestOutputFileTimestamp = determineOldestOutputFileTimestamp(context);
		boolean outdated = (newestInputFileTimestamp > oldestOutputFileTimestamp);
//		context.getLogger().logTrace("end determineIfOutdated(): newestInputFileTimestamp = " + newestInputFileTimestamp + ", oldestOutputFileTimestamp = " + oldestOutputFileTimestamp + ", outdated = " + outdated);
		return outdated;
		
	}

	/**
	 * Determines a list of expected output files. This method must
	 * be implemented by subclasses since there is no useful default
	 * behavior. Most importantly, the returned list triggers building
	 * the module on the first build, although it also helps with added
	 * files in delta builds.
	 * @return the list of expected output files
	 */
	protected abstract List<File> determineExpectedOutputFiles(IModuleToolContext context);

	/**
	 * Determines the newest timestamp of all input files. The default
	 * implementation scans the module source folder for this. Subclasses
	 * may want to override this method to include the timestamps of
	 * dependency modules -- this usually implies that these dependencies
	 * are build-requested before checking whether this module is
	 * outdated.
	 * @return the timestamp
	 */
	protected long determineNewestInputFileTimestamp(IModuleToolContext context) {
		return EcobuildFileUtil.getMinMaxTimestampRecursively(context.getModuleSourceFolder(), true);
	}

	/**
	 * Determines the oldest timestamp of all output files. The default
	 * implementation scans the module build folder for this and should
	 * be sufficient for most module tools.
	 * @return the timestamp
	 */
	protected long determineOldestOutputFileTimestamp(IModuleToolContext context) {
		return EcobuildFileUtil.getMinMaxTimestampRecursively(context.getModuleBuildFolder(), false);
	}
	
}
