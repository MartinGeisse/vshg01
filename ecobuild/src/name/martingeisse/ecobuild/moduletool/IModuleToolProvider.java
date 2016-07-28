/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

/**
 * This interface is able to provide a module tool for a
 * given module type.
 */
public interface IModuleToolProvider {

	/**
	 * Selects a tool for the specified module type.
	 * @param type the module type
	 * @return the selected tool
	 */
	public IModuleTool selectTool(String type);
	
}
