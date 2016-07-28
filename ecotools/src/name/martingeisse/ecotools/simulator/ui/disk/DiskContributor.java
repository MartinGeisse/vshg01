/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.disk;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.disk.Disk;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;

import org.eclipse.swt.widgets.TabFolder;

/**
 * This class implements a disk device that operated on a
 * disk image file on the host system.
 */
public class DiskContributor extends AbstractEcoSimulatorContributor {

	/**
	 * The physical base address of the disk device.
	 */
	public static final int DISK_BASE_ADDRESS = 0x30400000;

	/**
	 * the file
	 */
	private File file;
	
	/**
	 * the disk
	 */
	private Disk disk;
	
	/**
	 * Constructor
	 * @param file the disk image file
	 */
	public DiskContributor(File file) {
		this.file = file;
	}

	/**
	 * Getter method for the disk.
	 * @return the disk
	 */
	public Disk getDisk() {
		return disk;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {
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
		try {
			disk = new Disk(file);
			bus.add(DISK_BASE_ADDRESS, disk, new int[] {8});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
	}

}
