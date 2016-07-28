/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.output;

import java.io.File;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.output.OutputDevice;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;

import org.eclipse.swt.widgets.TabFolder;

/**
 * This class implements an "Output" device that writes to a
 * file on the host system.
 */
public class FileOutputContributor extends AbstractEcoSimulatorContributor {

	/**
	 * The physical base address of the output device.
	 */
	public static final int OUTPUT_DEVICE_BASE_ADDRESS = 0x3F000000;

	/**
	 * the file
	 */
	private File file;
	
	/**
	 * Constructor
	 * @param file the file where output is stored
	 */
	public FileOutputContributor(File file) {
		this.file = file;
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
		bus.add(OUTPUT_DEVICE_BASE_ADDRESS, new OutputDevice(file), new int[] {});
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
	}

}
