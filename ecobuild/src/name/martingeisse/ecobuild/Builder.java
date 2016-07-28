/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import name.martingeisse.ecobuild.moduletool.IModuleTool;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.moduletool.ModuleCollectionTool;
import name.martingeisse.ecobuild.moduletool.ModuleToolRegistry;
import name.martingeisse.ecobuild.moduletool.c.DevHostApplicationTool;
import name.martingeisse.ecobuild.moduletool.c.DevSystemLccTool;
import name.martingeisse.ecobuild.moduletool.eco32.Eco32LibraryTool;
import name.martingeisse.ecobuild.moduletool.eco32.EosApplicationTool;
import name.martingeisse.ecobuild.moduletool.eco32.EosLibraryTool;
import name.martingeisse.ecobuild.moduletool.eco32.IcodeTool;
import name.martingeisse.ecobuild.moduletool.eco32.KernelTool;
import name.martingeisse.ecobuild.moduletool.output.OutputTool;
import name.martingeisse.ecobuild.util.CommandLineToolInvocation;
import name.martingeisse.ecobuild.util.EcobuildFileUtil;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;
import name.martingeisse.ecobuild.util.buildlog.HtmlFlatTreeLogOrganizer;
import name.martingeisse.ecobuild.util.buildlog.LogLevel;
import name.martingeisse.ecobuild.util.buildlog.MessageListLogger;

import org.apache.commons.io.FileUtils;

/**
 * The main entry point of the Ecobuild system.
 */
public class Builder {

	/**
	 * the moduleToolRegistry
	 */
	private final ModuleToolRegistry moduleToolRegistry;

	/**
	 * the rootSourceFolder
	 */
	private final File rootSourceFolder;

	/**
	 * the rootBuildFolder
	 */
	private final File mainBuildFolder;

	/**
	 * the rootBuildFolder
	 */
	private final File rootBuildFolder;

	/**
	 * the verboseMode
	 */
	private boolean verboseMode;

	/**
	 * the treeLogOrganizer
	 */
	private final HtmlFlatTreeLogOrganizer treeLogOrganizer;

	/**
	 * the startedModules
	 */
	private Set<String> startedModules;

	/**
	 * the finishedModules
	 */
	private Map<String, Boolean> finishedModules;

	/**
	 * Constructor.
	 */
	public Builder() {
		moduleToolRegistry = new ModuleToolRegistry();
		rootSourceFolder = new File(Constants.SOURCE_FOLDER_NAME);
		mainBuildFolder = new File(Constants.MAIN_BUILD_FOLDER_NAME);
		rootBuildFolder = new File(mainBuildFolder, Constants.MODULE_BUILD_FOLDER_NAME);
		verboseMode = false;
		treeLogOrganizer = new HtmlFlatTreeLogOrganizer();
	}

	/**
	 * Getter method for the moduleToolRegistry.
	 * @return the moduleToolRegistry
	 */
	public ModuleToolRegistry getModuleToolRegistry() {
		return moduleToolRegistry;
	}

	/**
	 * Getter method for the rootSourceFolder.
	 * @return the rootSourceFolder
	 */
	public File getRootSourceFolder() {
		return rootSourceFolder;
	}

	/**
	 * Getter method for the rootBuildFolder.
	 * @return the rootBuildFolder
	 */
	public File getRootBuildFolder() {
		return rootBuildFolder;
	}

	/**
	 * Getter method for the verboseMode.
	 * @return the verboseMode
	 */
	public boolean isVerboseMode() {
		return verboseMode;
	}

	/**
	 * Setter method for the verboseMode.
	 * @param verboseMode the verboseMode to set
	 */
	public void setVerboseMode(final boolean verboseMode) {
		this.verboseMode = verboseMode;
	}

	/**
	 * Registers the default tools in the module tool registry.
	 */
	public void registerDefaultTools() {
		moduleToolRegistry.registerTool("ModuleCollection", new ModuleCollectionTool());
		moduleToolRegistry.registerTool("Output", new OutputTool());
		moduleToolRegistry.registerTool("DevApplication", new DevHostApplicationTool());
		moduleToolRegistry.registerTool("DevSystemLcc", new DevSystemLccTool());
		moduleToolRegistry.registerTool("EosApplication", new EosApplicationTool());
		moduleToolRegistry.registerTool("EosLibrary", new EosLibraryTool());
		moduleToolRegistry.registerTool("Kernel", new KernelTool());
		moduleToolRegistry.registerTool("Icode", new IcodeTool());
		moduleToolRegistry.registerTool("Eco32Library", new Eco32LibraryTool());
	}

