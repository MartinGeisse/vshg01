/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.blockconsole;

import name.martingeisse.ecotools.simulator.devices.blockdisplay.BlockGenerator;
import name.martingeisse.swtlib.color.Colors;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * This class implements SWT-specific functionality to generate block graphics.
 */
public class SwtBlockGenerator {

	/**
	 * the blocks
	 */
	private ImageData blocks;

	/**
	 * Constructor
	 * @param display the display for which this block generator is used
	 */
	public SwtBlockGenerator(Display display) {
		// TODO: there's no need for a Image instance here, just create the ImageData
		Image blocks = new Image(display, 16 * 64, 16);
		GC gc = new GC(blocks);
		for (int i = 0; i < 64; i++) {
			String blockDefinition = BlockGenerator.BLOCK_DATA[i];
			for (int y = 0; y < 16; y++) {
				int texelLine = y >> 1;
				String texelLineDefinition = blockDefinition.substring(64 - texelLine * 8 - 8, 64 - texelLine * 8);
				for (int x = 0; x < 16; x++) {
					int texelColumn = x >> 1;
					char texelDefinition = texelLineDefinition.charAt(7 - texelColumn);
					int texelColorIndex = (texelDefinition >= '0' && texelDefinition <= '9') ? (texelDefinition - '0') : (texelDefinition - 'a' + 10);
					gc.setForeground(Colors.getColor(texelColorIndex));
					gc.drawPoint(i * 16 + x, y);
				}
			}
		}
		gc.dispose();
		this.blocks = blocks.getImageData();
		blocks.dispose();
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
	 * @param block the block index of the block to draw
	 */
	public void drawBlock(ImageData data, int x, int y, int block) {
		int blocksBaseX = (block * 16);
		for (int i=0; i<16; i++) {
			for (int j=0; j<16; j++) {
				data.setPixel(x + j, y + i, blocks.getPixel(blocksBaseX + j, i));
			}
		}
	}

}
