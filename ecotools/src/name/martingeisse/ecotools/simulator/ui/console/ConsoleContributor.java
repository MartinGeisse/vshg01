/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.console;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterDisplay;
import name.martingeisse.ecotools.simulator.devices.keyboard.Keyboard;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;
import name.martingeisse.ecotools.simulator.ui.util.keyboard.GermanAppleToGermanWindowsKeyboardMap;
import name.martingeisse.swtlib.layout.CenterLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 */
public class ConsoleContributor extends AbstractEcoSimulatorContributor {

	/**
	 * The physical base address of the character display.
	 */
	public static final int CHARACTER_DISPLAY_BASE_ADDRESS = 0x30100000;

	/**
	 * The physical base address of the keyboard.
	 */
	public static final int KEYBOARD_BASE_ADDRESS = 0x30200000;

	/**
	 * The keyboard interrupt index.
	 */
	public static final int KEYBOARD_INTERRUPT = 4;

	/**
	 * the characterDisplay
	 */
	private CharacterDisplay characterDisplay;

	/**
	 * the keyboard
	 */
	private Keyboard keyboard;

	/**
	 * Constructor
	 */
	public ConsoleContributor() {
		this.characterDisplay = new CharacterDisplay();
		this.keyboard = new Keyboard();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {

		/** create a tab item **/
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("Console");

		/** create a parent composite for the canvas for positioning **/
		Composite positioningComposite = new Composite(tabFolder, 0);
		positioningComposite.setLayout(new CenterLayout(true, true));
		tabItem.setControl(positioningComposite);
		
		/** create the console canvas within the tab item **/
		ConsolePanel consolePanel = new ConsolePanel(positioningComposite, GermanAppleToGermanWindowsKeyboardMap.getInstance());
		ConsoleCanvas consoleCanvas = consolePanel.getConsoleCanvas();

		/** link the canvas and the character display device **/
		consoleCanvas.setCharacterDisplayUserInterfaceSocket(characterDisplay);
		characterDisplay.setUserInterface(consoleCanvas);
		
		/** link the canvas and the keyboard device **/
		consoleCanvas.setKeyboardUserInterfaceSocket(keyboard);
		keyboard.setUserInterface(consoleCanvas);

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#registerPeripheralDevices(name.martingeisse.ecotools.simulator.bus.Bus)
	 */
	@Override
	public void registerPeripheralDevices(Bus bus) {
		bus.add(CHARACTER_DISPLAY_BASE_ADDRESS, characterDisplay, new int[] {});
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
