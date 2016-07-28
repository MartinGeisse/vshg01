/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;

import org.eclipse.swt.widgets.TabFolder;

/**
 * This class is the basis for simulator plug-ins.
 */
public interface IEcoSimulatorContributor {

	/**
	 * Initializes this contributor. No features of the framework are
	 * accessible at this point, but this contributor may store the
	 * framework reference for later use.
	 * @param framework the parent framework that uses this contributor.
	 */
	public void initialize(EcoSimulatorFramework framework);
	
	/**
	 * Creates the tab items for the simulator GUI.
	 * @param tabFolder the tab folder to add the tab items to.
	 */
	public void createTabItems(TabFolder tabFolder);
	
	/**
	 * Registers the peripheral devices contributed by this pobject with the specified bus.
	 * @param bus the bus where devices shall be attached. This is always the bus that
	 * can be obtained from the {@link EcoSimulatorFramework} passed at initialization time.
	 */
	public void registerPeripheralDevices(Bus bus);

	/**
	 * Enables or disables fast simulation mode. In this mode, UI components that
	 * display highly transient values (such as CPU registers) should not
	 * react to UI update notifications. The exact decision of which events are
	 * visualized in the GUI is left to the individual components.
	 * @param enable whether to enable or disable fast simulation mode
	 */
	public void setEnableFastSimulationMode(boolean enable);
	
	/**
	 * This method returns an optional memory visualization contributor, or null if this
	 * simulator contributor does not contribute to memory visualization. The returned
	 * object helps to make the contents of device memory visible to the user.
	 * @return Returns the memory visualization contributor, or null if none is provided.
	 */
	public IMainMemoryVisualizationContributor getMemoryVisualizationContributor();
	
}
