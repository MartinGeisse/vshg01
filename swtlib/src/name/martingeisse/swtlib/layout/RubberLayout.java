/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * This layout accepts only a single child widget. It will use any
 * dimensions passed as a suggestion during the layout process, and
 * will use its own configurable fallback values only if SWT.DEFAULT
 * is passed. The child will fill the whole client area.
 */
public class RubberLayout extends Layout {

	/**
	 * the defaultWidth
	 */
	private int defaultWidth;

	/**
	 * the defaultHeight
	 */
	private int defaultHeight;

	/**
	 * Constructor
	 */
	public RubberLayout() {
		this(100);
	}

	/**
	 * Constructor
	 * @param defaultSize the default width and height to use if the parent passes SWT.DEFAULT
	 */
	public RubberLayout(int defaultSize) {
		this(defaultSize, defaultSize);
	}

	/**
	 * Constructor
	 * @param defaultWidth the default width to use if the parent passes SWT.DEFAULT
	 * @param defaultHeight the default height to use if the parent passes SWT.DEFAULT
	 */
	public RubberLayout(int defaultWidth, int defaultHeight) {
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
	}

	/**
	 * @return Returns the defaultWidth.
	 */
	public int getDefaultWidth() {
		return defaultWidth;
	}

	/**
	 * Sets the defaultWidth.
	 * @param defaultWidth the new value to set
	 */
	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	/**
	 * @return Returns the defaultHeight.
	 */
	public int getDefaultHeight() {
		return defaultHeight;
	}

	/**
	 * Sets the defaultHeight.
	 * @param defaultHeight the new value to set
	 */
	public void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite, int, int, boolean)
	 */
	@Override
	protected Point computeSize(Composite composite, int widthHint, int heightHint, boolean flushCache) {
		int width = (widthHint == SWT.DEFAULT) ? defaultWidth : widthHint;
		int height = (heightHint == SWT.DEFAULT) ? defaultHeight : heightHint;
		return new Point(width, height);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite, boolean)
	 */
	@Override
	protected void layout(Composite composite, boolean flushCache) {
		Rectangle clientArea = composite.getClientArea();
		for (Control child : composite.getChildren()) {
			child.setBounds(clientArea);
		}
	}

}
