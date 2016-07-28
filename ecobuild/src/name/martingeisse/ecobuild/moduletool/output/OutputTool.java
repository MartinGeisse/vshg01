/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.EmptyStackException;
import java.util.Stack;

import name.martingeisse.ecobuild.Constants;
import name.martingeisse.ecobuild.ModulePath;
import name.martingeisse.ecobuild.UserMessageBuildException;
import name.martingeisse.ecobuild.moduletool.IModuleTool;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;

import org.apache.commons.io.FileUtils;

/**
 * This module tool is used to generate the ultimate output of the
 * build process. It generates files and folders within the main
 * build folder, but next to the module build folders.
 * 
 * This tool is controlled by a file called build.txt that is
 * expected in the module source folder, next to the
 * module.properties files.
 * 
 * The main purpose of this tool is to take files built by other
 * tools and copy them together in user-friendly output folders.
 * 
 * The "folder" command sets the output folder into which files
 * get copied, relative to its previous value. The starting value
 * is the main build folder, which also acts as the root folder
 * for this purpose.
 * 
 * Output folders are actually stored on a stack, and the "folder"
 * command pushes onto this stack. The "endfolder" command restores
 * the previous output folder. Arbitrarily deep nesting is possible.
 * 
 * The "file" command copies a file. Its first argument is the
 * file to copy, and is resolved using the build module root as the root
 * and this module's folder as the current folder. The second
 * argument is optional and can be used to specify a name for the
 * file (the default is the original name). The second argument
 * is actually a full path, and is resolved with the main build
 * folder as the root and the current output folder as the current
 * folder.
 * 
 * Output folders are created as needed for the "folder" command,
 * but not for the "file" command, so actually using it for
 * anything but simple filenames is mostly useless.
 */
