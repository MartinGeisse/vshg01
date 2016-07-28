/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.swtlib.canvas;

import org.eclipse.swt.widgets.Composite;
import org.lwjgl.opengl.GL11;

/**
 * This class keeps an array of block values and a block palette. It draws its
 * blocks by drawing the image from the palette that corresponds to the block
 * value of the respective block.
 * 
 * If an invalid value is stored in a block, block 0 is drawn instead.
 */
public class OpenGlImageBlockCanvas extends AbstractOpenGlBlockCanvas {

	/**
	 * the blocks
	 */
	private final int[] blocks;

	/**
	 * the palette
	 */
	private OpenGlImageBlockPalette palette;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param blockWidth the width of a single block
	 * @param blockHeight the height of a single block
	 * @param horizontalBlockCount the number of blocks in a horizontal row
	 * @param verticalBlockCount the number of blocks in a vertical column
	 * @param palette the block palette
	 */
	public OpenGlImageBlockCanvas(final Composite parent, final int blockWidth, final int blockHeight, final int horizontalBlockCount, final int verticalBlockCount, final OpenGlImageBlockPalette palette) {
		super(parent, blockWidth, blockHeight, horizontalBlockCount, verticalBlockCount);
		this.blocks = new int[horizontalBlockCount * verticalBlockCount];
		this.palette = palette;
	}

	/**
	 * Getter method for the palette.
	 * @return the palette
	 */
	public OpenGlImageBlockPalette getPalette() {
		return palette;
	}

	/**
	 * Setter method for the palette.
	 * @param palette the palette to set
	 */
	public void setPalette(final OpenGlImageBlockPalette palette) {
		this.palette = palette;
	}

	/**
	 * Returns the index of the specified block, throwing an {@link IndexOutOfBoundsException}
	 * if the position is invalid.
	 * @param x the x position
	 * @param y the y position
	 * @return the block index
	 */
	private int getBlockIndex(final int x, final int y) {
		if (x < 0 || x >= getHorizontalBlockCount()) {
			throw new IndexOutOfBoundsException("invalid x value");
		}
		if (y < 0 || y >= getVerticalBlockCount()) {
			throw new IndexOutOfBoundsException("invalid y value");
		}
		return y * getHorizontalBlockCount() + x;
	}

	/**
	 * Returns the value of a block.
	 * @param x the x position
	 * @param y the y position
	 * @return the block value
	 */
	public int getBlock(final int x, final int y) {
		return blocks[getBlockIndex(x, y)];
	}

	/**
	 * Sets the value of a block.
	 * @param x the x position
	 * @param y the y position
	 * @param value the value to set
	 */
	public void setBlock(final int x, final int y, final int value) {
		blocks[getBlockIndex(x, y)] = value;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.canvas.AbstractOpenGlBlockCanvas#onBeforeDraw()
	 */
	@Override
	protected void onBeforeDraw() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.canvas.AbstractOpenGlBlockCanvas#drawBlock(int, int)
	 */
	@Override
	protected void drawBlock(final int x, final int y) {
		palette.safeBind(getBlock(x, y));
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		// glBegin/glEnd must be called per block because of changing textures
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex2i(x, y);
		GL11.glTexCoord2f(1.0f, 0.0f);
		GL11.glVertex2i(x + 1, y);
		GL11.glTexCoord2f(1.0f, 1.0f);
		GL11.glVertex2i(x + 1, y + 1);
		GL11.glTexCoord2f(0.0f, 1.0f);
		GL11.glVertex2i(x, y + 1);
		GL11.glEnd();
		
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.canvas.AbstractOpenGlBlockCanvas#onAfterDraw()
	 */
	@Override
	protected void onAfterDraw() {
	}

}
