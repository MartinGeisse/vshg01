/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecotools.simulator.ui.extension;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.disk.Disk;
import name.martingeisse.ecotools.simulator.ui.disk.DiskContributor;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;

import org.eclipse.swt.widgets.TabFolder;

/**
 * This contributor adds some nonstandard extensions to aid simulation.
 */
public class ExtensionContributor extends AbstractEcoSimulatorContributor {

	/**
	 * the diskContributor
	 */
	private DiskContributor diskContributor;
	
	/**
	 * Constructor.
	 * @param diskContributor the disk contributor
	 */
	public ExtensionContributor(DiskContributor diskContributor) {
		this.diskContributor = diskContributor;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#registerPeripheralDevices(name.martingeisse.ecotools.simulator.bus.Bus)
	 */
	@Override
	public void registerPeripheralDevices(Bus bus) {
		Disk disk = (diskContributor == null ? null : diskContributor.getDisk());
		getFramework().getCpu().setExtensionHandler(new CpuExtensionHandler(getFramework().getCpu(), bus, disk));
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
