/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.layout;

/**
 * A horizontal layout split.
 */
public class HSplit extends AbstractLayoutSplit {

	/**
	 * Constructor.
	 * @param firstElement the first (left) element
	 * @param secondElement the second (right) element
	 * @param splitPosition the x position where the split occurs
	 */
	public HSplit(final ILayoutTreeElement firstElement, final ILayoutTreeElement secondElement, final int splitPosition) {
		super(firstElement, secondElement, splitPosition);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecogui.prototype.layout.ILayoutTreeElement#getPixel(int, int)
	 */
	@Override
	public int getPixel(int x, int y) {
		int splitPosition = getSplitPosition();
		return (x < splitPosition ? getFirstElement().getPixel(x, y) : getSecondElement().getPixel(x - splitPosition, y));
	}

}
