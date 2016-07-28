/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.memory;

import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListenerRegistry;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy;
import name.martingeisse.swtlib.panel.AbstractTabbedBrowsingPanel;

import org.eclipse.swt.widgets.Composite;

/**
 * 
 */
public class MemoryPanel extends AbstractTabbedBrowsingPanel<MemoryTabPanel> {

	/**
	 * the visualization storage strategy
	 */
	private IMemoryVisualizationStorageStrategy storageStrategy;
	
	/**
	 * the mainMemoryVisualizationListenerRegistry
	 */
	private IMainMemoryVisualizationListenerRegistry mainMemoryVisualizationListenerRegistry;
	
	/**
	 * Constructor
	 * @param parent the parent widget
	 * @param storageStrategy the visualization storage strategy
	 * @param mainMemoryVisualizationListenerRegistry memory panels will register themselves with this
	 * listener to receive update notification when the main memory contents change.
	 */
	public MemoryPanel(Composite parent, IMemoryVisualizationStorageStrategy storageStrategy, IMainMemoryVisualizationListenerRegistry mainMemoryVisualizationListenerRegistry) {
		super(parent, MemoryTabPanel.class);
		this.storageStrategy = storageStrategy;
		this.mainMemoryVisualizationListenerRegistry = mainMemoryVisualizationListenerRegistry;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.AbstractTabbedBrowsingPanel#createNewTabControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected MemoryTabPanel createNewTabControl(Composite parent) {
		return new MemoryTabPanel(parent, storageStrategy, mainMemoryVisualizationListenerRegistry);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.AbstractTabbedBrowsingPanel#makeAddressActive(org.eclipse.swt.widgets.Control, java.lang.String)
	 */
	@Override
	protected void makeAddressActive(MemoryTabPanel memoryVisualizationPanel, String address) {
		try {
			long longAddressValue = Long.parseLong(address, 16);
			memoryVisualizationPanel.getMemoryVisualizationPanel().setStartAddress((int)longAddressValue);
		} catch (NumberFormatException e) {
			memoryVisualizationPanel.getMemoryVisualizationPanel().setStartAddress(0);
		}
		memoryVisualizationPanel.getMemoryVisualizationPanel().updateContents();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.AbstractTabbedBrowsingPanel#getDefaultAddress()
	 */
	@Override
	protected String getDefaultAddress() {
		return "e0000000";
	}

}
