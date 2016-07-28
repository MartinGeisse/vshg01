/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

/**
 * This class wraps a Slick-Util {@link Texture} object and provides the
 * mechanics of an image that is intended to be drawn as it exists in
 * the file, and not as a texture on a 3d shape.
 * 
 * Especially, this class can deal with the automatic resampling of images
 * to power-of-2 sizes that is done by Slick-Util.
 */
public class Image implements IDrawable {

	/**
	 * the texture
	 */
	private final Texture texture;

	/**
	 * Constructor.
	 * @param texture the texture to wrap
	 */
	public Image(final Texture texture) {
		this.texture = texture;
	}

	/**
	 * Draws this image to the specified screen position.
	 * @param x the x position to draw to
	 * @param y the y position to draw to
	 * @param horizontalAlignment the horizonal alignment
	 * @param verticalAlignment the horizonal alignment
	 */
	public void draw(final int x, final int y, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
		draw(horizontalAlignment.getTranslatedX(x, texture.getImageWidth()), verticalAlignment.getTranslatedY(y, texture.getImageHeight()));
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.meltdown.engine.IDrawable#draw(int, int, int)
	 */
	@Override
	public void draw(int x, int y, int startState) {
		draw(x, y);
	}

	/**
	 * Draws this image to the specified screen position.
	 * @param x the x position to draw to
	 * @param y the y position to draw to
	 */
	public void draw(final int x, final int y) {

		// preparation
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		glBegin(GL_QUADS);

		// upper left vertex
		glTexCoord2f(0.0f, 0.0f);
		glVertex2f(x, y);

		// upper right vertex
		glTexCoord2f(texture.getWidth(), 0.0f);
		glVertex2f(x + texture.getImageWidth(), y);

		// lower right vertex
		glTexCoord2f(texture.getWidth(), texture.getHeight());
		glVertex2f(x + texture.getImageWidth(), y + texture.getImageHeight());

		// lower left vertex
		glTexCoord2f(0.0f, texture.getHeight());
		glVertex2f(x, y + texture.getImageHeight());

		// cleanup
		glEnd();
		ShaderProgram.useNone();

	}

	/**
	 * Getter method for the texture.
	 * @return the texture
	 */
	public Texture getTexture() {
		return texture;
	}

}
