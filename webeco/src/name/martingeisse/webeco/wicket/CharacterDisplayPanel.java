/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.wicket;

import java.util.Queue;

import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterDisplay;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * This panel shows an 80x30 character matrix that is intended to be
 * connected to a {@link CharacterDisplay}.
 */
public class CharacterDisplayPanel extends Panel {

	/**
	 * the queue
	 */
	private final Queue<CharacterDisplayPatch> queue;
	
	/**
	 * Constructor.
	 * @param id the wicket id
	 * @param queue the patch queue
	 */
	public CharacterDisplayPanel(final String id, final Queue<CharacterDisplayPatch> queue) {
		super(id);
		this.queue = queue;
		setOutputMarkupId(true);

		add(new Loop("rows", 30) {
			@Override
			protected void populateItem(final LoopItem rowItem) {
				rowItem.add(new Loop("cells", 80) {
					@Override
					protected void populateItem(final LoopItem cellItem) {
						final int x = cellItem.getIndex();
						final int y = rowItem.getIndex();
						final int c = ((4*y+x) & 15); // socket.getCharacter(x, y);
						final int a = 0x0f; // socket.getAttribute(x, y);
						String address = "/chargen/" + byteToString(a) + byteToString(c);
						
						WebMarkupContainer image = new WebMarkupContainer("image");
						image.setOutputMarkupId(true);
						image.setMarkupId(CharacterDisplayPanel.this.getMarkupId() + "-" + x + "-" + y);
						image.add(new AttributeModifier("src", Model.of(address)));
						cellItem.add(image);
					}
				});
			}
		});
	}

	/**
	 * Feeds patches to the specified request target.
	 * @param ajaxRequestTarget the request target
	 */
	public void feed(AjaxRequestTarget ajaxRequestTarget) {
		while (true) {
			CharacterDisplayPatch patch = queue.poll();
			if (patch == null) {
				break;
			}
			String command = String.format("$('#%s-%s-%s').attr('src', '/chargen/%s%s');",
				getMarkupId(), patch.getX(), patch.getY(), byteToString(patch.getAttribute() & 0xff), byteToString(patch.getCharacter() & 0xff));
			ajaxRequestTarget.appendJavaScript(command);
		}
	}
	
	/**
	 * @param value
	 * @return
	 */
	private static String byteToString(int value) {
		String raw = Integer.toString(value, 16);
		return (value < 16) ? ("0" + raw) : raw;
	}

}
