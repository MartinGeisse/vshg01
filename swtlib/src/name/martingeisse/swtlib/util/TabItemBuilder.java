/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.swtlib.util;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Utility method to simplify the creation of tab items. Actually this
 * class doesn't do much, but unlike building tab items from a SWT
 * {@link TabFolder} directly, it is self-documenting.
 */
public class TabItemBuilder {

	/**
	 * the tabFolder
	 */
	private final TabFolder tabFolder;
	
	/**
	 * Constructor.
	 * @param tabFolder the tab folder to build tab items for
	 */
	public TabItemBuilder(TabFolder tabFolder) {
		this.tabFolder = tabFolder;
	}
	
	/**
	 * Returns the parent composite that must be passed when creating
	 * the controls for tab items.
	 * @return the parent composite
	 */
	public Composite getParent() {
		return tabFolder;
	}
	
	/**
	 * Creates a tab item using the specified control. The created tab item
	 * is already added to the tab folder as needed, and also returned in
	 * case the caller needs it (unlikely).
	 * @param title the title text for the tab
	 * @param control the main control of the tab item
	 * @return the tab item
	 */
	public TabItem createTabItem(String title, Control control) {
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("Test");
		tabItem.setControl(control);
		return tabItem;
	}
	
}
