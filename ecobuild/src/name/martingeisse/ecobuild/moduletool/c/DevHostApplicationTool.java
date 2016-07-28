/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.c;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecobuild.Constants;
import name.martingeisse.ecobuild.moduletool.AbstractSingleOutputFileTool;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;

/**
 * The tool used to build C applications running on the Dev-Host.
 */
public class DevHostApplicationTool extends AbstractSingleOutputFileTool {
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#onModuleOutdated(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void onModuleOutdated(IModuleToolContext context) throws IOException {
		
		File moduleSourceFolder = context.getModuleSourceFolder();
		File moduleBuildFolder = context.getModuleBuildFolder();
		AbstractLogger logger = context.getLogger();
		
		String executableName = getOutputFileName(context);
		String sourceFiles = StringUtils.join(moduleSourceFolder.list(new SuffixFileFilter(".c")), ' ');
		String outputFileName = moduleBuildFolder.getCanonicalPath() + "/" + executableName;
		String command = "$" + Constants.GCC_PATH + "$ -Wall -m32 -o $" + outputFileName + "$ " + sourceFiles;
		new GccInvocation(context, moduleSourceFolder, command, logger).invoke();
		File outputFile = new File(moduleBuildFolder, executableName);
		if (!outputFile.setExecutable(true)) {
			context.getLogger().logError("could not make DevApplication target executable: " + outputFile);
		}
	}

}
