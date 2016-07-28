/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

import java.io.IOException;

/**
 * A tool that controls the build process for one module. Only one
 * such tool exists for each module, and is selected by the "type"
 * field of the module.properties file.
 */
public interface IModuleTool {

	/**
	 * Executes this tool.
	 * @param context the build context (used to build dependency modules)
	 * @throws IOException on I/O errors
	 */
	public void execute(IModuleToolContext context) throws IOException;
	
}
