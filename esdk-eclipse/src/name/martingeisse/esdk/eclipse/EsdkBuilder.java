/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import name.martingeisse.esdk.picoblaze.PsmBinUtil;
import name.martingeisse.esdk.picoblaze.PsmVerilogUtil;
import name.martingeisse.esdk.picoblaze.assembler.IPicoblazeAssemblerErrorHandler;
import name.martingeisse.esdk.picoblaze.assembler.Range;
import name.martingeisse.esdk.picoblaze.assembler.ast.AstBuilder;
import name.martingeisse.esdk.picoblaze.assembler.ast.Context;
import name.martingeisse.esdk.picoblaze.assembler.ast.PsmFile;
import name.martingeisse.esdk.util.VariableObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * This is the central hub that distributes build events to 
 * the ESDK sub-modules.
 */
public class EsdkBuilder extends IncrementalProjectBuilder {

	/**
	 * The fully qualified id of the builder extension.
	 */
	public static final String BUILDER_ID = "name.martingeisse.esdk.EsdkBuilder";

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {

		// TODO: "cleaning" a project

		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else {
			final IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	/**
	 * Performs a full build.
	 * @param monitor
	 * @throws CoreException
	 */
	private void fullBuild(final IProgressMonitor monitor) throws CoreException {
		getProject().accept(new IResourceVisitor() {
			@Override
			public boolean visit(final IResource resource) throws CoreException {
				return handleResource(resource, monitor);
			}
		});
	}

	/**
	 * Performs an incremental build.
	 * @param monitor
	 * @throws CoreException
	 */
	private void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
		delta.accept(new IResourceDeltaVisitor() {
			@Override
			public boolean visit(final IResourceDelta delta) throws CoreException {
				return handleResource(delta.getResource(), monitor);
			}
		});
	}

	/**
	 * @param resource
	 * @param monitor
	 * @return
	 */
	private boolean handleResource(final IResource resource, final IProgressMonitor monitor) {
		if (resource instanceof IFile && resource.getName().toLowerCase().endsWith(".psm")) {
			handlePsmFile((IFile)resource, monitor);
		}
		return true;
	}

	private void handlePsmFile(final IFile inputFile, final IProgressMonitor monitor) {

		final String inputFilename = inputFile.getName();
		final String outputFilename = inputFilename.substring(0, inputFilename.length() - 4) + ".psmbin";
		final IFile outputFile = inputFile.getParent().getFile(new Path(outputFilename));
		Activator.getDefault().logInfo("* builder trying to compile " + inputFilename + " to " + outputFilename);

		handleInput: if (inputFile.exists()) {

			// remove old markers
			try {
				inputFile.deleteMarkers(EsdkMarkers.PROBLEM, true, IResource.DEPTH_INFINITE);
			} catch (final CoreException e) {
				fatalCompileError(inputFile, "could delete old ESDK problem markers from " + inputFile.getFullPath(), e);
				break handleInput;
			}

			// compile
			try {
				compilePsmFile(inputFile, outputFile, monitor);
				return;
			} catch (final CoreException e) {
				fatalCompileError(inputFile, "could not compile " + inputFile.getFullPath(), e);
				break handleInput;
			} catch (final IOException e) {
				fatalCompileError(inputFile, "could not compile " + inputFile.getFullPath(), e);
				break handleInput;
			}

		}

		/* Either no input exists, or handling the input failed. In either case we remove the
		 * output file.
		 */
		if (outputFile.exists()) {
			try {
				outputFile.delete(false, null);
			} catch (final CoreException e) {
				// bad luck, I guess
				fatalCompileError(null, "could not delete " + inputFile.getFullPath(), e);
			}
		}

	}

	private void fatalCompileError(final IFile inputFile, final String message, final Throwable exception) {

		// add a user-visible marker
		if (inputFile != null) {
			try {
				final IMarker marker = inputFile.createMarker(EsdkMarkers.PROBLEM);
				marker.setAttribute(IMarker.MESSAGE, message);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.USER_EDITABLE, false);

			} catch (final Exception e) {
				Activator.getDefault().logWarning("could not add fatal problem marker to file: " + inputFile.getFullPath(), e);
			}
		}

		// a fatal error in the compiler is not fatal for the plugin, hence the INFO log level
		Activator.getDefault().logInfo(message, exception);

	}