	/**
	 * Runs the build process.
	 */
	public void build() {

		// create the main build folder if it does not exist yet (for clean builds)
		if (!mainBuildFolder.exists()) {
			if (!mainBuildFolder.mkdir()) {
				throw new UserMessageBuildException("could not create main build folder " + EcobuildFileUtil.getCanonicalPath(mainBuildFolder));
			}
		}

		// create the root build folder if it does not exist yet (for clean builds)
		if (!rootBuildFolder.exists()) {
			if (!rootBuildFolder.mkdir()) {
				throw new UserMessageBuildException("could not create root build folder " + EcobuildFileUtil.getCanonicalPath(rootBuildFolder));
			}
		}

		// create an empty set to check for recursive dependencies
		startedModules = new HashSet<String>();
		finishedModules = new HashMap<String, Boolean>();

		// build the root module
		buildOptionalModule(rootSourceFolder, rootBuildFolder);

		// generate log output
		try {
			final FileWriter logWriter = new FileWriter(new File(mainBuildFolder, "buildlog.html"));
			treeLogOrganizer.setOutputWriter(logWriter);
			treeLogOrganizer.generateLogOutput();
			logWriter.close();
		} catch (final IOException e) {
			throw new FatalBuildException("could not write build log", e);
		}

	}

	/**
	 * Runs the clean process.
	 */
	public void clean() {
		try {
			FileUtils.deleteDirectory(mainBuildFolder);
		} catch (final Exception e) {
			throw new UserMessageBuildException("could not delete main build folder " + EcobuildFileUtil.getCanonicalPath(mainBuildFolder), e);
		}
	}

	/**
	 * Builds a module, taking the source and storing the built files to/from
	 * the specified folders. Throws an exception if the module fails to build.
	 * @param moduleSourceFolder the folder to load the source code from
	 * @param moduleBuildFolder the folder to store built files into
	 */
	private void buildModule(final File moduleSourceFolder, final File moduleBuildFolder) {
		final boolean success = buildOptionalModule(moduleSourceFolder, moduleBuildFolder);
		if (!success) {
			throw new UserMessageBuildException("Failed to build module: " + moduleSourceFolder);
		}
	}

	/**
	 * Builds a module, taking the source and storing the built files to/from
	 * the specified folders.
	 * 
	 * Unlike buildModule(), this method does not throw an exception if the module
	 * fails to build. Instead, the return value signals whether building the module
	 * has succeeded.
	 * 
	 * The typical uses of this method are (1) to build submodules of a
	 * ModuleCollection-type module, and (2) to build a dependency for which the
	 * calling module can somehow handle failed builds.
	 * 
	 * @param moduleSourceFolder the folder to load the source code from
	 * @param moduleBuildFolder the folder to store built files into
	 */
	private boolean buildOptionalModule(File moduleSourceFolder, File moduleBuildFolder) {

		// get rid of ".." path elements
		moduleSourceFolder = EcobuildFileUtil.getCanonicalFile(moduleSourceFolder);
		moduleBuildFolder = EcobuildFileUtil.getCanonicalFile(moduleBuildFolder);

		// build the module path for this module
		final ModulePath modulePath = new ModulePath(rootSourceFolder, moduleSourceFolder);
		final String modulePathText = modulePath.toString();

		// Make sure we don't build a module twice. Doing so would typically result
		// in a check that finds the module being up-to-date and not actually rebuild
		// it, but we want to avoid the check itself and also not rebuild a module
		// that does not implement a proper check.
		final Boolean previousSuccess = finishedModules.get(modulePathText);
		if (previousSuccess != null) {
			return previousSuccess;
		}

		// encapsulate logging
		final MessageListLogger logger = new MessageListLogger();
		boolean success = false;
		try {

			// check for recursive dependencies
			if (!startedModules.add(modulePathText)) {
				logger.logError("Recursive dependency involving module " + moduleSourceFolder);
				return false;
			}

			// actually build the module
			success = buildModuleInternal(moduleSourceFolder, moduleBuildFolder, logger);
			return success;

		} finally {

			// finish logging
			treeLogOrganizer.handleNodeLog(modulePath.toString(), logger.getEntries(), success);

			// stop checking for recursive dependencies
			startedModules.remove(modulePathText);

			// don't re-build this module
			finishedModules.put(modulePathText, success);

		}

	}

