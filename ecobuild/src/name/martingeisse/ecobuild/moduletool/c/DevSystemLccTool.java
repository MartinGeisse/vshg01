/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.moduletool.c;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import name.martingeisse.ecobuild.Constants;
import name.martingeisse.ecobuild.moduletool.AbstractLeafModuleTool;
import name.martingeisse.ecobuild.moduletool.IModuleToolContext;
import name.martingeisse.ecobuild.util.CommandLineToolInvocation;
import name.martingeisse.ecobuild.util.EcobuildFileUtil;
import name.martingeisse.ecobuild.util.buildlog.AbstractLogger;

/**
 * The tool used to build the LCC application.
 */
public class DevSystemLccTool extends AbstractLeafModuleTool {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#determineExpectedOutputFiles()
	 */
	@Override
	protected List<File> determineExpectedOutputFiles(IModuleToolContext context) {
		File buildFolder = context.getModuleBuildFolder();
		ArrayList<File> result = new ArrayList<File>();
		result.add(new File(buildFolder, "bprint"));
		result.add(new File(buildFolder, "lcc"));
		result.add(new File(buildFolder, "lburg"));
		result.add(new File(buildFolder, "cpp"));
		result.add(new File(buildFolder, "rcc"));
		return result;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.moduletool.c.AbstractLeafModuleTool#onModuleOutdated(name.martingeisse.ecobuild.moduletool.IModuleToolContext)
	 */
	@Override
	public void onModuleOutdated(IModuleToolContext context) throws IOException {
		
		File moduleSourceFolder = context.getModuleSourceFolder();
		File moduleBuildFolder = context.getModuleBuildFolder();
		
		// we cannot use -D here because double quotes within argument values aren't correctly passed on Windows
		File lccdirDefineHeader = new File(moduleBuildFolder, "lccdir.h");
		FileWriter lccdirDefineHeaderWriter = new FileWriter(lccdirDefineHeader);
		lccdirDefineHeaderWriter.write("#define LCCDIR \"" + context.getMainBuildFolder().getCanonicalPath().replace('\\', '/') + "/devtools/\"\n");
		lccdirDefineHeaderWriter.close();
		
		invokeGcc(context, moduleSourceFolder, "src", "bprint", null, "etc/bprint.c");
		new File(moduleBuildFolder, "bprint").setExecutable(true);
		invokeGcc(context, moduleSourceFolder, moduleBuildFolder.getCanonicalPath(), "lcc", null, "etc/lcc.c etc/eco32-linux.c");
		new File(moduleBuildFolder, "lcc").setExecutable(true);
		invokeGcc(context, moduleSourceFolder, "lburg", "lburg", null, "lburg/lburg.c lburg/gram.c");
		new File(moduleBuildFolder, "lburg").setExecutable(true);
		invokeGcc(context, moduleSourceFolder, "cpp", "cpp", null, "cpp/cpp.c cpp/lex.c cpp/nlist.c cpp/tokens.c cpp/macro.c cpp/eval.c cpp/include.c cpp/hideset.c cpp/getopt.c cpp/unix.c");
		new File(moduleBuildFolder, "cpp").setExecutable(true);
		
		invokeLburg(context, "dagcheck");
		invokeLburg(context, "alpha");
		invokeLburg(context, "mips");
		invokeLburg(context, "eco32");
		invokeLburg(context, "sparc");
		invokeLburg(context, "x86");
		invokeLburg(context, "x86linux");
		
		StringBuilder sourceFilesBuilder = new StringBuilder();
		sourceFilesBuilder.append("alloc.c bind.c dag.c decl.c enode.c error.c expr.c event.c init.c inits.c input.c lex.c list.c main.c output.c prof.c profio.c simp.c stmt.c string.c sym.c trace.c tree.c types.c null.c symbolic.c gen.c bytecode.c stab.c");
		sourceFilesBuilder.append(" $" + EcobuildFileUtil.getCanonicalPath(moduleBuildFolder) + "/dagcheck.c$");
		sourceFilesBuilder.append(" $" + EcobuildFileUtil.getCanonicalPath(moduleBuildFolder) + "/alpha.c$");
		sourceFilesBuilder.append(" $" + EcobuildFileUtil.getCanonicalPath(moduleBuildFolder) + "/mips.c$");
		sourceFilesBuilder.append(" $" + EcobuildFileUtil.getCanonicalPath(moduleBuildFolder) + "/eco32.c$");
		sourceFilesBuilder.append(" $" + EcobuildFileUtil.getCanonicalPath(moduleBuildFolder) + "/sparc.c$");
		sourceFilesBuilder.append(" $" + EcobuildFileUtil.getCanonicalPath(moduleBuildFolder) + "/x86.c$");
		sourceFilesBuilder.append(" $" + EcobuildFileUtil.getCanonicalPath(moduleBuildFolder) + "/x86linux.c$");
		invokeGcc(context, new File(moduleSourceFolder, "src"), ".", "rcc", null, sourceFilesBuilder.toString());
		new File(moduleBuildFolder, "rcc").setExecutable(true);
		
	}

	private void invokeGcc(IModuleToolContext context, File workingDirectory, String includePath, String executableName, String otherOptions, String sourceFileNames) throws IOException {
		File moduleBuildFolder = context.getModuleBuildFolder();
		String includeOption = (includePath == null ? "" : ("$-I" + includePath + "$"));
		otherOptions = (otherOptions == null ? "" : otherOptions); 
		String command = "$" + Constants.GCC_PATH + "$ " + includeOption + " -o $" + moduleBuildFolder.getCanonicalPath() + "/" + executableName + "$ " + otherOptions + " " + sourceFileNames;
		invokeGcc(context, workingDirectory, command);
	}
	
	private void invokeGcc(IModuleToolContext context, File workingDirectory, String command) throws IOException {
		AbstractLogger logger = context.getLogger();
		new GccInvocation(context, workingDirectory, command, logger).invoke();
	}

	private void invokeLburg(IModuleToolContext context, String baseFileName) throws IOException {
		File moduleSourceFolder = context.getModuleSourceFolder();
		File moduleBuildFolder = context.getModuleBuildFolder();
		AbstractLogger logger = context.getLogger();
		
		String command = moduleBuildFolder.getCanonicalPath() + "/lburg" + Constants.EXE_SUFFIX + " $" + moduleSourceFolder.getCanonicalPath() + "/src/" + baseFileName + ".md$ " + baseFileName + ".c";
		new CommandLineToolInvocation(context, moduleBuildFolder, command, logger).invoke();
	}
	
}
