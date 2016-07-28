/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.eco32;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import name.martingeisse.ecobuild.Constants;
import name.martingeisse.ecobuild.UserMessageBuildException;
import name.martingeisse.ecobuild.moduletool.AbstractSingleOutputFileTool;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.moduletool.eco32.tools.LccInvocation;
import name.martingeisse.ecobuild.util.CommandLineToolInvocation;
import name.martingeisse.ecobuild.util.EcobuildFileUtil;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecobuild.util.buildlog.LogLevel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

/**
 * The tool used to build the EOS32 kernel, boot loaders, and similar
 * executables that run outside the context of an operating system.
 */
public class KernelTool extends AbstractSingleOutputFileTool {
	
	/**
	 * the addressPattern
	 */
	private static Pattern addressPattern = Pattern.compile("0x[0-9a-fA-F]{8}");
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#buildDependencies(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void buildDependencies(IModuleToolContext context) throws IOException {
		LccInvocation.buildLcc(context);
		super.buildDependencies(context);
	}

	// TODO: take changes in include files into account for delta-builds, not just as dependencies
	
	/**
	 * @return the default output file name to use of no "output" property is defined
	 */
	@Override
	protected String getDefaultOutputFileName(IModuleToolContext context) {
		return context.getModuleSourceFolder().getName() + ".bin";
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#onModuleOutdated(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void onModuleOutdated(IModuleToolContext context) throws IOException {

		// store context values in local variables for quick access
		File moduleSourceFolder = context.getModuleSourceFolder();
		File moduleBuildFolder = context.getModuleBuildFolder();
		AbstractLogger logger = context.getLogger();

		// determine generic C compiler flags
		String cflags = context.getModuleProperties().getProperty("cflags", "");
		
		// determine C compiler flags specific to building OS kernels
		String memoryProperty = context.getModuleProperties().getProperty("memory", "ram");
		String codeLocation;
		if (memoryProperty.equals("ram")) {
			codeLocation = "0xc0000000";
		} else if (memoryProperty.equals("rom")) {
			codeLocation = "0xe0000000";
		} else if (addressPattern.matcher(memoryProperty).matches()) {
			codeLocation = memoryProperty;
		} else {
			throw new UserMessageBuildException("Invalid 'memory' property (only 'ram' or 'rom' allowed): " + memoryProperty);
		}

		// determine file names
		String binFilePath = moduleBuildFolder.getCanonicalPath() + "/" + getOutputFileName(context);
		String mapFilePath = EcobuildFileUtil.replaceFilenameExtension(binFilePath, ".bin", ".map");
		String exoFilePath = EcobuildFileUtil.replaceFilenameExtension(binFilePath, ".bin", ".exo");
		String dosExoFilePath = EcobuildFileUtil.replaceFilenameExtension(binFilePath, ".bin", "-dos.exo");
		String sourceFiles = getSourceFileList(moduleSourceFolder);
		String includePath = EcobuildFileUtil.getCanonicalPath(moduleBuildFolder);
		
		// compile the code
		String compilerOptions = "-A -h -Wo-nolib -Wl-rc -Wl" + codeLocation + " -Wl-m $-Wl" + mapFilePath + "$ $-I" + includePath + "$";
		String compilerCommand = LccInvocation.getLccCommand(context) + ' ' + cflags + ' ' + compilerOptions + " -o $" + binFilePath + "$ " + sourceFiles;
		new LccInvocation(context, moduleSourceFolder, compilerCommand, logger).invoke();
		
		// convert the BIN file to an EXO file (needed to load the BIN file into the flash ROM)
		String bin2exo = context.resolveMainBuildPath("/devtools/bin2exo");
		new CommandLineToolInvocation(context, moduleBuildFolder, bin2exo + " 0 $" + binFilePath + "$ $" + exoFilePath + "$", context.getLogger()).invoke();

		// create a version of the EXO file with DOS line endings
		FileUtils.copyFile(new File(exoFilePath), new File(dosExoFilePath));
		if (Constants.NEEDS_UNIX_TO_DOS_FOR_EXO) {
			new CommandLineToolInvocation(context, moduleBuildFolder, "/opt/local/bin/unix2dos $" + dosExoFilePath + "$", context.getLogger()) {
				@Override
				protected LogLevel determineToolOutputLogLevel(MyLoggerWriter loggerWriter, String message) {
					if (message.startsWith("unix2dos: converting file")) {
						return LogLevel.TRACE;
					} else {
						return super.determineToolOutputLogLevel(loggerWriter, message);
					}
				};
			}.invoke();
		}
		
	}
	
	/**
	 * Collects all .c and .s source files from the source folder.
	 * 
	 * This method treats two files, start.s and end.s, specially: If present,
	 * they are always listed first and last, respectively, in the source file
	 * list. This is required for OS kernels to place code at reset/interrupt/TLB
	 * entry points and place labels at the end of the kernel code.
	 * 
	 * @param sourceFolder the module source folder
	 * @return a string containing all source file names
	 */
	private String getSourceFileList(File sourceFolder) {
		StringBuilder builder = new StringBuilder();
		for (String cfile : sourceFolder.list(new SuffixFileFilter(".c"))) {
			builder.append(cfile).append(' ');
		}
		boolean start = false, end = false;
		for (String sfile : sourceFolder.list(new SuffixFileFilter(".s"))) {
			if (sfile.equals("start.s")) {
				start = true;
			} else if (sfile.equals("end.s")) {
				end = true;
			} else {
				builder.append(sfile).append(' ');
			}
		}
		return ((start ? "start.s " : "") + builder + (end ? " end.s" : ""));
	}

}
