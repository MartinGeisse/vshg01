/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.layout;

/**
 * Base class for horizontal and vertical layout splits.
 */
public abstract class AbstractLayoutSplit implements ILayoutTreeElement {

	/**
	 * the firstElement
	 */
	private ILayoutTreeElement firstElement;

	/**
	 * the secondElement
	 */
	private ILayoutTreeElement secondElement;

	/**
	 * the splitPosition
	 */
	private int splitPosition;

	/**
	 * Constructor.
	 * @param firstElement the first (left or upper) element
	 * @param secondElement the second (right or lower) element
	 * @param splitPosition the (x or y) position where the split occurs
	 */
	public AbstractLayoutSplit(final ILayoutTreeElement firstElement, final ILayoutTreeElement secondElement, final int splitPosition) {
		this.firstElement = firstElement;
		this.secondElement = secondElement;
		this.splitPosition = splitPosition;
	}

	/**
	 * Getter method for the firstElement.
	 * @return the firstElement
	 */
	public ILayoutTreeElement getFirstElement() {
		return firstElement;
	}

	/**
	 * Setter method for the firstElement.
	 * @param firstElement the firstElement to set
	 */
	public void setFirstElement(final ILayoutTreeElement firstElement) {
		this.firstElement = firstElement;
	}

	/**
	 * Getter method for the secondElement.
	 * @return the secondElement
	 */
	public ILayoutTreeElement getSecondElement() {
		return secondElement;
	}

	/**
	 * Setter method for the secondElement.
	 * @param secondElement the secondElement to set
	 */
	public void setSecondElement(final ILayoutTreeElement secondElement) {
		this.secondElement = secondElement;
	}

	/**
	 * Getter method for the splitPosition.
	 * @return the splitPosition
	 */
	public int getSplitPosition() {
		return splitPosition;
	}

	/**
	 * Setter method for the splitPosition.
	 * @param splitPosition the splitPosition to set
	 */
	public void setSplitPosition(final int splitPosition) {
		this.splitPosition = splitPosition;
	}

}
