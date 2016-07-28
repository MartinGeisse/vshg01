/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.eco32;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecobuild.moduletool.AbstractSingleOutputFileTool;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.moduletool.eco32.tools.LccInvocation;
import name.martingeisse.ecobuild.util.EcobuildFileUtil;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;

/**
 * The tool used to build C applications running on EOS32.
 */
public class EosApplicationTool extends AbstractSingleOutputFileTool {
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#buildDependencies(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void buildDependencies(IModuleToolContext context) throws IOException {
		LccInvocation.buildLcc(context);
		// TODO: take libs and includes into account for delta-builds
		context.buildDependency("/eos32/baselibout");
		context.buildDependency("/eos32/userland/lib");
		super.buildDependencies(context);
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#onModuleOutdated(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void onModuleOutdated(IModuleToolContext context) throws IOException {
		
		File moduleSourceFolder = context.getModuleSourceFolder();
		File moduleBuildFolder = context.getModuleBuildFolder();
		AbstractLogger logger = context.getLogger();
		
		String executableName = getOutputFileName(context);
		String executablePath = moduleBuildFolder.getCanonicalPath() + "/" + executableName;
		String mapFilePath = EcobuildFileUtil.replaceFilenameExtension(executablePath, ".bin", ".map");
		
		String sourceFiles = StringUtils.join(moduleSourceFolder.list(new SuffixFileFilter(".c")), ' ');
		String command = LccInvocation.getLccCommand(context) + " -A -Wl-m $-Wl" + mapFilePath + "$ -o $" + moduleBuildFolder.getCanonicalPath() + "/" + executableName + "$ " + sourceFiles;
		new LccInvocation(context, moduleSourceFolder, command, logger).invoke();

	}

}
