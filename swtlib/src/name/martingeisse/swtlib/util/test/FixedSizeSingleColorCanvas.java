/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.util.test;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * This subclass of {@link SingleColorCanvas} has a preferred
 * size and will ignore size hints from the parent.
 */
public class FixedSizeSingleColorCanvas extends SingleColorCanvas {

	/**
	 * the width
	 */
	private int width;
	
	/**
	 * the height
	 */
	private int height;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param r the red component of the color to use
	 * @param g the green component of the color to use
	 * @param b the blue component of the color to use
	 * @param width the width to report
	 * @param height the height to report
	 */
	public FixedSizeSingleColorCanvas(Composite parent, int r, int g, int b, int width, int height) {
		super(parent, r, g, b);
		this.width = width;
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int widthHint, int heightHint, boolean changed) {
		return new Point(width, height);
	}
	
}
