/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild.util.buildlog;

import java.io.PrintWriter;
import java.io.Writer;


/**
 * This organizer generates HTML output.
 * 
 * A module node is shown whenever either
 * (1) its build tool failed, or
 * (2) its build log contains a message that is not of
 *     "context" or "trace" level (i.e. module nodes with "unknown"-level
 *     messages are shown).
 */
public class HtmlFlatTreeLogOrganizer extends AbstractFlatTreeLogOrganizer {

	/**
	 * the out
	 */
	private PrintWriter out;
	
	/**
	 * the showCurrentNode
	 */
	private boolean showCurrentNode;

	/**
	 * Constructor.
	 */
	public HtmlFlatTreeLogOrganizer() {
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
		out.println("<html><body>");
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
		checkShowCurrentNode(entry);
		if (showCurrentNode) {
			out.println("<h3>" + entry.getAbsoluteModuleIdentifier() + "</h3>");
		}
	}
	
	private void checkShowCurrentNode(TreeEntry entry) {
		if (!entry.isSuccess()) {
			showCurrentNode = true;
			return;
		}
		for (LogMessageEntry message : entry.getMessageEntries()) {
			LogLevel level = message.getLogLevel();
			if (level != LogLevel.CONTEXT && level != LogLevel.TRACE) {
				showCurrentNode = true;
				return;
			}
		}
		showCurrentNode = false;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer#endNode(name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer.TreeEntry)
	 */
	@Override
	protected void endNode(TreeEntry entry) {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecobuild.util.AbstractFlatTreeLogOrganizer#handleMessage(name.martingeisse.ecobuild.util.buildlog.LogEntry)
	 */
	@Override
	protected void handleMessage(LogMessageEntry entry) {
		if (!showCurrentNode) {
			return;
		}
		
		String color;
		switch (entry.getLogLevel()) {
		
		case WARNING:
			color = "808000";
			break;
			
		case ERROR:
			color = "c00000";
			break;
			
		case UNKNOWN:
			color = "000080";
			break;
			
		default:
			color = null;
			break;
		}
		
		if (color != null) {
			out.println("<div style=\"color: #" + color + "\">");
		}
		out.println(entry.getMessage() + "<br />");
		if (entry.getException() != null) {
			out.println("<pre>");
			entry.getException().printStackTrace(out);
			out.println("</pre>");
		}
		if (color != null) {
			out.println("</div>");
		}
	}
	
}
