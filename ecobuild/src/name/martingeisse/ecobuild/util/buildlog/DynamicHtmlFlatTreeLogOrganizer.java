/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;

import name.martingeisse.ecobuild.FatalBuildException;

import org.apache.commons.io.IOUtils;

/**
 * This organizer generates JQuery-enhanced HTML output.
 */
public class DynamicHtmlFlatTreeLogOrganizer extends AbstractFlatTreeLogOrganizer {

	/**
	 * the out
	 */
	private PrintWriter out;
	
	/**
	 * the moduleCounter
	 */
	private int moduleCounter;

	/**
	 * Constructor.
	 */
	public DynamicHtmlFlatTreeLogOrganizer() {
		throw new RuntimeException("this class is not fully functional yet");
	}
	
	/**
	 * Constructor.
	 * @param w the writer to which the HTML is written
	 */
	public void setOutputWriter(Writer w) {
		this.out = new PrintWriter(w, true);
	}

	/**
	 * Constructor.
	 * @param out the writer to which the HTML is written
	 */
	public void setOutputWriter(PrintWriter out) {
		this.out = out;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer#beginTree()
	 */
	@Override
	protected void beginTree() {
		out.println("<html><body><script type=\"text/javascript\">");
		try {
			InputStream jqueryInputStream = DynamicHtmlFlatTreeLogOrganizer.class.getResourceAsStream("jquery-1.7.1.min.js");
			IOUtils.copy(jqueryInputStream, out, "utf-8");
			jqueryInputStream.close();
		} catch (IOException e) {
			throw new FatalBuildException("could not include JQuery code", e);
		}
		out.println("</script>");
		moduleCounter = 0;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer#endTree()
	 */
	@Override
	protected void endTree() {
		out.println("</body></html>");
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer#beginNode(name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer.TreeEntry)
	 */
	@Override
	protected void beginNode(TreeEntry entry) {
		out.println("<h3 id=\"moduleHeader" + moduleCounter + "\">" + entry.getAbsoluteModuleIdentifier() + "</h3>");
		out.println("<div id=\"module" + moduleCounter + "\">");
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer#endNode(name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer.TreeEntry)
	 */
	@Override
	protected void endNode(TreeEntry entry) {
		out.println("</div>");
		out.println("<script type=\"text/javascript\">$('#moduleHeader" + moduleCounter + "').click(function() {$('#module" + moduleCounter + "').slideToggle();});</script>");
		moduleCounter++;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer#handleMessage(name.martingeisse.ecobuild.util.buildlog.LogEntry)
	 */
	@Override
	protected void handleMessage(LogMessageEntry entry) {
		out.println(entry.getMessage() + "<br />");
		if (entry.getException() != null) {
			out.println("<pre>");
			entry.getException().printStackTrace(out);
			out.println("</pre>");
		}
	}
	
}
