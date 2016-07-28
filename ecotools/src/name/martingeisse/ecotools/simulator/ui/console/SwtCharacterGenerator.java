/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.console;

import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterGenerator;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * This class stores SWT-related resources for the character generator.
 * 
 * Internally, two images are used, one for the foreground colors and
 * one for the background colors. Each image contains character sub-images
 * just as they would appear on screen, in a matrix of 256x16 characters.
 * The x dimension encodes the character code. The y dimension encodes the
 * color. 
 */
public class SwtCharacterGenerator {

	/**
	 * Constructor
	 * @param display the display for which this character generator is used.
	 * This must be the same display as used for the Colors class.
	 */
	public SwtCharacterGenerator(Display display) {
	}

	/**
	 * Disposes of the image resources stored in this class.
	 */
	public void dispose() {
	}

	/**
	 * Draws a block to the specified image data.
	 * @param data the image data to draw to
	 * @param x the x coordinate of the upper left corner of the target area
	 * @param y the y coordinate of the upper left corner of the target area
	 * @param characterCode the character code
	 * @param foreground the foreground color index
	 * @param background the background color index
	 */
	public void drawBlock(ImageData data, int x, int y, int characterCode, int foreground, int background) {
		byte[] characterSpecification = CharacterGenerator.CHARACTER_DATA[characterCode];
		for (int i=0; i<16; i++) {
			int rowData = characterSpecification[i];
			for (int j=0; j<8; j++) {
				int pixelData = (rowData & (1 << j));
				int color = (pixelData == 0) ? background : foreground;
				try {
					data.setPixel(x + j, y + i, color);
				} catch (Exception e) {
					System.out.println("exception: " + x + ", " + j + ", " + y + ", " + i + ", " + color);
				}
			}
		}
	}

}
