/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.eco32;

import java.io.IOException;

import name.martingeisse.ecobuild.moduletool.IModuleToolContext;

/**
 * The tool used to build C / assembler libraries running on EOS32.
 * 
 * Note that this tool need not add the standard library path or
 * include path since they are hardcoded in the compiler. It only builds
 * the standard library as a dependency.
 */
public class EosLibraryTool extends Eco32LibraryTool {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#buildDependencies(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected void buildDependencies(IModuleToolContext context) throws IOException {
		super.buildDependencies(context);
		// TODO: take includes into account for delta-builds
		context.buildDependency("/eos32/userland/lib/include");
	}

}