	private void compilePsmFile(final IFile inputFile, final IFile outputFile, final IProgressMonitor monitor) throws CoreException, IOException {

		final VariableObject<Boolean> generateVerilog = new VariableObject<Boolean>(false);
		final MyErrorHandler errorHandler = new MyErrorHandler(inputFile);
		InputStream sourceFileInputStream = null;
		final AstBuilder builder = new AstBuilder() {
			@Override
			public void pragma(final Range fullRange, final String identifier, final String parameter) {
				if (identifier.equals("verilog")) {
					generateVerilog.value = true;
				} else {
					errorHandler.handleWarning(fullRange, "unknown pragma identifier: " + identifier);
				}
			}
		};
		try {
			sourceFileInputStream = inputFile.getContents();
			final InputStreamReader sourceFileReader = new InputStreamReader(sourceFileInputStream);
			builder.parse(sourceFileReader, errorHandler);
		} finally {
			if (sourceFileInputStream != null) {
				sourceFileInputStream.close();
			}
		}

		final PsmFile psmFile = builder.getResult();
		final Context psmContext = new Context(errorHandler);
		psmFile.collectConstantsAndLabels(psmContext);
		final int[] instructions = psmFile.encode(psmContext, errorHandler);
		final byte[] psmData = PsmBinUtil.encodePsmBin(instructions);

		if (outputFile.exists()) {
			outputFile.setContents(new ByteArrayInputStream(psmData), false, true, monitor);
		} else {
			outputFile.create(new ByteArrayInputStream(psmData), false, monitor);
		}

		String verilogCode = PsmVerilogUtil.generateBundleVerilog(instructions);
		byte[] verilogCodeBytes = verilogCode.getBytes(Charset.forName("utf-8"));
		String outputFileName = outputFile.getName();
		String verilogFileName = outputFileName.substring(0, outputFileName.length() - (".psmbin".length())) + ".v";
		IFile verilogFile = outputFile.getParent().getFile(new Path(verilogFileName));
		if (generateVerilog.value) {
			if (verilogFile.exists()) {
				verilogFile.setContents(new ByteArrayInputStream(verilogCodeBytes), false, true, monitor);
			} else {
				verilogFile.create(new ByteArrayInputStream(verilogCodeBytes), false, monitor);
			}
		} else if (verilogFile.exists()) {
			verilogFile.delete(false, monitor);
		}
		
	}
	
	private static class MyErrorHandler implements IPicoblazeAssemblerErrorHandler {

		/**
		 * the file
		 */
		private final IFile file;

		/**
		 * Constructor.
		 * @param file the file to add markers to
		 */
		public MyErrorHandler(final IFile file) {
			this.file = file;
		}

		private void handle(final Range range, final String message, final int severity) {
			try {
				final IMarker marker = file.createMarker(EsdkMarkers.PROBLEM);
				marker.setAttribute(IMarker.LOCATION, "line " + (range.getStartLine() + 1));
				marker.setAttribute(IMarker.MESSAGE, message);
				marker.setAttribute(IMarker.SEVERITY, severity);
				marker.setAttribute(IMarker.USER_EDITABLE, false);
				marker.setAttribute(IMarker.LINE_NUMBER, range.getStartLine() + 1);
				marker.setAttribute(IMarker.CHAR_START, range.getStartOffset());
				marker.setAttribute(IMarker.CHAR_END, range.getEndOffset());

			} catch (final CoreException e) {
				Activator.getDefault().logWarning("could not add compiler problem marker to file: " + file.getFullPath(), e);
			}
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.esdk.picoblaze.assembler.IPicoblazeAssemblerErrorHandler#handleError(name.martingeisse.esdk.picoblaze.assembler.Range, java.lang.String)
		 */
		@Override
		public void handleError(final Range range, final String message) {
			handle(range, message, IMarker.SEVERITY_ERROR);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.esdk.picoblaze.assembler.IPicoblazeAssemblerErrorHandler#handleWarning(name.martingeisse.esdk.picoblaze.assembler.Range, java.lang.String)
		 */
		@Override
		public void handleWarning(final Range range, final String message) {
			handle(range, message, IMarker.SEVERITY_WARNING);
		}

	}

}
