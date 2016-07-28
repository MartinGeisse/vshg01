/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.util;

import name.martingeisse.swtlib.util.test.SingleColorCanvas;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This composite contains a single child that is laid out using
 * a {@link FillLayout} and can be exchanged at runtime. While
 * no child is set, a placeholder can be used.
 */
public class ExchangeComposite extends Composite {

	/**
	 * Constructor
	 * @param parent the parent composite
	 */
	public ExchangeComposite(Composite parent) {
		super(parent, 0);
		setLayout(new FillLayout());
	}
	
	/**
	 * Creates the placeholder control and adds it to this composite.
	 */
	public void addPlaceholder() {
		new SingleColorCanvas(this, 255, 0, 0);
	}
	
	/**
	 * Removes all children.
	 */
	public void clear() {
		for (Control child : getChildren()) {
			child.dispose();
		}
	}
	
}
