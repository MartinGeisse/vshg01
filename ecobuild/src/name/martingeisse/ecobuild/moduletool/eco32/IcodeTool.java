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
import name.martingeisse.ecobuild.moduletool.eco32.tools.AsInvocation;
import name.martingeisse.ecobuild.moduletool.eco32.tools.LdInvocation;
import name.martingeisse.ecobuild.util.CommandLineToolInvocation;
import name.martingeisse.ecobuild.util.EcobuildFileUtil;

/**
 * The tool used to build C / assembler libraries running on EOS32.
 */
public class IcodeTool extends AbstractSingleOutputFileTool {
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#buildDependencies(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void buildDependencies(IModuleToolContext context) throws IOException {
		AsInvocation.buildAs(context);
		LdInvocation.buildLd(context);
		super.buildDependencies(context);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractSingleOutputFileTool#getOutputFileName(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected String getOutputFileName(IModuleToolContext context) {
		return "icode.dump";
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#onModuleOutdated(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void onModuleOutdated(IModuleToolContext context) throws IOException {

		File outFile = new File(context.getModuleBuildFolder(), "icode.o");
		String outPath = EcobuildFileUtil.getCanonicalPath(outFile);
		String asCommand = AsInvocation.getAsCommand(context) + " -o $" + outPath + "$ icode.s";
		new AsInvocation(context, context.getModuleSourceFolder(), asCommand, context.getLogger()).invoke();
//		String asCommand = ToolhostAsInvocation.asCommand + " -o $" + outPath + "$ icode.s";
//		new ToolhostAsInvocation(context, context.getModuleSourceFolder(), asCommand, context.getLogger()).invoke();
		
		String ldCommand = LdInvocation.getLdCommand(context) + " -h -o icode.bin icode.o";
		new LdInvocation(context, context.getModuleBuildFolder(), ldCommand, context.getLogger()).invoke();
//		String ldCommand = ToolhostLdInvocation.ldCommand + " -h -o icode.bin icode.o";
//		new ToolhostLdInvocation(context, context.getModuleBuildFolder(), ldCommand, context.getLogger()).invoke();
		
		String dumpCommandBase = context.resolveMainBuildPath("/devtools/icodedump");
		new CommandLineToolInvocation(context, context.getModuleBuildFolder(), dumpCommandBase + " icode.bin icode.dump", context.getLogger()).invoke();
		
	}

}
