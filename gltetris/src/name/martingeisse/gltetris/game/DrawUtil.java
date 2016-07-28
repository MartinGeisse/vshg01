/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

/**
 * This class contains static utility methods for drawing.
 */
public class DrawUtil {

	/**
	 * @param x the x position of the block
	 * @param y the y position of the block
	 * @param blockIndex the block index (0-63) that indicates the block image to draw
	 */
	public static void drawBlock(int x, int y, final int blockIndex) {

		// preparation
		x *= 16;
		y *= 16;
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		Resources.getBlockPalette().bind();
		glBegin(GL_QUADS);
		double paletteEntrySize = (1.0 / 64.0);
		double textureStartX = (blockIndex * paletteEntrySize);
		double textureEndX = ((blockIndex + 1) * paletteEntrySize);

		// upper left vertex
		glTexCoord2d(textureStartX, 0.0f);
		glVertex2f(x, y);

		// upper right vertex
		glTexCoord2d(textureEndX, 0.0f);
		glVertex2f(x + 16, y);

		// lower right vertex
		glTexCoord2d(textureEndX, 1.0);
		glVertex2f(x + 16, y + 16);

		// lower left vertex
		glTexCoord2d(textureStartX, 1.0);
		glVertex2f(x, y + 16);

		// cleanup
		glEnd();		

	}
	
	/**
	 * Draws the screen using the specified template.
	 * @param template the template to use
	 */
	public static void drawScreen(final byte[] template) {
		for (int i = 0; i < 40; i++) {
			for (int j = 0; j < 30; j++) {
				drawBlock(i, j, template[j * 40 + i]);
			}
		}
	}

	/**
	 * Draws a right-aligned number with customizable padding. The number is right-aligned in
	 * the specified box.
	 * @param x the x coordinate of the left end of the box
	 * @param y the y coordinate of the box
	 * @param value the value of the number to draw
	 * @param totalWidth the width of the box
	 * @param paddingBlockIndex the block index of the block used for padding on the left side
	 */
	public static void drawNumber (int x, int y, int value, int totalWidth, int paddingBlockIndex)
	{
		// initialize the result
		int[] result = new int[totalWidth];
		Arrays.fill(result, paddingBlockIndex);
		
		if (value <= 0) {

			// handle non-positive values
			result[totalWidth - 1] = 43;
			
		} else {

			// convert digits
			int digitsFilled = 0;
			while (value > 0 && digitsFilled < totalWidth) {
				int digitValue = value % 10;
				int digitBlockIndex = digitValue + 43;
				result[totalWidth - 1 - digitsFilled] = digitBlockIndex;
				value /= 10;
				digitsFilled++;
			}
			
		}
		
		// draw the result
		for (int i=0; i<totalWidth; i++) {
			drawBlock(x + i, y, result[i]);
		}
		
	}

	/**
	 * Draws a block corresponding to the specified character. Unrecognized characters are drawn as spaces.
	 * @param x the x position to draw at
	 * @param y the y position to draw at
	 * @param c the character to draw
	 */
	public static void drawCharacter(final int x, final int y, final int c) {

		int code;
		if (c >= 'A' && c <= 'Z') {
			code = c - 'A' + 17;
		} else if (c >= '0' && c <= '9') {
			code = c - '0' + 43;
		} else if (c == ':') {
			code = 53;
		} else if (c == '!') {
			code = 54;
		} else if (c == '-') {
			code = 55;
		} else if (c == '.') {
			code = 56;
		} else {
			code = 0;
		}

		drawBlock(x, y, code);
	}
	
	/**
	 * Draws a string at the specified position.
	 * @param x the x position of the first character
	 * @param y the y position of the string
	 * @param s the string to draw
	 */
	public static void drawString(final int x, final int y, final String s) {
		int i;
		for (i = 0; i < s.length(); i++) {
			drawCharacter(x + i, y, s.charAt(i));
		}
	}

	/**
	 * Draws a centered string at the specified y position.
	 * @param y the y position of the string
	 * @param s the string to draw
	 */
	public static void drawCenteredString(final int y, final String s) {
		drawString((40 - s.length()) / 2, y, s);
	}
	
	/**
	 * Draws a block multiple times in a horizontal row.
	 * @param x the x position of the leftmost block to draw
	 * @param y the y position of the row
	 * @param count the number of blocks to draw (the length of the row)
	 * @param blockIndex the block index to draw with
	 */
	public static void repeatBlockHorizontally(final int x, final int y, final int count, final int blockIndex) {
		for (int i=0; i<count; i++) {
			drawBlock(x + i, y, blockIndex);
		}
	}
	
}
