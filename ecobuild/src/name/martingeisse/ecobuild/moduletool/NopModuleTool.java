/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

import java.io.IOException;

/**
 * No-operation module tool.
 */
public class NopModuleTool implements IModuleTool {
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.IModuleTool#execute(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	public void execute(IModuleToolContext context) throws IOException {
	}
	
}
