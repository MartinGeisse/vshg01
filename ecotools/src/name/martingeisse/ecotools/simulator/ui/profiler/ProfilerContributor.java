/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecotools.simulator.ui.profiler;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.profiling.FlatCpuProfiler;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * This contributor adds a performance profiler.
 */
public class ProfilerContributor extends AbstractEcoSimulatorContributor {

	/**
	 * the profiler
	 */
	private FlatCpuProfiler profiler;
	
	/**
	 * Constructor.
	 */
	public ProfilerContributor() {
		this.profiler = new FlatCpuProfiler();
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {

		/** create a tab item **/
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("Profiling");

		/** create the profiler panel within the tab item **/
		ProfilerPanel profilerPanel = new ProfilerPanel(tabFolder, profiler);
		tabItem.setControl(profilerPanel);

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#registerPeripheralDevices(name.martingeisse.ecotools.simulator.bus.Bus)
	 */
	@Override
	public void registerPeripheralDevices(Bus bus) {
		getFramework().getCpu().setProfiler(profiler);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#getMemoryVisualizationContributor()
	 */
	@Override
	public IMainMemoryVisualizationContributor getMemoryVisualizationContributor() {
		return null;
	}

}
