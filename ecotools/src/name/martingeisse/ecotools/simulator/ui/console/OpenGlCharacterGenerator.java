/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.console;

import java.nio.ByteBuffer;

import name.martingeisse.ecotools.common.util.BitUtil;
import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterGenerator;
import name.martingeisse.swtlib.color.OpenGlColors;

import org.lwjgl.opengl.GL11;

/**
 * This class stores OpenGL-related resources for the character generator.
 * 
 * This class uses one big internal NIO buffer. Vertically arranged in this
 * buffer are characters, then pattern / inverted pattern, then bit rows.
 * The inverted pattern is needed to draw the background. Each bit row uses
 * one pattern byte followed by three padding byte (I don't know yet how
 * to make OpenGL use a single byte per row).
 */
public class OpenGlCharacterGenerator {

	/**
	 * the NIO buffer
	 */
	private ByteBuffer buffer;

	/**
	 * Constructor
	 */
	public OpenGlCharacterGenerator() {
		this.buffer = ByteBuffer.allocateDirect(256 * 2 * 16 * 4);
		
		/** loop over all characters **/
		for (int characterCode = 0; characterCode < 256; characterCode++) {
			byte[] characterSpecification = CharacterGenerator.CHARACTER_DATA[characterCode];

			/** layer 0 is foreground, layer 1 is background (inverted pixels) **/
			for (int layer = 0; layer < 2; layer++) {
				
				/** 16 pixel rows **/
				for (int row = 0; row < 16; row++) {
					
					/** write pixels, then three padding bytes **/
					byte foregroundPixels = characterSpecification[15 - row];
					byte layerPixels = (byte)((layer == 0) ? foregroundPixels : ~foregroundPixels);
					buffer.put((byte)BitUtil.revertByteBitOrder(layerPixels));
					buffer.put((byte)0);
					buffer.put((byte)0);
					buffer.put((byte)0);

				}
				
			}
		}
	}

	/**
	 * Draws a block using the current OpenGL context and the specified raster position.
	 * Implementation issues prevent to just re-use the current raster position.
	 * 
	 * @param x the x raster position to set
	 * @param y the y raster position to set
	 * @param characterCode the character code
	 * @param foreground the foreground color index
	 * @param background the background color index
	 */
	public void drawBlock(float x, float y, int characterCode, int foreground, int background) {
		
		/**
		 * Implementation note: glColor3f(...) only sets the color in a "thin" way -
		 * it does not push the new color down the rendering pipeline. Drawing a normal
		 * primitive usually then takes the new color with it. Unfortunately, glBitmap(...)
		 * is NOT such a primitive, thus the new color would never take effect as long
		 * as only bitmaps are drawn. However, glRasterPos2f(...) also takes the new color
		 * with it, so we explicitly set the raster position in this method just to
		 * set the color.
		 */
		OpenGlColors.useColor(background);
		GL11.glRasterPos2f(x, y);
		buffer.position(characterCode * 2 * 16 * 4 + 16 * 4);
		GL11.glBitmap(8, 16, 0.0f, 0.0f, 0.0f, 0.0f, buffer);
		
		OpenGlColors.useColor(foreground);
		GL11.glRasterPos2f(x, y);
		buffer.position(characterCode * 2 * 16 * 4);
		GL11.glBitmap(8, 16, 0.0f, 0.0f, 0.0f, 0.0f, buffer);
		
	}

}
