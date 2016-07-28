/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.canvas;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * This canvas implementation retains its contents and reacts automatically
 * to paint events. Subclasses and/or clients may choose when to redraw
 * parts of the contents independently, but should call redraw()
 * accordingly. This canvas has a fixed size determined at construction.
 */
public class ContentRetainingCanvas extends Canvas {

	/**
	 * the contents
	 */
	private Image contents;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param width the width of this canvas
	 * @param height the height of this canvas
	 */
	public ContentRetainingCanvas(Composite parent, int width, int height) {
		super(parent, 0);
		contents = new Image(getDisplay(), width, height);
		addPaintListener(new MyPaintListener());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		contents.dispose();
		super.dispose();
	}

	/**
	 * Getter method for the contents.
	 * @return the contents
	 */
	public Image getContents() {
		return contents;
	}
	
	/**
	 * Setter method for the contents.
	 * @param contents the contents to set
	 */
	public void setContents(Image contents) {
		this.contents = contents;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(contents.getBounds().width, contents.getBounds().height);
	}

	/**
	 * This method is invoked directly before a paint event is processed. This gives
	 * the subclass a chance to modify the content image on-demand. The default
	 * implementation does nothing.
	 */
	protected void onBeforePaint() {
	}
	
	/**
	 * This paint listener draws the contents of the canvas on the GC.
	 */
	private class MyPaintListener implements PaintListener {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		@Override
		public void paintControl(PaintEvent event) {
			
			onBeforePaint();
			
			int x = event.x;
			if (x < 0) {
				x = 0;
			}
			
			int y = event.y;
			if (y < 0) {
				y = 0;
			}
			
			int width = event.width;
			if (width > contents.getBounds().width - x) {
				width = contents.getBounds().width - x;
			}
			
			int height = event.height;
			if (height > contents.getBounds().height - y) {
				height = contents.getBounds().height - y;
			}
			
			event.gc.drawImage(contents, x, y, width, height, x, y, width, height);
		}

	}
}
