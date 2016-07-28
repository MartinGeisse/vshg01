/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.wicket;

import java.util.Queue;

import name.martingeisse.terra.javascript.JavascriptAssemblerUtil;
import name.martingeisse.terra.wicket.util.WicketHeadUtil;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextArea;

/**
 * This class feeds text from a queue to a text area on AJAX requests. This can be
 * conbined, for example, with an {@link AbstractAjaxTimerBehavior} to have a stream
 * of text arrive at the client without user interaction.
 */
public class QueueTextFeeder extends TextArea<String> implements IHeaderContributor {

	/**
	 * the queue
	 */
	private final Queue<String> queue;

	/**
	 * Constructor.
	 * @param id the wicket id
	 * @param queue the queue from which text is fetched
	 */
	public QueueTextFeeder(final String id, final Queue<String> queue) {
		super(id);
		setOutputMarkupId(true);
		this.queue = queue;
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.markup.html.IHeaderContributor#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(final IHeaderResponse response) {
		WicketHeadUtil.includeClassJavascript(response, QueueTextFeeder.class);
	}

	/**
	 * Takes as much text from the queue as possible and feeds it to the client.
	 * @param ajaxRequestTarget the AJAX request target
	 */
	public void feed(final AjaxRequestTarget ajaxRequestTarget) {
		ajaxRequestTarget.appendJavaScript("updateQueueTextFeeder('" + getMarkupId() + "', " + JavascriptAssemblerUtil.formatStringLiteral(fetchText()) + ");");
	}

	/**
	 * Fetches as much text as possible from the queue.
	 * @return the fetched text
	 */
	private String fetchText() {
		final StringBuilder builder = new StringBuilder();
		while (true) {
			final String part = queue.poll();
			if (part != null) {
				builder.append(part);
			} else {
				break;
			}
		}
		return builder.toString();
	}

}
