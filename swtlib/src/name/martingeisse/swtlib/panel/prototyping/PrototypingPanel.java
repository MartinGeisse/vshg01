/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.swtlib.panel.prototyping;

import name.martingeisse.swtlib.util.ClassAssociatedResourceUtil;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * This panel allows to build quick and simple prototyping UIs.
 */
public class PrototypingPanel extends Composite {

	/**
	 * The size of a single cell along each axis.
	 */
	public static final int CELL_SIZE = 20;
	
	/**
	 * the image
	 */
	private static Image image;
	
	/**
	 * the cellCountX
	 */
	private final int cellCountX;
	
	/**
	 * the cellCountY
	 */
	private final int cellCountY;
	
	/**
	 * Constructor.
	 * @param parent the parent composite
	 * @param cellCountX the number of cells in the X direction
	 * @param cellCountY the number of cells in the Y direction
	 */
	public PrototypingPanel(Composite parent, int cellCountX, int cellCountY) {
		super(parent, 0);
		this.cellCountX = cellCountX;
		this.cellCountY = cellCountY;
		enforceSize();
		
		if (image == null) {
			image = ClassAssociatedResourceUtil.loadImage(PrototypingPanel.class, null, "png", getDisplay());
		}
		setBackgroundImage(image);
		
	}
	
	/**
	 * Sets the size to the intrinsic size determined from the cell count.
	 */
	public void enforceSize() {
		setSize(cellCountX * CELL_SIZE, cellCountY * CELL_SIZE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int widthHint, int heightHint, boolean contentsChanged) {
		return new Point(cellCountX * CELL_SIZE, cellCountY * CELL_SIZE);
	}
	
}
