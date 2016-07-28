/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.operations;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;
import name.martingeisse.swtlib.layout.CenterLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * This contributor adds the "operations" panel.
 */
public class OperationsContributor extends AbstractEcoSimulatorContributor {

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {

		/** create a tab item **/
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("Operations");

		/** create a parent composite for the operations panel for positioning **/
		Composite positioningComposite = new Composite(tabFolder, 0);
		positioningComposite.setLayout(new CenterLayout(true, true));
		tabItem.setControl(positioningComposite);
		
		/** create the operations panel within the tab item **/
		new OperationsPanel(positioningComposite, getFramework());

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#getMemoryVisualizationContributor()
	 */
	@Override
	public IMainMemoryVisualizationContributor getMemoryVisualizationContributor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#registerPeripheralDevices(name.martingeisse.ecotools.simulator.bus.Bus)
	 */
	@Override
	public void registerPeripheralDevices(Bus bus) {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
	}

}
