/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.pixeldisplay;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.pixeldisplay.PixelDisplay;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * The contributor for the pixel display.
 */
public class PixelDisplayContributor extends AbstractEcoSimulatorContributor {

	/**
	 * The physical base address of the pixel display device.
	 */
	public static final int PIXEL_DISPLAY_BASE_ADDRESS = 0x3FC00000;

	/**
	 * the pixelDisplay
	 */
	private PixelDisplay pixelDisplay;
	
	/**
	 * Constructor
	 */
	public PixelDisplayContributor() {
		this.pixelDisplay = new PixelDisplay();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {

		/** create a tab item **/
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("GFX");

		/** create the pixel display canvas within the tab item **/
		PixelDisplayCanvas pixelDisplayCanvas = new PixelDisplayCanvas(tabFolder);
		tabItem.setControl(pixelDisplayCanvas);

		/** link the terminal panel and the terminal device **/
		pixelDisplay.setUserInterface(pixelDisplayCanvas);
		pixelDisplayCanvas.setUserInterfaceSocket(pixelDisplay);

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
		bus.add(PIXEL_DISPLAY_BASE_ADDRESS, pixelDisplay, new int[] {});
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
	}

}
