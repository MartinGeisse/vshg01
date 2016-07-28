/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.swtlib.panel.prototyping;

import org.eclipse.swt.widgets.Control;

/**
 * Base class for all components that can be put on a {@link PrototypingPanel}.
 * 
 * Position and size of this class are measured in cells, not pixels.
 * 
 * @param <T> the SWT component type
 */
public class AbstractPrototypingComponent<T extends Control> {

	/**
	 * the prototypingPanel
	 */
	private PrototypingPanel prototypingPanel;
	
	/**
	 * the x
	 */
	private int x;

	/**
	 * the y
	 */
	private int y;

	/**
	 * the width
	 */
	private int width;

	/**
	 * the height
	 */
	private int height;
	
	/**
	 * the control
	 */
	private T control;

	/**
	 * Constructor.
	 * @param prototypingPanel the prototyping panel that hosts this component
	 */
	public AbstractPrototypingComponent(PrototypingPanel prototypingPanel) {
		this.prototypingPanel = prototypingPanel;
		this.x = 0;
		this.y = 0;
		this.width = 1;
		this.height = 1;
		this.control = null;
	}

	/**
	 * Getter method for the prototypingPanel.
	 * @return the prototypingPanel
	 */
	public PrototypingPanel getPrototypingPanel() {
		return prototypingPanel;
	}
	
	/**
	 * Getter method for the x.
	 * @return the x
	 */
	public final int getX() {
		return x;
	}

	/**
	 * Setter method for the x.
	 * @param x the x to set
	 */
	public final void setX(final int x) {
		this.x = x;
		updateControlBounds();
	}

	/**
	 * Getter method for the y.
	 * @return the y
	 */
	public final int getY() {
		return y;
	}

	/**
	 * Setter method for the y.
	 * @param y the y to set
	 */
	public final void setY(final int y) {
		this.y = y;
		updateControlBounds();
	}

	/**
	 * Getter method for the width.
	 * @return the width
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * Setter method for the width.
	 * This method is called ...Internal so it can be final, yet made public under its
	 * intended name in a subclass.
	 * @param width the width to set
	 */
	protected final void setWidthInternal(final int width) {
		this.width = width;
		updateControlBounds();
	}

	/**
	 * Getter method for the height.
	 * @return the height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * Setter method for the height.
	 * This method is called ...Internal so it can be final, yet made public under its
	 * intended name in a subclass.
	 * @param height the height to set
	 */
	protected final void setHeightInternal(final int height) {
		this.height = height;
		updateControlBounds();
	}

	/**
	 * Getter method for the control.
	 * @return the control
	 */
	public final T getControl() {
		return control;
	}
	
	/**
	 * Setter method for the control.
	 * This method is called ...Internal so it can be final, yet made public under its
	 * intended name in a subclass.
	 * @param control the control to set
	 */
	protected final void setControlInternal(T control) {
		if (control != null && control.getParent() != prototypingPanel) {
			throw new IllegalArgumentException("control's parent is not this component's prototyping panel");
		}
		this.control = control;
		updateControlBounds();
	}
	
	/**
	 * Sets the bounds stored in this object for the control.
	 * If no control has been set, this method does nothing.
	 */
	private final void updateControlBounds() {
		if (control != null) {
			control.setBounds(x * PrototypingPanel.CELL_SIZE, y * PrototypingPanel.CELL_SIZE, width * PrototypingPanel.CELL_SIZE, height* PrototypingPanel.CELL_SIZE);
		}
	}
	
}
