/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.layout;

/**
 * A vertical layout split.
 */
public class VSplit extends AbstractLayoutSplit {

	/**
	 * Constructor.
	 * @param firstElement the first (upper) element
	 * @param secondElement the second (lower) element
	 * @param splitPosition the y position where the split occurs
	 */
	public VSplit(final ILayoutTreeElement firstElement, final ILayoutTreeElement secondElement, final int splitPosition) {
		super(firstElement, secondElement, splitPosition);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecogui.prototype.layout.ILayoutTreeElement#getPixel(int, int)
	 */
	@Override
	public int getPixel(int x, int y) {
		int splitPosition = getSplitPosition();
		return (y < splitPosition ? getFirstElement().getPixel(x, y) : getSecondElement().getPixel(x, y - splitPosition));
	}

}