	/**
	 * Internal build method that takes a logger from the caller. Returns true on success,
	 * false on failure. Only throws an exception on fatal errors.
	 * 
	 * Both folders must use canonical paths.
	 */
	private boolean buildModuleInternal(final File moduleSourceFolder, final File moduleBuildFolder, final AbstractLogger logger) {

		// verbose output
		if (verboseMode) {
			logger.logTrace("starting to build module: " + moduleSourceFolder);
		}

		// load the module build properties
		final File modulePropertiesFile = new File(moduleSourceFolder, "module.properties");
		final Properties properties = new Properties();
		FileInputStream propertiesInputStream = null;
		try {
			propertiesInputStream = new FileInputStream(modulePropertiesFile);
			properties.load(propertiesInputStream);
		} catch (final Exception e) {
			logger.logError("error loading " + modulePropertiesFile.getPath(), e);
			return false;
		} finally {
			if (propertiesInputStream != null) {
				try {
					propertiesInputStream.close();
				} catch (final Exception e) {
				}
			}
		}

		// determine the module type
		String moduleType = properties.getProperty("type");
		if (moduleType == null) {
			logger.logError("no 'type' specified in " + modulePropertiesFile.getPath());
			return false;
		}
		moduleType = moduleType.trim();

		// determine the module tool to use for this module
		final IModuleTool moduleTool = moduleToolRegistry.selectTool(moduleType);
		if (moduleTool == null) {
			logger.logError("no module tool registered for type: " + moduleType);
			return false;
		}

		// create the output folder if it does not exist
		try {
			FileUtils.forceMkdir(moduleBuildFolder);
		} catch (final IOException e) {
			logger.logError("exception while creating the output folder " + moduleBuildFolder, e);
			return false;
		}

		// execute the module tool
		boolean success = false;
		try {

			// this object is used by module tools to communicate with this builder
			final ModuleToolContext context = new ModuleToolContext(moduleSourceFolder, moduleBuildFolder, properties, logger);

			// generic dependency mechanism
			final String genericDependencies = context.getModuleProperties().getProperty("dependencies");
			if (genericDependencies != null) {
				for (final String dependency : genericDependencies.trim().split("\\s+")) {
					context.buildDependency(dependency);
				}
			}

			// generic pre-build scripts
			runGenericScript(context, moduleSourceFolder, "prebuild");

			// actually build the module
			moduleTool.execute(context);

			// generic post-build scripts
			runGenericScript(context, moduleSourceFolder, "postbuild");

			// verbose output
			if (verboseMode) {
				logger.logTrace("finished building module normally: " + moduleSourceFolder);
			}

			// determine success
			success = !context.failed;
			
		} catch (final UserMessageBuildException e) {
			logger.logError(e.getMessage());
		} catch (final FatalBuildException e) {
			logger.logError("A fatal exception occurred while invoking the module build tool for module " + moduleSourceFolder.getPath(), e);
			throw e;
		} catch (final Exception e) {
			logger.logError("An exception occurred while invoking the module build tool for module " + moduleSourceFolder.getPath(), e);
		}

		// check if failed
		if (!success) {
			logger.logError("Module failed to build: " + moduleSourceFolder);
		}
		return success;

	}

	/**
	 * Runs a generic build script. This allows users to do things not implemented by
	 * module tools. The current working directory is the folder in which the script
	 * lies.
	 * 
	 * The script is expected in the specified parent folder, named baseName.sh. If
	 * a baseName-expected-output file exists, it is read as a {@link PatternListFile}
	 * and used to filter expected messages so they don't cause a build error. Without
	 * such a file, the script is not expected to produce any output.
	 * 
	 * If the script does not exist, it is silently skipped.
	 * 
	 * The following arguments are passed:
	 * - the module source folder
	 * - the root source folder
	 * - the module build folder
	 * - the root build folder
	 * - the main output folder
	 */
	private void runGenericScript(final ModuleToolContext context, final File parentFolder, final String baseName) throws IOException {
		final File script = new File(parentFolder, baseName + ".sh");
		if (script.exists()) {
			final PatternListFile expectedOutput = new PatternListFile(new File(parentFolder, baseName + "-expected-output"), true);
			final StringBuilder command = new StringBuilder("$" + Constants.BASH_PATH + "$ " + script.getName());
			command.append(" $").append(EcobuildFileUtil.getCanonicalPath(context.getModuleSourceFolder()));
			command.append("$ $").append(EcobuildFileUtil.getCanonicalPath(context.getRootSourceFolder()));
			command.append("$ $").append(EcobuildFileUtil.getCanonicalPath(context.getModuleBuildFolder()));
			command.append("$ $").append(EcobuildFileUtil.getCanonicalPath(context.getRootBuildFolder()));
			command.append("$ $").append(EcobuildFileUtil.getCanonicalPath(context.getMainBuildFolder()));
			command.append("$");
			new CommandLineToolInvocation(context, parentFolder, command.toString(), context.getLogger()) {
				@Override
				protected LogLevel determineToolOutputLogLevel(MyLoggerWriter loggerWriter, String message) {
					return (expectedOutput.matchesAny(message) ? LogLevel.TRACE : LogLevel.ERROR);
				};
			}.invoke();
		}
	}

