/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationPanel;

/**
 * This listener updates a {@link MemoryVisualizationPanel} on
 * each store operation.
 */
public class UpdateMemoryVisualizationPanelOnStoreListener implements IMainMemoryVisualizationListener {

	/**
	 * the panel
	 */
	private MemoryVisualizationPanel panel;

	/**
	 * Constructor
	 * @param panel he panel to refresh on store operations.
	 */
	public UpdateMemoryVisualizationPanelOnStoreListener(MemoryVisualizationPanel panel) {
		this.panel = panel;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListener#onAfterWrite()
	 */
	@Override
	public void onAfterWrite() {
		panel.updateContents();
	}

}
