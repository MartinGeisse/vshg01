/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.eco32;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import name.martingeisse.ecobuild.moduletool.AbstractLeafModuleTool;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.moduletool.eco32.tools.AsInvocation;
import name.martingeisse.ecobuild.moduletool.eco32.tools.LccInvocation;
import name.martingeisse.ecobuild.util.EcobuildFileUtil;

/**
 * The base class for tools used to build C / assembler libraries running on Eco32.
 * 
 * Subclasses typically add standard libraries. They must build such libraries
 * as dependencies and add them to the include / library paths.
 */
public class Eco32LibraryTool extends AbstractLeafModuleTool {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#buildDependencies(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void buildDependencies(IModuleToolContext context) throws IOException {
		super.buildDependencies(context);
		LccInvocation.buildLcc(context);
		AsInvocation.buildAs(context);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#determineExpectedOutputFiles(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected List<File> determineExpectedOutputFiles(IModuleToolContext context) {
		List<File> result = new ArrayList<File>();
		for (File srcFile : context.getModuleSourceFolder().listFiles()) {
			String name = srcFile.getName();
			if (name.endsWith(".c") || name.endsWith(".s")) {
				File outFile = new File(context.getModuleBuildFolder(), name.substring(0, name.length() - 2) + ".o");
				result.add(outFile);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#onModuleOutdated(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void onModuleOutdated(IModuleToolContext context) throws IOException {

		// extract properties
		String cflags = context.getModuleProperties().getProperty("cflags", "");
		
		// compile each source file individually
		for (File srcFile : context.getModuleSourceFolder().listFiles()) {
			String srcFileName = srcFile.getName();
			File outFile = new File(context.getModuleBuildFolder(), srcFileName.substring(0, srcFileName.length() - 2) + ".o");
			if (srcFileName.endsWith(".c")) {
				compileCFile(context, srcFile, outFile, cflags);
			} else if (srcFileName.endsWith(".s")) {
				compileSFile(context, srcFile, outFile, cflags);
			}
		}
		
	}
	
	/**
	 * Compiles an S (assembler) source file.
	 * @param srcFile the source file
	 * @param outFile the output file to generate
	 */
	protected void compileSFile(IModuleToolContext context, File srcFile, File outFile, String cflags) throws IOException {
		String command = AsInvocation.getAsCommand(context) + " -o $" + EcobuildFileUtil.getCanonicalPath(outFile) + "$ " + srcFile.getName();
		new AsInvocation(context, context.getModuleSourceFolder(), command, context.getLogger()).invoke();
	}

	/**
	 * Compiles a C source file.
	 * @param srcFile the source file
	 * @param outFile the output file to generate
	 */
	protected void compileCFile(IModuleToolContext context, File srcFile, File outFile, String cflags) throws IOException {
		String command = LccInvocation.getLccCommand(context) + " -A -c " + cflags + " -o $" + EcobuildFileUtil.getCanonicalPath(outFile) + "$ " + srcFile.getName();
		new LccInvocation(context, context.getModuleSourceFolder(), command, context.getLogger()).invoke();
	}
	
}
