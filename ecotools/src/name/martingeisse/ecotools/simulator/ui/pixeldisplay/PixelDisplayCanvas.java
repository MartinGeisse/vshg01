/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.pixeldisplay;

import name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterface;
import name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterfaceSocket;
import name.martingeisse.swtlib.canvas.ContentRetainingCanvas;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

/**
 * This canvas behaves as the user interface component for a pixel display.
 */
public class PixelDisplayCanvas extends ContentRetainingCanvas implements IPixelDisplayUserInterface {

	/**
	 * the userInterfaceSocket
	 */
	private IPixelDisplayUserInterfaceSocket userInterfaceSocket;

	/**
	 * the delayUpdates
	 */
	private boolean delayUpdates;

	/**
	 * Constructor
	 * @param parent the parent composite
	 */
	public PixelDisplayCanvas(Composite parent) {
		super(parent, 640, 480);
		this.delayUpdates = false;
	}

	/**
	 * @return Returns the userInterfaceSocket.
	 */
	public IPixelDisplayUserInterfaceSocket getUserInterfaceSocket() {
		return userInterfaceSocket;
	}

	/**
	 * Sets the userInterfaceSocket.
	 * @param userInterfaceSocket the new value to set
	 */
	public void setUserInterfaceSocket(IPixelDisplayUserInterfaceSocket userInterfaceSocket) {
		this.userInterfaceSocket = userInterfaceSocket;
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
			updateAllPixels();
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.pixeldisplay.IPixelDisplayUserInterfaceSocket, int, int)
	 */
	@Override
	public void update(IPixelDisplayUserInterfaceSocket pixelDisplayUserInterfaceSocket, int x, int y) {
		if (!delayUpdates) {
			int pixel = userInterfaceSocket.getPixel(x, y);
			GC gc = new GC(getContents());
			Color color = new Color(getDisplay(), (pixel >> 16) & 0xff, (pixel >> 8) & 0xff, pixel & 0xff);
			gc.setForeground(color);
			gc.drawPoint(x, y);
			color.dispose();
			gc.dispose();
		}
	}

	/**
	 * Updates all pixels from the pixel display.
	 */
	public void updateAllPixels() {
		if (!delayUpdates) {
			GC gc = new GC(getContents());
			for (int i=0; i<640; i++) {
				for (int j=0; j<480; j++) {
					int pixel = userInterfaceSocket.getPixel(i, j);
					Color color = new Color(getDisplay(), (pixel >> 16) & 0xff, (pixel >> 8) & 0xff, pixel & 0xff);
					gc.setForeground(color);
					gc.drawPoint(i, j);
					color.dispose();
				}
			}
			gc.dispose();
		}
	}

}
