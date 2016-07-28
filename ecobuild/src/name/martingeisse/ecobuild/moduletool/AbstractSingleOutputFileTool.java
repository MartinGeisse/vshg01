/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * Base class for tools that generate a single output file. This class
 * provides an implementation for determineExpectedOutputFiles() as
 * well as a method to get the output file name or {@link File} object.
 * 
 * The getOutputFileName() method can be overridden to return the name
 * of that file. The default implementation returns the "output" module
 * property if present, otherwise the result of getDefaultOutputFileName(),
 * whose default implementation returns the name of the module folder.
 */
public abstract class AbstractSingleOutputFileTool extends AbstractLeafModuleTool {

	/**
	 * @return the default output file name to use of no "output" property is defined
	 */
	protected String getDefaultOutputFileName(IModuleToolContext context) {
		return context.getModuleSourceFolder().getName();
	}
	
	/**
	 * @return the name of the file which is built by this tool
	 */
	protected String getOutputFileName(IModuleToolContext context) {
		Properties moduleProperties = context.getModuleProperties();
		String executableName = moduleProperties.getProperty("output", getDefaultOutputFileName(context));
		return executableName;
	}
	
	/**
	 * @param context the build context
	 * @return the output file
	 */
	protected final File getOutputFile(IModuleToolContext context) {
		return new File(context.getModuleBuildFolder(), getOutputFileName(context));
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#determineExpectedOutputFiles(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	protected List<File> determineExpectedOutputFiles(IModuleToolContext context) {
		return Arrays.asList(getOutputFile(context));
	}

}
