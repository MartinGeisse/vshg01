/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.model;

import name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterface;
import name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterfaceSocket;
import name.martingeisse.webeco.GuiMessageHub;
import name.martingeisse.webeco.wicket.CharacterDisplayPatch;

/**
 * Fake implementation of {@link ICharacterDisplayUserInterface}.
 */
public class CharacterDisplayUserInterface implements ICharacterDisplayUserInterface {

	/**
	 * the guiMessageHub
	 */
	private GuiMessageHub guiMessageHub;
	
	/**
	 * Constructor.
	 * @param guiMessageHub the GUI message hub
	 */
	public CharacterDisplayUserInterface(GuiMessageHub guiMessageHub) {
		this.guiMessageHub = guiMessageHub;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterfaceSocket, int, int)
	 */
	@Override
	public void update(ICharacterDisplayUserInterfaceSocket characterDisplayUserInterfaceSocket, int x, int y) {
		int character = characterDisplayUserInterfaceSocket.getCharacter(x, y);
		int attribute = characterDisplayUserInterfaceSocket.getAttribute(x, y);
		CharacterDisplayPatch patch = new CharacterDisplayPatch(x, y, character, attribute);
		guiMessageHub.getCharacterDisplayQueue().add(patch);
	}

}
