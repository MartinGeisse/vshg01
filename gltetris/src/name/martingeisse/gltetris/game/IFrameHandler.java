/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

/**
 * Common interface for the game and the title screen.
 */
public interface IFrameHandler {

	/**
	 * Handles a frame.
	 * @param frameCounter the number of the current frame
	 */
	public void handleFrame(int frameCounter);
	
}
