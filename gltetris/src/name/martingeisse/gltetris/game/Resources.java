/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import java.io.IOException;

import name.martingeisse.meltdown.engine.Engine;

import org.newdawn.slick.opengl.Texture;

/**
 * TODO: document me
 *
 */
public class Resources {

	/**
	 * the blockPalette
	 */
	private static Texture blockPalette;

	/**
	 * @param engine the engine
	 * @throws IOException on I/O errors
	 */
	public static void load(final Engine engine) throws IOException {
		blockPalette = engine.loadPngTexture("BlockPalette.png");
	}

	/**
	 * Getter method for the blockPalette.
	 * @return the blockPalette
	 */
	public static Texture getBlockPalette() {
		return blockPalette;
	}

}
