/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * This layout accepts a single child control. Size hints are either passed on to
 * the child when computing its preferred size (context-sensitive) or are replaced
 * by SWT.DEFAULT (context-free). Context sensitivity can be configured independently
 * for the x and y dimensions.
 * 
 * The layout adapts to any externally requested size and to any preferred size of the
 * child. The preferred size of the child is determined, then the child is positioned
 * in the center of the container using that size.
 * 
 * In case the environment does not pass a size hint for a dimension, this layout
 * returns the child's preferred size in that dimension.
 */
public class CenterLayout extends Layout {

	/**
	 * the widthContextSensitive
	 */
	private boolean widthContextSensitive;

	/**
	 * the heightContextSensitive
	 */
	private boolean heightContextSensitive;

	/**
	 * Constructor
	 * @param widthContextSensitive whether the width is context-sensitive (true) or context-free (false)
	 * @param heightContextSensitive whether the height is context-sensitive (true) or context-free (false)
	 */
	public CenterLayout(boolean widthContextSensitive, boolean heightContextSensitive) {
		this.widthContextSensitive = widthContextSensitive;
		this.heightContextSensitive = heightContextSensitive;
	}

	/**
	 * @param widthHint the width hint
	 * @return widthHint Returns the width hint if width context sensitive, or SWT.DEFAULT if width context free.
	 */
	private int translateWidthHint(int widthHint) {
		return (widthContextSensitive ? widthHint : SWT.DEFAULT);
	}

	/**
	 * @param heightHint the height hint
	 * @return heightHint Returns the height hint if height context sensitive, or SWT.DEFAULT if height context free.
	 */
	private int translateHeightHint(int heightHint) {
		return (heightContextSensitive ? heightHint : SWT.DEFAULT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite, int, int, boolean)
	 */
	@Override
	protected Point computeSize(Composite subject, int widthHint, int heightHint, boolean changed) {
		if (subject.getChildren().length == 0) {
			return new Point(20, 20);
		}
		Point preferredChildSize = subject.getChildren()[0].computeSize(translateWidthHint(widthHint), translateHeightHint(heightHint));
		int width = (widthHint == SWT.DEFAULT) ? preferredChildSize.x : widthHint;
		int height = (heightHint == SWT.DEFAULT) ? preferredChildSize.y : heightHint;
		return new Point(width, height);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite, boolean)
	 */
	@Override
	protected void layout(Composite subject, boolean flushCache) {
		if (subject.getChildren().length == 0) {
			return;
		}
		Control child = subject.getChildren()[0];
		Point preferredChildSize = child.computeSize(translateWidthHint(subject.getSize().x), translateHeightHint(subject.getSize().y));
		child.setSize(preferredChildSize);
		int x = (subject.getSize().x - child.getSize().x) / 2;
		int y = (subject.getSize().y - child.getSize().y) / 2;
		child.setLocation(x, y);
	}

}
