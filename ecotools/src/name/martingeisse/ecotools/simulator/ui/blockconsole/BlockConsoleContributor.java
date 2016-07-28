/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.blockconsole;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.blockdisplay.BlockDisplay;
import name.martingeisse.ecotools.simulator.devices.keyboard.Keyboard;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;
import name.martingeisse.swtlib.layout.CenterLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 */
public class BlockConsoleContributor extends AbstractEcoSimulatorContributor {

	/**
	 * The physical base address of the block display.
	 */
	public static final int BLOCK_DISPLAY_BASE_ADDRESS = 0x30100000;

	/**
	 * The physical base address of the keyboard.
	 */
	public static final int KEYBOARD_BASE_ADDRESS = 0x30200000;

	/**
	 * The keyboard interrupt index.
	 */
	public static final int KEYBOARD_INTERRUPT = 4;

	/**
	 * the blockDisplay
	 */
	private BlockDisplay blockDisplay;

	/**
	 * the keyboard
	 */
	private Keyboard keyboard;

	/**
	 * Constructor
	 */
	public BlockConsoleContributor() {
		this.blockDisplay = new BlockDisplay();
		this.keyboard = new Keyboard();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {

		/** create a tab item **/
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("Block Console");

		/** create a parent composite for the canvas for positioning **/
		Composite positioningComposite = new Composite(tabFolder, 0);
		positioningComposite.setLayout(new CenterLayout(true, true));
		tabItem.setControl(positioningComposite);
		
		/** create the console canvas within the tab item **/
		BlockConsoleCanvas consoleCanvas = new BlockConsoleCanvas(positioningComposite);

		/** link the canvas and the character display device **/
		consoleCanvas.setBlockDisplayUserInterfaceSocket(blockDisplay);
		blockDisplay.setUserInterface(consoleCanvas);

		// test
		for (int y=0; y<8; y++) {
			for (int x=0; x<8; x++) {
				blockDisplay.setBlock(x, y, y*8+x);
			}
		}
		
		/** link the canvas and the keyboard device **/
		consoleCanvas.setKeyboardUserInterfaceSocket(keyboard);
		keyboard.setUserInterface(consoleCanvas);

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#registerPeripheralDevices(name.martingeisse.ecotools.simulator.bus.Bus)
	 */
	@Override
	public void registerPeripheralDevices(Bus bus) {
		bus.add(BLOCK_DISPLAY_BASE_ADDRESS, blockDisplay, new int[] {});
		bus.add(KEYBOARD_BASE_ADDRESS, keyboard, new int[] {
			KEYBOARD_INTERRUPT
		});
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
		/** the console UI remains unchanged in fast simulation mode **/
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#getMemoryVisualizationContributor()
	 */
	@Override
	public IMainMemoryVisualizationContributor getMemoryVisualizationContributor() {
		return null;
	}

}