public class OutputTool implements IModuleTool {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.IModuleTool#execute(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	public void execute(IModuleToolContext context) throws IOException {
		FileReader buildScriptReader;
		try {
			buildScriptReader = new FileReader(new File(context.getModuleSourceFolder(), "build.txt"));
		} catch (FileNotFoundException e) {
			throw new UserMessageBuildException("Output build script 'build.txt' not found for module " + context.getModuleSourceFolder());
		}
		LineNumberReader lineReader = new LineNumberReader(buildScriptReader);
		State state = new State();
		state.currentFolder = context.getMainBuildFolder();
		state.folderStack = new Stack<File>();
		while (true) {
			String line = lineReader.readLine();
			if (line == null) {
				break;
			}
			handleLine(context, state, line);
		}
		buildScriptReader.close();
	}

	/**
	 * Handles a line from the build script.
	 * @param context the module tool context
	 * @param line the script line to handle
	 * @throws IOException on I/O errors
	 */
	protected void handleLine(IModuleToolContext context, State state, String line) throws IOException {
		line = line.trim();
		if (line.isEmpty() || line.charAt(0) == '#') {
			return;
		}
		String[] segments = line.split("\\s+");
		handleLine(context, state, segments);
	}

	/**
	 * Handles a line from the build script. This method acts as a switch between the different command
	 * implementations.
	 * 
	 * @param context the module tool context
	 * @param line the script line to handle
	 * @throws IOException on I/O errors
	 */
	protected void handleLine(IModuleToolContext context, State state, String[] segments) throws IOException {
		if (segments[0].equals("folder")) {
			handleFolder(context, state, segments);
		} else if (segments[0].equals("endfolder")) {
			handleEndfolder(context, state, segments);
		} else if (segments[0].equals("file")) {
			handleFile(context, state, segments, false, "");
		} else if (segments[0].equals("srcfile")) {
			handleFile(context, state, segments, true, "");
		} else if (segments[0].equals("exefile")) {
			handleFile(context, state, segments, false, Constants.EXE_SUFFIX);
		} else {
			context.getLogger().logError("unknown output build script command: " + segments[0]);
		}
	}

	// handling for the "folder" command
	protected void handleFolder(IModuleToolContext context, State state, String[] segments) throws IOException {
		
		// basic command syntax check
		if (segments.length != 2) {
			context.getLogger().logError("Found 'folder' command with " + segments.length + " arguments");
			return;
		}
		
		// parse the module path of the folder
		ModulePath modulePath;
		try {
			modulePath = new ModulePath(segments[1]);
		} catch (UserMessageBuildException e) {
			context.getLogger().logError("Found 'folder' command with malformed folder path. " + e.getMessage());
			return;
		}
		
		// resolve the module path to a folder path
		File folderPath;
		try {
			folderPath = modulePath.resolve(context.getRootBuildFolder(), state.currentFolder);
		} catch (UserMessageBuildException e) {
			context.getLogger().logError("Found 'folder' command with unresolvable folder path. " + e.getMessage());
			return;
		}
		
		// create the folder and any required ancestor folders
		FileUtils.forceMkdir(folderPath);
		
		// make this the current folder, remembering the previous one
		state.folderStack.push(state.currentFolder);
		state.currentFolder = folderPath;
		
	}
	
	// handling for the "endfolder" command
	protected void handleEndfolder(IModuleToolContext context, State state, String[] segments) throws IOException {
		
		// basic command syntax check
		if (segments.length != 1) {
			context.getLogger().logError("Found 'endfolder' command with " + segments.length + " arguments");
			return;
		}
		
		// restore the previous folder
		try {
			state.currentFolder = state.folderStack.pop();
		} catch (EmptyStackException e) {
			context.getLogger().logError("Found 'endfolder' command without a matching 'folder' command");
			return;
		}
		
	}

	// handling for the "file" command
	protected void handleFile(IModuleToolContext context, State state, String[] segments, boolean takeFromSourceTree, String autoSuffix) throws IOException {

		// basic command syntax check
		if (segments.length != 2 && segments.length != 3) {
			context.getLogger().logError("Found 'file' command with " + segments.length + " arguments");
			return;
		}

		// parse the copy-source module path
		final ModulePath copySourceModulePath;
		try {
			copySourceModulePath = new ModulePath(segments[1] + autoSuffix);
		} catch (UserMessageBuildException e) {
			context.getLogger().logError("Found 'file' command with malformed copy-source path. " + e.getMessage());
			return;
		}
		
		// build the module from which the file shall be copied
		if (!takeFromSourceTree) {
			context.buildDependency(copySourceModulePath.getParent());
		}

		// parse the copy-destination module path
		final ModulePath copyDestinationModulePath;
		if (segments.length == 2) {
			copyDestinationModulePath = null;
		} else {
			try {
				copyDestinationModulePath = new ModulePath(segments[2] + autoSuffix);
			} catch (UserMessageBuildException e) {
				context.getLogger().logError("Found 'file' command with malformed copy-destination path. " + e.getMessage());
				return;
			}
		}
		
		// resolve the copy-source path
		File copySourceFilePath;
		try {
			if (takeFromSourceTree) {
				copySourceFilePath = copySourceModulePath.resolve(context.getRootSourceFolder(), context.getModuleSourceFolder());
			} else {
				copySourceFilePath = copySourceModulePath.resolve(context.getRootBuildFolder(), context.getModuleBuildFolder());
			}
		} catch (UserMessageBuildException e) {
			context.getLogger().logError("Found 'file' command with unresolvable copy-source path. " + e.getMessage());
			return;
		}

		// resolve the copy-destination path
		File copyDestinationFilePath;
		if (copyDestinationModulePath != null) {
			try {
				copyDestinationFilePath = copyDestinationModulePath.resolve(context.getMainBuildFolder(), state.currentFolder);
			} catch (UserMessageBuildException e) {
				context.getLogger().logError("Found 'file' command with unresolvable copy-destination path. " + e.getMessage());
				return;
			}
		} else {
			copyDestinationFilePath = new File(state.currentFolder, copySourceFilePath.getName());
		}
		
		// copy the file
		try {
			FileUtils.copyFile(copySourceFilePath, copyDestinationFilePath);
			if (copySourceFilePath.canExecute()) {
				copyDestinationFilePath.setExecutable(true);
			}
		} catch (FileNotFoundException e) {
			context.getLogger().logError("Copy failed for 'file' command: " + e.getMessage());
			return;
		} catch (IOException e) {
			context.getLogger().logError("Copy failed for 'file' command. ", e);
			return;
		}

	}
	
	/**
	 * An instance of this class is passed around between command handler methods.
	 */
	protected static class State {
		protected File currentFolder;
		protected Stack<File> folderStack;
	}
	
}
