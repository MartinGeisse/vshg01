/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

import java.io.File;
import java.io.IOException;

/**
 * This tool simple recurses on each subfolder.
 */
public class ModuleCollectionTool implements IModuleTool {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.IModuleTool#execute(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	public void execute(IModuleToolContext context) throws IOException {
		File moduleSourceFolder = context.getModuleSourceFolder();
		for (File sourceSubfolder : moduleSourceFolder.listFiles()) {
			if (sourceSubfolder.isDirectory()) {
				String name = sourceSubfolder.getName();
				if (!name.startsWith(".")) {
					context.buildOptionalDependency(name);
				}
			}
		}
	}

}
