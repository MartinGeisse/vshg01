/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.canvas;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;

/**
 * This canvas retains its contents and draws it from a block-oriented
 * data source.
 */
public abstract class AbstractBlockContentRetainingCanvas extends ContentRetainingCanvas {

	/**
	 * the blockWidth
	 */
	private int blockWidth;

	/**
	 * the blockHeight
	 */
	private int blockHeight;

	/**
	 * the horizontalBlockCount
	 */
	private int horizontalBlockCount;

	/**
	 * the verticalBlockCount
	 */
	private int verticalBlockCount;

	/**
	 * the delayUpdates
	 */
	private boolean delayUpdates;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param blockWidth the width of a single block
	 * @param blockHeight the height of a single block
	 * @param horizontalBlockCount the number of blocks in a horizontal row
	 * @param verticalBlockCount the number of blocks in a vertical column
	 */
	public AbstractBlockContentRetainingCanvas(Composite parent, int blockWidth, int blockHeight, int horizontalBlockCount, int verticalBlockCount) {
		super(parent, blockWidth * horizontalBlockCount, blockHeight * verticalBlockCount);
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.horizontalBlockCount = horizontalBlockCount;
		this.verticalBlockCount = verticalBlockCount;
		this.delayUpdates = false;
	}

	/**
	 * @return Returns the blockWidth.
	 */
	public int getBlockWidth() {
		return blockWidth;
	}

	/**
	 * @return Returns the blockHeight.
	 */
	public int getBlockHeight() {
		return blockHeight;
	}

	/**
	 * @return Returns the horizontalBlockCount.
	 */
	public int getHorizontalBlockCount() {
		return horizontalBlockCount;
	}

	/**
	 * @return Returns the verticalBlockCount.
	 */
	public int getVerticalBlockCount() {
		return verticalBlockCount;
	}

	/**
	 * @return Returns the delayUpdates.
	 */
	public boolean isDelayUpdates() {
		return delayUpdates;
	}

	/**
	 * Sets the delayUpdates.
	 * @param delayUpdates the new value to set
	 */
	public void setDelayUpdates(boolean delayUpdates) {
		boolean updateNow = this.delayUpdates && !delayUpdates;
		this.delayUpdates = delayUpdates;
		if (updateNow) {
			redraw();
		}
	}

	/**
	 * Causes this canvas to refresh the specified block from the underlying data source
	 * and redraw it.
	 * @param blockIndexX the X index of the block to update
	 * @param blockIndexY the Y index of the block to update
	 */
	public void updateBlock(int blockIndexX, int blockIndexY) {
		if (!delayUpdates) {
			redraw(blockIndexX * blockWidth, blockIndexY * blockHeight, blockWidth, blockHeight, false);
		}
	}
	
	/**
	 * Causes this canvas to refresh all blocks from the underlying data source and
	 * redraw them.
	 */
	public void updateAllBlocks() {
		if (!delayUpdates) {
			redraw();
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.canvas.ContentRetainingCanvas#onBeforePaint()
	 */
	@Override
	protected void onBeforePaint() {
		if (!delayUpdates) {
			ImageData data = getContents().getImageData();
			for (int x = 0; x < horizontalBlockCount; x++) {
				for (int y = 0; y < verticalBlockCount; y++) {
					drawBlock(data, x, y);
				}
			}
			getContents().dispose();
			setContents(new Image(getDisplay(), data));
		}
	}
	
	/**
	 * This method must be implemented by subclassed to draw a block in the contents.
	 * The GC passed to this method draws to the content image of the retaining canvas. 
	 * @param gc the GC to draw to
	 * @param blockIndexX the X index of the block to draw
	 * @param blockIndexY the Y index of the block to draw
	 */
	protected abstract void drawBlock(ImageData data, int blockIndexX, int blockIndexY);

}
