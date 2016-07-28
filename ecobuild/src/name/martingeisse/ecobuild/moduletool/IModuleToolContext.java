/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

import java.io.File;
import java.util.Properties;

import name.martingeisse.ecobuild.ModulePath;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;

/**
 * The build context in which a module tool is invoked
 */
public interface IModuleToolContext {

	/**
	 * Getter method for the root source folder.
	 * @return the root source folder
	 */
	public File getRootSourceFolder();
	
	/**
	 * Getter method for the module's source folder.
	 * @return the module's source folder
	 */
	public File getModuleSourceFolder();
	
	/**
	 * Convenience method to turn the specified path text to a module path,
	 * resolve it against the current and root module source folders, and
	 * convert the result to a canonical file path.
	 * 
	 * @param path the path to resolve
	 * @return the resolved path
	 */
	public String resolveModuleSourcePath(String path);

	/**
	 * Getter method for the root build folder.
	 * @return the root build folder
	 */
	public File getRootBuildFolder();
	
	/**
	 * Convenience method to turn the specified path text to a module path,
	 * resolve it against the current and root module build folders, and
	 * convert the result to a canonical file path.
	 * 
	 * @param path the path to resolve
	 * @return the resolved path
	 */
	public String resolveModuleBuildPath(String path);
	
	/**
	 * Getter method for the module's build folder.
	 * @return the module's build folder
	 */
	public File getModuleBuildFolder();

	/**
	 * Convenience method to turn the specified path text to a module path,
	 * resolve it against the root main build folder, and convert the result
	 * to a canonical file path.
	 * 
	 * @param path the path to resolve
	 * @return the resolved path
	 */
	public String resolveMainBuildPath(String path);
	
	/**
	 * Getter method for the main build folder that contains the ultimate output
	 * of the builder.
	 * @return the main build folder
	 */
	public File getMainBuildFolder();
	
	/**
	 * Getter method for the module's properties.
	 * @return the module's properties
	 */
	public Properties getModuleProperties();

	/**
	 * Getter method for the logger.
	 * @return the logger
	 */
	public AbstractLogger getLogger();
	
	/**
	 * Builds a dependency module.
	 * 
	 * @param modulePath the module path of the module to build
	 * @return the build folder of the dependency module
	 */
	public File buildDependency(ModulePath modulePath);

	/**
	 * Builds a dependency module. This is a convenience method that builds the
	 * module path from its textual representation.
	 * 
	 * @param modulePath the module path of the module to build
	 * @return the build folder of the dependency module
	 * @see IModuleToolContext#buildDependency(ModulePath)
	 */
	public File buildDependency(String modulePath);
	
	/**
	 * Builds an optional dependency module. The main difference to
	 * buildDependency() is that most of the exceptions that can occur
	 * while building the dependency are caught and logged (fatal
	 * exceptions will not be caught).
	 * 
	 * @param modulePath the module path of the module to build
	 * @return the build folder of the dependency module
	 */
	public File buildOptionalDependency(ModulePath modulePath);

	/**
	 * Builds an optional dependency module. This is a convenience method that builds the
	 * module path from its textual representation.
	 * 
	 * @param modulePath the module path of the module to build
	 * @return the build folder of the dependency module
	 * @see IModuleToolContext#buildOptionalDependency(ModulePath)
	 */
	public File buildOptionalDependency(String modulePath);
	
	/**
	 * Notifies the build system that this module tool has failed. This causes
	 * trace-level messages to become visible, and strong dependency build
	 * calls to fail with an exception.
	 */
	public void notifyAboutFailure();
	
}