	/**
	 * Helper method for buildDependency() and buildOptionalDependency().
	 */
	private File buildDependency(final File moduleSourceFolder, final File moduleBuildFolder, final ModulePath modulePath, final boolean optional) {

		// argument check
		if (modulePath == null) {
			throw new ToolBuildException("dependency module path is null");
		}

		// determine the dependency's source and build folders
		final File dependencySourceFolder = modulePath.resolve(rootSourceFolder, moduleSourceFolder);
		final File dependencyBuildFolder = modulePath.resolve(rootBuildFolder, moduleBuildFolder);

		// build the dependency module
		if (optional) {
			buildOptionalModule(dependencySourceFolder, dependencyBuildFolder);
		} else {
			buildModule(dependencySourceFolder, dependencyBuildFolder);
		}

		// return the dependency's build folder so the caller can use the built files
		return dependencyBuildFolder;
	}

	/**
	 * Implementation of {@link IModuleToolContext}.
	 */
	private class ModuleToolContext implements IModuleToolContext {

		/**
		 * the moduleSourceFolder
		 */
		private final File moduleSourceFolder;

		/**
		 * the moduleBuildFolder
		 */
		private final File moduleBuildFolder;

		/**
		 * the moduleProperties
		 */
		private final Properties moduleProperties;

		/**
		 * the logger
		 */
		private final AbstractLogger logger;

		/**
		 * the failed
		 */
		private boolean failed;

		/**
		 * Constructor.
		 */
		private ModuleToolContext(final File moduleSourceFolder, final File moduleBuildFolder, final Properties moduleProperties, final AbstractLogger logger) {
			this.moduleSourceFolder = moduleSourceFolder;
			this.moduleBuildFolder = moduleBuildFolder;
			this.moduleProperties = moduleProperties;
			this.logger = logger;
			this.failed = false;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#getRootSourceFolder()
		 */
		@Override
		public File getRootSourceFolder() {
			return rootSourceFolder;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#getModuleSourceFolder()
		 */
		@Override
		public File getModuleSourceFolder() {
			return moduleSourceFolder;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#resolveModuleSourcePath(java.lang.String)
		 */
		@Override
		public String resolveModuleSourcePath(final String path) {
			return EcobuildFileUtil.getCanonicalPath(new ModulePath(path).resolve(rootSourceFolder, moduleSourceFolder));
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#getRootBuildFolder()
		 */
		@Override
		public File getRootBuildFolder() {
			return rootBuildFolder;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#getModuleBuildFolder()
		 */
		@Override
		public File getModuleBuildFolder() {
			return moduleBuildFolder;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#resolveModuleBuildPath(java.lang.String)
		 */
		@Override
		public String resolveModuleBuildPath(final String path) {
			return EcobuildFileUtil.getCanonicalPath(new ModulePath(path).resolve(rootBuildFolder, moduleBuildFolder));
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#getMainBuildFolder()
		 */
		@Override
		public File getMainBuildFolder() {
			return mainBuildFolder;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#resolveMainBuildPath(java.lang.String)
		 */
		@Override
		public String resolveMainBuildPath(final String path) {
			return EcobuildFileUtil.getCanonicalPath(new ModulePath(path).resolve(mainBuildFolder, mainBuildFolder));
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#getModuleProperties()
		 */
		@Override
		public Properties getModuleProperties() {
			return moduleProperties;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#getLogger()
		 */
		@Override
		public AbstractLogger getLogger() {
			return logger;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#buildDependency(name.martingeisse.ecobuild.ModulePath)
		 */
		@Override
		public File buildDependency(final ModulePath modulePath) {
			logger.logTrace("buildDependency: " + modulePath);
			return Builder.this.buildDependency(moduleSourceFolder, moduleBuildFolder, modulePath, false);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#buildDependency(java.lang.String)
		 */
		@Override
		public File buildDependency(final String modulePath) {
			return buildDependency(new ModulePath(modulePath));
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#buildOptionalDependency(name.martingeisse.ecobuild.ModulePath)
		 */
		@Override
		public File buildOptionalDependency(final ModulePath modulePath) {
			logger.logTrace("buildOptionalDependency: " + modulePath);
			return Builder.this.buildDependency(moduleSourceFolder, moduleBuildFolder, modulePath, true);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#buildOptionalDependency(java.lang.String)
		 */
		@Override
		public File buildOptionalDependency(final String modulePath) {
			return buildOptionalDependency(new ModulePath(modulePath));
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecobuild.moduletool.IModuleToolContext#notifyAboutFailure()
		 */
		@Override
		public void notifyAboutFailure() {
			failed = true;
		}

	}

}
