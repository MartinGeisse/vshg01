/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.swtlib.canvas;

import java.io.FileInputStream;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * Manages a palette of images / textures for use with an {@link OpenGlImageBlockCanvas}.
 * 
 * Note that only PNG files are currently supported.
 */
public class OpenGlImageBlockPalette {

	/**
	 * the textures
	 */
	private Texture[] textures;
	
	/**
	 * Constructor.
	 * @param count the number of images
	 * @param filenamePattern the pattern from which image file names are constructed, using
	 * a dollar sign as the placeholder for the image index.
	 * @throws IOException on I/O errors
	 */
	public OpenGlImageBlockPalette(int count, String filenamePattern) throws IOException {
		if (count < 1) {
			throw new IllegalArgumentException("OpenGlImageBlockPalette: count must be at least 1");
		}
		textures = new Texture[count];
		for (int i=0; i<count; i++) {
			String filename = filenamePattern.replace("$", Integer.toString(i));
			textures[i] = TextureLoader.getTexture("PNG", new FileInputStream(filename));
		}
	}
	
	/**
	 * @return the number of images stored in this palette.
	 */
	public int getCount() {
		return textures.length;
	}
	
	/**
	 * Returns the texture with the specified index.
	 * @param index the index
	 * @return the texture
	 */
	public Texture getTexture(int index) {
		return textures[index];
	}
	
	/**
	 * Binds the texture with the specified index, or texture 0 if the index
	 * is invalid.
	 * @param index the index of the texture to bind
	 */
	public void safeBind(int index) {
		if (index < 0 || index >= textures.length) {
			textures[0].bind();
		} else {
			textures[index].bind();
		}
	}

}
