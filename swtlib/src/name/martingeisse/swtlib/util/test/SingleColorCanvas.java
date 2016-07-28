/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.util.test;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * This control paints itself in a single flat color.
 * If the color was allocated by this canvas then it is
 * disposed when this canvas is.
 */
public class SingleColorCanvas extends Canvas {

	/**
	 * the color
	 */
	private Color color;
	
	/**
	 * the disposeColor
	 */
	private boolean disposeColor;
	
	/**
	 * Constructor. This constructor allocates a new Color object and
	 * disposes it when this canvas is disposed.
	 * @param parent the parent composite
	 * @param r the red component of the color to use
	 * @param g the green component of the color to use
	 * @param b the blue component of the color to use
	 */
	public SingleColorCanvas(Composite parent, int r, int g, int b) {
		this(parent, new Color(parent.getDisplay(), r, g, b), true);
	}
	
	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param color the color to use. This color is not disposed by this canvas.
	 */
	public SingleColorCanvas(Composite parent, Color color) {
		this(parent, color, false);
	}

	/**
	 * Internal Constructor.
	 * @param parent the parent composite
	 * @param color the color to use
	 * @param disposeColor whether the color shall be disposed when this canvas is disposed
	 */
	private SingleColorCanvas(Composite parent, Color color, boolean disposeColor) {
		super(parent, 0);
		this.color = color;
		this.disposeColor = disposeColor;
		addPaintListener(new MyPaintListener());
	}

	/**
	 * @return Returns the color.
	 */
	public Color getColor() {
		return color;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		if (disposeColor) {
			color.dispose();
		}
		super.dispose();
	}
	
	/**
	 * this listener redraws the canvas using the specified color.
	 */
	private class MyPaintListener implements PaintListener {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		@Override
		public void paintControl(PaintEvent event) {
			event.gc.setBackground(color);
			event.gc.fillRectangle(event.x, event.y, event.width, event.height);
		}
		
	}
	
}
