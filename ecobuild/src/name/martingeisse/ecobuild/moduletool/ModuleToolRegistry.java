/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps track of module tools for types.
 * 
 * Special rule: For types beginning with a double-slash,
 * this registry always returns a {@link NopModuleTool}.
 */
public class ModuleToolRegistry implements IModuleToolProvider {

	/**
	 * the tools
	 */
	private Map<String, IModuleTool> tools;

	/**
	 * Constructor.
	 */
	public ModuleToolRegistry() {
		this.tools = new HashMap<String, IModuleTool>();
	}
	
	/**
	 * Registers a tool.
	 * @param type the type to register the tool for
	 * @param tool the tool to register
	 */
	public void registerTool(String type, IModuleTool tool) {
		tools.put(type, tool);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.IModuleToolProvider#selectTool(java.lang.String)
	 */
	@Override
	public IModuleTool selectTool(String type) {
		if (type.startsWith("//")) {
			return new NopModuleTool();
		} else {
			return tools.get(type);
		}
	}

}
